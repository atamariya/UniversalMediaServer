package net.pms.dlna.search;

// Generated from Upnp.g4 by ANTLR 4.7
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class UpnpParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, WHITESPACE=3, LOGOP=4, BINOP=5, RELOP=6, STRINGOP=7, EXISTSOP=8, 
		BOOLVAL=9, WCHAR=10, PROPERTY=11, HTAB=12, SPACE=13, DQUOTE=14, ASTERISK=15, 
		STRING_LITERAL=16;
	public static final int
		RULE_searchCrit = 0, RULE_searchExp = 1, RULE_relExp = 2, RULE_quotedVal = 3, 
		RULE_escapedQuote = 4;
	public static final String[] ruleNames = {
		"searchCrit", "searchExp", "relExp", "quotedVal", "escapedQuote"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'('", "')'", null, null, null, null, null, "'exists'", null, null, 
		null, "'\t'", "' '", "'\"'", "'*'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, "WHITESPACE", "LOGOP", "BINOP", "RELOP", "STRINGOP", 
		"EXISTSOP", "BOOLVAL", "WCHAR", "PROPERTY", "HTAB", "SPACE", "DQUOTE", 
		"ASTERISK", "STRING_LITERAL"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Upnp.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public UpnpParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class SearchCritContext extends ParserRuleContext {
		public SearchExpContext searchExp() {
			return getRuleContext(SearchExpContext.class,0);
		}
		public TerminalNode ASTERISK() { return getToken(UpnpParser.ASTERISK, 0); }
		public SearchCritContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_searchCrit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UpnpListener ) ((UpnpListener)listener).enterSearchCrit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UpnpListener ) ((UpnpListener)listener).exitSearchCrit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UpnpVisitor ) return ((UpnpVisitor<? extends T>)visitor).visitSearchCrit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SearchCritContext searchCrit() throws RecognitionException {
		SearchCritContext _localctx = new SearchCritContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_searchCrit);
		try {
			setState(12);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
			case PROPERTY:
				enterOuterAlt(_localctx, 1);
				{
				setState(10);
				searchExp(0);
				}
				break;
			case ASTERISK:
				enterOuterAlt(_localctx, 2);
				{
				setState(11);
				match(ASTERISK);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SearchExpContext extends ParserRuleContext {
		public RelExpContext relExp() {
			return getRuleContext(RelExpContext.class,0);
		}
		public List<SearchExpContext> searchExp() {
			return getRuleContexts(SearchExpContext.class);
		}
		public SearchExpContext searchExp(int i) {
			return getRuleContext(SearchExpContext.class,i);
		}
		public List<TerminalNode> WCHAR() { return getTokens(UpnpParser.WCHAR); }
		public TerminalNode WCHAR(int i) {
			return getToken(UpnpParser.WCHAR, i);
		}
		public TerminalNode LOGOP() { return getToken(UpnpParser.LOGOP, 0); }
		public SearchExpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_searchExp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UpnpListener ) ((UpnpListener)listener).enterSearchExp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UpnpListener ) ((UpnpListener)listener).exitSearchExp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UpnpVisitor ) return ((UpnpVisitor<? extends T>)visitor).visitSearchExp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SearchExpContext searchExp() throws RecognitionException {
		return searchExp(0);
	}

	private SearchExpContext searchExp(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		SearchExpContext _localctx = new SearchExpContext(_ctx, _parentState);
		SearchExpContext _prevctx = _localctx;
		int _startState = 2;
		enterRecursionRule(_localctx, 2, RULE_searchExp, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(32);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case PROPERTY:
				{
				setState(15);
				relExp();
				}
				break;
			case T__0:
				{
				setState(16);
				match(T__0);
				setState(20);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==WCHAR) {
					{
					{
					setState(17);
					match(WCHAR);
					}
					}
					setState(22);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(23);
				searchExp(0);
				setState(27);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==WCHAR) {
					{
					{
					setState(24);
					match(WCHAR);
					}
					}
					setState(29);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(30);
				match(T__1);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(49);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new SearchExpContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_searchExp);
					setState(34);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(36); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(35);
						match(WCHAR);
						}
						}
						setState(38); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==WCHAR );
					setState(40);
					match(LOGOP);
					setState(42); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(41);
						match(WCHAR);
						}
						}
						setState(44); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==WCHAR );
					setState(46);
					searchExp(3);
					}
					} 
				}
				setState(51);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class RelExpContext extends ParserRuleContext {
		public TerminalNode PROPERTY() { return getToken(UpnpParser.PROPERTY, 0); }
		public TerminalNode BINOP() { return getToken(UpnpParser.BINOP, 0); }
		public QuotedValContext quotedVal() {
			return getRuleContext(QuotedValContext.class,0);
		}
		public List<TerminalNode> WCHAR() { return getTokens(UpnpParser.WCHAR); }
		public TerminalNode WCHAR(int i) {
			return getToken(UpnpParser.WCHAR, i);
		}
		public TerminalNode EXISTSOP() { return getToken(UpnpParser.EXISTSOP, 0); }
		public TerminalNode BOOLVAL() { return getToken(UpnpParser.BOOLVAL, 0); }
		public RelExpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relExp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UpnpListener ) ((UpnpListener)listener).enterRelExp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UpnpListener ) ((UpnpListener)listener).exitRelExp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UpnpVisitor ) return ((UpnpVisitor<? extends T>)visitor).visitRelExp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RelExpContext relExp() throws RecognitionException {
		RelExpContext _localctx = new RelExpContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_relExp);
		int _la;
		try {
			setState(78);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(52);
				match(PROPERTY);
				setState(54); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(53);
					match(WCHAR);
					}
					}
					setState(56); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==WCHAR );
				setState(58);
				match(BINOP);
				setState(60); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(59);
					match(WCHAR);
					}
					}
					setState(62); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==WCHAR );
				setState(64);
				quotedVal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(65);
				match(PROPERTY);
				setState(67); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(66);
					match(WCHAR);
					}
					}
					setState(69); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==WCHAR );
				setState(71);
				match(EXISTSOP);
				setState(73); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(72);
					match(WCHAR);
					}
					}
					setState(75); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==WCHAR );
				setState(77);
				match(BOOLVAL);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class QuotedValContext extends ParserRuleContext {
		public List<TerminalNode> DQUOTE() { return getTokens(UpnpParser.DQUOTE); }
		public TerminalNode DQUOTE(int i) {
			return getToken(UpnpParser.DQUOTE, i);
		}
		public EscapedQuoteContext escapedQuote() {
			return getRuleContext(EscapedQuoteContext.class,0);
		}
		public QuotedValContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_quotedVal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UpnpListener ) ((UpnpListener)listener).enterQuotedVal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UpnpListener ) ((UpnpListener)listener).exitQuotedVal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UpnpVisitor ) return ((UpnpVisitor<? extends T>)visitor).visitQuotedVal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final QuotedValContext quotedVal() throws RecognitionException {
		QuotedValContext _localctx = new QuotedValContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_quotedVal);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(80);
			match(DQUOTE);
			setState(81);
			escapedQuote(0);
			setState(82);
			match(DQUOTE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EscapedQuoteContext extends ParserRuleContext {
		public List<TerminalNode> STRING_LITERAL() { return getTokens(UpnpParser.STRING_LITERAL); }
		public TerminalNode STRING_LITERAL(int i) {
			return getToken(UpnpParser.STRING_LITERAL, i);
		}
		public List<EscapedQuoteContext> escapedQuote() {
			return getRuleContexts(EscapedQuoteContext.class);
		}
		public EscapedQuoteContext escapedQuote(int i) {
			return getRuleContext(EscapedQuoteContext.class,i);
		}
		public List<TerminalNode> WCHAR() { return getTokens(UpnpParser.WCHAR); }
		public TerminalNode WCHAR(int i) {
			return getToken(UpnpParser.WCHAR, i);
		}
		public EscapedQuoteContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_escapedQuote; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UpnpListener ) ((UpnpListener)listener).enterEscapedQuote(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UpnpListener ) ((UpnpListener)listener).exitEscapedQuote(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UpnpVisitor ) return ((UpnpVisitor<? extends T>)visitor).visitEscapedQuote(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EscapedQuoteContext escapedQuote() throws RecognitionException {
		return escapedQuote(0);
	}

	private EscapedQuoteContext escapedQuote(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		EscapedQuoteContext _localctx = new EscapedQuoteContext(_ctx, _parentState);
		EscapedQuoteContext _prevctx = _localctx;
		int _startState = 8;
		enterRecursionRule(_localctx, 8, RULE_escapedQuote, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(86); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(85);
					match(STRING_LITERAL);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(88); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			}
			_ctx.stop = _input.LT(-1);
			setState(100);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new EscapedQuoteContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_escapedQuote);
					setState(90);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(94);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==WCHAR) {
						{
						{
						setState(91);
						match(WCHAR);
						}
						}
						setState(96);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(97);
					escapedQuote(2);
					}
					} 
				}
				setState(102);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 1:
			return searchExp_sempred((SearchExpContext)_localctx, predIndex);
		case 4:
			return escapedQuote_sempred((EscapedQuoteContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean searchExp_sempred(SearchExpContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 2);
		}
		return true;
	}
	private boolean escapedQuote_sempred(EscapedQuoteContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\22j\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\3\2\3\2\5\2\17\n\2\3\3\3\3\3\3\3\3\7\3\25\n"+
		"\3\f\3\16\3\30\13\3\3\3\3\3\7\3\34\n\3\f\3\16\3\37\13\3\3\3\3\3\5\3#\n"+
		"\3\3\3\3\3\6\3\'\n\3\r\3\16\3(\3\3\3\3\6\3-\n\3\r\3\16\3.\3\3\7\3\62\n"+
		"\3\f\3\16\3\65\13\3\3\4\3\4\6\49\n\4\r\4\16\4:\3\4\3\4\6\4?\n\4\r\4\16"+
		"\4@\3\4\3\4\3\4\6\4F\n\4\r\4\16\4G\3\4\3\4\6\4L\n\4\r\4\16\4M\3\4\5\4"+
		"Q\n\4\3\5\3\5\3\5\3\5\3\6\3\6\6\6Y\n\6\r\6\16\6Z\3\6\3\6\7\6_\n\6\f\6"+
		"\16\6b\13\6\3\6\7\6e\n\6\f\6\16\6h\13\6\3\6\2\4\4\n\7\2\4\6\b\n\2\2\2"+
		"s\2\16\3\2\2\2\4\"\3\2\2\2\6P\3\2\2\2\bR\3\2\2\2\nV\3\2\2\2\f\17\5\4\3"+
		"\2\r\17\7\21\2\2\16\f\3\2\2\2\16\r\3\2\2\2\17\3\3\2\2\2\20\21\b\3\1\2"+
		"\21#\5\6\4\2\22\26\7\3\2\2\23\25\7\f\2\2\24\23\3\2\2\2\25\30\3\2\2\2\26"+
		"\24\3\2\2\2\26\27\3\2\2\2\27\31\3\2\2\2\30\26\3\2\2\2\31\35\5\4\3\2\32"+
		"\34\7\f\2\2\33\32\3\2\2\2\34\37\3\2\2\2\35\33\3\2\2\2\35\36\3\2\2\2\36"+
		" \3\2\2\2\37\35\3\2\2\2 !\7\4\2\2!#\3\2\2\2\"\20\3\2\2\2\"\22\3\2\2\2"+
		"#\63\3\2\2\2$&\f\4\2\2%\'\7\f\2\2&%\3\2\2\2\'(\3\2\2\2(&\3\2\2\2()\3\2"+
		"\2\2)*\3\2\2\2*,\7\6\2\2+-\7\f\2\2,+\3\2\2\2-.\3\2\2\2.,\3\2\2\2./\3\2"+
		"\2\2/\60\3\2\2\2\60\62\5\4\3\5\61$\3\2\2\2\62\65\3\2\2\2\63\61\3\2\2\2"+
		"\63\64\3\2\2\2\64\5\3\2\2\2\65\63\3\2\2\2\668\7\r\2\2\679\7\f\2\28\67"+
		"\3\2\2\29:\3\2\2\2:8\3\2\2\2:;\3\2\2\2;<\3\2\2\2<>\7\7\2\2=?\7\f\2\2>"+
		"=\3\2\2\2?@\3\2\2\2@>\3\2\2\2@A\3\2\2\2AB\3\2\2\2BQ\5\b\5\2CE\7\r\2\2"+
		"DF\7\f\2\2ED\3\2\2\2FG\3\2\2\2GE\3\2\2\2GH\3\2\2\2HI\3\2\2\2IK\7\n\2\2"+
		"JL\7\f\2\2KJ\3\2\2\2LM\3\2\2\2MK\3\2\2\2MN\3\2\2\2NO\3\2\2\2OQ\7\13\2"+
		"\2P\66\3\2\2\2PC\3\2\2\2Q\7\3\2\2\2RS\7\20\2\2ST\5\n\6\2TU\7\20\2\2U\t"+
		"\3\2\2\2VX\b\6\1\2WY\7\22\2\2XW\3\2\2\2YZ\3\2\2\2ZX\3\2\2\2Z[\3\2\2\2"+
		"[f\3\2\2\2\\`\f\3\2\2]_\7\f\2\2^]\3\2\2\2_b\3\2\2\2`^\3\2\2\2`a\3\2\2"+
		"\2ac\3\2\2\2b`\3\2\2\2ce\5\n\6\4d\\\3\2\2\2eh\3\2\2\2fd\3\2\2\2fg\3\2"+
		"\2\2g\13\3\2\2\2hf\3\2\2\2\21\16\26\35\"(.\63:@GMPZ`f";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}