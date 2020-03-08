package nanomorpho;

import java.util.Vector;
import java.util.HashMap;

public class NanoMorphoCompiler {

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

  // Forward one lexeme.
  // Returns the lexeme advanced over.
  static String advance() throws Exception {
    return NanoMorphoLexer.advance();
  }

  // Forward one lexeme which must have the given token.
  // Returns the lexeme advanced over.
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

  // The symbol table consists of the following two variables.
  private static int varCount;
  private static HashMap<String,Integer> varTable;

  // Adds a new variable to the symbol table.
  // Throws Error if the variable already exists.
  private static void addVar( String name ) {

    if (varTable.get(name) != null) {
      throw new Error("Variable "+name+" already exists, near line "+NanoMorphoLexer.getNextLine());
    }
      varTable.put(name,varCount++);
  }
  
  // Finds the location of an existing variable.
  // Throws Error if the variable does not exist.
  private static int findVar( String name ) {
    Integer res = varTable.get(name);

    if (res == null) {
      throw new Error("Variable "+name+" does not exist, near line "+NanoMorphoLexer.getNextLine());
    }
      return res;
  }

  // Compiler  Intermediate Code

  public static void start() throws Exception {
    Object[] code = null;

    try {
      NanoMorphoLexer.init();
      code = program();
    }
    catch (Throwable e) {
      System.err.println(e.getMessage());
    }
    //generateProgram(args[0], code);
  }

  public static Object[] program() throws Exception {

    while (getCurrToken() != 0) function();

    return new Object[] {};
  }

  public static Object[] function() throws Exception {
    varCount = 0;
    varTable = new HashMap<String, Integer>();

    addVar(over(NAME));
    over('(');

    if (getCurrToken() != ')') {
      for(;;) {
        addVar(over(NAME));
        if(getCurrToken() != ',' ) break;
        over(',');
      }
    }

    over(')');
    over('{');

    while (getCurrToken() == VAR) {
      varCount += decl();
      over(';');
    }

    while (getCurrToken() != '}') {
      //expr();
      over(';');
    }
    over('}');

    return new Object[] {};
  }

  public static int decl() throws Exception {
    int varCount = 1;


    return varCount;
  }
}