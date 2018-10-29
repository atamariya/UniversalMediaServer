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

import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.omertron.thetvdbapi.TheTVDBApi;
import com.omertron.thetvdbapi.TvDbException;
import com.omertron.thetvdbapi.model.Banner;
import com.omertron.thetvdbapi.model.BannerType;
import com.omertron.thetvdbapi.model.Banners;
import com.omertron.thetvdbapi.model.Episode;
import com.omertron.thetvdbapi.model.Language;
import com.omertron.thetvdbapi.model.Series;

import net.pms.medialibrary.commons.enumarations.FileProperty;
import net.pms.medialibrary.commons.enumarations.FileType;
import net.pms.medialibrary.commons.exceptions.FileImportException;
import net.pms.plugin.fileimport.thetvdb.configuration.GlobalConfiguration;
import net.pms.plugin.fileimport.thetvdb.fileparser.EpisodeFile;
import net.pms.plugin.fileimport.thetvdb.fileparser.EpisodeFileParser;
import net.pms.plugin.fileimport.thetvdb.fileparser.EpisodeFileParserException;
import net.pms.plugin.fileimport.thetvdb.gui.GlobalConfigurationPanel;
import net.pms.plugins.FileImportPlugin;
import net.pms.util.PmsProperties;

/**
 *
 * @author Corey
 */
@SuppressWarnings("deprecation")
public class TheTVDBImportPlugin implements FileImportPlugin {

	public static final ResourceBundle MESSAGES = ResourceBundle.getBundle("thetvdbepisodeimportplugin-i18n.messages");

	private static final Logger LOGGER = LoggerFactory.getLogger(TheTVDBImportPlugin.class);
	private static final ImageIcon PLUGIN_ICON = new ImageIcon(TheTVDBImportPlugin.class.getResource("/thetvdb-32.png"));

	// Holds only the project version. It's used to always use the maven buildnumber in code
	private static final PmsProperties PROPERTIES = new PmsProperties();

	static {
		try {
			PROPERTIES.loadFromResourceFile("/thetvdbimportplugin.properties", TheTVDBImportPlugin.class);
		} catch (IOException e) {
			LOGGER.error("Could not load thetvdbimportplugin.properties", e);
		}
	}

	// The global configuration is shared amongst all plugin instances.
	private static final GlobalConfiguration GLOBAL_CONFIGURATION;

	static {
		GLOBAL_CONFIGURATION = new GlobalConfiguration();
		try {
			GLOBAL_CONFIGURATION.load();
		} catch (IOException e) {
			LOGGER.error("Failed to load global configuration", e);
		}
	}

	private TheTVDBApi theTvDbApi;

	/**
	 * The found episode object
	 */
	private Episode currentEpisode;
	private Series currentSeries;
	private String cover;

	private GlobalConfigurationPanel pGlobalConfiguration;

	/**
	 * Available tags.
	 */
	private enum Tag {
		EpisodeNumber, SeasonNumber, FirstAired, GuestStars, Writers, Runtime, Network, SeriesName
	}

	@Override
	public void importFile(String title, String filePath) throws FileImportException {
		currentSeries = null;
		currentEpisode = null;
		cover = null;

		LOGGER.debug("Importing TheTVDB episode with file '%s'", filePath);

		Banners banners = null;
		EpisodeFileParser fileParser = new EpisodeFileParser(filePath);
		EpisodeFile fileObg = new EpisodeFile();
		try {
			fileObg = fileParser.parse();
		} catch (EpisodeFileParserException ex) {
			throw new FileImportException(String.format("Unable to parse TV Episode information from file '%s'", filePath));
		}

		LOGGER.debug("Search TVDB for series='%s', season='%s', episode='%s'", fileObg.getSeries(), fileObg.getSeason(), fileObg.getEpisode());
		List<Series> series;
		try {
			series = theTvDbApi.searchSeries(fileObg.getSeries(), GLOBAL_CONFIGURATION.getImportLanguage());
		} catch (TvDbException e) {
			throw new FileImportException(String.format("Failed to search for series='%s' for language='%s'", fileObg.getSeries(), GLOBAL_CONFIGURATION.getImportLanguage()), e);
		}

		if (series != null && series.size() > 0) {
			// we've found at least one result

			// use the first one
			String seriesId = series.get(0).getId();
			try {
				currentSeries = theTvDbApi.getSeries(seriesId, GLOBAL_CONFIGURATION.getImportLanguage());
			} catch (TvDbException e) {
				throw new FileImportException(String.format("Failed to get series with id='%s', language='%s'", seriesId, GLOBAL_CONFIGURATION.getImportLanguage()), e);
			}

			// log the results received
			LOGGER.info("Series matched for '%s' on TvDB has imdbDb='%s', name='%s', id='%s'.", fileObg.getSeries(), currentSeries.getImdbId(), currentSeries.getSeriesName(), seriesId);
			try {
				currentEpisode = theTvDbApi.getEpisode(seriesId, fileObg.getSeason(), fileObg.getEpisode(), GLOBAL_CONFIGURATION.getImportLanguage());
			} catch (TvDbException e) {
				LOGGER.warn(String.format("Failed to get episode for series with id='%s', season='%s', episode='%s', language='%s'", seriesId, fileObg.getSeason(), fileObg.getEpisode(), GLOBAL_CONFIGURATION.getImportLanguage()), e);
			}

			if (currentEpisode != null) {
				// log the results received
				LOGGER.info("Episode matched for series='%s' episode='%s'", currentSeries.getSeriesName(), currentEpisode.getEpisodeName());
			}

			// Find the most suitable cover
			try {
				banners = theTvDbApi.getBanners(seriesId);
			} catch (TvDbException e) {
				LOGGER.warn(String.format("Failed to get banners for series with id='%s'", seriesId), e);
			}

			if (banners != null) {
				if (!banners.getSeasonList().isEmpty()) {
					for (Banner banner : banners.getSeasonList()) {
						if ((banner.getSeason() == fileObg.getSeason()) && (banner.getBannerType2() == BannerType.SEASON)) {
							cover = banner.getUrl();
							break;
						}
					}
				} else {
					cover = banners.getPosterList().get(0).getUrl();
				}
			}

			if (cover == null) {
				cover = currentSeries.getPoster();
			}
			LOGGER.debug("Using cover '%s'", cover);

			if (series.size() > 1) {
				String seriesStr = "Other (not considered) matches are: ";
				for (int i = 1; i < series.size(); i++) {
					seriesStr += String.format("id='%s', name='%s'; ", series.get(i).getId(), series.get(i).getSeriesName());
				}
				// Remove the last separator
				seriesStr = seriesStr.substring(0, seriesStr.length() - 2);
				LOGGER.info(seriesStr);
			}
		}
	}

	@Override
	public void importFileById(String id) throws FileImportException {
		currentSeries = null;
		currentEpisode = null;

		try {
			currentEpisode = theTvDbApi.getEpisodeById(id, GLOBAL_CONFIGURATION.getImportLanguage());
		} catch (TvDbException e) {
			throw new FileImportException(String.format("Failed to get episode by ID with id='%s', language='%s'", id, GLOBAL_CONFIGURATION.getImportLanguage()), e);
		}

		try {
			currentSeries = theTvDbApi.getSeries(currentEpisode.getSeriesId(), GLOBAL_CONFIGURATION.getImportLanguage());
		} catch (TvDbException e) {
			throw new FileImportException(String.format("Failed to get series with id='%s', language='%s'", currentEpisode.getSeriesId(), GLOBAL_CONFIGURATION.getImportLanguage()), e);
		}
	}

	@Override
	public boolean isImportByIdPossible() {
		return true;
	}

	@Override
	public boolean isSearchForFilePossible() {
		return false;
	}

	@Override
	public List<Object> searchForFile(String name) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void importFileBySearchObject(Object searchObject) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public List<FileProperty> getSupportedFileProperties() {
		// add all supported properties
		List<FileProperty> res = new ArrayList<FileProperty>();
		res.add(FileProperty.VIDEO_COVERURL);
		res.add(FileProperty.VIDEO_DIRECTOR);
		res.add(FileProperty.VIDEO_GENRES);
		res.add(FileProperty.VIDEO_IMDBID);
		res.add(FileProperty.VIDEO_OVERVIEW);
		res.add(FileProperty.VIDEO_RATINGPERCENT);
		res.add(FileProperty.VIDEO_NAME);
		res.add(FileProperty.VIDEO_SORTNAME);
		res.add(FileProperty.VIDEO_YEAR);
		return res;
	}

	@Override
	public Object getFileProperty(FileProperty property) {
		Object res = null;
		// return the proper object for every supported file property
		switch (property) {
			case VIDEO_COVERURL:
				res = cover;
				break;

			case VIDEO_DIRECTOR:
				res = currentEpisode == null && currentEpisode.getDirectors().size() > 0 ? null : currentEpisode.getDirectors().get(0);
				break;

			case VIDEO_GENRES:
				res = currentSeries == null ? null : currentSeries.getGenres();
				break;

			case VIDEO_IMDBID:
				res = currentEpisode == null ? null : currentEpisode.getImdbId();
				break;

			case VIDEO_OVERVIEW:
				res = currentEpisode == null ? null : currentEpisode.getOverview();
				break;

			case VIDEO_RATINGPERCENT:
				if (currentEpisode != null && currentEpisode.getRating() != null && !currentEpisode.getRating().equals("")) {
					try {
						double rating = Double.parseDouble(currentEpisode.getRating());
						res = (int) (10 * rating);
					} catch (Exception ex) {
						LOGGER.error("Failed to parse rating as a double. value='%s'", currentEpisode.getRating());
					}
				}
				break;

			case VIDEO_NAME:
				res = currentEpisode == null ? null : currentEpisode.getEpisodeName();
				break;

			case VIDEO_SORTNAME:
				res = currentEpisode == null ? null : String.format("%d%03d", currentEpisode.getSeasonNumber(), currentEpisode.getEpisodeNumber());
				break;

			case VIDEO_YEAR:
				if (currentEpisode != null && currentEpisode.getFirstAired() != null && !currentEpisode.getFirstAired().equals("")) {
					try {
						res = Integer.parseInt(currentEpisode.getFirstAired().substring(0, 4));
					} catch (Exception ex) {
						LOGGER.error("Failed to parse the year in first air date. value='%s'", currentEpisode.getFirstAired());
					}
				}
				break;

			default:
				LOGGER.warn("Unexpected FileProperty received: '%s'", property);
				break;
		}

		return res;
	}

	@Override
	public List<String> getSupportedTags(FileType fileType) {
		List<String> res = new ArrayList<String>();
		for (Tag t : Tag.values()) {
			res.add(t.toString());
		}

		return res;
	}

	@Override
	public List<String> getTags(String tagName) {
		List<String> res = new ArrayList<String>();
		if (tagName.equals(Tag.EpisodeNumber.toString())) {
			res.add(String.valueOf(currentEpisode.getEpisodeNumber()));
		} else if (tagName.equals(Tag.SeasonNumber.toString())) {
			res.add(String.valueOf(currentEpisode.getSeasonNumber()));
		} else if (tagName.equals(Tag.FirstAired.toString())) {
			res.add(String.valueOf(currentEpisode.getFirstAired()));
		} else if (tagName.equals(Tag.GuestStars.toString())) {
			res.addAll(currentEpisode.getGuestStars());
		} else if (tagName.equals(Tag.Writers.toString())) {
			res.addAll(currentEpisode.getWriters());
		} else if (tagName.equals(Tag.Runtime.toString())) {
			res.add(String.valueOf(currentSeries.getRuntime()));
		} else if (tagName.equals(Tag.Network.toString())) {
			res.add(String.valueOf(currentSeries.getNetwork()));
		} else if (tagName.equals(Tag.SeriesName.toString())) {
			res.add(String.valueOf(currentSeries.getSeriesName()));
		}

		return res;
	}

	@Override
	public List<FileType> getSupportedFileTypes() {
		return Arrays.asList(FileType.VIDEO);
	}

	@Override
	public int getMinPollingIntervalMs() {
		return 1000;
	}

	@Override
	public String getName() {
		return "TheTVDB";
	}

	@Override
	public String getVersion() {
		return PROPERTIES.get("project.version");
	}

	@Override
	public Icon getPluginIcon() {
		return PLUGIN_ICON;
	}

	@Override
	public String getShortDescription() {
		return MESSAGES.getString("TheTVDBEpisodeImportPlugin.ShortDescription");
	}

	@Override
	public String getLongDescription() {
		return MESSAGES.getString("TheTVDBEpisodeImportPlugin.LongDescription");
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
		theTvDbApi = new TheTVDBApi("D19EF2AFF971007D", new DefaultHttpClient());
		
		// Set the supported languages in the global configuration
		try {
			Map<String, String> supportedLanguages = new HashMap<String, String>();
			for (Language language : theTvDbApi.getLanguages()) {
				supportedLanguages.put(language.getAbbreviation(), language.getName());
			}
			GLOBAL_CONFIGURATION.setSupportedLanguages(supportedLanguages);
		} catch (TvDbException e) {
			LOGGER.error("Failed to get the list of supported languages", e);
		}
	}

	@Override
	public void shutdown() {
	}

	@Override
	public JComponent getGlobalConfigurationPanel() {
		if (pGlobalConfiguration == null) {
			pGlobalConfiguration = new GlobalConfigurationPanel(GLOBAL_CONFIGURATION);
		}
		pGlobalConfiguration.applyConfig();

		return pGlobalConfiguration;
	}

	@Override
	public void saveConfiguration() {
		if (pGlobalConfiguration != null) {
			pGlobalConfiguration.updateConfiguration(GLOBAL_CONFIGURATION);
			try {
				GLOBAL_CONFIGURATION.save();
			} catch (IOException e) {
				LOGGER.error("Failed to save global configuration", e);
			}
		}
	}

	@Override
	public boolean isPluginAvailable() {
		return true;
	}
}
