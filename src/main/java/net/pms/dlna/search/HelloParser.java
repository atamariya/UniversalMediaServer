package net.pms.dlna.search;

// Generated from Upnp.g by ANTLR 4.6
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({ "all", "warnings", "unchecked", "unused", "cast" })
public class HelloParser {
	private String sql;
	public static void main(String[] args) {
		String query = 
				"(upnp:class = \"object.container.album.musicAlbum\" and dc:title contains \"cap\")";
//				"(upnp:class derivedfrom \"object.item.audioItem.musicTrack\" and (upnp:genre contains \"FindThis\"))";
		HelloParser parser = new HelloParser(query);
	}
	
	public HelloParser(String query) {
		ANTLRInputStream is = new ANTLRInputStream(query);
		UpnpLexer lexer = new UpnpLexer(is);
		TokenStream tokens = new CommonTokenStream(lexer);
		UpnpParser parser = new UpnpParser(tokens);

//		System.out.println(parser.searchCrit());
		UpnpVisitor<String> visitor = new UpnpVisitorImpl();
		setSql(visitor.visit(parser.searchCrit()));
		System.out.println(getSql());
	}

	public String getSql() {
		return sql;
	}

	private void setSql(String sql) {
		this.sql = sql;
	}
}

class UpnpVisitorImpl extends UpnpBaseVisitor<String> {
	@Override
	public String visitTerminal(TerminalNode node) {
//		System.out.println("T:" + node.getSymbol());
		String result = node.getText();
		switch (result) {
		case "(":
		case ")":
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
		return result;
	}
	
	@Override
	protected String aggregateResult(String aggregate, String nextResult) {
		StringBuilder result = new StringBuilder();
		if (aggregate != null)
			result.append(aggregate);
		if (nextResult != null)
			result.append(nextResult);
		return result.toString();
	}
}