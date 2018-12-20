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
	 * Store <filename, id>
	 */
	Map<String, String> idMap = new HashMap<>();
	/**
	 * Store <id, DLNAResource>
	 */
	Ehcache resourcesMap = null;
	/**
	 * Store <cookie, Renderer>
	 */
	Ehcache renderCache = null;
	/**
	 * Store list of invalid files <filename, filename>
	 */
	Ehcache invalidFiles = null;
	
	// Global ids start at 1, since id 0 is reserved as a pseudonym for 'renderer root'
	private int globalId = 1, deletions = 0, index = 1;

	private CacheManager cacheManager;

	public GlobalIdRepo() {
		cacheManager = CacheManager.newInstance();
		resourcesMap = cacheManager.addCacheIfAbsent("PMS");
		renderCache = cacheManager.addCacheIfAbsent("renderer");
		invalidFiles = cacheManager.addCacheIfAbsent("invalidFiles");
	}

	public String getId(String filename) {
		String id = null;
		if (filename != null) {
			DLNAResource resource = null;
			List keys = resourcesMap.getKeys();
			for (Object object : keys) {
				resource = (DLNAResource) resourcesMap.get(object).getObjectValue();
				if (filename.equals(resource.getSystemName())) {
					id = resource.getId();
					break;
				}
			}

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
		String filename = d.getSystemName();
		String id = d.getId();
		if (get(id) != null)
			return;
		
		if (id == null && resourcesMap.isValueInCache(d)) {
		    // Update id in d; update other values in existing from d
			id = getId(filename);
			DLNAResource existing = get(id);
			d.setId(id);
			
			existing.setMedia(d.getMedia());
			existing.setMediaSubtitle(d.getMediaSubtitle());
			existing.setUpdateId(d.getUpdateId());
			existing.setChildren(d.getChildren());
			return;
		}

		// If media is null, it has not been resolved yet. Don't add to cache.
		if (d.getMedia() == null && !d.isFolder() && !(d instanceof WebStreamItem))
			return;
		
		if (id != null) {
//			ID is present in DB
		} else {
			d.setIndexId(globalId++);
			id = d.getId();
		}
		Element el = new Element(id, d);
		el.setEternal(true);
//		System.out.println(id + ": " + filename);
		
		resourcesMap.put(el);
//		idMap.put(filename, id);
//		filenameMap.put(id, filename);
//		System.out.println(resourcesMap.isKeyInCache(el) + " :::: " + get(id));
	}

	public DLNAResource get(String id) {
		Element el = resourcesMap.get(id);
		if (el == null)
			return null;
		return (DLNAResource) el.getObjectValue();
	}

	public void remove(DLNAResource d) {
		String id = d.getId();
		if (id == null)
			id = getId(d.getSystemName());
		remove(id);
	}

	private void remove(String id) {
		resourcesMap.remove(id);
	}
	
	/**
	 * Clear all elements and resets global id
	 */
	public void clear() {
		resourcesMap.removeAll();
		invalidFiles.removeAll();
		renderCache.removeAll();
		
		resetIndex();
	}

	public int getIndex() {
		return index;
	}

	private void resetIndex() {
		DLNAMediaDatabase database = PMS.get().getDatabase();
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
	
	@Override
	public String toString() {
		List keys = resourcesMap.getKeys();
		for (Object  el : keys) {
			System.out.println(resourcesMap.get(el));
		}
		return super.toString();
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
		return idMap.containsKey(id);
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

