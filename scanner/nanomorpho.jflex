/**
	JFlex scanner example based on a scanner for NanoLisp.
	Author: Snorri Agnarsson, 2017-2020

	This stand-alone scanner/lexical analyzer can be built and run using:
		java -jar JFlex-full-1.7.0.jar NanoMorpho.jflex
		javac NanoMorpho.java
		java NanoMorpho inputfile > outputfile
	Also, the program 'make' can be used with the proper 'makefile':
		make test
 */

import java.io.*;

%%

%public
%class NanoMorpho
%unicode
%byaccj

%{

// This part becomes a verbatim part of the program text inside
// the class, NanoMorpho.java, that is generated.

// Definitions of tokens:
final static int ERROR = -1;
final static int IF = 1001;
final static int DEFINE = 1002;
final static int NAME = 1003;
final static int LITERAL = 1004;

// A variable that will contain lexemes as they are recognized:
private static String lexeme;

// This runs the scanner:
public static void main( String[] args ) throws Exception
{
	NanoMorpho lexer = new NanoMorpho(new FileReader(args[0]));
	int token = lexer.yylex();
	while( token!=0 )
	{
		System.out.println(""+token+": \'"+lexeme+"\'");
		token = lexer.yylex();
	}
}

%}

  /* Reglulegar skilgreiningar */

  /* Regular definitions */

_DIGIT=[0-9]
_FLOAT={_DIGIT}+\.{_DIGIT}+([eE][+-]?{_DIGIT}+)?
_INT={_DIGIT}+
_STRING=\"([^\"\\]|\\b|\\t|\\n|\\f|\\r|\\\"|\\\'|\\\\|(\\[0-3][0-7][0-7])|\\[0-7][0-7]|\\[0-7])*\"
_CHAR=\'([^\'\\]|\\b|\\t|\\n|\\f|\\r|\\\"|\\\'|\\\\|(\\[0-3][0-7][0-7])|(\\[0-7][0-7])|(\\[0-7]))\'
_DELIM=[()]
_NAME=([:letter:]|[\+\-*/!%=><\:\^\~&|?]|{_DIGIT})+

%%

  /* Lesgreiningarreglur */
  /* Scanning rules */

{_DELIM} {
	lexeme = yytext();
	return yycharat(0);
}

{_STRING} | {_FLOAT} | {_CHAR} | {_INT} | null | true | false {
	lexeme = yytext();
	return LITERAL;
}

"if" {
	lexeme = yytext();
	return IF;
}

"define" {
	lexeme = yytext();
	return DEFINE;
}

{_NAME} {
	lexeme = yytext();
	return NAME;
}

";".*$ {
}

[ \t\r\n\f] {
}

. {
	lexeme = yytext();
	return ERROR;
}
