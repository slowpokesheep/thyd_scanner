package nanomorpho;

public class NanoMorphoParser {

  static final int ERROR   = NanoMorphoLexer.ERROR;
  static final int IF      = NanoMorphoLexer.IF;
  static final int ELSE    = NanoMorphoLexer.ELSE;
  static final int ELSIF   = NanoMorphoLexer.ELSIF;
  static final int WHILE   = NanoMorphoLexer.WHILE;
  static final int VAR     = NanoMorphoLexer.VAR;
  static final int RETURN  = NanoMorphoLexer.RETURN;
  static final int NAME    = NanoMorphoLexer.NAME;
  static final int OPNAME  = NanoMorphoLexer.OPNAME;
  static final int LITERAL = NanoMorphoLexer.LITERAL;

  static String advance() throws Exception {
    return NanoMorphoLexer.advance();
  }

  static String over(int token) throws Exception {
    return NanoMorphoLexer.over(token);
  }
  static String over(char token) throws Exception {
    return NanoMorphoLexer.over(token);
  }

  static int getCurrToken() {
    return NanoMorphoLexer.getCurrToken();
  }
  static int getNextToken() {
    return NanoMorphoLexer.getNextToken();
  }

  static void expected(String token) {
    NanoMorphoLexer.expected(token);
  }

  // Parser starts

  public static void start() throws Exception {
    NanoMorphoLexer.init();
    program();
  }

  private static void program() throws Exception {
    while (getCurrToken() != 0) function();
  }

  static void function() throws Exception {
    over(NAME);
    over('(');

    if (getCurrToken() != ')') {
      for(;;) {
        over(NAME);
        if(getCurrToken() != ',' ) break;
        over(',');
      }
    }

    over(')');
    over('{');

    while (getCurrToken() == VAR) {
      decl();
      over(';');
    }

    while (getCurrToken() != '}') {
      expr();
      over(';');
    }
    over('}');
  }
  
  static void decl() throws Exception {
    over(VAR);

    for(;;) {
      over(NAME);
      if (getCurrToken() != ',') break;
      over(',');
    }
  }

  static void expr() throws Exception {

    if( getCurrToken()==RETURN ) {
        over(RETURN); expr();
    }
    else if (getCurrToken() == NAME && getNextToken() == '=') {
        over(NAME); over('='); expr();
    }
    else {
      binopexpr();
    }
  }

  static void  binopexpr() throws Exception {
    smallexpr();
    while (getCurrToken() == OPNAME) {
      over(OPNAME); smallexpr();
    }
  }

  static void smallexpr() throws Exception {

    switch (getCurrToken()) {
    case NAME:
      over(NAME);

      if (getCurrToken() == '(') {
        over('(');
        if (getCurrToken() != ')') {
            for(;;) {
              expr();
              if (getCurrToken() == ')') break;
              over(',');
            }
        }
        over(')');
      }
      return;
    case WHILE:
      over(WHILE); expr(); body(); return;
    case IF:
      over(IF); expr(); body();

      while (getCurrToken() == ELSIF) {
        over(ELSIF); expr(); body();
      }

      if (getCurrToken() == ELSE) {
        over(ELSE); body();
      }
      return;
    case LITERAL:
      over(LITERAL); return;
    case OPNAME:
      over(OPNAME); smallexpr(); return;
    case '(':
      over('('); expr(); over(')'); return;
    default:
      expected("expression");
    }
  }

  static void body() throws Exception {
    over('{');

    while (getCurrToken() != '}') {
      expr(); over(';');
    }
    over('}');
  }
}