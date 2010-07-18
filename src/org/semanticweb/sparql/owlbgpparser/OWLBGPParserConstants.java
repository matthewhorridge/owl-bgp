/* Generated By:JavaCC: Do not edit this line. OWLBGPParserConstants.java */
package org.semanticweb.sparql.owlbgpparser;


/**
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface OWLBGPParserConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int COMMENT = 5;
  /** RegularExpression Id. */
  int STRING = 8;
  /** RegularExpression Id. */
  int LONG_STRING = 11;
  /** RegularExpression Id. */
  int DIGIT = 12;
  /** RegularExpression Id. */
  int INTEGER = 13;
  /** RegularExpression Id. */
  int DOUBLE = 14;
  /** RegularExpression Id. */
  int DECIMAL = 15;
  /** RegularExpression Id. */
  int EXPONENT = 16;
  /** RegularExpression Id. */
  int OPENPAR = 17;
  /** RegularExpression Id. */
  int CLOSEPAR = 18;
  /** RegularExpression Id. */
  int OPEN_SQUARE_BRACKET = 19;
  /** RegularExpression Id. */
  int CLOSE_SQUARE_BRACKET = 20;
  /** RegularExpression Id. */
  int COMMA = 21;
  /** RegularExpression Id. */
  int DOT = 22;
  /** RegularExpression Id. */
  int SEMICOLON = 23;
  /** RegularExpression Id. */
  int PREFIX = 24;
  /** RegularExpression Id. */
  int DOUBLE_CARET = 25;
  /** RegularExpression Id. */
  int BASE = 26;
  /** RegularExpression Id. */
  int AT = 27;
  /** RegularExpression Id. */
  int A = 28;
  /** RegularExpression Id. */
  int EMPTY_BLANK_NODE = 29;
  /** RegularExpression Id. */
  int NODE_ID_START = 30;
  /** RegularExpression Id. */
  int TRUE = 31;
  /** RegularExpression Id. */
  int FALSE = 32;
  /** RegularExpression Id. */
  int LETTER = 33;
  /** RegularExpression Id. */
  int FULLIRI = 34;
  /** RegularExpression Id. */
  int PNAME_NS = 35;
  /** RegularExpression Id. */
  int PN_LOCAL = 36;
  /** RegularExpression Id. */
  int PNAME_LN = 37;
  /** RegularExpression Id. */
  int PN_PREFIX = 38;
  /** RegularExpression Id. */
  int PN_CHARS_BASE = 39;
  /** RegularExpression Id. */
  int PN_CHARS = 40;
  /** RegularExpression Id. */
  int PN_CHARS_U = 41;
  /** RegularExpression Id. */
  int VAR = 42;
  /** RegularExpression Id. */
  int VAR1 = 43;
  /** RegularExpression Id. */
  int VAR2 = 44;
  /** RegularExpression Id. */
  int VARNAME = 45;
  /** RegularExpression Id. */
  int NODEID = 46;
  /** RegularExpression Id. */
  int ERROR = 47;

  /** Lexical state. */
  int DEFAULT = 0;
  /** Lexical state. */
  int IN_STRING = 1;
  /** Lexical state. */
  int IN_LONG_STRING = 2;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\n\"",
    "\"\\t\"",
    "\"\\r\"",
    "<COMMENT>",
    "\"\\\"\"",
    "<token of kind 7>",
    "\"\\\"\"",
    "\"\\\"\\\"\\\"\"",
    "<token of kind 10>",
    "\"\\\"\\\"\\\"\"",
    "<DIGIT>",
    "<INTEGER>",
    "<DOUBLE>",
    "<DECIMAL>",
    "<EXPONENT>",
    "\"(\"",
    "\")\"",
    "\"[\"",
    "\"]\"",
    "\",\"",
    "\".\"",
    "\";\"",
    "\"@prefix\"",
    "\"^^\"",
    "\"@base\"",
    "\"@\"",
    "\"a\"",
    "<EMPTY_BLANK_NODE>",
    "\"_:\"",
    "\"true\"",
    "\"false\"",
    "<LETTER>",
    "<FULLIRI>",
    "<PNAME_NS>",
    "<PN_LOCAL>",
    "<PNAME_LN>",
    "<PN_PREFIX>",
    "<PN_CHARS_BASE>",
    "<PN_CHARS>",
    "<PN_CHARS_U>",
    "<VAR>",
    "<VAR1>",
    "<VAR2>",
    "<VARNAME>",
    "<NODEID>",
    "<ERROR>",
  };

}