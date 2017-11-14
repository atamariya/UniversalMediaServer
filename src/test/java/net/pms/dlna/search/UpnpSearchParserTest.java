/**
 * 
 */
package net.pms.dlna.search;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Anand Tamariya
 *
 */
public class UpnpSearchParserTest {
	@Test
	public void testUpnpObjectUtil() {

		List<String> derivedChildren = UpnpObjectUtil.getDerivedChildren("object.item.audioItem");
		Assert.assertEquals(4, derivedChildren.size());

		String parent = UpnpObjectUtil.getParent("object.container.album");
		Assert.assertEquals("object.container", parent);

		parent = UpnpObjectUtil.getParent("object");
		Assert.assertEquals(null, parent);
		
		parent = UpnpObjectUtil.getName("object.container.album.musicAlbum");
		Assert.assertEquals("MusicAlbum", parent);

		boolean hasAttribute = UpnpObjectUtil.hasAttribute("object.item.videoItem", "dc:title");
		Assert.assertEquals(true, hasAttribute);

		hasAttribute = UpnpObjectUtil.hasAttribute("object.container.album.musicAlbum", "upnp:album");
		Assert.assertEquals(false, hasAttribute);
	}
	
	@Test
	public void testUpnpSearchParser() {
		String query =
				"(dc:title contains \"cap \\\"a\\\"\")";
		String query1 = query;
		UpnpSearchParser parser = null;
		
		parser = new UpnpSearchParser(query);
		Assert.assertEquals(query, parser.getQuery());
		Assert.assertEquals(24, parser.getObjects().size());
		
		query1 =
				"(dc:title contains \"cap\")";
		query =
				"(upnp:class = \"object.container.album.musicAlbum\" and dc:title contains \"cap\")";
		parser = new UpnpSearchParser(query);
		Assert.assertEquals(query1, parser.getQuery());
		Assert.assertEquals(1, parser.getObjects().size());
		
		query =
				"(dc:title contains \"cap\" and upnp:class = \"object.container.album.musicAlbum\")";
		parser = new UpnpSearchParser(query);
		Assert.assertEquals(query1, parser.getQuery());
		Assert.assertEquals(1, parser.getObjects().size());
		
		query =
				"dc:title contains \"cap\" and (upnp:class = \"object.container.album.musicAlbum\")";
		query1 = "dc:title contains \"cap\"";
		parser = new UpnpSearchParser(query);
		Assert.assertEquals(query1, parser.getQuery());
		Assert.assertEquals(1, parser.getObjects().size());

		query =
				"upnp:album exists false";
		parser = new UpnpSearchParser(query);
		Assert.assertEquals(null, parser.getQuery());
		Assert.assertEquals(25, parser.getObjects().size());

		query =
				"upnp:album exists true";
		parser = new UpnpSearchParser(query);
		Assert.assertEquals(null, parser.getQuery());
		Assert.assertEquals(2, parser.getObjects().size());
		
		query =
				"((upnp:class derivedfrom \"object.container.album\" and (upnp:genre contains \"FindThis\")) and dc:title contains \"cap\")";
		query1 =
				"(((upnp:genre contains \"FindThis\")) and dc:title contains \"cap\")";
		parser = new UpnpSearchParser(query);
		Assert.assertEquals(query1, parser.getQuery());
		Assert.assertEquals(1, parser.getObjects().size());

		query =
				"(upnp:class derivedfrom &quot;object.item.audioItem&quot;)";
		parser = new UpnpSearchParser(query);
		Assert.assertEquals(null, parser.getQuery());
		Assert.assertEquals(4, parser.getObjects().size());
		
		query =
				"upnp:class derivedfrom \"object.item.imageItem\" and @refID exists false";
		parser = new UpnpSearchParser(query);
		Assert.assertEquals(null, parser.getQuery());
		Assert.assertEquals(2, parser.getObjects().size());
		
//		query =
//				"((upnp:class = \"object.item.audioItem.musicTrack\" and dc:title contains \"cap\") or " + 
//				"(dc:title contains \"cap\" and upnp:class = \"object.container.album.musicAlbum\"))";
//		parser = new UpnpSearchParser(query);
	
	}
	
	@Test
	public void testUpnpDBMapper() {
		String query = "dc:title contains \"cap\"";
		String[] sql = UpnpDBMapper.getSQLForContainer("object.container.album.musicAlbum", query);
		String sql1 = "SELECT DISTINCT A.ALBUM FROM FILES F, AUDIOTRACKS A WHERE F.ID = A.FILEID AND F.TYPE = 1 and lcase(album) like '%cap%' ORDER BY A.ALBUM";
		String sql2 = "select * from FILES F, AUDIOTRACKS A WHERE F.ID = A.FILEID AND F.TYPE = 1 and A.ALBUM = '${0}' ORDER BY TITLE";
		Assert.assertEquals(sql1, sql[0]);
		Assert.assertEquals(sql2, sql[1]);
		
		sql[0] = UpnpDBMapper.getSQLForItem("object.item", query);
		Assert.assertEquals(null, sql[0]);
		
		sql[0] = UpnpDBMapper.getSQLForItem("object.item.imageItem", null);
		Assert.assertEquals("SELECT * FROM FILES WHERE TYPE = 2", sql[0]);
	}
}
