package net.pms.remote;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import net.pms.PMS;
import net.pms.configuration.PmsConfiguration;
import net.pms.configuration.WebRender;
import net.pms.dlna.DLNAMediaSubtitle;
import net.pms.dlna.DLNAResource;
import net.pms.dlna.LiveStream;
import net.pms.dlna.Playlist;
import net.pms.dlna.RootFolder;
import net.pms.dlna.StreamItem;
import net.pms.dlna.WebStreamItem;
import net.pms.dlna.virtual.VirtualVideoAction;
import net.pms.formats.Format;
import net.pms.io.OutputParams;

public class RemotePlayHandler implements HttpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(RemotePlayHandler.class);
	private RemoteWeb parent;
	private static final PmsConfiguration configuration = PMS.getConfiguration();

	public RemotePlayHandler(RemoteWeb parent) {
		this.parent = parent;
	}

	private String returnPage() {
		// special page to return
		return "<html><head><script>window.refresh=true;history.back()</script></head></html>";
	}

	private void addNextByType(DLNAResource d, HashMap<String, Object> vars) {
		List<DLNAResource> children = d.getParent().getChildren();
		boolean looping = configuration.getWebAutoLoop(d.getFormat());
		int type = d.getType();
		int size = children.size();
		int mod = looping ? size : 9999;
		int self = children.indexOf(d);
		for (int step = -1; step < 2; step += 2) {
			int i = self;
			int offset = (step < 0 && looping) ? size : 0;
			DLNAResource next = null;
			while (true) {
				i = (offset + i + step) % mod;
				if (i >= size || i < 0 || i == self) {
					break; // Not found
				}
				next = children.get(i);
				if (next.getType() == type && !next.isFolder()) {
					break; // Found
				}
				next = null;
			}
			String pos = step > 0 ? "next" : "prev";
			vars.put(pos + "Id", next != null ? next.getResourceId() : null);
			vars.put(pos + "Attr", next != null ? (" title=\"" + StringEscapeUtils.escapeHtml(next.resumeName()) + "\"") : " disabled");
		}
	}

	private String mkPage(String id, HttpExchange t) throws IOException {
		HashMap<String, Object> vars = new HashMap<>();
		vars.put("serverName", configuration.getServerDisplayName());

		LOGGER.debug("Make play page " + id);
		String user = RemoteUtil.userName(t);
		RootFolder root = parent.getRoot(user, true, t);
		WebRender renderer = parent.getRenderer(user, t);
		renderer.setBrowserInfo(RemoteUtil.getCookie("UMSINFO", t), t.getRequestHeaders().getFirst("User-agent"));

		//List<DLNAResource> res = root.getDLNAResources(id, false, 0, 0, renderer);
		DLNAResource r = root.getDLNAResource(id, renderer);
		if (r == null) {
			LOGGER.debug("Bad web play id: " + id);
			throw new IOException("Bad Id");
		}
		if (!r.isCodeValid(r)) {
			LOGGER.debug("coded object with invalid code");
			throw new IOException("Bad code");
		}
		if (r instanceof VirtualVideoAction) {
			// for VVA we just call the enable fun directly
			// waste of resource to play dummy video
			if (((VirtualVideoAction) r).enable()) {
				renderer.notify(renderer.INFO, r.getName() + " enabled");
			} else {
				renderer.notify(renderer.INFO, r.getName() + " disabled");
			}
			return returnPage();
		}

		Format format =  r.getFormat();
		boolean isImage = r.getType() == Format.IMAGE;
		boolean isVideo = r.getType() == Format.VIDEO;
		boolean isAudio = r.getType() == Format.AUDIO;
		String query = t.getRequestURI().getQuery();
		boolean forceFlash = StringUtils.isNotEmpty(RemoteUtil.getQueryVars(query, "flash"));
		boolean forcehtml5 = StringUtils.isNotEmpty(RemoteUtil.getQueryVars(query, "html5"));
		boolean flowplayer = isVideo && (forceFlash || (!forcehtml5 && configuration.getWebFlash()));

		// hack here to ensure we got a root folder to use for recently played etc.
//		renderer.setRootFolder(root);
		String id1 = URLEncoder.encode(id, "UTF-8");
		String name = StringEscapeUtils.escapeHtml(r.resumeName());
		String mime = r.getRendererMimeType(renderer);
		String mediaType = isVideo ? "video" : isAudio ? "audio" : isImage ? "image" : "";
		String auto = "autoplay";
		@SuppressWarnings("unused")
		String coverImage = "";

        boolean resume = !(r instanceof StreamItem) && r.getMedia().getPlayPosition() > 0
                && (r.getMedia().getDuration() - r.getMedia().getPlayPosition() < 1);
		vars.put("resume", resume);
		if (resume) {
    		vars.put("time", r.getMedia().getPlayPosition());
    		vars.put("timeStr", DurationFormatUtils.formatDuration((long) r.getMedia().getPlayPosition() * 1000, "HH:mm:ss"));
    		vars.put("resumeTxt", RemoteUtil.getMsgString("Web.12", t));
		}

//		if (!RemoteUtil.directmime(mime)) {
//			mime = r.getMedia().isAudio() ? RemoteUtil.MIME_MP3 :
//				r.getMedia().isVideo() ? RemoteUtil.MIME_MP4 : RemoteUtil.MIME_JPG;
//		}
//		if (isVideo) {
//			if (mime.equals(FormatConfiguration.MIMETYPE_AUTO)) {
//				if (r.getMedia() != null && r.getMedia().getMimeType() != null) {
//					mime = r.getMedia().getMimeType();
//				}
//			}
//			if (!flowplayer) {
//				if (!RemoteUtil.directmime(mime) || RemoteUtil.transMp4(mime, r.getMedia()) || r.isResume()) {
//					mime = renderer != null ? renderer.getVideoMimeType() : RemoteUtil.transMime();
//				}
//			}
//		}
		vars.put("isVideo", isVideo);
		vars.put("hls", (r instanceof LiveStream));
		vars.put("name", name);
		vars.put("id1", id1);
		vars.put("url", r.getTranscodedFileURL(renderer));
		vars.put("autoContinue", configuration.getWebAutoCont(format));
		if (isAudio && !(r instanceof WebStreamItem)) {
			vars.put("artist", DLNAResource.getDisplayName(r.getMedia().getAudioTracksList().get(0).getArtist()));
			vars.put("album", DLNAResource.getDisplayName(r.getMedia().getAudioTracksList().get(0).getAlbum()));
		}
		if (configuration.isDynamicPls()) {
			if (r.getParent() instanceof Playlist) {
				vars.put("plsOp", "del");
				vars.put("plsSign", "-");
				vars.put("plsAttr", RemoteUtil.getMsgString("Web.4", t));
			} else {
				vars.put("plsOp", "add");
				vars.put("plsSign", "+");
				vars.put("plsAttr", RemoteUtil.getMsgString("Web.5", t));
			}
		}
		addNextByType(r, vars);
		if (isImage) {
			// do this like this to simplify the code
			// skip all player crap since img tag works well
			int delay = configuration.getWebImgSlideDelay() * 1000;
			if (delay > 0 && configuration.getWebAutoCont(format)) {
				vars.put("delay", delay);
			}
		} else {
			vars.put("mediaType", mediaType);
			vars.put("auto", auto);
			vars.put("mime", mime);
			if (flowplayer) {
				if (
					RemoteUtil.directmime(mime) 
//					&&
//					!RemoteUtil.transMp4(mime, r.getMedia()) &&
//					!r.isResume() &&
//					!forceFlash
				) {
//					vars.put("src", true);
				}
			} else {
//				vars.put("width", renderer.getVideoWidth());
//				vars.put("height", renderer.getVideoHeight());
			}
		}
		if (configuration.useWebControl()) {
			vars.put("push", true);
		}

		class Subtitle {
		    String url, lang, label;
		}
		if (isVideo) {
            if (configuration.getWebSubs()) {
                List<Subtitle> subs = new ArrayList<>();
                if (r.getMediaSubtitle() != null) {
                    // Media selected from subs selection folder
                    DLNAMediaSubtitle sub = r.getMediaSubtitle();
                    Subtitle obj = new Subtitle();
                    if (sub.getLang() != null)
                        obj.lang = sub.getLang();
                    else
                        obj.lang = PMS.getLocale().getLanguage();
                    obj.label = sub.getLangFullName();
                    obj.url = String.format("/files/proxy?u=%s.vtt",
                            URLEncoder.encode(FilenameUtils.removeExtension(r.getSubsURL(sub)), "utf-8"));

                    subs.add(obj);
                } else if (r.getMedia() != null) {
                    for (DLNAMediaSubtitle sub : r.getMedia().getSubtitleTracksList()) {
                        Subtitle obj = new Subtitle();
                        if (sub.getLang() != null)
                            obj.lang = sub.getLang();
                        else
                            obj.lang = PMS.getLocale().getLanguage();
                        obj.label = sub.getLangFullName();
                        obj.url = String.format("/files/proxy?u=%s.vtt",
                                URLEncoder.encode(FilenameUtils.removeExtension(r.getSubsURL(sub)), "utf-8"));

                        subs.add(obj);
                    }
                }
                vars.put("sub", subs);
            } else {
                // With HTML5 video, we don't need this part
                // only if subs are requested as <track> tags
                // otherwise we'll transcode them in
                OutputParams p = new OutputParams(configuration);
                // boolean isFFmpegFontConfig = configuration.isFFmpegFontConfig();
                // if (isFFmpegFontConfig) { // do not apply fontconfig to flowplayer subs
                // configuration.setFFmpegFontConfig(false);
                // }
                // Player.setAudioAndSubs(r.getName(), r.getMedia(), p);
                if (p.sid != null && p.sid.getType().isText()) {
                    try {
                        // File subFile = SubtitleUtils.getSubtitles(r, r.getMedia(), p, configuration,
                        // SubtitleType.WEBVTT);
                        // LOGGER.debug("subFile " + subFile);
                        // if (subFile != null) {
                        // vars.put("sub", parent.getResources().add(subFile));
                        // }
                    } catch (Exception e) {
                        LOGGER.debug("error when doing sub file " + e);
                    }
                }

                // configuration.setFFmpegFontConfig(isFFmpegFontConfig); // return back original fontconfig value
            }
		}
        vars.put("page", 0);
        vars.put("count", 20);

		return parent.getResources().getTemplate(isImage ? "image.html" : flowplayer ? "flow.html" : "play.html").execute(vars);
	}

	@Override
	public void handle(HttpExchange t) {
		try {
			if (RemoteUtil.deny(t)) {
				throw new IOException("Access denied");
			}
			String p = t.getRequestURI().getPath();
			WebRender renderer = parent.getRenderer(RemoteUtil.userName(t), t);

			if (p.contains("/play/")) {
				LOGGER.debug("got a play request " + t.getRequestURI());
				String id = RemoteUtil.getId("play/", t);
				String response = mkPage(id, t);
				// LOGGER.trace("play page " + response);
				RemoteUtil.respond(t, response, 200, "text/html");
			} else if (p.contains("/playerstatus/")) {
				String json = IOUtils.toString(t.getRequestBody(), "UTF-8");
				LOGGER.trace("got player status: " + json);
				RemoteUtil.respond(t, "", 200, "text/html");

//				RootFolder root = parent.getRoot(RemoteUtil.userName(t), t);
//				if (root == null) {
//					LOGGER.debug("root not found");
//					throw new IOException("Unknown root");
//				}
				((WebRender.WebPlayer) renderer.getPlayer()).setData(json);
			} else if (p.contains("/playlist/")) {
				String[] tmp = p.split("/");
				// sanity
				if (tmp.length < 3) {
					throw new IOException("Bad request");
				}
				String op = tmp[tmp.length - 2];
				String id = tmp[tmp.length - 1];
				DLNAResource r = PMS.getGlobalRepo().get(id);
				if (r != null) {
					RootFolder root = parent.getRoot(RemoteUtil.userName(t), t);
					if (root == null) {
						LOGGER.debug("root not found");
						throw new IOException("Unknown root");
					}
					if (op.equals("add")) {
						PMS.get().getDynamicPls().add(r);
						renderer.notify(renderer.OK, "Added '" + r.getDisplayName() + "' to dynamic playlist");
					} else if (op.equals("del") && (r.getParent() instanceof Playlist)) {
						((Playlist) r.getParent()).remove(r);
						renderer.notify(renderer.INFO, "Removed '" + r.getDisplayName() + "' from playlist");
					}
				}
				RemoteUtil.respond(t, returnPage(), 200, "text/html");
			}
		} catch (Exception e) {
			// Not catching the exception will kill the web thread.
			LOGGER.error("Error in request: {}", e);
				// Invalid id, object not in cache or session expired; redirect to root
//				String path = t.getRequestURI().getPath();
//				String response = "<html><body>404 - File Not Found: " + path + "</body></html>";
//				RemoteUtil.respond(t, response, 302, "text/html");
				
			Headers hdr = t.getResponseHeaders();
			hdr.add("Location", "/browse/0");
			RemoteUtil.respond(t, "", 302, "text/html");
		}
	}
}
