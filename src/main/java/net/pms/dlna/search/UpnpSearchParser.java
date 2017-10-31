package net.pms.dlna.search;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.container.MusicAlbum;
import org.fourthline.cling.support.model.container.MusicArtist;
import org.fourthline.cling.support.model.container.PlaylistContainer;
import org.fourthline.cling.support.model.item.AudioItem;
import org.fourthline.cling.support.model.item.ImageItem;
import org.fourthline.cling.support.model.item.Item;
import org.fourthline.cling.support.model.item.MusicTrack;
import org.fourthline.cling.support.model.item.VideoItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.pms.dlna.search.UpnpParser.RelExpContext;
import net.pms.dlna.search.UpnpParser.SearchCritContext;
import net.pms.dlna.search.UpnpParser.SearchExpContext;

public class UpnpSearchParser {
	private static final Logger LOGGER = LoggerFactory.getLogger(UpnpSearchParser.class);
	private String sql;
	private DIDLObject result;
	
	public static void main(String[] args) {
		String query =
				"(dc:title contains \"cap\")";
		UpnpSearchParser parser = null;
		parser = new UpnpSearchParser(query);
		
		System.out.println("2");
		query =
				"(upnp:class = \"object.container.album.musicAlbum\" and dc:title contains \"cap\")";
		parser = new UpnpSearchParser(query);
		
		System.out.println("3");
		query =
				"(dc:title contains \"cap\" and upnp:class = \"object.container.album.musicAlbum\")";
		parser = new UpnpSearchParser(query);
		
		System.out.println("4");
		query =
				"(upnp:class derivedfrom \"object.item.audioItem.musicTrack\" and (upnp:genre contains \"FindThis\"))";
		parser = new UpnpSearchParser(query);
		
		System.out.println("5");
		query =
				"((upnp:class = \"object.item.audioItem.musicTrack\" and dc:title contains \"cap\") or " + 
				"(dc:title contains \"cap\" and upnp:class = \"object.container.album.musicAlbum\"))";
		parser = new UpnpSearchParser(query);
	
	}
	
	public UpnpSearchParser(String query) {
		CharStream is = CharStreams.fromString(query);
		UpnpLexer lexer = new UpnpLexer(is);
		TokenStream tokens = new CommonTokenStream(lexer);
		UpnpParser parser = new UpnpParser(tokens);

		LOGGER.trace(query);
		UpnpVisitorImpl visitor = new UpnpVisitorImpl();
		result = visitor.visit(parser.searchCrit()); 
		setSql(visitor.sql.toString());
		
		System.out.println(result.getClass());
		System.out.println(result.getTitle());
		System.out.println(result instanceof Item || result instanceof Container);
		if (result instanceof Container) {
			for (DIDLObject o : ((Container) result).getItems()) {
				System.out.println(o.getClass());
				System.out.println(o.getTitle());
			}
			for (DIDLObject o : ((Container) result).getContainers()) {
				System.out.println(o.getClass());
				System.out.println(o.getTitle());
			}
//		} else {
//			System.out.println(result.getTitle());
		}
		LOGGER.trace(getSql());
	}

	public String getSql() {
		return sql;
	}

	private void setSql(String sql) {
		this.sql = sql;
	}

	public DIDLObject getResult() {
		return result;
	}

	public void setResult(DIDLObject result) {
		this.result = result;
	}
}

class Query {
	public String object, sql;
}

/**
 * Return a DIDL object with SQL in title.
 * @author anand
 *
 */
class UpnpVisitorImpl extends UpnpBaseVisitor<DIDLObject> {
	StringBuilder sql = new StringBuilder();
	List<Query>	query = new ArrayList<>();
	
	@Override
	public DIDLObject visitSearchCrit(SearchCritContext ctx) {
		DIDLObject result = super.visitSearchCrit(ctx);
		if (result == null) {
			// Generic search - search Items and Containers
			Container c = new Container();
			
			result = new Item();
			result.setTitle(sql.toString());
			c.addItem((Item) result);
			
			result = new Container();
			result.setTitle(sql.toString());
			c.addContainer((Container) result);
			
			result = c;
		}
		return result;
	}
	
	@Override
	public DIDLObject visitSearchExp(SearchExpContext ctx) {
		TerminalNode node = ctx.LOGOP();
		if (node != null) {
			// If DIDLObject is not null, avoid the logical operator
			DIDLObject left = super.visitSearchExp(ctx.searchExp(0));
//			System.out.println("1:" + sql);
			String temp = null;
			Item item = null;
			Container container = null;
			if (left == null) {
//				System.out.println(left.getClass());
				temp = (sql.toString());
				sql.setLength(0);
			}
			
			DIDLObject right = super.visitSearchExp(ctx.searchExp(1));
//			System.out.println("2:" + sql);
			if (right == null) {
//				System.out.println(right.getClass());
				temp = (sql.toString());
				sql.setLength(0);
				
				if (left instanceof Item)
					((Item) left).setTitle(temp);
				if (left instanceof Container)
					((Container) left).setTitle(temp);
				return left;
			}

			if (left == null) {
				if (right instanceof Item)
					((Item) right).setTitle(temp);
				if (right instanceof Container)
					((Container) right).setTitle(temp);
				return right;
			}
			
			container = new Container();
			if (left instanceof Item)
				container.addItem((Item) left);
			if (left instanceof Container)
				container.addContainer((Container) left);
			if (right instanceof Item)
				container.addItem((Item) right);
			if (right instanceof Container)
				container.addContainer((Container) right);
			return container;
			
		}
		return super.visitSearchExp(ctx);
	}
	
	@Override
	public DIDLObject visitRelExp(RelExpContext ctx) {
		if (ctx.PROPERTY().getText().equals("upnp:class")) {
			TerminalNode node = ctx.QUOTEDVAL();
			String op = ctx.BINOP().getText();
			String result = "";
			DIDLObject result1 = null;
			
//			System.out.println(node.getText());
//			System.out.println(ctx.BINOP().getText());
			
			result = node.getText();
			switch (result) {
			case "\"object.container.person.musicArtist\"":
				result = "f.type = 1 and lower(artist) like '%s'";
				result1 = new MusicArtist();
				break;
			case "\"object.container.album.musicAlbum\"":
				result = "f.type = 1 and lower(album) like '%s'";
				result1 = new MusicAlbum();
				break;
			case "\"object.item.audioItem.musicTrack\"":
				result = "f.type = 1 and lower(title) like '%s'";
				result1 = new MusicTrack();
				break;
			case "\"object.item.audioItem\"":
				result = "1";
				result1 = new AudioItem();
				break;
			case "\"object.item.imageItem\"":
				result = "2";
				result1 = new ImageItem();
				break;
			case "\"object.item.videoItem\"":
				result1 = new VideoItem();
				result = "4";
				break;
			case "\"object.container.playlistContainer\"":
				result = "16";
				result1 = new PlaylistContainer();
				break;
			
			default:
//				result1 = new DIDLObject() {
//				};
			}
			if ("derivedfrom".equals(op)) {
				result1.getClazz().setIncludeDerived(true);
			}
			return result1;
		}
		return super.visitRelExp(ctx);
	}
	
	@Override
	public DIDLObject visitTerminal(TerminalNode node) {
//		System.out.println("T:" + node.getSymbol());
		String result = node.getText();
		switch (result) {
		case "(":
		case ")":
			result = null;
			break;

		case "upnp:class":
			result = "f.type";
			break;
		case "upnp:genre":
			result = "genre";
			break;
		case "dc:title":
			result = "LOWER(TITLE)";
			break;
		case "upnp:album":
			result = "LOWER(ALBUM)";
			break;
		case "upnp:artist":
		case "dc:creator":
			result = "LOWER(ARTIST)";
			break;
		case "\"object.container.person.musicArtist\"":
		case "\"object.container.album.musicAlbum\"":
		case "\"object.item.audioItem.musicTrack\"":
		case "\"object.item.audioItem\"":
			result = "1";
			break;
		case "\"object.item.imageItem\"":
			result = "2";
			break;
		case "\"object.item.videoItem\"":
			result = "4";
			break;
		case "\"object.container.playlistContainer\"":
			result = "16";
			break;
		
		case "contains":
			result = "like";
			break;
		case "derivedfrom":
		case "exists":
			result = "=";
			break;
			
		default:
			if (node.getSymbol().getType() == UpnpParser.QUOTEDVAL) {
				result = String.format("'%%%s%%'", result.substring(1, result.length() - 1));
			} else if (node.getSymbol().getType() == UpnpParser.WCHAR
					|| node.getSymbol().getType() == UpnpParser.LOGOP
					|| node.getSymbol().getType() == UpnpParser.BINOP) {
//				result = null;
			} else {
				result = "1";
			}
		}
		if (result != null)
			sql.append(result);
		return null;
	}
	
	@Override
	protected DIDLObject aggregateResult(DIDLObject aggregate, DIDLObject nextResult) {
		DIDLObject result = aggregate;
		if (result == null)
			result = nextResult;
		if ((aggregate instanceof Item && nextResult instanceof Container)
				|| (nextResult instanceof Item && aggregate instanceof Container)
//				|| result == null
				) {
			result = new Container();
		}
		return result;
	}
}