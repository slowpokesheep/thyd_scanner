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

  // Print error message
  private static void error(String s) {
    throw new Error(
        "Line " + lexer.getLineNumber() +
        ": Expected " + s + " but found " + lexer.getLexeme()
    );
  }

  private static String errorMsg(int expected) {
    switch (expected) {
      case NanoMorpho.NAME: return "name";
      default: return "unknown";
    }
  }

  private static int asciiValue(char c) {
    return (int) c;
  }

  // Check if token is valid
  private static void check(int expected) {
    if (token != expected) error(errorMsg(expected));
  }

  // Check if token is valid
  private static void check(char expected) {
    if (token != (asciiValue(expected))) error(""+expected);
  }

  // Optional check for token
  private static boolean optionalCheck(int opt) {
    if (token != opt) return false;
    return true;
  }

  // Optional check for token
  private static boolean optionalCheck(char opt) {
    if (token != asciiValue(opt)) return false;
    return true;
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
    check(NanoMorpho.NAME);
    
    // Read next token
    advance(true);
    check('(');
    
    // Read next token
    advance(true);
    

    if (! optionalCheck(')')) {

      check(NanoMorpho.NAME);

      // Read next token
      advance(true);

      // Reading function parameters
      while (optionalCheck(',')) {

        // Read next token
        advance(true);
        check(NanoMorpho.NAME);

        // Read next token
        advance(true);
      }
      
    }

    // Closing function paranthesis
    check(')');
    
    // Read next token
    advance(true);
    check('{');

    // *** { decl;* expr;*} *** //
    decl();
    
    /* 
      we check for expressions until we reach },
      or we reach an invalid ending.
      We also want to make sure the function contains
      at least one expression.
    */    
    boolean contains_expr = false;

    while (! optionalCheck('}')) {
      expr();

      // Read next token
      advance(true);
      check(';');

      // Read next token
      advance(true);
      contains_expr = true;
    }
    if (!contains_expr) error("expression");
  }

  // *** var NAME,NAME..... *** //
  public static void decl() throws Exception {

    // Lookahead next token
    advance(false);
    
    while (optionalCheck(NanoMorpho.VAR)) {

      // Use token
      advance(true);

      // Read next token
      advance(true);
      check(NanoMorpho.NAME);

      // Additional variables, seperate by commas
      advance(true);
      while (optionalCheck(',')) {

        // Read next token
        advance(true);
        check(NanoMorpho.NAME);

        advance(true);
      }
      check(';');

      // Lookahead next token
      advance(false);
    }
  }

  public static void expr() throws Exception {

    boolean is_empty = true;
    
    // *** NAME | NAME = expr | NAME = (expr,....) *** //
    if (optionalCheck(NanoMorpho.NAME)) {
      
      
      // assigning to a variable => NAME = expr
      should_advance = true;


      // Lookahead next token
      advance(false);

      if (optionalCheck('=')) {

        // Read next token
        advance(true);
        advance(true);

        expr();
      }
      
      // Lookahead next token
      advance(false);

      if (optionalCheck('(')) {

        // Read next token
        advance(true);
        // Lookahead next token
        advance(false);

        if (! optionalCheck(')')) {

          // First parameter
          advance(true);
          expr();

          // Additional parameters
          advance(true);
          while (optionalCheck(',')) {
            // n parameter
            advance(true);
            expr();
          }

          // Read next token
          advance(true);
          check(')');

        }
        else advance(true);

      }
      is_empty = false;
    }

    // *** return expr | OPNAME expr *** //
    else if (optionalCheck(NanoMorpho.RETURN) || optionalCheck(NanoMorpho.OPNAME)) {
      
      // Read next token
      advance(true);
      expr();
      is_empty = false;
    }

    // *** LITERAL *** //
    else if (optionalCheck(NanoMorpho.LITERAL)) is_empty = false;


    else if (optionalCheck('(')) {
      
      // Read next token
      advance(true);
      expr();

      // Read next token
      advance(true);
      check(')');

      is_empty = false;
    }

    else if (optionalCheck(NanoMorpho.WHILE)) {

      // Read next token
      advance(true);
      check('(');
      
      // Read next token
      advance(true);
      expr();

      // Read next token
      advance(true);
      check(')');

      body();
      is_empty = false;
    }
    else is_empty = ifexpr();

    // Lookahead and check if next token is an operator, if so
    // we need to follow up with an expression
    advance(false);
    
    if (optionalCheck(NanoMorpho.OPNAME)) {

      advance(true);

      // Read next token
      advance(true);
      expr();
    }
    
    // Empty expressions result in an error
    if (is_empty) error("expression");
  }

  // Returns true if the ifexpr is empty
  public static boolean ifexpr() throws Exception {
    // debug(168);
    
    if (optionalCheck(NanoMorpho.IF)) {

      // Read next token
      advance(true);
      check('(');
      
      // Read next token
      advance(true);
      expr();

      // Read next token
      advance(true);
      check(')');

      body();

      // Lookahead next token
      advance(false);
      if (optionalCheck(NanoMorpho.ELSIF)) {

        // Use token
        advance(true);

        // Read next token
        advance(true);
        check('(');

        // Read next token
        advance(true);
        expr();

        // Read next token
        advance(true);
        check(')');

        body();
      }
      
      // Lookahead next token
      advance(false);
      if (optionalCheck(NanoMorpho.ELSE)) {

        // Use token
        advance(true);
        body();
      }
      return false;
    }
    return true;
  }

  public static void body() throws Exception {
    // debug(194);
    
    // Read next token
    advance(true);
    check('{');

    while (! optionalCheck(';')) {

      advance(true);
      expr();
      advance(true);
    }

    // Read next token
    advance(true);
    check('}');
  }

}