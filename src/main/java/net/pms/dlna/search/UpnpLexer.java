package net.pms.dlna.search;

// Generated from Upnp.g4 by ANTLR 4.7
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class UpnpLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, WHITESPACE=3, LOGOP=4, BINOP=5, RELOP=6, STRINGOP=7, EXISTSOP=8, 
		BOOLVAL=9, WCHAR=10, PROPERTY=11, HTAB=12, SPACE=13, DQUOTE=14, ASTERISK=15, 
		STRING_LITERAL=16;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "WHITESPACE", "LOGOP", "BINOP", "RELOP", "STRINGOP", "EXISTSOP", 
		"BOOLVAL", "WCHAR", "PROPERTY", "HTAB", "SPACE", "DQUOTE", "ASTERISK", 
		"STRING_LITERAL"
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


	public UpnpLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Upnp.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\22\u01b7\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\3\2\3"+
		"\2\3\3\3\3\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\5\5\61\n\5\3\6\3\6\5\6"+
		"\65\n\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7@\n\7\3\b\3\b\3\b\3\b\3"+
		"\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b"+
		"\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5\bc\n\b\3\t\3\t\3\t\3\t"+
		"\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\5\nu\n\n\3\13\3\13\5"+
		"\13y\n\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f"+
		"\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3"+
		"\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f"+
		"\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3"+
		"\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f"+
		"\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3"+
		"\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f"+
		"\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3"+
		"\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f"+
		"\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3"+
		"\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f"+
		"\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3"+
		"\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f"+
		"\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3"+
		"\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f"+
		"\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3"+
		"\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f"+
		"\3\f\3\f\3\f\3\f\3\f\3\f\3\f\5\f\u01a9\n\f\3\r\3\r\3\16\3\16\3\17\3\17"+
		"\3\20\3\20\3\21\3\21\3\21\5\21\u01b6\n\21\2\2\22\3\3\5\4\7\5\t\6\13\7"+
		"\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22\3\2\4\4\2\13"+
		"\f\17\17\6\2\60\60\62;C\\c|\2\u01db\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2"+
		"\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23"+
		"\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2"+
		"\2\2\2\37\3\2\2\2\2!\3\2\2\2\3#\3\2\2\2\5%\3\2\2\2\7\'\3\2\2\2\t\60\3"+
		"\2\2\2\13\64\3\2\2\2\r?\3\2\2\2\17b\3\2\2\2\21d\3\2\2\2\23t\3\2\2\2\25"+
		"x\3\2\2\2\27\u01a8\3\2\2\2\31\u01aa\3\2\2\2\33\u01ac\3\2\2\2\35\u01ae"+
		"\3\2\2\2\37\u01b0\3\2\2\2!\u01b5\3\2\2\2#$\7*\2\2$\4\3\2\2\2%&\7+\2\2"+
		"&\6\3\2\2\2\'(\t\2\2\2()\3\2\2\2)*\b\4\2\2*\b\3\2\2\2+,\7c\2\2,-\7p\2"+
		"\2-\61\7f\2\2./\7q\2\2/\61\7t\2\2\60+\3\2\2\2\60.\3\2\2\2\61\n\3\2\2\2"+
		"\62\65\5\r\7\2\63\65\5\17\b\2\64\62\3\2\2\2\64\63\3\2\2\2\65\f\3\2\2\2"+
		"\66@\7?\2\2\678\7#\2\28@\7?\2\29@\7>\2\2:;\7>\2\2;@\7?\2\2<@\7@\2\2=>"+
		"\7@\2\2>@\7?\2\2?\66\3\2\2\2?\67\3\2\2\2?9\3\2\2\2?:\3\2\2\2?<\3\2\2\2"+
		"?=\3\2\2\2@\16\3\2\2\2AB\7e\2\2BC\7q\2\2CD\7p\2\2DE\7v\2\2EF\7c\2\2FG"+
		"\7k\2\2GH\7p\2\2Hc\7u\2\2IJ\7f\2\2JK\7q\2\2KL\7g\2\2LM\7u\2\2MN\7p\2\2"+
		"NO\7q\2\2OP\7v\2\2PQ\7e\2\2QR\7q\2\2RS\7p\2\2ST\7v\2\2TU\7c\2\2UV\7k\2"+
		"\2Vc\7p\2\2WX\7f\2\2XY\7g\2\2YZ\7t\2\2Z[\7k\2\2[\\\7x\2\2\\]\7g\2\2]^"+
		"\7f\2\2^_\7h\2\2_`\7t\2\2`a\7q\2\2ac\7o\2\2bA\3\2\2\2bI\3\2\2\2bW\3\2"+
		"\2\2c\20\3\2\2\2de\7g\2\2ef\7z\2\2fg\7k\2\2gh\7u\2\2hi\7v\2\2ij\7u\2\2"+
		"j\22\3\2\2\2kl\7v\2\2lm\7t\2\2mn\7w\2\2nu\7g\2\2op\7h\2\2pq\7c\2\2qr\7"+
		"n\2\2rs\7u\2\2su\7g\2\2tk\3\2\2\2to\3\2\2\2u\24\3\2\2\2vy\5\33\16\2wy"+
		"\5\31\r\2xv\3\2\2\2xw\3\2\2\2y\26\3\2\2\2z{\7t\2\2{|\7g\2\2|}\7u\2\2}"+
		"~\7B\2\2~\177\7t\2\2\177\u0080\7g\2\2\u0080\u0081\7u\2\2\u0081\u0082\7"+
		"q\2\2\u0082\u0083\7n\2\2\u0083\u0084\7w\2\2\u0084\u0085\7v\2\2\u0085\u0086"+
		"\7k\2\2\u0086\u0087\7q\2\2\u0087\u01a9\7p\2\2\u0088\u0089\7t\2\2\u0089"+
		"\u008a\7g\2\2\u008a\u008b\7u\2\2\u008b\u008c\7B\2\2\u008c\u008d\7f\2\2"+
		"\u008d\u008e\7w\2\2\u008e\u008f\7t\2\2\u008f\u0090\7c\2\2\u0090\u0091"+
		"\7v\2\2\u0091\u0092\7k\2\2\u0092\u0093\7q\2\2\u0093\u01a9\7p\2\2\u0094"+
		"\u0095\7f\2\2\u0095\u0096\7e\2\2\u0096\u0097\7<\2\2\u0097\u0098\7v\2\2"+
		"\u0098\u0099\7k\2\2\u0099\u009a\7v\2\2\u009a\u009b\7n\2\2\u009b\u01a9"+
		"\7g\2\2\u009c\u009d\7f\2\2\u009d\u009e\7e\2\2\u009e\u009f\7<\2\2\u009f"+
		"\u00a0\7e\2\2\u00a0\u00a1\7t\2\2\u00a1\u00a2\7g\2\2\u00a2\u00a3\7c\2\2"+
		"\u00a3\u00a4\7v\2\2\u00a4\u00a5\7q\2\2\u00a5\u01a9\7t\2\2\u00a6\u00a7"+
		"\7w\2\2\u00a7\u00a8\7r\2\2\u00a8\u00a9\7p\2\2\u00a9\u00aa\7r\2\2\u00aa"+
		"\u00ab\7<\2\2\u00ab\u00ac\7c\2\2\u00ac\u00ad\7e\2\2\u00ad\u00ae\7v\2\2"+
		"\u00ae\u00af\7q\2\2\u00af\u01a9\7t\2\2\u00b0\u00b1\7w\2\2\u00b1\u00b2"+
		"\7r\2\2\u00b2\u00b3\7p\2\2\u00b3\u00b4\7r\2\2\u00b4\u00b5\7<\2\2\u00b5"+
		"\u00b6\7c\2\2\u00b6\u00b7\7t\2\2\u00b7\u00b8\7v\2\2\u00b8\u00b9\7k\2\2"+
		"\u00b9\u00ba\7u\2\2\u00ba\u01a9\7v\2\2\u00bb\u00bc\7w\2\2\u00bc\u00bd"+
		"\7r\2\2\u00bd\u00be\7p\2\2\u00be\u00bf\7r\2\2\u00bf\u00c0\7<\2\2\u00c0"+
		"\u00c1\7i\2\2\u00c1\u00c2\7g\2\2\u00c2\u00c3\7p\2\2\u00c3\u00c4\7t\2\2"+
		"\u00c4\u01a9\7g\2\2\u00c5\u00c6\7w\2\2\u00c6\u00c7\7r\2\2\u00c7\u00c8"+
		"\7p\2\2\u00c8\u00c9\7r\2\2\u00c9\u00ca\7<\2\2\u00ca\u00cb\7c\2\2\u00cb"+
		"\u00cc\7n\2\2\u00cc\u00cd\7d\2\2\u00cd\u00ce\7w\2\2\u00ce\u01a9\7o\2\2"+
		"\u00cf\u00d0\7f\2\2\u00d0\u00d1\7e\2\2\u00d1\u00d2\7<\2\2\u00d2\u00d3"+
		"\7f\2\2\u00d3\u00d4\7c\2\2\u00d4\u00d5\7v\2\2\u00d5\u01a9\7g\2\2\u00d6"+
		"\u00d7\7w\2\2\u00d7\u00d8\7r\2\2\u00d8\u00d9\7p\2\2\u00d9\u00da\7r\2\2"+
		"\u00da\u00db\7<\2\2\u00db\u00dc\7e\2\2\u00dc\u00dd\7n\2\2\u00dd\u00de"+
		"\7c\2\2\u00de\u00df\7u\2\2\u00df\u01a9\7u\2\2\u00e0\u00e1\7B\2\2\u00e1"+
		"\u00e2\7k\2\2\u00e2\u01a9\7f\2\2\u00e3\u00e4\7B\2\2\u00e4\u00e5\7t\2\2"+
		"\u00e5\u00e6\7g\2\2\u00e6\u00e7\7h\2\2\u00e7\u00e8\7K\2\2\u00e8\u01a9"+
		"\7F\2\2\u00e9\u00ea\7B\2\2\u00ea\u00eb\7r\2\2\u00eb\u00ec\7t\2\2\u00ec"+
		"\u00ed\7q\2\2\u00ed\u00ee\7v\2\2\u00ee\u00ef\7q\2\2\u00ef\u00f0\7e\2\2"+
		"\u00f0\u00f1\7q\2\2\u00f1\u00f2\7n\2\2\u00f2\u00f3\7K\2\2\u00f3\u00f4"+
		"\7p\2\2\u00f4\u00f5\7h\2\2\u00f5\u01a9\7q\2\2\u00f6\u00f7\7w\2\2\u00f7"+
		"\u00f8\7r\2\2\u00f8\u00f9\7p\2\2\u00f9\u00fa\7r\2\2\u00fa\u00fb\7<\2\2"+
		"\u00fb\u00fc\7c\2\2\u00fc\u00fd\7w\2\2\u00fd\u00fe\7v\2\2\u00fe\u00ff"+
		"\7j\2\2\u00ff\u0100\7q\2\2\u0100\u01a9\7t\2\2\u0101\u0102\7f\2\2\u0102"+
		"\u0103\7e\2\2\u0103\u0104\7<\2\2\u0104\u0105\7f\2\2\u0105\u0106\7g\2\2"+
		"\u0106\u0107\7u\2\2\u0107\u0108\7e\2\2\u0108\u0109\7t\2\2\u0109\u010a"+
		"\7k\2\2\u010a\u010b\7r\2\2\u010b\u010c\7v\2\2\u010c\u010d\7k\2\2\u010d"+
		"\u010e\7q\2\2\u010e\u01a9\7p\2\2\u010f\u0110\7r\2\2\u0110\u0111\7x\2\2"+
		"\u0111\u0112\7<\2\2\u0112\u0113\7c\2\2\u0113\u0114\7x\2\2\u0114\u0115"+
		"\7M\2\2\u0115\u0116\7g\2\2\u0116\u0117\7{\2\2\u0117\u0118\7y\2\2\u0118"+
		"\u0119\7q\2\2\u0119\u011a\7t\2\2\u011a\u011b\7f\2\2\u011b\u01a9\7u\2\2"+
		"\u011c\u011d\7r\2\2\u011d\u011e\7x\2\2\u011e\u011f\7<\2\2\u011f\u0120"+
		"\7t\2\2\u0120\u0121\7c\2\2\u0121\u0122\7v\2\2\u0122\u0123\7k\2\2\u0123"+
		"\u0124\7p\2\2\u0124\u01a9\7i\2\2\u0125\u0126\7w\2\2\u0126\u0127\7r\2\2"+
		"\u0127\u0128\7p\2\2\u0128\u0129\7r\2\2\u0129\u012a\7<\2\2\u012a\u012b"+
		"\7u\2\2\u012b\u012c\7g\2\2\u012c\u012d\7t\2\2\u012d\u012e\7k\2\2\u012e"+
		"\u012f\7g\2\2\u012f\u0130\7u\2\2\u0130\u0131\7V\2\2\u0131\u0132\7k\2\2"+
		"\u0132\u0133\7v\2\2\u0133\u0134\7n\2\2\u0134\u01a9\7g\2\2\u0135\u0136"+
		"\7w\2\2\u0136\u0137\7r\2\2\u0137\u0138\7p\2\2\u0138\u0139\7r\2\2\u0139"+
		"\u013a\7<\2\2\u013a\u013b\7g\2\2\u013b\u013c\7r\2\2\u013c\u013d\7k\2\2"+
		"\u013d\u013e\7u\2\2\u013e\u013f\7q\2\2\u013f\u0140\7f\2\2\u0140\u0141"+
		"\7g\2\2\u0141\u0142\7P\2\2\u0142\u0143\7w\2\2\u0143\u0144\7o\2\2\u0144"+
		"\u0145\7d\2\2\u0145\u0146\7g\2\2\u0146\u01a9\7t\2\2\u0147\u0148\7w\2\2"+
		"\u0148\u0149\7r\2\2\u0149\u014a\7p\2\2\u014a\u014b\7r\2\2\u014b\u014c"+
		"\7<\2\2\u014c\u014d\7f\2\2\u014d\u014e\7k\2\2\u014e\u014f\7t\2\2\u014f"+
		"\u0150\7g\2\2\u0150\u0151\7e\2\2\u0151\u0152\7v\2\2\u0152\u0153\7q\2\2"+
		"\u0153\u01a9\7t\2\2\u0154\u0155\7w\2\2\u0155\u0156\7r\2\2\u0156\u0157"+
		"\7p\2\2\u0157\u0158\7r\2\2\u0158\u0159\7<\2\2\u0159\u015a\7t\2\2\u015a"+
		"\u015b\7c\2\2\u015b\u015c\7v\2\2\u015c\u015d\7k\2\2\u015d\u015e\7p\2\2"+
		"\u015e\u01a9\7i\2\2\u015f\u0160\7w\2\2\u0160\u0161\7r\2\2\u0161\u0162"+
		"\7p\2\2\u0162\u0163\7r\2\2\u0163\u0164\7<\2\2\u0164\u0165\7e\2\2\u0165"+
		"\u0166\7j\2\2\u0166\u0167\7c\2\2\u0167\u0168\7p\2\2\u0168\u0169\7p\2\2"+
		"\u0169\u016a\7g\2\2\u016a\u016b\7n\2\2\u016b\u016c\7P\2\2\u016c\u01a9"+
		"\7t\2\2\u016d\u016e\7w\2\2\u016e\u016f\7r\2\2\u016f\u0170\7p\2\2\u0170"+
		"\u0171\7r\2\2\u0171\u0172\7<\2\2\u0172\u0173\7e\2\2\u0173\u0174\7j\2\2"+
		"\u0174\u0175\7c\2\2\u0175\u0176\7p\2\2\u0176\u0177\7p\2\2\u0177\u0178"+
		"\7g\2\2\u0178\u0179\7n\2\2\u0179\u017a\7P\2\2\u017a\u017b\7c\2\2\u017b"+
		"\u017c\7o\2\2\u017c\u01a9\7g\2\2\u017d\u017e\7w\2\2\u017e\u017f\7r\2\2"+
		"\u017f\u0180\7p\2\2\u0180\u0181\7r\2\2\u0181\u0182\7<\2\2\u0182\u0183"+
		"\7n\2\2\u0183\u0184\7q\2\2\u0184\u0185\7p\2\2\u0185\u0186\7i\2\2\u0186"+
		"\u0187\7F\2\2\u0187\u0188\7g\2\2\u0188\u0189\7u\2\2\u0189\u018a\7e\2\2"+
		"\u018a\u018b\7t\2\2\u018b\u018c\7k\2\2\u018c\u018d\7r\2\2\u018d\u018e"+
		"\7v\2\2\u018e\u018f\7k\2\2\u018f\u0190\7q\2\2\u0190\u01a9\7p\2\2\u0191"+
		"\u0192\7r\2\2\u0192\u0193\7x\2\2\u0193\u0194\7<\2\2\u0194\u0195\7e\2\2"+
		"\u0195\u0196\7c\2\2\u0196\u0197\7r\2\2\u0197\u0198\7v\2\2\u0198\u0199"+
		"\7w\2\2\u0199\u019a\7t\2\2\u019a\u019b\7g\2\2\u019b\u019c\7f\2\2\u019c"+
		"\u019d\7c\2\2\u019d\u019e\7v\2\2\u019e\u01a9\7g\2\2\u019f\u01a0\7r\2\2"+
		"\u01a0\u01a1\7x\2\2\u01a1\u01a2\7<\2\2\u01a2\u01a3\7e\2\2\u01a3\u01a4"+
		"\7w\2\2\u01a4\u01a5\7u\2\2\u01a5\u01a6\7v\2\2\u01a6\u01a7\7q\2\2\u01a7"+
		"\u01a9\7o\2\2\u01a8z\3\2\2\2\u01a8\u0088\3\2\2\2\u01a8\u0094\3\2\2\2\u01a8"+
		"\u009c\3\2\2\2\u01a8\u00a6\3\2\2\2\u01a8\u00b0\3\2\2\2\u01a8\u00bb\3\2"+
		"\2\2\u01a8\u00c5\3\2\2\2\u01a8\u00cf\3\2\2\2\u01a8\u00d6\3\2\2\2\u01a8"+
		"\u00e0\3\2\2\2\u01a8\u00e3\3\2\2\2\u01a8\u00e9\3\2\2\2\u01a8\u00f6\3\2"+
		"\2\2\u01a8\u0101\3\2\2\2\u01a8\u010f\3\2\2\2\u01a8\u011c\3\2\2\2\u01a8"+
		"\u0125\3\2\2\2\u01a8\u0135\3\2\2\2\u01a8\u0147\3\2\2\2\u01a8\u0154\3\2"+
		"\2\2\u01a8\u015f\3\2\2\2\u01a8\u016d\3\2\2\2\u01a8\u017d\3\2\2\2\u01a8"+
		"\u0191\3\2\2\2\u01a8\u019f\3\2\2\2\u01a9\30\3\2\2\2\u01aa\u01ab\7\13\2"+
		"\2\u01ab\32\3\2\2\2\u01ac\u01ad\7\"\2\2\u01ad\34\3\2\2\2\u01ae\u01af\7"+
		"$\2\2\u01af\36\3\2\2\2\u01b0\u01b1\7,\2\2\u01b1 \3\2\2\2\u01b2\u01b6\t"+
		"\3\2\2\u01b3\u01b4\7^\2\2\u01b4\u01b6\7$\2\2\u01b5\u01b2\3\2\2\2\u01b5"+
		"\u01b3\3\2\2\2\u01b6\"\3\2\2\2\13\2\60\64?btx\u01a8\u01b5\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}