package net.pms.dlna.search;

// Generated from Upnp.g4 by ANTLR 4.7
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link UpnpParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface UpnpVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link UpnpParser#searchCrit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSearchCrit(UpnpParser.SearchCritContext ctx);
	/**
	 * Visit a parse tree produced by {@link UpnpParser#searchExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSearchExp(UpnpParser.SearchExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link UpnpParser#relExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelExp(UpnpParser.RelExpContext ctx);
}