package nanomorpho;

import java.util.Vector;
import java.util.Arrays;
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

  // Intermediate code element identification strings
  enum type {
    RETURN, STORE, OR, AND, NOT, CALL, FETCH, LITERAL, IF, WHILE, BODY
  }

  // Expressions:
  // ["RETURN",expr]
  // ["STORE",pos,expr]
  // ["OR",expr,expr]
  // ["AND",expr,expr]
  // ["NOT",expr]
  // ["CALL",name,args]
  // ["FETCH",pos]
  // ["LITERAL",string]
  // ["IF",expr,expr,expr]
  // ["WHILE",expr,expr]
  // ["BODY",exprs]

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

  static String getCurrLexeme() {
    return NanoMorphoLexer.getCurrLexeme();
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
    generateProgram(NanoMorphoLexer.filename, code);
  }

  public static Object[] program() throws Exception {

    Vector<Object> programInfo = new Vector<>();

    // Get information about all the functions
    while (getCurrToken() != 0) {
      programInfo.add(function());
    }

    return programInfo.toArray();
  }

  // returns: [functionName, argCount, varCount, exprs]
  public static Object[] function() throws Exception {
    int argCount = 0;
    varCount = 0;
    varTable = new HashMap<String, Integer>();

    Vector<Object> info = new Vector<>();

    info.add(over(NAME));
    over('(');

    // Check for arguments
    if (getCurrToken() != ')') {
      for(;;) {
        addVar(over(NAME));
        argCount++;
        if(getCurrToken() != ',' ) break;
        over(',');
      }
    }

    over(')');
    over('{');

    // Check for variable declerations
    while (getCurrToken() == VAR) {
      varCount = decl();
      over(';');
    }

    Vector<Object> exprs = new Vector<>();
    // Check for expresions
    while (getCurrToken() != '}') {
      exprs.add(expr());
      over(';');
    }
    over('}');

    info.add(argCount);
    info.add(varCount);
    info.add(exprs.toArray());

    return info.toArray();
  }

  // Variable declerations, example
  // var varName, varName2;
  public static int decl() throws Exception {
    int varCount = 0;
    over(VAR);

    // Check for additional variable declerations
    for(;;) {
      addVar(over(NAME));
      varCount++;
      if (getCurrToken() != ',') break;
      over(',');
    }

    return varCount;
  }

  static Object[] expr() throws Exception {

    if (getCurrToken() == RETURN) {
        over(RETURN);
        return new Object[] { type.RETURN, expr() };
    }
    else if (getCurrToken() == NAME && getNextToken() == '=') {
      String varName = over(NAME);
      over('=');
      return new Object[] { type.STORE, findVar(varName), expr() };
    }
    else {
      return binopexpr(1);
    }
  }

  static Object[] binopexpr(int pri) throws Exception {
    if (pri > 7) {
      return smallexpr();
    }
    else if (pri == 2) {
      Object[] e = binopexpr(3);
      if (getCurrToken() == OPNAME && priority(NanoMorphoLexer.getCurrLexeme()) == 2) {
        String op = advance();
        e = new Object[] { type.CALL, op, new Object[] { e, binopexpr(2) } };
      }
      return e;
    }
    else {
      Object[] e = binopexpr(pri + 1);
      while (getCurrToken() == OPNAME && priority(NanoMorphoLexer.getCurrLexeme()) == pri) {
        String op = advance();
        e = new Object[] { type.CALL, op, new Object[] { e, binopexpr(pri + 1) } };
      }
      return e;
    }
  }

  static int priority(String opname) {
    switch (opname.charAt(0)) {
      case '^':
      case '?':
      case '~':
          return 1;
      case ':':
          return 2;
      case '|':
          return 3;
      case '&':
          return 4;
      case '!':
      case '=':
      case '<':
      case '>':
          return 5;
      case '+':
      case '-':
          return 6;
      case '*':
      case '/':
      case '%':
          return 7;
      default:
          throw new Error("Invalid opname");
      }
  }

  static Object[] smallexpr() throws Exception {

    String varName;
    Vector<Object> e = new Vector<>();
    Vector<Object> exprs = null;

    switch (getCurrToken()) {
      case NAME:
        varName = over(NAME);

        if (getCurrToken() == '(') {
          over('(');
          if (getCurrToken() != ')') {

            exprs = new Vector<>();
            for(;;) {
              exprs.add(expr());
              if (getCurrToken() == ')') break;
              over(',');
            }

          }
          over(')');
        }
      return new Object[] { type.CALL, varName, exprs };
    case WHILE:
      over(WHILE);
      return new Object[] { type.WHILE, expr(), body() };
    case IF:
      over(IF);
      e.add(new Object[] { type.IF, expr(), body(), null });

      while (getCurrToken() == ELSIF) {
        over(ELSIF);
        e.add(new Object[] { type.IF, expr(), body(), null });
      }

      if (getCurrToken() == ELSE) {
        over(ELSE);
        e.add(new Object[] { type.IF, true, body(), null });
      }
      return e.toArray();
    case LITERAL:
      varName = over(LITERAL);
      return new Object[] { type.LITERAL, varName };
    case OPNAME:
      varName = over(OPNAME);
      return new Object[] { type.CALL, varName, smallexpr() };
    case '(':
      over('(');
      exprs = new Vector<>();
      exprs.add(expr());
      over(')');
      return exprs.toArray();
    default:
      expected("expression");
      return null;
    }
  }

  static Object[] body() throws Exception {
    Vector<Object> exprs = new Vector<>();

    over('{');

    while (getCurrToken() != '}') {
      exprs.add(expr());
      over(';');
    }
    over('}');
    return new Object[] { type.BODY, exprs.toArray() };
  }

  static void print(String s) {
    System.out.println(s);
  }

  // Final code

  static void generateProgram(String filename, Object[] funs) {
    String programname = filename.substring(0,filename.indexOf('.'));
    System.out.println("\""+programname+".mexe\" = main in");
    System.out.println("!");
    System.out.println("{{");
    
    for (Object f: funs) {
      generateFunction((Object[]) f);
    }

    print("}}");
    print("*");
    print("BASIS;");
  }

  // [functionName, argCount, varCount, exprs]
  static void generateFunction(Object[] fun) {
    String functionName = (String) fun[0];
    int argCount = (Integer) fun[1];
    int varCount = (Integer) fun[2];
    Object[] exprs = (Object[]) fun[3];

    print("#\"" +functionName+ "[f" +argCount+ "]\" =");
    print("[");


    for (int i = 0; i < argCount; ++i) {
      print("(MakeVal null)");
      print("(Push)");
    }

    for (Object e: exprs) {
      generateExpr((Object[]) e);
    }


    print("(Return)");
    print("];");
  }

  static void generateExpr(Object[] e) {
    //
  }
}