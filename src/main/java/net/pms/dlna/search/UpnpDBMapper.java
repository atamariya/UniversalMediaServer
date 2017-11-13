/**
 * 
 */
package net.pms.dlna.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for mapping UPnP objects to DB.<br>
 * 
 * @author Anand Tamariya
 *
 */
public class UpnpDBMapper {
	private static final String[] objects = new String[] {
//			"object.container.album",
			"object.container.album.musicAlbum",
//			"object.container.album.photoAlbum",
//			"object.container.genre",
//			"object.container.genre.movieGenre",
			"object.container.genre.musicGenre",
//			"object.container.person",
			"object.container.person.musicArtist",
//			"object.container.playlistContainer",

			"object.item.audioItem",
//			"object.item.audioItem.audioBook",
//			"object.item.audioItem.audioBroadcast",
//			"object.item.audioItem.musicTrack",
			"object.item.imageItem",
//			"object.item.imageItem.photo",
//			"object.item.playlistItem",
			"object.item.videoItem",
//			"object.item.videoItem.movie",
//			"object.item.videoItem.musicVideoClip",
//			"object.item.videoItem.videoBroadcast"
	};
	
	private static final String[] tables = new String[] {
			"SELECT DISTINCT A.ALBUM FROM FILES F, AUDIOTRACKS A WHERE F.ID = A.FILEID AND F.TYPE = 1",
			"SELECT DISTINCT A.GENRE FROM FILES F, AUDIOTRACKS A WHERE F.ID = A.FILEID AND F.TYPE = 1",
			"SELECT DISTINCT A.ARTIST FROM FILES F, AUDIOTRACKS A WHERE F.ID = A.FILEID AND F.TYPE = 1",
			
			"SELECT * FROM FILES F, AUDIOTRACKS A WHERE F.ID = A.FILEID AND F.TYPE = 1",
			"SELECT * FROM FILES WHERE TYPE = 2",
			"SELECT * FROM FILES WHERE TYPE = 4",
	};
	
	private static final String[][] attributes = new String[][] {
		// TODO: Not all attributes are populated
		{ "dc:title" },
		{ "dc:title" },
		{ "dc:title" },
		
		{ "@id", "dc:title", "upnp:artist", "upnp:album" },
		{ "@id", "dc:title" },
		{ "@id", "dc:title" },
	};
	
	private static final String[][] column = new String[][] {
		{ "album" },
		{ "genre" },
		{ "artist" },
		
		{ "id", "title", "artist", "album" },
		{ "id", "title" },
		{ "id", "title" },
	};

	private static final Pattern PATTERN_CONTAINS = Pattern.compile(" contains \"([0-9a-z ]*)\"", Pattern.CASE_INSENSITIVE);
	
	private static final Pattern PATTERN_COLNAME = Pattern.compile("select distinct ([0-9a-z \\.]*) from", Pattern.CASE_INSENSITIVE);
	
	/**
	 * 
	 * @param obj UPNP object
	 * @param query Search criteria
	 * @return 
	 */
	public static String getSQLForItem(String obj, String query) {
		String[] result = getSQLForContainer(obj, query);
		
		if (result == null)
			return null;
		else
			return result[0];
	}
	
	/**
	 * Create sql[] for containers.
	 * 
	 * @param obj Container object
	 * @param query
	 * @return
	 */
	public static String[] getSQLForContainer(String obj, String query) {
		if (obj == null)
			return null;
		
		StringBuilder result = null;
		StringBuilder allFiles = null;
		for (int i = 0; i < objects.length; i++) {
			String item = objects[i];
			if (item.equalsIgnoreCase(obj)) {
				result = new StringBuilder(tables[i]);
				allFiles = new StringBuilder();
				Matcher matcher = null;

				if (query != null) {
					// Replace UPNP attributes with column names
					for (int j = 0; j < attributes[i].length; j++) {
						String attr = attributes[i][j];
						if (query.contains(attr)) {
							query = query.replaceAll(attr, "lcase(" + column[i][j] + ")");
						}
					}

					// Replace "contains" with "like" clause
					// Replace " with '
					matcher = PATTERN_CONTAINS.matcher(query);
					if (matcher.find()) {
						query = matcher.replaceAll(" like '%$1%'");
					}
					query = query.replaceAll("\"", "'");

					result.append(" and ");
					result.append(query);
				}

				matcher = PATTERN_COLNAME.matcher(tables[i]);
				if (matcher.find()) {
					result.append(" ORDER BY ").append(matcher.group(1));
					allFiles.append(matcher.replaceFirst("select * from")).append(" and ").append(matcher.group(1))
							.append(" = '${0}'").append(" ORDER BY TITLE");
				}
				
				break;
			}

		}
		
		if (result == null)
			return null;
		else
			return new String[]{result.toString(), allFiles.toString()};
	}
}
