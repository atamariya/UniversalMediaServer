package net.pms.plugin.fileimport;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import net.pms.medialibrary.commons.enumarations.FileProperty;
import net.pms.medialibrary.commons.exceptions.FileImportException;

public class AllocineImportPluginTests {
	private static AllocineImportPlugin allocinePlugin;

	@SuppressWarnings("unchecked")
	@Test
	public void movieImportByNameTest() {
		AllocineImportPlugin allocinePlugin = getAllocinePlugin();
		String movieName = "Le Chevalier Noir";

		// Import the movie
		try {
			allocinePlugin.importFile(movieName, null);
		} catch (FileImportException e) {
			assertTrue(false);
		}

		// Test properties
		Object movieNameObject = allocinePlugin.getFileProperty(FileProperty.VIDEO_NAME);
		assertTrue(movieNameObject.getClass() == String.class);
		assertTrue(((String) movieNameObject).length() > 0);

		Object movieOriginalNameObject = allocinePlugin.getFileProperty(FileProperty.VIDEO_ORIGINALNAME);
		assertTrue(movieOriginalNameObject.getClass() == String.class);
		assertTrue(((String) movieOriginalNameObject).length() > 0);

		// Object movieCeritificationObject = allocinePlugin.getFileProperty(FileProperty.VIDEO_CERTIFICATION);
		// assertTrue(movieCeritificationObject.getClass() == String.class);
		// assertTrue(((String) movieCeritificationObject).length() > 0);

		Object movieDirectorObject = allocinePlugin.getFileProperty(FileProperty.VIDEO_DIRECTOR);
		assertTrue(movieDirectorObject.getClass() == String.class);
		assertTrue(((String) movieDirectorObject).length() > 0);

		Object movieGenresObject = allocinePlugin.getFileProperty(FileProperty.VIDEO_GENRES);
		assertTrue(movieGenresObject instanceof List);
		assertTrue(((List<String>) movieGenresObject).size() > 0);

		Object movieCoverurlObject = allocinePlugin.getFileProperty(FileProperty.VIDEO_COVERURL);
		assertTrue(movieCoverurlObject.getClass() == String.class);
		assertTrue(((String) movieCoverurlObject).startsWith("http"));

		Object movieYearObject = allocinePlugin.getFileProperty(FileProperty.VIDEO_YEAR);
		assertTrue(movieYearObject.getClass() == Integer.class);
		assertTrue((int) movieYearObject == 2008);

		Object movieOverviewObject = allocinePlugin.getFileProperty(FileProperty.VIDEO_OVERVIEW);
		assertTrue(movieOverviewObject.getClass() == String.class);
		assertTrue(((String) movieOverviewObject).length() > 0);

		Object movieTaglineObject = allocinePlugin.getFileProperty(FileProperty.VIDEO_TAGLINE);
		assertTrue(movieTaglineObject.getClass() == String.class);
		assertTrue(((String) movieTaglineObject).length() > 0);

		Object movieTrailerUrlObject = allocinePlugin.getFileProperty(FileProperty.VIDEO_TRAILERURL);
		assertTrue(movieTrailerUrlObject.getClass() == String.class);
		assertTrue(((String) movieTrailerUrlObject).startsWith("http"));

		Object movieRatingPercentObject = allocinePlugin.getFileProperty(FileProperty.VIDEO_RATINGPERCENT);
		assertTrue(movieRatingPercentObject.getClass() == Integer.class);
		assertTrue((int) movieRatingPercentObject >= 0 && (int) movieRatingPercentObject <= 100);

		Object movieRatingVotersObject = allocinePlugin.getFileProperty(FileProperty.VIDEO_RATINGVOTERS);
		assertTrue(movieRatingVotersObject.getClass() == Integer.class);
		assertTrue((int) movieRatingVotersObject >= 0);

		Object movieBudgetObject = allocinePlugin.getFileProperty(FileProperty.VIDEO_BUDGET);
		assertTrue(movieBudgetObject.getClass() == Integer.class);
		assertTrue((int) movieBudgetObject >= 0);

		// Test tags
		List<String> movieActors = allocinePlugin.getTags("Actor");
		assertTrue(movieActors != null);
		assertTrue(movieActors.size() > 0);

		List<String> movieWriters = allocinePlugin.getTags("Writer");
		assertTrue(movieWriters != null);
		assertTrue(movieWriters.size() > 0);

		List<String> movieProducers = allocinePlugin.getTags("Producer");
		assertTrue(movieProducers != null);
		assertTrue(movieProducers.size() > 0);

		List<String> movieAwards = allocinePlugin.getTags("Award");
		assertTrue(movieAwards != null);
		assertTrue(movieAwards.size() > 0);
	}

	private AllocineImportPlugin getAllocinePlugin() {
		if (allocinePlugin == null) {
			allocinePlugin = new AllocineImportPlugin();
			allocinePlugin.initialize();
		}

		return allocinePlugin;
	}
}
