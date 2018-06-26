package net.pms.remote;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

import net.pms.Messages;
import net.pms.PMS;
import net.pms.configuration.IpFilter;
import net.pms.configuration.WebRender;
import net.pms.dlna.DLNAMediaInfo;
import net.pms.dlna.Range;
import net.pms.formats.Format;
import net.pms.network.RequestHandlerV2;
import net.pms.newgui.LooksFrame;
import net.pms.util.FileWatcher;
import net.pms.util.Languages;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;

public class RemoteUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(RemoteUtil.class);

	public static final String MIME_MP4 = "video/mp4";
	public static final String MIME_OGG = "video/ogg";
	public static final String MIME_WEBM = "video/webm";
	//public static final String MIME_TRANS = MIME_MP4;
	public static final String MIME_TRANS = MIME_OGG;
	//public static final String MIME_TRANS = MIME_WEBM;
	public static final String MIME_MP3 = "audio/mpeg";
	public static final String MIME_WAV = "audio/wav";
	public static final String MIME_AUDIO_OGG = "audio/ogg";
	public static final String MIME_PNG = "image/png";
	public static final String MIME_JPG = "image/jpeg";
	public static final String MIME_HTML = "text/html";

	public static void respond(HttpExchange t, String response, int status, String mime) {
		if (response != null) {
			respond(t, response.getBytes(), status, mime);
		}
	}

	public static void respond(HttpExchange t, byte[] response, int status, String mime) {
		if (response != null) {
			if (mime != null) {
				if (MIME_HTML.equalsIgnoreCase(mime))
					mime += ";charset=UTF-8";
				Headers hdr = t.getResponseHeaders();
				hdr.add("Content-Type", mime);
			}
			try (OutputStream os = t.getResponseBody()) {
				t.sendResponseHeaders(status, response.length);
				os.write(response);
				os.close();
			} catch (Exception e) {
				LOGGER.debug("Error sending response: " + e);
			}
		}
	}

	public static void dumpFile(String file, HttpExchange t) throws IOException {
		File f = new File(file);
		dumpFile(f, t);
	}

	public static void dumpFile(File f, HttpExchange t) throws IOException {
		LOGGER.debug("file " + f + " " + f.length());
		if (!f.exists()) {
			throw new IOException("no file");
		}
		t.sendResponseHeaders(200, f.length());
		dump(new FileInputStream(f), t.getResponseBody());
		LOGGER.debug("dump of " + f.getName() + " done");
	}


	public static void dump(final InputStream in, final OutputStream os) {
		dump(in, os, null);
	}

	public static void dump(final InputStream in, final OutputStream os, final WebRender renderer) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				byte[] buffer = new byte[32 * 1024];
				int bytes;
				int sendBytes = 0;

				try {
					while ((bytes = in.read(buffer)) != -1) {
						sendBytes += bytes;
						os.write(buffer, 0, bytes);
						os.flush();
					}
				} catch (IOException e) {
					LOGGER.trace("Sending stream with premature end: " + sendBytes + " bytes. Reason: " + e.getMessage());
				} finally {
					try {
						in.close();
					} catch (IOException e) {
					}
				}
				try {
					os.close();
				} catch (IOException e) {
				}
				if (renderer != null) {
					renderer.stop();
				}
			}
		};
		new Thread(r).start();
	}

	public static String read(File f) {
		try {
			return FileUtils.readFileToString(f, Charset.forName("UTF-8"));
		} catch (IOException e) {
			LOGGER.debug("Error reading file: " + e);
		}
		return null;
	}

	public static String getId(String path, HttpExchange t) {
		String id = "0";
		int pos = t.getRequestURI().getPath().indexOf(path);
		if (pos != -1) {
			id = t.getRequestURI().getPath().substring(pos + path.length());
		}
		return id;
	}

	public static String strip(String id) {
		int pos = id.lastIndexOf('.');
		if (pos != -1) {
			return id.substring(0, pos);
		}
		return id;
	}

	public static boolean deny(HttpExchange t) {
		InetAddress address = t.getRemoteAddress().getAddress();
//		return  (!address.isLoopbackAddress() && !PMS.getConfiguration().getIpFiltering().allowed(address)) || !PMS.isReady();
		return RequestHandlerV2.filterIp(address);
	}

	private static Range.Byte nullRange(long len) {
		return new Range.Byte(0L, len);
	}

	public static Range.Byte parseRange(Headers hdr, long len) {
		if (hdr == null) {
			return nullRange(len);
		}
		List<String> r = hdr.get("Range");
		if (r == null) { // no range
			return nullRange(len);
		}
		// assume only one
		String range = r.get(0);
		String[] tmp = range.split("=")[1].split("-");
		long start = Long.parseLong(tmp[0]);
		long end = tmp.length == 1 ? len : Long.parseLong(tmp[1]);
		return new Range.Byte(start, end);
	}

	public static void sendLogo(HttpExchange t) throws IOException {
		InputStream in = LooksFrame.class.getResourceAsStream("/resources/images/logo.png");
		t.sendResponseHeaders(200, 0);
		OutputStream os = t.getResponseBody();
		dump(in, os);
	}

	public static boolean directmime(String mime) {
		if (
			mime != null &&
			(
				mime.equals(MIME_MP4) ||
				mime.equals(MIME_WEBM) ||
				mime.equals(MIME_OGG) ||
				mime.equals(MIME_AUDIO_OGG) ||
				mime.equals(MIME_MP3) ||
				mime.equals(MIME_PNG) ||
				mime.equals(MIME_JPG) ||
				Format.getExtension(mime).equals(Format.getExtension("audio/mp4")) ||
				Format.getExtension(mime).equals(Format.getExtension(MIME_AUDIO_OGG))
			)
		) {
			return true;
		}

		return false;
	}

	public static String userName(HttpExchange t) {
		HttpPrincipal p = t.getPrincipal();
		if (p == null) {
			return "";
		}
		return p.getUsername();
	}

	public static String getQueryVars(String query, String var) {
		if (StringUtils.isEmpty(query)) {
			return null;
		}
		for (String p : query.split("&")) {
			String[] pair = p.split("=");
			if (pair[0].equalsIgnoreCase(var)) {
				if (pair.length > 1 && StringUtils.isNotEmpty(pair[1])) {
					return pair[1];
				}
			}
		}
		return null;
	}

	public static WebRender matchRenderer(String user, HttpExchange t) {
		String cookie = RemoteUtil.getCookie("UMS", t);
		WebRender r = (WebRender) PMS.get().getGlobalRepo().getRenderer(cookie);
		return r;
	}
	
	public static WebRender matchRenderer(String cookiestr, String ua, InetAddress address) {
		String user = getCookie("UMS", cookiestr);
		WebRender r = (WebRender) PMS.get().getGlobalRepo().getRenderer(user);
		return r;
	}

	public static String getCookie(String name, HttpExchange t) {
		String cstr = t.getRequestHeaders().getFirst("Cookie");
		String result = getCookie(name, cstr);
		if (result == null)
			LOGGER.debug("Cookie '{}' not found: {}", name, t.getRequestHeaders().get("Cookie"));
		return result;
	}

	public static String getCookie(String name, String cstr) {
		String cookie = null;
		if (!StringUtils.isEmpty(cstr)) {
			name += "=";
			for (String str : cstr.trim().split("\\s*;\\s*")) {
				if (str.startsWith(name)) {
					cookie = str.substring(name.length());
					break;
				}
			}
		}
		return cookie;
	}

	private static final int WIDTH = 0;
	private static final int HEIGHT = 1;

	private static final int DEFAULT_WIDTH = 720;
	private static final int DEFAULT_HEIGHT = 404;

	private static int getHW(int cfgVal, int id, int def) {
		if (cfgVal != 0) {
			// if we have a value cfg return that
			return cfgVal;
		}
		String s = PMS.getConfiguration().getWebSize();
		if (StringUtils.isEmpty(s)) {
			// no size string return default
			return def;
		}
		String[] tmp = s.split("x", 2);
		if (tmp.length < 2) {
			// bad format resort to default
			return def;
		}
		try {
			// pick whatever we got
			return Integer.parseInt(tmp[id]);
		} catch (NumberFormatException e) {
			// bad format (again) resort to default
			return def;
		}
	}

	public static int getHeight() {
		return getHW(PMS.getConfiguration().getWebHeight(), HEIGHT, DEFAULT_HEIGHT);
	}

	public static int getWidth() {
		return getHW(PMS.getConfiguration().getWebWidth(), WIDTH, DEFAULT_WIDTH);
	}

	public static boolean transMp4(String mime, DLNAMediaInfo media) {
		LOGGER.debug("mp4 profile " + media.getH264Profile());
		return mime.equals(MIME_MP4) && (PMS.getConfiguration().isWebMp4Trans() || media.getAvcAsInt() >= 40);
	}

	private static IpFilter bumpFilter = null;

	public static boolean bumpAllowed(HttpExchange t) {
		if (bumpFilter == null) {
			bumpFilter = new IpFilter(PMS.getConfiguration().getBumpAllowedIps());
		}
		return bumpFilter.allowed(t.getRemoteAddress().getAddress());
	}

	public static String transMime() {
		return MIME_TRANS;
	}

	public static String getContentType(String filename) {
		return filename.endsWith(".html") ? "text/html" :
			filename.endsWith(".css") ? "text/css" :
			filename.endsWith(".js") ? "text/javascript" :
			URLConnection.guessContentTypeFromName(filename);
	}

	public static Template compile(InputStream stream) {
		try {
			return Mustache.compiler().escapeHTML(false).compile(new InputStreamReader(stream));
		} catch (Exception e) {
			LOGGER.debug("Error compiling mustache template: " + e);
		}
		return null;
	}

	public static LinkedHashSet<String> getLangs(HttpExchange t) {
		String hdr = t.getRequestHeaders().getFirst("Accept-language");
		LinkedHashSet<String> result = new LinkedHashSet<>();
		if (StringUtils.isEmpty(hdr)) {
			return result;
		}

		String[] tmp = hdr.split(",");
		for (String language : tmp) {
			String[] l1 = language.split(";");
			result.add(l1[0]);
		}
		return result;
	}

	public static String getFirstSupportedLanguage(HttpExchange t) {
		LinkedHashSet<String> languages = getLangs(t);
		for (String language : languages) {
			String code = Languages.toLanguageTag(language);
			if (code != null) {
				return code;
			}
		}
		return "";
	}

	public static String getMsgString(String key, HttpExchange t) {
		if (PMS.getConfiguration().useWebLang()) {
			String lang = getFirstSupportedLanguage(t);
			if (!lang.isEmpty()) {
				return Messages.getString(key, Locale.forLanguageTag(lang));
			}
		}
		return Messages.getString(key);
	}

	/**
	 * A web resource manager to act as:
	 *
	 * - A resource finder with native java classpath search behaviour (including in zip files)
	 *   to allow flexible customizing/skinning of the web interface.
	 *
	 * - A file manager to control access to arbitrary non-web resources, i.e. subtitles,
	 *   logs, etc.
	 *
	 * - A template manager.
	 */
	public static class ResourceManager extends URLClassLoader {
		private HashSet<File> files;
		private HashMap<String, Template> templates;

		public ResourceManager(String... urls) {
			super(new URL[]{});
			try {
				for (String url : urls) {
					addURL(new URL(url));
				}
			} catch (MalformedURLException e) {
				LOGGER.debug("Error adding resource url: " + e);
			}
			files = new HashSet<>();
			templates = new HashMap<>();
		}

		public InputStream getInputStream(String filename) {
			InputStream stream = getResourceAsStream(filename);
			if (stream == null) {
				File file = getFile(filename);
				if (file != null && file.exists()) {
					try {
						stream = new FileInputStream(file);
					} catch (Exception e) {
						LOGGER.debug("Error opening stream: " + e);
					}
				}
			}
			return stream;
		}

		/**
		 * Give preference to over-ridden config
		 */
		@Override
		public URL getResource(String name) {
			URL url = findResource(name);
			if (url == null && getParent() != null) {
				url = getParent().getResource(name);
			}
			return url;
		}

		/**
		 * Register a file as servable.
		 *
		 * @return its hashcode (for use as a 'filename' in an http path)
		 */
		public int add(File f) {
			files.add(f);
			return f.hashCode();
		}

		/**
		 * Retrieve a servable file by its hashcode.
		 */
		public File getFile(String hash) {
			try {
				int h = Integer.valueOf(hash);
				for (File f : files) {
					if (f.hashCode() == h) {
						return f;
					}
				}
			} catch (NumberFormatException e) {
			}
			return null;
		}

		public String read(String filename) {
			try {
				return IOUtils.toString(getInputStream(filename), "UTF-8");
			} catch (IOException e) {
				LOGGER.debug("Error reading resource {}: {}", filename, e);
			}
			return null;
		}

		/**
		 * Write the given resource as an http response body.
		 */
		public boolean write(String filename, HttpExchange t) throws IOException {
			InputStream stream = getInputStream(filename);
			if (stream != null) {
				Headers headers = t.getResponseHeaders();
				if (!headers.containsKey("Content-Type")) {
					String mime = getContentType(filename);
					if (mime != null) {
						headers.add("Content-Type", mime);
					}
				}
				// Note: available() isn't officially guaranteed to return the full
				// stream length but effectively seems to do so in our context.
				t.sendResponseHeaders(200, stream.available());
				dump(stream, t.getResponseBody());
				return true;
			}
			return false;
		}

		/**
		 * Retrieve the given mustache template, compiling as necessary.
		 */
		public Template getTemplate(String filename) {
			Template t = null;
			if (templates.containsKey(filename)) {
				t = templates.get(filename);
			} else {
				URL url = getResource(filename);
				if (url != null) {
					t = compile(getInputStream(filename));
					templates.put(filename, t);
					if (url.getProtocol().equals("file"))
					    PMS.getFileWatcher().add(new FileWatcher.Watch(url.getFile(), recompiler));
				}
			}
			return t;
		}

		/**
		 * Automatic recompiling
		 */
		FileWatcher.Listener recompiler = new FileWatcher.Listener() {
			@Override
			public void notify(String filename, String event, FileWatcher.Watch watch, boolean isDir) {
				String path = watch.fspec.startsWith("web/") ? watch.fspec.substring(4) : watch.fspec;
				if (templates.containsKey(path)) {
					templates.put(path, compile(getInputStream(path)));
					LOGGER.info("Recompiling template: {}", path);
				}
			}
		};
	}
}
