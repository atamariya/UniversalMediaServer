package net.pms.dlna.search;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.pms.dlna.search.UpnpParser.RelExpContext;
import net.pms.dlna.search.UpnpParser.SearchExpContext;

public class UpnpSearchParser {
	private static final Logger LOGGER = LoggerFactory.getLogger(UpnpSearchParser.class);
	private String sql;
	
	public static void main(String[] args) {
		System.out.println(UpnpObjectUtil.getDerivedChildren("object.item.audioItem"));
		System.out.println(UpnpObjectUtil.getParent("object.container.album"));
		System.out.println(UpnpObjectUtil.getParent("object"));
		System.out.println(UpnpObjectUtil.hasAttribute("object.item.videoItem", "dc:title"));
		System.out.println(UpnpObjectUtil.hasAttribute("object.container.album.musicAlbum", "upnp:album"));
		
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
				"((upnp:class derivedfrom \"object.container.album\" and (upnp:genre contains \"FindThis\")) and dc:title contains \"cap\")";
		parser = new UpnpSearchParser(query);

		System.out.println("5");
		query =
				"upnp:album exists false";
		parser = new UpnpSearchParser(query);

		System.out.println("6");
		query =
				"upnp:album exists true";
		parser = new UpnpSearchParser(query);
//		
//		System.out.println("7");
//		query =
//				"((upnp:class = \"object.item.audioItem.musicTrack\" and dc:title contains \"cap\") or " + 
//				"(dc:title contains \"cap\" and upnp:class = \"object.container.album.musicAlbum\"))";
//		parser = new UpnpSearchParser(query);
	
	}
	
	public UpnpSearchParser(String query) {
		CharStream is = CharStreams.fromString(query);
		UpnpLexer lexer = new UpnpLexer(is);
		TokenStream tokens = new CommonTokenStream(lexer);
		UpnpParser parser = new UpnpParser(tokens);

		LOGGER.trace(query);
		UpnpVisitorImpl visitor = new UpnpVisitorImpl();
		System.out.println(visitor.visit(parser.searchCrit()));
		System.out.println(visitor.objects);
	}

	public String getSql() {
		return sql;
	}

	private void setSql(String sql) {
		this.sql = sql;
	}
}

class UpnpVisitorImpl extends UpnpBaseVisitor<String> {
	List<String> objects;
	StringBuilder query = new StringBuilder();
	boolean append = false;
	
	@Override
	public String visitSearchExp(SearchExpContext ctx) {
//		System.out.println("S:" + ctx.getText());
		TerminalNode node = ctx.LOGOP();
		if (node != null) {
			// If DIDLObject is not null, avoid the logical operator
			super.visitSearchExp(ctx.searchExp(0));
//			System.out.println("1:" + s);
			
			if (append) {
				query.append(" ");
				query.append(node.getText());
				query.append(" ");
			} else {
				append = true;
			}

			super.visitSearchExp(ctx.searchExp(1));

			return null;
		}
		return super.visitSearchExp(ctx);
	}
		
	public String visitRelExp(RelExpContext ctx) {
//		System.out.println("R:" + ctx.getText());
	
		String result = null;
		String property = ctx.PROPERTY().getText();
		if (property.equals("upnp:class")) {
			String op = ctx.BINOP().getText();
			String node = ctx.QUOTEDVAL().getText();
			if (node.startsWith("\""))
				node = node.replaceAll("\"", "");
			
			List<String> children = null;
			if ("derivedfrom".equals(op)) {
				children = UpnpObjectUtil.getDerivedChildren(node);
			} else if ("=".equals(op)) {
				children = new ArrayList<>();
				children.add(node);
			} else if ("!=".equals(op)) {
				children = UpnpObjectUtil.getDerivedChildren("object");
				children.remove(node);
			}
			
			if (objects != null)
				objects.retainAll(children);
			else
				objects = children;
			append = false;
		} else {
			if (objects == null)
				objects = UpnpObjectUtil.getDerivedChildren("object");
			
			if (ctx.EXISTSOP() != null && "false".equalsIgnoreCase(ctx.BOOLVAL().getText())){
				List<String> children = UpnpObjectUtil.getDerivedChildren("object");
				UpnpObjectUtil.filterByAttribute(children, property);
				objects.removeAll(children);
			} else {
				UpnpObjectUtil.filterByAttribute(objects, property);
				// If no object has the requested attribute, the query should return null
				if (ctx.EXISTSOP() == null && objects.size() > 0) {
					query.append(ctx.getText());
				}
			}
		}
		return result;
	}
	@Override
	public String visitTerminal(TerminalNode node) {
//		System.out.println("T:" + node.getSymbol());
		String result = node.getText();
		query.append(result);
		return result;
	}
	
	@Override
	protected String aggregateResult(String aggregate, String nextResult) {
		return query.toString();
	}
	
}
