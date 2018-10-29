package net.pms.plugin.fileimport;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import net.pms.medialibrary.commons.enumarations.FileProperty;
import net.pms.medialibrary.commons.enumarations.FileType;
import net.pms.medialibrary.commons.exceptions.FileImportException;

public class KodiNfoImportPluginTests {
	private static final String RESOURCES_FOLDER_PATH = "src/test/resources/";
	private KodiNfoImportPlugin kodiNfoImportPlugin;

	/**
	 * Tests the NFO parser<br>
	 * - Parse the NFO file according to a specified NFO file path<br>
	 * - Assert that the parsed Kodi NFO video is not null
	 */
	@Test
	public void kodiNfoParserTest() {
		KodiNfoParser kodiNfoParser = new KodiNfoParser();
		try {
			KodiNfoVideo kodiNfoVideo = kodiNfoParser.getKodiNfoVideo(getResourceFilePath("testMovie1.nfo"));
			assertNotNull(kodiNfoVideo);
		} catch (FileImportException e) {
			assertTrue(false);
		}
	}

	/**
	 * Tests the import of a movie NFO<br>
	 * - Load the NFO by replacing the extension of a movie by .nfo<br>
	 * - Assert that all values read from the NFO correspond with their expected values
	 */
	@Test
	public void importMovieTest() {
		String plot = "Die beiden jungen Polizisten Schmidt und Jenko werden aufgrund ihres jugendlichen Aussehens der geheimen Jump-Street-Einheit zugeteilt und treten in der örtlichen Highschool ihren Dienst undercover an. Schmidt und Jenko tauschen ihre Waffen und Dienstmarken gegen Rucksäcke ein und riskieren ihr Leben, um gegen einen gewalttätigen und gefährlichen Drogenring zu ermitteln. Dabei müssen sie bald feststellen, dass die Highschool von heute nicht mehr das ist, was sie mal war, als sie selbst vor einigen Jahren dort noch zur Schule gegangen sind. Ausserdem hatten sie nicht damit gerechnet, sich noch einmal mit den Schrecken und Ängsten, ein Teenager zu sein, auseinandersetzen zu müssen oder mit all den anderen Problemen, von denen sie glaubten, sie lägen längst hinter ihnen.";

		try {
			// Do the import
			KodiNfoImportPlugin importPlugin = getKodiNfoImportPlugin();
			importPlugin.importFile("", getResourceFilePath("testMovie1.avi"));

			// Test file properties
			// Simple tests (string comparisons)
			assertTrue(importPlugin.getFileProperty(FileProperty.VIDEO_NAME).equals("21 Jump Street"));
			assertTrue(importPlugin.getFileProperty(FileProperty.VIDEO_SORTNAME).equals("21 Jump Street (sort)"));
			assertTrue(importPlugin.getFileProperty(FileProperty.VIDEO_ORIGINALNAME).equals("21 Jump Street (original)"));
			assertTrue(importPlugin.getFileProperty(FileProperty.VIDEO_CERTIFICATION).equals("Rated 12"));
			assertTrue(importPlugin.getFileProperty(FileProperty.VIDEO_IMDBID).equals("tt1232829"));
			assertTrue(importPlugin.getFileProperty(FileProperty.VIDEO_TRAILERURL).equals("http://www.imdb.com/video/imdb/vi1681366553"));
			assertTrue(importPlugin.getFileProperty(FileProperty.VIDEO_OVERVIEW).equals(plot));
			assertTrue(importPlugin.getFileProperty(FileProperty.VIDEO_TAGLINE).equals("Sie dachten, auf den Strassen geht es mies zu. Bis sie zurück an die Highschool kamen."));
			Object coverUrlObject = importPlugin.getFileProperty(FileProperty.VIDEO_COVERURL);
			assertTrue(coverUrlObject.getClass() == String.class);
			assertTrue(((String) coverUrlObject).endsWith("testMovie1-poster.jpg"));

			// Test rating (percent and voters)
			Object ratingPercent = importPlugin.getFileProperty(FileProperty.VIDEO_RATINGPERCENT);
			assertTrue(ratingPercent.getClass() == Integer.class);
			assertTrue((int) ratingPercent == 72);
			Object ratingVoters = importPlugin.getFileProperty(FileProperty.VIDEO_RATINGVOTERS);
			assertTrue(ratingVoters.getClass() == Integer.class);
			assertTrue((int) ratingVoters == 378531);

			// Test year
			Object year = importPlugin.getFileProperty(FileProperty.VIDEO_YEAR);
			assertTrue(year.getClass() == Integer.class);
			assertTrue((int) year == 2012);

			// Test genres
			Object genresObject = importPlugin.getFileProperty(FileProperty.VIDEO_GENRES);
			assertTrue(genresObject instanceof List);
			@SuppressWarnings("unchecked")
			List<String> genres = (List<String>) genresObject;
			assertTrue(genres.size() == 3);
			assertTrue(genres.contains("Action"));
			assertTrue(genres.contains("Komödie"));
			assertTrue(genres.contains("Krimi"));
			assertTrue(!genres.contains("Krimi "));

			// Test director
			Object directorObject = importPlugin.getFileProperty(FileProperty.VIDEO_DIRECTOR);
			assertTrue(directorObject.getClass() == String.class);
			String director = (String) directorObject;
			assertTrue(director.contains("Phil Lord"));
			assertTrue(director.contains("Chris Miller"));
			assertTrue(!director.contains("Chris Miller "));
			assertTrue(!director.trim().endsWith(","));

			// Test Tags
			String actorTagName = KodiNfoConstants.VideoTag.Actor.toString();
			List<String> supportedTags = importPlugin.getSupportedTags(FileType.VIDEO);
			assertTrue(supportedTags.contains(actorTagName));
			List<String> actors = importPlugin.getTags(actorTagName);
			assertTrue(actors.size() == 16);
			assertTrue(actors.contains("Johnny Simmons"));
			assertTrue(actors.contains("Caroline Aaron"));
			assertTrue(actors.contains("Johnny Depp"));
			assertTrue(!actors.contains("Johnny Depp "));
		} catch (FileImportException e) {
			assertTrue(false);
		}
	}

	@Test
	public void importMovieTestCoverThumb() {
		try {
			// Do the import
			KodiNfoImportPlugin importPlugin = getKodiNfoImportPlugin();
			importPlugin.importFile("", getResourceFilePath("testMovie2.avi"));

			// Test file properties
			// Simple tests (string comparisons)
			Object coverUrlObject = importPlugin.getFileProperty(FileProperty.VIDEO_COVERURL);
			assertTrue(coverUrlObject.getClass() == String.class);
			assertTrue(((String) coverUrlObject).endsWith("testMovie2-thumb.jpg"));
		} catch (FileImportException e) {
			assertTrue(false);
		}
	}

	@Test
	public void importMovieTestCoverUrl() {
		try {
			// Do the import
			KodiNfoImportPlugin importPlugin = getKodiNfoImportPlugin();
			importPlugin.importFile("", getResourceFilePath("testMovie3.avi"));

			// Test file properties
			// Simple tests (string comparisons)
			Object coverUrlObject = importPlugin.getFileProperty(FileProperty.VIDEO_COVERURL);
			assertTrue(coverUrlObject.getClass() == String.class);
			assertTrue(((String) coverUrlObject).startsWith("http"));
		} catch (FileImportException e) {
			assertTrue(false);
		}
	}

	/**
	 * Tests the import for a movie having no NFO file<br>
	 * - The import file operation must throw a FileImportException
	 */
	@Test
	public void importMovieNotExistTest() {
		try {
			KodiNfoImportPlugin importPlugin = getKodiNfoImportPlugin();
			importPlugin.importFile("", getResourceFilePath("testMovieNotExist.avi"));

			assertTrue(false);
		} catch (FileImportException e) {
			assertTrue(true);
		}

	}

	/**
	 * Tests the import of a episode NFO<br>
	 * - Load the NFO by replacing the extension of an episode by .nfo<br>
	 * - Assert that all values read from the NFO correspond with their expected values
	 */
	@Test
	public void importEpisodeTest() {
		String plot = "Der Highschool Chemieleher Walter erfährt bei einem Arztbesuch, dass er an Lungenkrebs  erkrankt ist und nicht mehr lange zu leben hat. Um die finanzielle Sicherheit seiner Familie weiter zu garantieren, beginnt er mit seinem ehemaligen Schüler Jesse Meth herzustellen und damit zu handeln.";

		try {
			// Do the import
			KodiNfoImportPlugin importPlugin = getKodiNfoImportPlugin();
			importPlugin.importFile("", getResourceFilePath("testSeries.mkv"));

			// Simple tests (string comparisons)
			assertTrue(importPlugin.getFileProperty(FileProperty.VIDEO_NAME).equals("Der Einstieg"));
			assertTrue(importPlugin.getFileProperty(FileProperty.VIDEO_OVERVIEW).equals(plot));
			assertTrue(importPlugin.getFileProperty(FileProperty.VIDEO_COVERURL).equals("http://thetvdb.com/banners/episodes/81189/349232.jpg"));
			assertTrue(importPlugin.getFileProperty(FileProperty.VIDEO_DIRECTOR).equals("Vince Gilligan (director)"));
			assertTrue(importPlugin.getFileProperty(FileProperty.VIDEO_CERTIFICATION).equals("TV-MA"));

			// Test rating (percent and voters)
			Object ratingPercent = importPlugin.getFileProperty(FileProperty.VIDEO_RATINGPERCENT);
			assertTrue(ratingPercent.getClass() == Integer.class);
			assertTrue((int) ratingPercent == 82);
			Object ratingVoters = importPlugin.getFileProperty(FileProperty.VIDEO_RATINGVOTERS);
			assertTrue(ratingVoters.getClass() == Integer.class);
			assertTrue((int) ratingVoters == 12214);

			// Test year
			Object year = importPlugin.getFileProperty(FileProperty.VIDEO_YEAR);
			assertTrue(year.getClass() == Integer.class);
			assertTrue((int) year == 2008);

			// Test Tags
			String actorTagName = KodiNfoConstants.VideoTag.Actor.toString();
			String episodeNumberTagName = KodiNfoConstants.VideoTag.EpisodeNumber.toString();
			String seasonNumberTagName = KodiNfoConstants.VideoTag.SeasonNumber.toString();
			String firstAiredTagName = KodiNfoConstants.VideoTag.FirstAired.toString();
			String networkTagName = KodiNfoConstants.VideoTag.Network.toString();
			String seriesNameTagName = KodiNfoConstants.VideoTag.SeriesName.toString();
			String writerTagName = KodiNfoConstants.VideoTag.Writer.toString();

			List<String> supportedTags = importPlugin.getSupportedTags(FileType.VIDEO);
			assertTrue(supportedTags.contains(actorTagName));
			assertTrue(supportedTags.contains(episodeNumberTagName));
			assertTrue(supportedTags.contains(firstAiredTagName));
			assertTrue(supportedTags.contains(networkTagName));
			assertTrue(supportedTags.contains(seasonNumberTagName));
			assertTrue(supportedTags.contains(seriesNameTagName));
			assertTrue(supportedTags.contains(writerTagName));

			// Test actors
			List<String> actors = importPlugin.getTags(actorTagName);
			assertTrue(actors.size() == 21);
			assertTrue(actors.contains("Bryan Cranston"));
			assertTrue(actors.contains("Aaron Paul"));
			assertTrue(!actors.contains("Aaron Paul "));

			// Test episode number
			List<String> episodeNumberTagValues = importPlugin.getTags(episodeNumberTagName);
			assertTrue(episodeNumberTagValues.size() == 1);
			assertTrue(episodeNumberTagValues.contains("01"));

			// Test season number
			List<String> seasonNumberTagValues = importPlugin.getTags(seasonNumberTagName);
			assertTrue(seasonNumberTagValues.size() == 1);
			assertTrue(seasonNumberTagValues.contains("01"));

			// Test series name
			List<String> seriesNameTagValues = importPlugin.getTags(seriesNameTagName);
			assertTrue(seriesNameTagValues.size() == 1);
			assertTrue(seriesNameTagValues.contains("Breaking Bad"));

			// Test episode number
			List<String> firstAiredTagValues = importPlugin.getTags(firstAiredTagName);
			assertTrue(firstAiredTagValues.size() == 1);
			assertTrue(firstAiredTagValues.contains("2008-01-20"));

			// Test network
			List<String> networkTagValues = importPlugin.getTags(networkTagName);
			assertTrue(networkTagValues.size() == 1);
			assertTrue(networkTagValues.contains("AMC"));

			// Test writer
			List<String> writersTagValues = importPlugin.getTags(writerTagName);
			assertTrue(writersTagValues.size() == 1);
			assertTrue(writersTagValues.contains("Vince Gilligan (writer)"));
		} catch (FileImportException e) {
			assertTrue(false);
		}
	}

	/**
	 * Gets the absolute file path of the test resource.
	 *
	 * @param resourceName the resource name
	 * @return the resource file path
	 */
	private String getResourceFilePath(String resourceName) {
		return Paths.get(RESOURCES_FOLDER_PATH, resourceName).toUri().getPath();
	}

	/**
	 * Gets the initialized Kodi NFO import plugin.
	 *
	 * @return the Kodi NFO import plugin
	 */
	private KodiNfoImportPlugin getKodiNfoImportPlugin() {
		if (kodiNfoImportPlugin == null) {
			kodiNfoImportPlugin = new KodiNfoImportPlugin();
			kodiNfoImportPlugin.initialize();
		}

		return kodiNfoImportPlugin;
	}
}