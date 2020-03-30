
import java.io.*;

%%

%public
%class NanoMorphoLexer

%unicode
%byaccj

// Switch these variables on
%line   // yyline
%column // yycolumn
%char   // yychar

%{

// This part becomes a verbatim part of the program text inside
// the class, NanoMorpho.java, that is generated.

public NanoMorphoParser yyparser;

public NanoMorphoLexer(java.io.Reader r, NanoMorphoParser yyparser) {
  this(r);
  this.yyparser = yyparser;
}

// Getters
public int getLine() { return yyline; }
public int getColumn() { return yycolumn; }

%}

/* Regular definitions */

%include lexicalrules.flex

%%

/* Scanning rules */

{_DELIM} {
  yyparser.yylval = new NanoMorphoParserVal(yytext());
  return yycharat(0);
}

{_AND} {
  yyparser.yylval = new NanoMorphoParserVal(yytext());
  return NanoMorphoParser.AND;
}

{_OR} {
  yyparser.yylval = new NanoMorphoParserVal(yytext());
  return NanoMorphoParser.OR;
}

{_STRING} | {_FLOAT} | {_CHAR} | {_INT} | {_BOOL} | null {
  yyparser.yylval = new NanoMorphoParserVal(yytext());
  return NanoMorphoParser.LITERAL;
}

"var" { return NanoMorphoParser.VAR; }
"return" { return NanoMorphoParser.RETURN; }
"while" { return NanoMorphoParser.WHILE; }
"if" { return NanoMorphoParser.IF; }
"elsif" { return NanoMorphoParser.ELSIF; }
"else" { return NanoMorphoParser.ELSE; }

{_NAME} {
  yyparser.yylval = new NanoMorphoParserVal(yytext());
  return NanoMorphoParser.NAME;
}

{_OPNAME} {
  yyparser.yylval = new NanoMorphoParserVal(yytext());
  switch (yytext().charAt(0)) {
    case '^':
    case '?':
    case '~':
        return NanoMorphoParser.OPNAME_1;
    case ':':
        return NanoMorphoParser.OPNAME_2;
    case '|':
        return NanoMorphoParser.OPNAME_3;
    case '&':
        return NanoMorphoParser.OPNAME_4;
    case '!':
    case '=':
    case '<':
    case '>':
        return NanoMorphoParser.OPNAME_5;
    case '+':
    case '-':
        return NanoMorphoParser.OPNAME_6;
    case '*':
    case '/':
    case '%':
        return NanoMorphoParser.OPNAME_7;
    default:
        throw new Error("Invalid opname");
  }
}

// Comment
";;;".*$ { /* Ignore */ }

// White spaces
[ \t\r\n\f] { /* Ignore */ }

// If all rules fail, return an error
[^] {
  return NanoMorphoParser.YYERRCODE;
}
