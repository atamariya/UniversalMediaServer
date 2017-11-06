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

import org.apache.commons.lang3.text.WordUtils;

/**
 * Utility class for handling common functions related to UPnP objects.<br>
 * 
 * Ref 2.3.11.2 UPnP-av-ContentDirectory-v2-Service.pdf <br>
 * 
 * <ul>
 * <li><b>String comparisons.</b> All operators when applied to strings use case-insensitive comparisons</li>
 * <li><b>Property omission.</b> Any property value query (as distinct from an existence query) applied to an
object that does not have that property evaluates to false.</li>
 * </ul>
 * @author Anand Tamariya
 *
 */
public class UpnpObjectUtil {
	private static final String[] objects = new String[] {
			"object",
			"object.container",
			"object.item",
			
			"object.container.album",
			"object.container.album.musicAlbum",
			"object.container.album.photoAlbum",
			"object.container.genre",
			"object.container.genre.movieGenre",
			"object.container.genre.musicGenre",
			"object.container.person",
			"object.container.person.musicArtist",
			"object.container.playlistContainer",
			"object.container.storageFolder",
			"object.container.storageSystem",
			"object.container.storageVolume",

			"object.item.audioItem",
			"object.item.audioItem.audioBook",
			"object.item.audioItem.audioBroadcast",
			"object.item.audioItem.musicTrack",
			"object.item.imageItem",
			"object.item.imageItem.photo",
			"object.item.playlistItem",
			"object.item.textItem",
			"object.item.videoItem",
			"object.item.videoItem.movie",
			"object.item.videoItem.musicVideoClip",
			"object.item.videoItem.videoBroadcast"
	};
	
	private static final String[][] attributes = new String[][] {
		// TODO: Not all attributes are populated
		{ "@id", "@parentID", "@restricted", "dc:title", "upnp:class", "dc:creator", "upnp:writeStatus" },
		{ "@childCount", "upnp:createClass", "upnp:searchClass", "@searchable", "@neverPlayable" },
		{ "@refID", "upnp:bookmarkID" },
		{ "upnp:storageMedium", "dc:longDescription", "dc:description", "dc:publisher", "dc:contributor", "dc:date", "dc:relation", "dc:rights" },
		{ "upnp:artist", "upnp:genre", "upnp:producer", "upnp:albumArtURI", "upnp:toc" },
		{  },
		{ "upnp:genre", "upnp:longDescription", "dc:description" },
		{  },
		{  },
		{ "dc:language" },
		{  },
		{  },
		{  },
		{  },
		{  },
		{ "upnp:genre", "dc:description", "upnp:longDescription", "dc:publisher", "dc:language", "dc:relation", "dc:rights" },
		{  },
		{  },
		{ "upnp:artist", "upnp:album", "upnp:originalTrackNumber", "upnp:playlist", "upnp:storageMedium", "dc:contributor", "dc:date" },
		{ "upnp:longDescription", "upnp:storageMedium", "upnp:rating", "dc:description", "dc:publisher", "dc:date", "dc:rights" },
		{ "upnp:album" },
		{  },
		{  },
		{ "upnp:genre", "upnp:genre@id", "upnp:genre@type", "upnp:longDescription", "upnp:producer", "upnp:rating", "upnp:actor", "upnp:director", "dc:description", "dc:publisher", "dc:language", "dc:relation", "upnp:playbackCount", "upnp:lastPlaybackTime", "upnp:lastPlaybackPosition", "upnp:recordedDayOfWeek", "upnp:srsRecordScheduleID" }
	};
	
	private static final Map<String, String[]> map = new TreeMap<>();

	static {
		// objects and attributes are stored in arrays for readability. However for efficient queries, they must be sorted.
		int i = 0;
		for (String[] strings : attributes) {
//			System.out.println(objects[i] + " " + (strings.length > 0 ? strings[0] : ""));
			Arrays.sort(strings);
			map.put(objects[i++], strings);
		}
		
		Arrays.sort(objects);
	}
	
	/**
	 * Useful for derivedfrom operation.
	 * 
	 * @param parent
	 * @return List of objects which derive from parent including the parent
	 */
	public static List<String> getDerivedChildren(String parent) {
		if (parent == null)
			return null;
		
		List<String> children = new ArrayList<>();
		parent = parent.toLowerCase();
		for (String item : objects) {
			if (item.toLowerCase().startsWith(parent))
				children.add(item);
		}
		return children;
	}
	
	public static String getParent(String obj) {
		if (obj == null || objects[0].equalsIgnoreCase(obj))
			return null;
		return obj.substring(0, obj.lastIndexOf("."));
	}
	
	/**
	 * Get object name for display purpose
	 *
	 * @param obj
	 * @return
	 */
	public static String getName(String obj) {
		if (obj == null)
			return null;
		
		String result = obj.substring(obj.lastIndexOf(".") + 1);
		result = WordUtils.capitalize(result);
		return result;
	}
	
	public static boolean isItem(String obj) {
		boolean result = false;
		if (obj != null && obj.startsWith("object.item"))
			result = true;
		return result;
	}

	public static boolean isContainer(String obj) {
		boolean result = false;
		if (obj != null && obj.startsWith("object.container"))
			result = true;
		return result;
	}

	/**
	 * Check if the object has attribute
	 * 
	 * @param obj
	 * @param attr
	 * @return True if obj has the attribute attr
	 */
	public static boolean hasAttribute(String obj, String attr) {
		boolean result = false;
		
		if (obj != null && map.containsKey(obj)) {
			int i = Arrays.binarySearch(map.get(obj), attr);
			if (i < 0) {
				result = hasAttribute(getParent(obj), attr);
			} else {
				result = true;
			}
		}
		return result;
	}
	
	/**
	 * Filters the list of objects based on the existence of attr.
	 * 
	 * @param list
	 * @param attr
	 */
	public static void filterByAttribute(List<String> list, String attr) {
		for (Iterator<String> iter = list.iterator(); iter.hasNext();) {
			String obj = iter.next();
			if (!hasAttribute(obj, attr))
				iter.remove();
		}
	}
}
