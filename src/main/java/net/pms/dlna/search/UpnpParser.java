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
		T__0=1, T__1=2, T__2=3, NUMBER=4, WHITESPACE=5, LOGOP=6, BINOP=7, RELOP=8, 
		STRINGOP=9, EXISTSOP=10, BOOLVAL=11, QUOTEDVAL=12, WCHAR=13, PROPERTY=14, 
		HTAB=15, SPACE=16, DQUOTE=17, ASTERISK=18, STRING_LITERAL=19;
	public static final int
		RULE_operation = 0, RULE_searchCrit = 1, RULE_searchExp = 2, RULE_relExp = 3;
	public static final String[] ruleNames = {
		"operation", "searchCrit", "searchExp", "relExp"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'+'", "'('", "')'", null, null, null, null, null, null, "'exists'", 
		null, null, null, null, "'\t'", "' '", "'\"'", "'*'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, "NUMBER", "WHITESPACE", "LOGOP", "BINOP", "RELOP", 
		"STRINGOP", "EXISTSOP", "BOOLVAL", "QUOTEDVAL", "WCHAR", "PROPERTY", "HTAB", 
		"SPACE", "DQUOTE", "ASTERISK", "STRING_LITERAL"
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
	public static class OperationContext extends ParserRuleContext {
		public List<TerminalNode> NUMBER() { return getTokens(UpnpParser.NUMBER); }
		public TerminalNode NUMBER(int i) {
			return getToken(UpnpParser.NUMBER, i);
		}
		public OperationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof UpnpListener ) ((UpnpListener)listener).enterOperation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof UpnpListener ) ((UpnpListener)listener).exitOperation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof UpnpVisitor ) return ((UpnpVisitor<? extends T>)visitor).visitOperation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OperationContext operation() throws RecognitionException {
		OperationContext _localctx = new OperationContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_operation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(8);
			match(NUMBER);
			setState(9);
			match(T__0);
			setState(10);
			match(NUMBER);
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
		enterRule(_localctx, 2, RULE_searchCrit);
		try {
			setState(14);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__1:
			case PROPERTY:
				enterOuterAlt(_localctx, 1);
				{
				setState(12);
				searchExp(0);
				}
				break;
			case ASTERISK:
				enterOuterAlt(_localctx, 2);
				{
				setState(13);
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
		int _startState = 4;
		enterRecursionRule(_localctx, 4, RULE_searchExp, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(34);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case PROPERTY:
				{
				setState(17);
				relExp();
				}
				break;
			case T__1:
				{
				setState(18);
				match(T__1);
				setState(22);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==WCHAR) {
					{
					{
					setState(19);
					match(WCHAR);
					}
					}
					setState(24);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(25);
				searchExp(0);
				setState(29);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==WCHAR) {
					{
					{
					setState(26);
					match(WCHAR);
					}
					}
					setState(31);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(32);
				match(T__2);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(51);
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
					setState(36);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(38); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(37);
						match(WCHAR);
						}
						}
						setState(40); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==WCHAR );
					setState(42);
					match(LOGOP);
					setState(44); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(43);
						match(WCHAR);
						}
						}
						setState(46); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==WCHAR );
					setState(48);
					searchExp(3);
					}
					} 
				}
				setState(53);
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
		public TerminalNode QUOTEDVAL() { return getToken(UpnpParser.QUOTEDVAL, 0); }
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
		enterRule(_localctx, 6, RULE_relExp);
		int _la;
		try {
			setState(80);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(54);
				match(PROPERTY);
				setState(56); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(55);
					match(WCHAR);
					}
					}
					setState(58); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==WCHAR );
				setState(60);
				match(BINOP);
				setState(62); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(61);
					match(WCHAR);
					}
					}
					setState(64); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==WCHAR );
				setState(66);
				match(QUOTEDVAL);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(67);
				match(PROPERTY);
				setState(69); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(68);
					match(WCHAR);
					}
					}
					setState(71); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==WCHAR );
				setState(73);
				match(EXISTSOP);
				setState(75); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(74);
					match(WCHAR);
					}
					}
					setState(77); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==WCHAR );
				setState(79);
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

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 2:
			return searchExp_sempred((SearchExpContext)_localctx, predIndex);
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

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\25U\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\3\2\3\2\3\2\3\2\3\3\3\3\5\3\21\n\3\3\4\3\4\3\4\3\4"+
		"\7\4\27\n\4\f\4\16\4\32\13\4\3\4\3\4\7\4\36\n\4\f\4\16\4!\13\4\3\4\3\4"+
		"\5\4%\n\4\3\4\3\4\6\4)\n\4\r\4\16\4*\3\4\3\4\6\4/\n\4\r\4\16\4\60\3\4"+
		"\7\4\64\n\4\f\4\16\4\67\13\4\3\5\3\5\6\5;\n\5\r\5\16\5<\3\5\3\5\6\5A\n"+
		"\5\r\5\16\5B\3\5\3\5\3\5\6\5H\n\5\r\5\16\5I\3\5\3\5\6\5N\n\5\r\5\16\5"+
		"O\3\5\5\5S\n\5\3\5\2\3\6\6\2\4\6\b\2\2\2\\\2\n\3\2\2\2\4\20\3\2\2\2\6"+
		"$\3\2\2\2\bR\3\2\2\2\n\13\7\6\2\2\13\f\7\3\2\2\f\r\7\6\2\2\r\3\3\2\2\2"+
		"\16\21\5\6\4\2\17\21\7\24\2\2\20\16\3\2\2\2\20\17\3\2\2\2\21\5\3\2\2\2"+
		"\22\23\b\4\1\2\23%\5\b\5\2\24\30\7\4\2\2\25\27\7\17\2\2\26\25\3\2\2\2"+
		"\27\32\3\2\2\2\30\26\3\2\2\2\30\31\3\2\2\2\31\33\3\2\2\2\32\30\3\2\2\2"+
		"\33\37\5\6\4\2\34\36\7\17\2\2\35\34\3\2\2\2\36!\3\2\2\2\37\35\3\2\2\2"+
		"\37 \3\2\2\2 \"\3\2\2\2!\37\3\2\2\2\"#\7\5\2\2#%\3\2\2\2$\22\3\2\2\2$"+
		"\24\3\2\2\2%\65\3\2\2\2&(\f\4\2\2\')\7\17\2\2(\'\3\2\2\2)*\3\2\2\2*(\3"+
		"\2\2\2*+\3\2\2\2+,\3\2\2\2,.\7\b\2\2-/\7\17\2\2.-\3\2\2\2/\60\3\2\2\2"+
		"\60.\3\2\2\2\60\61\3\2\2\2\61\62\3\2\2\2\62\64\5\6\4\5\63&\3\2\2\2\64"+
		"\67\3\2\2\2\65\63\3\2\2\2\65\66\3\2\2\2\66\7\3\2\2\2\67\65\3\2\2\28:\7"+
		"\20\2\29;\7\17\2\2:9\3\2\2\2;<\3\2\2\2<:\3\2\2\2<=\3\2\2\2=>\3\2\2\2>"+
		"@\7\t\2\2?A\7\17\2\2@?\3\2\2\2AB\3\2\2\2B@\3\2\2\2BC\3\2\2\2CD\3\2\2\2"+
		"DS\7\16\2\2EG\7\20\2\2FH\7\17\2\2GF\3\2\2\2HI\3\2\2\2IG\3\2\2\2IJ\3\2"+
		"\2\2JK\3\2\2\2KM\7\f\2\2LN\7\17\2\2ML\3\2\2\2NO\3\2\2\2OM\3\2\2\2OP\3"+
		"\2\2\2PQ\3\2\2\2QS\7\r\2\2R8\3\2\2\2RE\3\2\2\2S\t\3\2\2\2\16\20\30\37"+
		"$*\60\65<BIOR";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}