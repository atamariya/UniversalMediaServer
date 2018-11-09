package net.pms.dlna;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.pms.PMS;
import net.pms.configuration.RendererConfiguration;
import net.pms.dlna.virtual.VirtualFolder;
import net.pms.formats.v2.SubtitleType;
import net.pms.util.OpenSubtitle;
import net.pms.util.UMSUtils;

public class SubSelFile extends VirtualFolder {
	private static final Logger LOGGER = LoggerFactory.getLogger(SubSelFile.class);
	private DLNAResource orig;

	public SubSelFile(DLNAResource r) {
		super(r.getDisplayName(), r.getThumbnailURL());
		orig = r;
	}

	@Override
	public InputStream getThumbnailInputStream() {
		try {
			return orig.getThumbnailInputStream();
		} catch (Exception e) {
			return super.getThumbnailInputStream();
		}
	}
	
	@Override
	public String getSystemName() {
	    return "subs:" + super.getSystemName();
	}

	@Override
	public boolean isRefreshNeeded() {
	    return !isDiscovered();
	}
	
	@Override
	public void doRefreshChildren() {
		Map<String, String> data;
		RealFile rf = null;
		getChildren().clear();
		try {
			if (orig instanceof RealFile) {
				rf = (RealFile) orig;
				data = OpenSubtitle.findSubs(rf.getFile(), getDefaultRenderer());
			} else {
				data = OpenSubtitle.querySubs(orig.getDisplayName(), getDefaultRenderer());
			}
		} catch (IOException e) {
			return;
		}
		if (data == null || data.isEmpty()) {
			return;
		}
		List<String> sortedKeys = new ArrayList<>(data.keySet());
		Collections.sort(sortedKeys, new SubSort(getDefaultRenderer()));
		int i = orig.getMedia().getSubtitleTracksList().size();
		for (String lang : sortedKeys) {
		    i++;
			LOGGER.debug("Add play subtitle child " + lang + " rf " + orig);
			DLNAMediaSubtitle sub = orig.getMediaSubtitle();
			if (sub == null) {
				sub = new DLNAMediaSubtitle();
			}
			String name = rf.getMedia().getImdbId();
			if (StringUtils.isBlank(name))
			    name = FilenameUtils.getBaseName(rf.getName());
			sub.setType(SubtitleType.SUBRIP);
			sub.setId(i);
			sub.setLang(lang);
			sub.setLiveSub(data.get(lang), OpenSubtitle.subFile(name + "." + lang));
			orig.getMedia().getSubtitleTracksList().add(sub);
			
			DLNAResource nrf = orig.clone();
			nrf.setMediaSubtitle(sub);
			nrf.setHasExternalSubtitles(true);
			addChild(nrf);
			if (rf != null) {
				((RealFile) nrf).ignoreThumbHandling();
			}
		}
		
        setDiscovered(true);
        PMS.get().getDatabase().insertSubtitles(orig.getMedia(), Integer.valueOf(getId()));
	}

	private static class SubSort implements Comparator<String> {
		private List<String> langs;

		SubSort(RendererConfiguration r) {
			langs = Arrays.asList(UMSUtils.getLangList(r, true).split(","));
		}

		@Override
		public int compare(String key1, String key2) {
			if (langs == null) {
				return 0;
			}
			Integer index1 = langs.indexOf(OpenSubtitle.getLang(key1));
			Integer index2 = langs.indexOf(OpenSubtitle.getLang(key2));

			if (index1 == -1) {
				index1 = 999;
			}

			if (index2 == -1) {
				index2 = 999;
			}

			return index1.compareTo(index2);
		}
	}
}
