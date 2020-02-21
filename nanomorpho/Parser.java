import java.io.FileReader;
import java.io.IOException;

public class Parser {
  private static NanoMorpho lexer;
  private static int token;

  private static boolean should_advance = true;

  public static void main(String[] args) throws Exception {
    lexer = new NanoMorpho(new FileReader(args[0]));
    token = advance(true);

    while (token != 0) {
      func();
      token = lexer.yylex();
    }
    System.out.println("Accepted!");
  }

  private static void debug(int line) {
    System.out.println("DEBUG: line-" + line + ", token-" + token + ", lexeme-" + lexer.getLexeme());
  }

  private static void error(String s) {
    throw new Error(
        "Line " + lexer.getLineNumber() +
        ": Expected " + s + " but found " + lexer.getLexeme()
    );
  }

  private static int asciiValue(char c) {
    return (int) c;
  }

  // we can use the adv parameter to "lookahead"
  private static int advance(boolean adv) throws Exception {
    if (should_advance) {
      token = lexer.yylex();
      if (token == 0) {
        throw new Error("Ending is invalid");
      }
    }
    // debug(44);   
    should_advance = adv;
    return token;
  }

  public static void func() throws Exception {
     // *** NAME(... , ...) *** //
    if (token != NanoMorpho.NAME) error("function name");
    if (advance(true) != asciiValue('(')) error("(");
    // function variables are optional
    if (advance(true) != asciiValue(')')) {
      if (token != NanoMorpho.NAME) error("function variable name");
      // multiple function variables
      while (advance(true) == asciiValue(',')) {
        if (advance(true) != NanoMorpho.NAME) error("function variable name");
      }
    }
    // closing function paranthesis
    if (token != asciiValue(')')) error(")");
    
    // *** { decl;* expr;*} *** //
    if (advance(true) != asciiValue('{')) error("{");
    decl();
    /* 
      we check for expressions until we reach },
      or we reach an invalid ending.
      We also want to make sure the function contains
      at least one expression.
    */
    boolean contains_expr = false;
    while (token != asciiValue('}')) {
      expr(token);
      if (advance(true) != asciiValue(';')) error(";");
      advance(true);
      contains_expr = true;
    }
    if (!contains_expr) {
      error("expression");
    }
  }

  // *** var NAME,NAME..... *** //
  public static void decl() throws Exception {
    while (advance(false) == NanoMorpho.VAR) { // lookahead
      advance(true); // use
      if (advance(true) != NanoMorpho.NAME) error("variable name");
      // additional variables seperated by commas
      while (advance(true) == asciiValue(',')) {
        if (advance(true) != NanoMorpho.NAME) error("variable name");
      }
      if (token != asciiValue(';')) error(";");
    }
  }

  public static void expr(int first) throws Exception {
    boolean is_empty = true;
    // *** NAME | NAME = expr | NAME = (expr,....) *** //
    if (first == NanoMorpho.NAME) {
      // assigning to a variable => NAME = expr
      if (advance(false) == asciiValue('=')) { // lookahead
        advance(true); // use
        expr(advance(true));
      }
      // function call => (expr, expr.....)
      else if (advance(false) == asciiValue('(')) { // lookahead
        advance(true); // use
        if (advance(false) != asciiValue(')')) { // lookahead
          // first parameter
          expr(advance(true));
          // additional parameters
          while (advance(true) == asciiValue(',')) expr(advance(true));
          if (advance(true) != asciiValue(')')) error(")");
        } else {
          advance(true); // use
        }
      }
      is_empty = false;
    }

    // *** return expr | OPNAME expr *** //
    else if (first == NanoMorpho.RETURN || first == NanoMorpho.OPNAME) {
      expr(advance(true));
      is_empty = false;
    }

    // *** LITERAL *** //
    else if (first == NanoMorpho.LITERAL) {
      is_empty = false;
    }


    else if (first == asciiValue('(')) {
      expr(advance(true));
      if (advance(true) != asciiValue(')')) error(")");
      is_empty = false;
    }

    else if (first == NanoMorpho.WHILE) {
      if (advance(true) != asciiValue('(')) error("(");
      expr(advance(true));
      if (advance(true) != asciiValue(')')) error(")");
      body();
      is_empty = false;
    }

    else {
      is_empty = ifexpr(first);
    }

    // lookahead and check if next token is an operator, if so
    // we need to follow up with an expression
    if (advance(false) == NanoMorpho.OPNAME) {
      advance(true);
      expr(advance(true));
    }
    
    // empty expressions result in an error
    if (is_empty) {
      error("expression");
    }
  }

  // returns true if the ifexpr is empty
  public static boolean ifexpr(int first) throws Exception {
    // debug(168);
    if (first == NanoMorpho.IF) {
      if (advance(true) != asciiValue('(')) error("(");
      expr(advance(true));
      if (advance(true) != asciiValue(')')) error(")");
      body();

      if (advance(false) == NanoMorpho.ELSIF) { // lookahead
        advance(true); // use
        if (advance(true) != asciiValue('(')) error("(");
        expr(advance(true));
        if (advance(true) != asciiValue(')')) error(")");
        body();
      }
      
      if (advance(false) == NanoMorpho.ELSE) { // lookahead
        advance(true); // use
        body();
      }

      return false;
    }
    return true;
  }

  public static void body() throws Exception {
    // debug(194);
    if (advance(true) != asciiValue('{')) {
      error("{");
    }
    while (token != asciiValue(';')) {
      expr(advance(true));
      advance(true);
    }
    if (advance(true) != asciiValue('}')) {
      error("}");
    }
  }

}