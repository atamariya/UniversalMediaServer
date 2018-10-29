package net.pms.plugin.fileimport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.pms.medialibrary.commons.exceptions.FileImportException;
import net.pms.medialibrary.commons.helpers.FileHelper;

/**
 * Class used to parse Kodi NFO files
 */
public class KodiNfoParser {
	private static final Logger LOGGER = LoggerFactory.getLogger(KodiNfoImportPlugin.class);

	private static final String TAG_MOVIE = "movie";
	private static final String TAG_EPISODEDETAILS = "episodedetails";

	/**
	 * The main method used to test the parser.
	 *
	 * @param args the arguments. The first argument must be the file path of a video having a Kodi NFO file.
	 */
	public static void main(String[] args) {
		KodiNfoParser parser = new KodiNfoParser();
		try {
			if (args.length != 1) {
				LOGGER.error("The path of the video file has to be specified");
				return;
			}

			KodiNfoVideo kodiNfoVideo = parser.getKodiNfoVideo(args[0]);
			System.out.println(String.format("Result: %s", kodiNfoVideo));
		} catch (FileImportException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the Kodi NFO video according to the specified file path.
	 *
	 * @param nfoFilePath the NFO file path
	 * @return the Kodi NFO video
	 * @throws FileImportException thrown when the NFO could not be read or was badly formatted
	 */
	public KodiNfoVideo getKodiNfoVideo(String nfoFilePath) throws FileImportException {
		try {
			File nfoFile = new File(nfoFilePath);
			if (!nfoFile.exists()) {
				throw new FileImportException(String.format("The NFO file '%s' does not exist", nfoFilePath));
			}

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(nfoFile);
			document.getDocumentElement().normalize();

			// Determine how this NFO has to be parsed and do the job
			String rootNodeName = document.getDocumentElement().getNodeName();
			switch (rootNodeName) {
				case TAG_MOVIE:
					return parseMovieNfo(document, nfoFilePath);

				case TAG_EPISODEDETAILS:
					return parseEpisodeNfo(document, nfoFilePath);

				default:
					throw new FileImportException(String.format("Failed to read NFO file '%s'. Unexpected root node '%s'", nfoFilePath, rootNodeName));
			}

		} catch (SAXException | IOException | ParserConfigurationException e) {
			throw new FileImportException(String.format("Failed to read NFO '%s'", nfoFilePath), e);
		}
	}

	/**
	 * Parses the Kodi NFO for an episode.
	 *
	 * @param document the XML document
	 * @param nfoFilePath the path of the NFO file
	 * @return the Kodi NFO video
	 * @throws FileImportException the file import exception
	 */
	private KodiNfoVideo parseEpisodeNfo(Document document, String nfoFilePath) throws FileImportException {
		KodiNfoVideo result = new KodiNfoVideo();

		Element movieElement = document.getDocumentElement();

		// Set text values
		result.setName(getSingleElementText(movieElement, "title"));
		result.setSeriesName(getSingleElementText(movieElement, "showtitle"));
		result.setOverview(getSingleElementText(movieElement, "plot"));
		result.setWriter(getSingleElementText(movieElement, "writer"));
		result.setDirector(getSingleElementText(movieElement, "director"));
		result.setFirstAired(getSingleElementText(movieElement, "premiered"));
		result.setNetwork(getSingleElementText(movieElement, "studio"));
		result.setCertification(getSingleElementText(movieElement, "mpaa"));
		result.setWriter(getSingleElementText(movieElement, "credits"));

		// Values which need some processing
		result.setCoverUrl(getThumbnail(movieElement, nfoFilePath));
		result.setSeasonNumber(leftPad(getSingleElementText(movieElement, "season"), 2, "0"));
		result.setEpisodeNumber(leftPad(getSingleElementText(movieElement, "episode"), 2, "0"));
		result.setRatingPercent((int) (getSingleElementDouble(movieElement, "rating") * 10));
		result.setRatingVoters((int) getSingleElementDouble(movieElement, "votes"));
		result.setYear((int) getSingleElementDouble(movieElement, "year"));
		result.setActors(getActors(movieElement));

		return result;
	}

	/**
	 * Parses the Kodi NFO for a movie.
	 *
	 * @param document the XML document
	 * @param nfoFilePath the path of the NFO file
	 * @return the Kodi NFO video
	 * @throws FileImportException the file import exception
	 */
	private KodiNfoVideo parseMovieNfo(Document document, String nfoFilePath) throws FileImportException {
		KodiNfoVideo result = new KodiNfoVideo();

		Element movieElement = document.getDocumentElement();

		// Set text values
		result.setName(getSingleElementText(movieElement, "title"));
		result.setOriginalName(getSingleElementText(movieElement, "originaltitle"));
		result.setSortName(getSingleElementText(movieElement, "sorttitle"));
		result.setOverview(getSingleElementText(movieElement, "plot"));
		result.setTagLine(getSingleElementText(movieElement, "tagline"));
		result.setCertification(getSingleElementText(movieElement, "mpaa"));
		result.setImdbId(getSingleElementText(movieElement, "id"));
		result.setTrailerUrl(getSingleElementText(movieElement, "trailer"));

		// Values which need some processing
		result.setCoverUrl(getThumbnail(movieElement, nfoFilePath));
		result.setRatingPercent((int) (getSingleElementDouble(movieElement, "rating") * 10));
		result.setYear((int) getSingleElementDouble(movieElement, "year"));
		result.setRatingVoters((int) getSingleElementDouble(movieElement, "votes"));
		result.setDirector(getDirector(movieElement));
		result.setActors(getActors(movieElement));
		result.setGenres(getGenres(movieElement));

		return result;
	}

	/**
	 * Gets a single child element according to tag name from parent element.
	 *
	 * @param parentElement the parent element
	 * @param tagName the tag name
	 * @return the text value of the element; if there is none or more than one, NULL will be returned
	 */
	private String getSingleElementText(Element parentElement, String tagName) {
		NodeList allElements = parentElement.getElementsByTagName(tagName);

		// Only consider direct child nodes and not nodes having the same tag name in the entire hierarchy
		List<Node> elements = getDirectChildrenOnly(allElements, parentElement);
		if (elements != null && elements.size() == 1) {
			return elements.get(0).getTextContent().trim();
		}

		return null;
	}

	/**
	 * Gets a single child element according to tag name from parent element and returns it as a double.
	 *
	 * @param parentElement the parent element
	 * @param tagName the tag name
	 * @return the double value of the element; if there is none or more than one, NULL will be returned
	 */
	private double getSingleElementDouble(Element parentElement, String tagName) {
		String stringValue = getSingleElementText(parentElement, tagName);
		if (stringValue != null) {
			try {
				// Remove commas from the string
				// TODO: check if this is dependent on the OS language
				return Double.parseDouble(stringValue.replace(",", ""));
			} catch (NumberFormatException e) {
				LOGGER.warn(String.format("Failed to convert string '%s' to an Integer value", stringValue), e);
			}
		}

		return 0;
	}

	/**
	 * Gets the director. If there are multiple directors, they will be concatenated separated by a comma.
	 *
	 * @param parentElement the parent element
	 * @return the director
	 */
	private String getDirector(Element parentElement) {
		NodeList allElements = parentElement.getElementsByTagName("director");
		List<Node> elements = getDirectChildrenOnly(allElements, parentElement);
		if (elements != null && elements.size() > 0) {
			String directors = "";
			for (int i = 0; i < elements.size(); i++) {
				directors += elements.get(i).getTextContent().trim() + ", ";
			}

			// Remove the last separator (, )
			if (directors.length() > 1) {
				directors = directors.substring(0, directors.length() - 2);
			}

			return directors;
		}

		return null;
	}

	/**
	 * Gets the actors.
	 *
	 * @param parentElement the parent element
	 * @return the actors
	 */
	private List<String> getActors(Element parentElement) {
		NodeList allElements = parentElement.getElementsByTagName("actor");
		List<Node> elements = getDirectChildrenOnly(allElements, parentElement);
		if (elements != null && elements.size() > 0) {
			List<String> actors = new ArrayList<>();
			for (int i = 0; i < elements.size(); i++) {
				Node actorNode = elements.get(i);
				if (actorNode.getNodeType() == Node.ELEMENT_NODE) {
					Element actorElement = (Element) actorNode;
					actors.add(actorElement.getElementsByTagName("name").item(0).getTextContent().trim());
				}
			}

			return actors;
		}

		return null;
	}

	/**
	 * Gets the genres from the parent element.
	 *
	 * @param parentElement the parent element
	 * @return the genres
	 */
	private List<String> getGenres(Element parentElement) {
		NodeList allElements = parentElement.getElementsByTagName("genre");
		List<Node> elements = getDirectChildrenOnly(allElements, parentElement);
		if (elements != null && elements.size() > 0) {
			List<String> genres = new ArrayList<>();
			for (int i = 0; i < elements.size(); i++) {
				genres.add(elements.get(i).getTextContent().trim());
			}

			return genres;
		}

		return null;
	}

	/**
	 * Gets the thumbnail.<br>
	 * 1. Return the file path to the file <i>video_name</i>-poster.jpg if it exists<br>
	 * 2. Return the file path to the file <i>video_name</i>-thumb.jpg if it exists<br>
	 * 3. Return the first valid URL specified in a 'thumb' element<br>
	 * 4. Return NULL if none of the above is possible
	 *
	 * @param parentElement the parent element
	 * @param nfoFilePath the NFO file path
	 * @return the thumbnail (file path or URL)
	 */
	private String getThumbnail(Element parentElement, String nfoFilePath) {
		// 1. Return the file path to the file <video_name>-poster.jpg if it exists
		String coverFilePath = nfoFilePath.substring(0, nfoFilePath.lastIndexOf('.')) + "-poster.jpg";
		if (new File(coverFilePath).exists()) {
			return coverFilePath;
		}

		// 2. Return the file path to the file <video_name>-thumb.jpg if it exists
		coverFilePath = nfoFilePath.substring(0, nfoFilePath.lastIndexOf('.')) + "-thumb.jpg";
		if (new File(coverFilePath).exists()) {
			return coverFilePath;
		}

		// 3. Return the first valid URL specified in a 'thumb' element
		NodeList allElements = parentElement.getElementsByTagName("thumb");
		List<Node> elements = getDirectChildrenOnly(allElements, parentElement);
		if (elements != null && elements.size() > 0) {
			for (int i = 0; i < elements.size(); i++) {
				String coverUrl = elements.get(i).getTextContent();
				if (FileHelper.isValidUrl(coverUrl)) {
					return coverUrl;

				}
			}
		}

		// 4. Return NULL if none of the above is possible
		return null;
	}

	/**
	 * Gets a list of nodes which are direct children (not in nested elements)
	 *
	 * @param allElements the all elements
	 * @param parentElement the parent element
	 * @return the direct children only
	 */
	private List<Node> getDirectChildrenOnly(NodeList allElements, Element parentElement) {
		List<Node> elements = new ArrayList<>();
		for (int i = 0; i < allElements.getLength(); i++) {
			Node currentNode = allElements.item(i);
			if (currentNode.getParentNode() == parentElement) {
				elements.add(currentNode);
			}
		}

		return elements;
	}

	/**
	 * Left pad a String with a specified String.
	 *
	 * @param text the text to pad
	 * @param size the size to pad
	 * @param padString the pad string
	 * @return the left-padded string
	 */
	private String leftPad(String text, int size, String padString) {
		if (text == null) {
			return text;
		}

		return StringUtils.leftPad(text, size, padString);
	}
}