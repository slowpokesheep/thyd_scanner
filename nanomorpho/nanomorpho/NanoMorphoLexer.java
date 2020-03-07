package nanomorpho;

import java.io.*;

public class NanoMorphoLexer {

  // Definitions of tokens:
  public static final int ERROR   = -1;
  public static final int IF      = 1001;
  public static final int ELSE    = 1002;
  public static final int ELSIF   = 1003;
  public static final int WHILE   = 1004;
  public static final int VAR     = 1005;
  public static final int RETURN  = 1006;
  public static final int NAME    = 1007;
  public static final int OPNAME  = 1008;
  public static final int LITERAL = 1009;

  // Variables for scanner and parser
  private static int currToken, nextToken;
  private static String currLexeme, nextLexeme;
  private static int currLine, nextLine;
  private static int currColumn, nextColumn;

  private static NanoMorpho2 lexer;

  public NanoMorphoLexer(NanoMorpho2 l) {
    lexer = l;
  }

  // Run just the scanner
  public static void scan() throws Exception {
    init();

    while(currToken != 0) {
      System.out.format("%4s | %s\n", currToken, currLexeme);
      advance();
    }
  }

  public static void init() throws Exception {
    nextToken = lexer.yylex();
    nextLexeme = lexer.yytext();
    nextLine = lexer.getLine();
    nextColumn = lexer.getColumn();
    advance();
  }

  public static String advance() throws Exception {
    String res = currLexeme;

    currToken = nextToken;
    currLexeme = nextLexeme;
    currLine = nextLine;
    currColumn = nextColumn;

    if (nextToken != 0) {
      nextToken = lexer.yylex();
      nextLexeme = lexer.yytext();
      nextLine = lexer.getLine();
      nextColumn = lexer.getColumn();
    }

    return res;
  }

  private static String tokenName(int token) {

    if (token < 1000) return "" + (char) token;

    switch(token) {
      case IF: return "if";
      case ELSE: return "else";
      case ELSIF: return "elsif";
      case WHILE: return "while";
      case VAR: return "var";
      case RETURN: return "return";
      case NAME: return "name";
      case OPNAME: return "operation";
      case LITERAL: return "literal";
      default: throw new Error();
    }
  }

  private static void expected(int token) {
    expected(tokenName(token));
  }

  private static void expected(char token) {
    expected("" + token);
  }

  public static void expected(String token) {
    throw new Error("Expected "+token+", found '"+currLexeme+"' near line "+(currLine + 1)+", column "+(currColumn + 1)+"");
  }

  public static String over(int token) throws Exception {

    if (currToken != token) expected(token);
    String res = currLexeme;
    advance();
    return res;
  }

  public static String over(char token) throws Exception {

    if (currToken != token) expected(token);
    String res = currLexeme;
    advance();
    return res;
  }

  // Getters
  public static int getCurrToken() { return currToken; }
  public static int getNextToken() { return nextToken; }
  public static String getCurrLexeme() { return currLexeme; }
  public static int getCurrLine() { return currLine + 1; }
  public static int getCurrColumn() { return currColumn + 1; }
}