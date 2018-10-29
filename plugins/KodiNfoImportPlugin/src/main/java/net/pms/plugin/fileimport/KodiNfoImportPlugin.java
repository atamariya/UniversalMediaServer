package net.pms.plugin.fileimport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.pms.medialibrary.commons.enumarations.FileProperty;
import net.pms.medialibrary.commons.enumarations.FileType;
import net.pms.medialibrary.commons.exceptions.FileImportException;
import net.pms.plugin.fileimport.KodiNfoConstants.VideoTag;
import net.pms.plugins.FileImportPlugin;
import net.pms.util.PmsProperties;

/**
 * Ums-mlx plugin to import properties and tags from Kodi NFO files
 */
public class KodiNfoImportPlugin implements FileImportPlugin {
	private static final Logger LOGGER = LoggerFactory.getLogger(KodiNfoImportPlugin.class);
	private static final PmsProperties PROPERTIES = new PmsProperties();

	static {
		try {
			PROPERTIES.loadFromResourceFile("/kodinfoimportplugin.properties", KodiNfoImportPlugin.class);
		} catch (IOException e) {
			LOGGER.error("Could not load kodinfoimportplugin.properties", e);
		}
	}

	// These lists will be lazy-initialized on first use
	private static final Map<FileType, List<String>> supportedTagsByFileType = new HashMap<FileType, List<String>>();
	private static List<FileProperty> supportedFileProperties;
	private static List<FileType> supportedFileTypes;

	protected static final ResourceBundle MESSAGES = ResourceBundle.getBundle("kodinfoimportplugin-i18n.messages");

	private KodiNfoVideo kodiNfoVideo;
	private KodiNfoParser kodiNfoParser;

	@Override
	public String getName() {
		return "Kodi NFO";
	}

	@Override
	public String getVersion() {
		return PROPERTIES.get("project.version");
	}

	@Override
	public Icon getPluginIcon() {
		return new ImageIcon(getClass().getResource("/kodi_icon-32.png"));
	}

	@Override
	public String getShortDescription() {
		return MESSAGES.getString("ShortDescription");
	}

	@Override
	public String getLongDescription() {
		return MESSAGES.getString("LongDescription");
	}

	@Override
	public String getUpdateUrl() {
		return null;
	}

	@Override
	public String getWebSiteUrl() {
		return "http://www.universalmediaserver.com/forum/viewtopic.php?f=6&t=3355";
	}

	@Override
	public void initialize() {
		kodiNfoParser = new KodiNfoParser();
	}

	@Override
	public void shutdown() {
		kodiNfoParser = null;
	}

	@Override
	public JComponent getGlobalConfigurationPanel() {
		return null;
	}

	@Override
	public void saveConfiguration() {
	}

	@Override
	public boolean isPluginAvailable() {
		return true;
	}

	@Override
	public void importFile(String title, String filePath) throws FileImportException {
		// replace the file extension by .nfo and read the file
		String nfoFilePath = filePath.substring(0, filePath.lastIndexOf('.')) + KodiNfoConstants.NFO_FILE_EXTENSION;
		kodiNfoVideo = kodiNfoParser.getKodiNfoVideo(nfoFilePath);
	}

	@Override
	public void importFileById(String id) throws FileImportException {
	}

	@Override
	public boolean isImportByIdPossible() {
		return false;
	}

	@Override
	public boolean isSearchForFilePossible() {
		return false;
	}

	@Override
	public List<Object> searchForFile(String name) {
		return null;
	}

	@Override
	public void importFileBySearchObject(Object searchObject) {
	}

	@Override
	public List<FileProperty> getSupportedFileProperties() {
		if (supportedFileProperties == null) {
			// Lazy-initialize supported file properties
			supportedFileProperties = Arrays.asList(FileProperty.VIDEO_NAME, FileProperty.VIDEO_SORTNAME, FileProperty.VIDEO_ORIGINALNAME,
					FileProperty.VIDEO_RATINGPERCENT, FileProperty.VIDEO_YEAR, FileProperty.VIDEO_RATINGVOTERS, FileProperty.VIDEO_OVERVIEW,
					FileProperty.VIDEO_COVERURL, FileProperty.VIDEO_CERTIFICATION, FileProperty.VIDEO_IMDBID, FileProperty.VIDEO_TRAILERURL,
					FileProperty.VIDEO_GENRES, FileProperty.VIDEO_DIRECTOR, FileProperty.VIDEO_TAGLINE);
		}

		return supportedFileProperties;
	}

	@Override
	public Object getFileProperty(FileProperty property) {
		switch (property) {
			case VIDEO_NAME:
				return kodiNfoVideo.getName();
			case VIDEO_SORTNAME:
				return kodiNfoVideo.getSortName();
			case VIDEO_ORIGINALNAME:
				return kodiNfoVideo.getOriginalName();
			case VIDEO_RATINGPERCENT:
				return kodiNfoVideo.getRatingPercent();
			case VIDEO_YEAR:
				return kodiNfoVideo.getYear();
			case VIDEO_RATINGVOTERS:
				return kodiNfoVideo.getRatingVoters();
			case VIDEO_OVERVIEW:
				return kodiNfoVideo.getOverview();
			case VIDEO_COVERURL:
				return kodiNfoVideo.getCoverUrl();
			case VIDEO_CERTIFICATION:
				return kodiNfoVideo.getCertification();
			case VIDEO_IMDBID:
				return kodiNfoVideo.getImdbId();
			case VIDEO_TRAILERURL:
				return kodiNfoVideo.getTrailerUrl();
			case VIDEO_GENRES:
				return kodiNfoVideo.getGenres();
			case VIDEO_DIRECTOR:
				return kodiNfoVideo.getDirector();
			case VIDEO_TAGLINE:
				return kodiNfoVideo.getTagLine();
			default:
				LOGGER.warn(String.format("Unsupported file property (%s) received", property));
				return null;
		}
	}

	@Override
	public List<String> getSupportedTags(FileType fileType) {
		switch (fileType) {
			case VIDEO:
				if (!supportedTagsByFileType.containsKey(fileType)) {
					// Lazy-initialize supported video tags
					List<String> supportedVideoTags = new ArrayList<String>();
					for (VideoTag t : KodiNfoConstants.VideoTag.values()) {
						supportedVideoTags.add(t.toString());
					}
					supportedTagsByFileType.put(fileType, supportedVideoTags);
				}

				return supportedTagsByFileType.get(fileType);

			default:
				// Only videos are currently supported
				return null;
		}
	}

	@Override
	public List<String> getTags(String tagName) {
		if (tagName.equals(VideoTag.Actor.toString())) {
			return kodiNfoVideo.getActors();
		} else if (tagName.equals(VideoTag.EpisodeNumber.toString())) {
			return kodiNfoVideo.getEpisodeNumber() == null ? null : Arrays.asList(kodiNfoVideo.getEpisodeNumber());
		} else if (tagName.equals(VideoTag.SeasonNumber.toString())) {
			return kodiNfoVideo.getSeasonNumber() == null ? null : Arrays.asList(kodiNfoVideo.getSeasonNumber());
		} else if (tagName.equals(VideoTag.FirstAired.toString())) {
			return kodiNfoVideo.getFirstAired() == null ? null : Arrays.asList(kodiNfoVideo.getFirstAired());
		} else if (tagName.equals(VideoTag.Network.toString())) {
			return kodiNfoVideo.getNetwork() == null ? null : Arrays.asList(kodiNfoVideo.getNetwork());
		} else if (tagName.equals(VideoTag.SeriesName.toString())) {
			return kodiNfoVideo.getSeriesName() == null ? null : Arrays.asList(kodiNfoVideo.getSeriesName());
		} else if (tagName.equals(VideoTag.Writer.toString())) {
			return kodiNfoVideo.getWriter() == null ? null : Arrays.asList(kodiNfoVideo.getWriter());
		}

		return null;
	}

	@Override
	public List<FileType> getSupportedFileTypes() {
		if (supportedFileTypes == null) {
			// Lazy-initialize supported file types
			supportedFileTypes = Arrays.asList(FileType.VIDEO);
		}

		return supportedFileTypes;
	}

	@Override
	public int getMinPollingIntervalMs() {
		return 0;
	}

}
