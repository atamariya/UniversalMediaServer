package net.pms.dlna.search;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.pms.dlna.search.UpnpParser.RelExpContext;
import net.pms.dlna.search.UpnpParser.SearchExpContext;

public class UpnpSearchParser {
	private static final Logger LOGGER = LoggerFactory.getLogger(UpnpSearchParser.class);
	private String query;
	private List<String> objects;
	
	public UpnpSearchParser(String query) {
		CharStream is = CharStreams.fromString(StringEscapeUtils.unescapeXml(query));
		UpnpLexer lexer = new UpnpLexer(is);
		TokenStream tokens = new CommonTokenStream(lexer);
		UpnpParser parser = new UpnpParser(tokens);

		LOGGER.trace(query);
		UpnpVisitorImpl visitor = new UpnpVisitorImpl();
		this.query = visitor.visit(parser.searchCrit());
		this.objects = visitor.objects;
//		System.out.println(this.query);
//		System.out.println(this.objects);
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public List<String> getObjects() {
		return objects;
	}

	public void setObjects(List<String> objects) {
		this.objects = objects;
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
			
			/*  Control points may use the existence of the @refID property to distinguish
				between a referenced item and all of the reference items that point to it.
				Hence an exception below.
			*/
			if (ctx.EXISTSOP() != null && "false".equalsIgnoreCase(ctx.BOOLVAL().getText())
					&& !property.equalsIgnoreCase("@refID")){
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
