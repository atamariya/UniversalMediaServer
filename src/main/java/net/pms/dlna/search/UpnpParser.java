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
		QUOTEDVAL=16;
	public static final int
		RULE_searchCrit = 0, RULE_searchExp = 1, RULE_relExp = 2;
	public static final String[] ruleNames = {
		"searchCrit", "searchExp", "relExp"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'('", "')'", null, null, null, null, null, "'exists'", null, null, 
		null, "'\t'", "' '", "'\"'", "'*'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, "WHITESPACE", "LOGOP", "BINOP", "RELOP", "STRINGOP", 
		"EXISTSOP", "BOOLVAL", "WCHAR", "PROPERTY", "HTAB", "SPACE", "DQUOTE", 
		"ASTERISK", "QUOTEDVAL"
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
			setState(8);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
			case PROPERTY:
				enterOuterAlt(_localctx, 1);
				{
				setState(6);
				searchExp(0);
				}
				break;
			case ASTERISK:
				enterOuterAlt(_localctx, 2);
				{
				setState(7);
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
			setState(28);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case PROPERTY:
				{
				setState(11);
				relExp();
				}
				break;
			case T__0:
				{
				setState(12);
				match(T__0);
				setState(16);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==WCHAR) {
					{
					{
					setState(13);
					match(WCHAR);
					}
					}
					setState(18);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(19);
				searchExp(0);
				setState(23);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==WCHAR) {
					{
					{
					setState(20);
					match(WCHAR);
					}
					}
					setState(25);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(26);
				match(T__1);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(45);
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
					setState(30);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(32); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(31);
						match(WCHAR);
						}
						}
						setState(34); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==WCHAR );
					setState(36);
					match(LOGOP);
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
					searchExp(3);
					}
					} 
				}
				setState(47);
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
		enterRule(_localctx, 4, RULE_relExp);
		int _la;
		try {
			setState(74);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(48);
				match(PROPERTY);
				setState(50); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(49);
					match(WCHAR);
					}
					}
					setState(52); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==WCHAR );
				setState(54);
				match(BINOP);
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
				match(QUOTEDVAL);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(61);
				match(PROPERTY);
				setState(63); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(62);
					match(WCHAR);
					}
					}
					setState(65); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==WCHAR );
				setState(67);
				match(EXISTSOP);
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
		case 1:
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\22O\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\3\2\3\2\5\2\13\n\2\3\3\3\3\3\3\3\3\7\3\21\n\3\f\3\16\3\24\13"+
		"\3\3\3\3\3\7\3\30\n\3\f\3\16\3\33\13\3\3\3\3\3\5\3\37\n\3\3\3\3\3\6\3"+
		"#\n\3\r\3\16\3$\3\3\3\3\6\3)\n\3\r\3\16\3*\3\3\7\3.\n\3\f\3\16\3\61\13"+
		"\3\3\4\3\4\6\4\65\n\4\r\4\16\4\66\3\4\3\4\6\4;\n\4\r\4\16\4<\3\4\3\4\3"+
		"\4\6\4B\n\4\r\4\16\4C\3\4\3\4\6\4H\n\4\r\4\16\4I\3\4\5\4M\n\4\3\4\2\3"+
		"\4\5\2\4\6\2\2\2W\2\n\3\2\2\2\4\36\3\2\2\2\6L\3\2\2\2\b\13\5\4\3\2\t\13"+
		"\7\21\2\2\n\b\3\2\2\2\n\t\3\2\2\2\13\3\3\2\2\2\f\r\b\3\1\2\r\37\5\6\4"+
		"\2\16\22\7\3\2\2\17\21\7\f\2\2\20\17\3\2\2\2\21\24\3\2\2\2\22\20\3\2\2"+
		"\2\22\23\3\2\2\2\23\25\3\2\2\2\24\22\3\2\2\2\25\31\5\4\3\2\26\30\7\f\2"+
		"\2\27\26\3\2\2\2\30\33\3\2\2\2\31\27\3\2\2\2\31\32\3\2\2\2\32\34\3\2\2"+
		"\2\33\31\3\2\2\2\34\35\7\4\2\2\35\37\3\2\2\2\36\f\3\2\2\2\36\16\3\2\2"+
		"\2\37/\3\2\2\2 \"\f\4\2\2!#\7\f\2\2\"!\3\2\2\2#$\3\2\2\2$\"\3\2\2\2$%"+
		"\3\2\2\2%&\3\2\2\2&(\7\6\2\2\')\7\f\2\2(\'\3\2\2\2)*\3\2\2\2*(\3\2\2\2"+
		"*+\3\2\2\2+,\3\2\2\2,.\5\4\3\5- \3\2\2\2.\61\3\2\2\2/-\3\2\2\2/\60\3\2"+
		"\2\2\60\5\3\2\2\2\61/\3\2\2\2\62\64\7\r\2\2\63\65\7\f\2\2\64\63\3\2\2"+
		"\2\65\66\3\2\2\2\66\64\3\2\2\2\66\67\3\2\2\2\678\3\2\2\28:\7\7\2\29;\7"+
		"\f\2\2:9\3\2\2\2;<\3\2\2\2<:\3\2\2\2<=\3\2\2\2=>\3\2\2\2>M\7\22\2\2?A"+
		"\7\r\2\2@B\7\f\2\2A@\3\2\2\2BC\3\2\2\2CA\3\2\2\2CD\3\2\2\2DE\3\2\2\2E"+
		"G\7\n\2\2FH\7\f\2\2GF\3\2\2\2HI\3\2\2\2IG\3\2\2\2IJ\3\2\2\2JK\3\2\2\2"+
		"KM\7\13\2\2L\62\3\2\2\2L?\3\2\2\2M\7\3\2\2\2\16\n\22\31\36$*/\66<CIL";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}