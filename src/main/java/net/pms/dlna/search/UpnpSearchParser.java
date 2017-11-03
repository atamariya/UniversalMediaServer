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
		
		System.out.println("7");
		query =
				"dc:title contains \"cap\" and (upnp:class = \"object.container.album.musicAlbum\")";
		parser = new UpnpSearchParser(query);
		
//		System.out.println("8");
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
	
	@Override
	public String visitSearchExp(SearchExpContext ctx) {
//		System.out.println("S:" + ctx.getText());
		TerminalNode node = ctx.LOGOP();
		if (node != null) {
			// If query result is DIDLObject, avoid the logical operator
			String left = super.visitSearchExp(ctx.searchExp(0));
//			System.out.println("1:" + left);
			
			String right = super.visitSearchExp(ctx.searchExp(1));
//			System.out.println("2:" + right);

			StringBuilder query = new StringBuilder();
			if (left != null && right != null) {
				query.append(left);
				query.append(" ");
				query.append(node.getText());
				query.append(" ");
				query.append(right);
			} else if (left != null)
				query.append(left);
			else if (right != null)
				query.append(right);

			return query.toString();
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
					result = ctx.getText();
				}
			}
		}
		return result;
	}
	@Override
	public String visitTerminal(TerminalNode node) {
//		System.out.println("T:" + node.getSymbol());
		String result = node.getText();
		return result;
	}
	
	@Override
	protected String aggregateResult(String aggregate, String nextResult) {
		StringBuilder result = new StringBuilder();
		if (aggregate != null)
			result.append(aggregate);
		if (nextResult != null)
			result.append(nextResult);
		if ("()".equals(result.toString()))
			result.setLength(0);
		return result.length() > 0 ? result.toString(): null;
	}
	
}
