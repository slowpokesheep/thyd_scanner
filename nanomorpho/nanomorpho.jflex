/*
  JFlex scanner for NanoMorpho

  Based on Snorri Agnarssons NanoLisp scanner.

  Authors:  Hjalti Geir Garðarsson
            Egill Ragnarsson
            Guðmundur Óli Norland
  
  Running the program:
    Compile:
      java -jar JFlex-full-1.7.0.jar nanomorpho.jflex
      javac NanoMorpho.java
    Run:
      java NanoMorpho <input_file> > <output_file>
  
  Use the makefile:
*/

import java.io.*;

%%

%public
%class NanoMorpho
%unicode
%byaccj
%line

%{

// This part becomes a verbatim part of the program text inside
// the class, NanoMorpho.java, that is generated.

// Definitions of tokens:
static final int ERROR = -1;

static final int NAME = 1001;
static final int LITERAL = 1002;
static final int OPNAME = 1003;

// Decleration <decl>
static final int VAR = 1010;

// Expression <expr>
static final int RETURN = 1020;
static final int WHILE = 1021;

// If Expression <ifexpr>
static final int IF = 1030;
static final int ELSIF = 1031;
static final int ELSE = 1032;

// A variable that will contain lexemes as they are recognized:
private static String lexeme;

public String getLexeme() {
  return lexeme;
}

public int getLineNumber() {
  return yyline + 1;
}

// This runs the scanner:
public static void main( String[] args ) throws Exception
{
  NanoMorpho lexer = new NanoMorpho(new FileReader(args[0]));
  int token = lexer.yylex();
  
  while(token != 0) {
    //System.out.println(""+token+": \'"+lexeme+"\'");
    System.out.format("%4s | %s\n", token, lexeme);
    token = lexer.yylex();
  }
}

%}

/* Regular definitions */
_DIGIT=[0-9]
_FLOAT={_DIGIT}+\.{_DIGIT}+([eE][+-]?{_DIGIT}+)?
_INT={_DIGIT}+
_BOOL=(true|false)
_ESCAPE=\\b|\\t|\\n|\\f|\\r|\\\"|\\\'|\\\\|(\\[0-3][0-7][0-7])|(\\[0-7][0-7])|(\\[0-7])
_CHAR=\'([^\'\\]|{_ESCAPE})\'
_STRING=\"([^\"\\]|{_ESCAPE})*\"
_DELIM=[()\{\},;=]
_NAME=([:letter:]|{_DIGIT}|_)+
_OPNAME=([\+\-*/!%=><\:\^\~&|?])+

%%

/* Scanning rules */

{_DELIM} {
  lexeme = yytext();
  return yycharat(0);
}

{_STRING} | {_FLOAT} | {_CHAR} | {_INT} | {_BOOL} | null {
  lexeme = yytext();
  return LITERAL;
}

"var" {
  lexeme = yytext();
  return VAR;
}

"return" {
  lexeme = yytext();
  return RETURN;
}

"while" {
  lexeme = yytext();
  return WHILE;
}

"if" {
  lexeme = yytext();
  return IF;
}

"elsif" {
  lexeme = yytext();
  return ELSIF;
}

"else" {
  lexeme = yytext();
  return ELSE;
}

{_NAME} {
  lexeme = yytext();
  return NAME;
}

{_OPNAME} {
  lexeme = yytext();
  return OPNAME;
}

// EOL character
";;;".*$ {
}

// White spaces are ignored
[ \t\r\n\f] {
}

// If all rules fail, return an error
. {
	lexeme = yytext();
	return ERROR;
}
