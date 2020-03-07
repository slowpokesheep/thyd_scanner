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

package nanomorpho;

import java.io.*;
//import Utils.Utils;

%%

%public
%class NanoMorpho2

%unicode
%byaccj

// Switch these variables on
%line   // yyline
%column // yycolumn
%char   // yychar

// Default main classes
//%debug
//%standalone

%{

// This part becomes a verbatim part of the program text inside
// the class, NanoMorpho.java, that is generated.

public static void main(String args[]) throws Exception {  
  NanoMorphoLexer lexer = new NanoMorphoLexer(
      new NanoMorpho2(new FileReader(args[0]))
    );

  NanoMorphoParser parser = new NanoMorphoParser();

  //lexer.scan(); // Activate only scanner
  parser.start();
}

// Getters
public int getLine() { return yyline; }
public int getColumn() { return yycolumn; }

/*
// Variables

yyline = Number of newlines encountered up to the start of the matched text
yychar = Number of characters up to the start of the matched text
yycolumn = Number of characters from the last newline up to the start of the matched text

// Functions

yylex = Resumes scanning until the next regular expression is matched, the end of input is encountered or an I/O-Error occurs.

yytext = Returns the text matched by the current regular expression.
*/

%}

%eof{
  System.out.println("End of file");
%eof}

/* Regular definitions */

%include lexicalrules.flex

%%

/* Scanning rules */

{_DELIM} { return yycharat(0); }

{_STRING} | {_FLOAT} | {_CHAR} | {_INT} | {_BOOL} | null {
  return NanoMorphoLexer.LITERAL;
}

"var" { return NanoMorphoLexer.VAR; }
"return" { return NanoMorphoLexer.RETURN; }
"while" { return NanoMorphoLexer.WHILE; }
"if" { return NanoMorphoLexer.IF; }
"elsif" { return NanoMorphoLexer.ELSIF; }
"else" { return NanoMorphoLexer.ELSE; }

{_NAME} { return NanoMorphoLexer.NAME; }

{_OPNAME} { return NanoMorphoLexer.OPNAME; }

// Comment
";;;".*$ { /* Ignore */ }

// White spaces
[ \t\r\n\f] { /* Ignore */ }

// If all rules fail, return an error
[^] {
	//return ERROR;
  throw new Error("Illegal character <"+yytext()+">");
}
