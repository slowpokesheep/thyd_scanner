import java.io.FileReader;
import java.io.IOException;

public class Parser {
  private static NanoMorpho lexer;
  private static int token;

  private static boolean accepted = false;

  public static void main(String[] args) throws Exception {
    lexer = new NanoMorpho(new FileReader(args[0]));
    token = -1;

    while (token != 0) {
      func();
    }
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

  private static int advance() throws Exception {
    token = lexer.yylex();
    if (token == 0) {
      if (!accepted) {
        throw new Error("Unexpected ending");
      }
      System.exit(0);
    }
    System.out.println(token + " " + lexer.getLexeme());
    return token;
  }

  public static void func() throws Exception {
    // === NAME(... , ...) {decl -> expr} === //
    if (advance() != NanoMorpho.NAME) error("function name");
    if (advance() != asciiValue('(')) error("(");
    // optional function variables
    if (advance() != asciiValue(')')) {
      if (token != NanoMorpho.NAME) error("function variable name");
      // multiple function variables
      while (advance() == asciiValue(',')) {
        if (advance() != NanoMorpho.NAME) error("function variable name");
      }
    }
    // closing function paranthesis
    if (token != asciiValue(')')) error(")");
    
    if (advance() != asciiValue('{')) error("{");
    decl();
    // we check for expressions until we reach }, or we reach
    // an unexpected ending
    while (token != asciiValue('}')) {
      boolean is_empty = expr(token);
      if (is_empty) {
        error("expression");
      }
      if (advance() != asciiValue(';')) error(";");
      advance();
    }
    // we reached the end!
    accepted = true;
    System.out.println("Accepted!");
  }

  // decl => var x; or var x, y, z....;
  public static void decl() throws Exception {
    while (advance() == NanoMorpho.VAR) {
      if (advance() != NanoMorpho.NAME) error("variable name");
      while (advance() == asciiValue(',')) {
        if (advance() != NanoMorpho.NAME) error("variable name");
      }
      if (token != asciiValue(';')) error(";");
    }
  }

  // returns true if the expr is empty
  public static boolean expr(int first) throws Exception {
    System.out.println("expr: " + first);
    // NAME | NAME = expr | NAME = (expr,....)
    if (first == NanoMorpho.NAME) {
      // assigning to a variable => NAME = expr
      if (advance() == asciiValue('=')) {
        expr(token);
      }
      // function call => (expr, expr.....)
      if (advance() == asciiValue('(')) {
        if (advance() != asciiValue(')')) {
          // first parameter
          expr(token); // we have already advanced in the if statement
          // additional parameters
          while (advance() == asciiValue(',')) expr(advance());
          if (advance() != asciiValue(')')) error(")");
        }
      }
      return false;
    }
    
    else if (first == NanoMorpho.RETURN || first == NanoMorpho.OPNAME) {
      expr(advance());
      return false;
    }

    else if (first == NanoMorpho.LITERAL) return true;

    else if (first == asciiValue('(')) {
      boolean is_empty = expr(advance());
      if (is_empty) error("expression");
      if (advance() != asciiValue(')')) error(")");
      return false;
    }

    else if (first == NanoMorpho.WHILE) {
      if (advance() != asciiValue('(')) error("(");
      // þarf að vera expr!
      boolean is_empty = expr(advance());
      if (is_empty) error("expression");
      if (advance() != asciiValue(')')) error(")");
      body();
      return false;
    }

    else {
      boolean is_empty = ifexpr(first);
      return is_empty;
    }
  }

  // returns true if the ifexpr is empty
  public static boolean ifexpr(int first) throws Exception {
    System.out.println("ifexpr");
    if (first == NanoMorpho.IF) {
      if (advance() != asciiValue('(')) error("(");
      boolean is_empty = expr(advance());
      if (is_empty) error("expression");
      if (advance() != asciiValue(')')) error(")");
      body();

      if (advance() == NanoMorpho.ELSIF) {
        if (advance() != asciiValue('(')) error("(");
        is_empty = expr(advance());
        if (is_empty) error("expression");
        if (advance() != asciiValue(')')) error(")");
        body();
      }

      if (advance() == NanoMorpho.ELSE) body();

      return false;
    }
    return true;
  }

  public static void body() throws Exception {
    System.out.println("body");
    if (advance() != asciiValue('{')) error("{");
    while (token != asciiValue(';')) {
      boolean is_empty = expr(advance());
      if (is_empty) {
        error("expression");
      }
      advance();
    }
    if (advance() != asciiValue('}')) error("}");
  }

}