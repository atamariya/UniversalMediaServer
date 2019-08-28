package net.pms.dlna;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.pms.PMS;
import net.pms.network.UPNPControl.Renderer;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

/**
 * GlobalIdRepo ensures unique id for all DLNA resources. Only folders need generated ids.
 * Files use the ID field in FILES table.<p>
 * Index starts at MAX(ID) in FILES table. When FILES.ID reaches {@link #index}, {@link #index}
 * is reset and cache is invalidated.
 * 
 * @author Anand Tamariya
 *
 */
public class GlobalIdRepo {
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalIdRepo.class);
	
	public static final int BUFFER = 1000;

	/**
	 * Store <cookie, Renderer>
	 */
	private Ehcache renderCache = null;
	/**
	 * Store list of invalid files <filename, filename>
	 */
	private Ehcache invalidFiles = null;
	
	// Global ids start at 1, since id 0 is reserved as a pseudonym for 'renderer root'
	private int globalId = 1, index = 1;

	private CacheManager cacheManager;
	
	private DLNAMediaDatabase database = PMS.get().getDatabase();


	public GlobalIdRepo() {
		cacheManager = CacheManager.newInstance();
		renderCache = cacheManager.addCacheIfAbsent("renderer");
		invalidFiles = cacheManager.addCacheIfAbsent("invalidFiles");
		resetIndex();
	}

	public String getId(String filename) {
		String id = null;
		if (filename != null) {
			DLNAResource resource = database.getNodeByFilename(filename);
			if (resource != null)
				id = resource.getId();
		}
		return id;
	}
	
	public void addRenderer(String cookie, Renderer r) {
		Element el = new Element(cookie, r);
		el.setTimeToIdle(2 * 60); // 2 minutes
		renderCache.put(el);
	}
	
	public Renderer getRenderer(String cookie) {
		Renderer r = null;
		Element el = renderCache.get(cookie);
		if (el != null)
			r = (Renderer) el.getObjectValue();
		
		return r;
	}
	
	/**
	 * This method ensures a unique id for DLNAResource identified by a file (path + filename).
	 * @param d
	 */
	public synchronized void add(DLNAResource d) {
//		String filename = d.getSystemName();
		String id = d.getId();
//		if (get(id) != null)
//			return;
		
//		id = getId(filename);
//		DLNAResource existing = get(id);
//		if (id != null && d.equals(existing)) {
//		    // Update id in d; update other values in existing from d
//			d.setId(id);
//			
//			existing.setMedia(d.getMedia());
//			existing.setMediaSubtitle(d.getMediaSubtitle());
//			existing.setUpdateId(d.getUpdateId());
////			if (d.isDiscovered())
//			    existing.setChildren(d.getChildren());
//			
//			if (d instanceof WebStreamItem) {
//			    WebStreamItem w = (WebStreamItem) existing;
//			    w.setThumbURL(((WebStreamItem) d).getThumbURL());
//			    w.setTitle(((WebStreamItem) d).getTitle());
//			}
//			return;
//		}

		// If media is null, it has not been resolved yet. Don't add to cache.
//		if (d.getMedia() == null && !d.isFolder() && !(d instanceof StreamItem))
//			return;
		
		if (!d.isFolder()) {
//			Media file is present in DB
		} else {
			if (d instanceof RootFolder) {
				d.setIndexId(0);
			} else if (d.getMedia() != null && d.getMediaSubtitle() == null) {
				// Media file; avoid subtitle variant
				return;
			} else if (id == null){
				d.setIndexId(globalId++);
			}
			id = d.getId();
			LOGGER.debug("globalId: {}", globalId);
			database.updateDLNATree(id, d);
		}
		
	}

	public DLNAResource get(String id) {
		return database.getNodeById(id);
	}

	public void remove(DLNAResource d) {
		String id = d.getId();
		if (id == null)
			id = getId(d.getSystemName());
		if (id == null)
			id = ""; // File might not have been added yet. Avoid clearing complete cache.
		remove(id);
	}

	private void remove(String id) {
		database.removeNode(id);
	}
	
	/**
	 * Clear all elements and resets global id
	 */
	public void clear() {
		database.removeNode(null);
		
		invalidFiles.removeAll();
		renderCache.removeAll();
		
		resetIndex();
	}

	public int getIndex() {
		return index;
	}

	private void resetIndex() {
		List<String> children = database.getStrings("SELECT MAX(ID) FROM FILES");
		if (children != null && !DLNAMediaDatabase.NONAME.equals(children.get(0))) {
			// Keep some buffer (1000) to allow for file additions
			index = Integer.parseInt(children.get(0));
		} else {
			index = 1;
		}
		index += BUFFER;
		globalId = index;
	}
	
	public void shutdown() {
		cacheManager.shutdown();
	}
	
	public static int parseIndex(String id) {
		try {
			// Id strings may have optional tags beginning with $ appended, e.g. '1234$Temp'
			return Integer.parseInt(StringUtils.substringBefore(id, "$"));
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public boolean exists(String id) {
		return database.getNodeById(id) != null;
	}

	public void markInvalid(String filename) {
		Element el = new Element(filename, filename);
		el.setEternal(true);
		invalidFiles.put(el);
	}
	
	public boolean isInvalid(String filename) {
		boolean r = false;
		Element el = invalidFiles.get(filename);
		if (el != null)
			r = true;
		
		return r;
	}
}

