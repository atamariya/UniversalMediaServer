package net.pms.dlna.search;

// Generated from Upnp.g4 by ANTLR 4.7
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link UpnpParser}.
 */
public interface UpnpListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link UpnpParser#searchCrit}.
	 * @param ctx the parse tree
	 */
	void enterSearchCrit(UpnpParser.SearchCritContext ctx);
	/**
	 * Exit a parse tree produced by {@link UpnpParser#searchCrit}.
	 * @param ctx the parse tree
	 */
	void exitSearchCrit(UpnpParser.SearchCritContext ctx);
	/**
	 * Enter a parse tree produced by {@link UpnpParser#searchExp}.
	 * @param ctx the parse tree
	 */
	void enterSearchExp(UpnpParser.SearchExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link UpnpParser#searchExp}.
	 * @param ctx the parse tree
	 */
	void exitSearchExp(UpnpParser.SearchExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link UpnpParser#relExp}.
	 * @param ctx the parse tree
	 */
	void enterRelExp(UpnpParser.RelExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link UpnpParser#relExp}.
	 * @param ctx the parse tree
	 */
	void exitRelExp(UpnpParser.RelExpContext ctx);
}