package nanomorpho;

import java.io.PrintWriter;
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

  static PrintWriter writer;

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
    String filename = NanoMorphoLexer.filename;
    String programname = filename.substring(0, filename.indexOf('.'));
    writer = new PrintWriter(programname+".masm", "UTF-8");
    generateProgram(programname, code);
    writer.close();
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
    // Check for expressions
    while (getCurrToken() != '}') {
      // prump
      Object[] ex = expr();
      // single expression
      if (ex[0].getClass().isEnum()) {
        exprs.add(ex);
      }
      // array of expressions
      else {
        for (Object e: ex) {
          exprs.add(e);
        }
      }
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
    Vector<Object[]> ifexpr = new Vector<>();
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
        else {
          return new Object[] { type.FETCH, findVar(varName) };
        }
      return new Object[] { type.CALL, varName, exprs.toArray()};
    case WHILE:
      over(WHILE);
      return new Object[] { type.WHILE, expr(), body() };
    case IF:
      over(IF);
      ifexpr.add(new Object[] { type.IF, expr(), body(), null });

      while (getCurrToken() == ELSIF) {
        over(ELSIF);
        Object[] e = new Object[] { type.IF, expr(), body(), null };

        if (ifexpr.size() == 1) ifexpr.get(0)[3] = e;
        else ifexpr.get(ifexpr.size() - 1)[3] = e;
      }

      if (getCurrToken() == ELSE) {
        over(ELSE);
        Object[] e = new Object[] { type.IF, true, body(), null };
        if (ifexpr.size() == 1) ifexpr.get(0)[3] = e;
        else ifexpr.get(ifexpr.size() - 1)[3] = e;
      }
      return ifexpr.toArray();
    case LITERAL:
      varName = over(LITERAL);
      return new Object[] { type.LITERAL, varName };
    case OPNAME:
      varName = over(OPNAME);
      return new Object[] { type.CALL, varName, smallexpr() };
    case '(':
      over('(');
      Object[] ex = expr();
      over(')');
      return ex;
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
    //System.out.println(s);
    writer.println(s);
  }

  // Final code

  static void generateProgram(String programname, Object[] funs) {
    print("\""+programname+".mexe\" = main in");
    print("!");
    print("{{");
    
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


    for (int i = 0; i < varCount; ++i) {
      print("(MakeVal null)");
      print("(Push)");
    }

    for (Object e: exprs) {
      generateExpr((Object[]) e);
    }


    print("(Return)");
    print("];");
  }
  
  // All existing labels, i.e. labels the generated
  // code that we have already produced, should be
  // of form
  //    _xxxx
  // where xxxx corresponds to an integer n
  // such that 0 <= n < nextLab.
  // So we should update nextLab as we generate
  // new labels.
  // The first generated label would be _0, the
  // next would be _1, and so on.
  private static int nextLab = 0;
  
  // Returns a new, previously unused, label.
  // Useful for control-flow expressions.
  static String newLabel() {
      return "_"+(nextLab++);
  }

  static int newIntLabel() {
    return nextLab++;
  }

  // RETURN, STORE, OR, AND, NOT, CALL, FETCH, LITERAL, IF, WHILE, BODY
  static void generateExpr(Object[] e) {

    switch((type) e[0]) {
      case RETURN: // ["RETURN", expr]
        generateExpr((Object[]) e[1]);
        print("(Return)");
        break;
      case STORE: // ["STORE", pos, expr]
        generateExpr((Object[]) e[2]);
        print("(Store "+e[1]+")");
        break;
      case NOT: // ["NOT", expr]
        generateExpr((Object[]) e[1]);
        print("(Not)");
        break;
      case CALL: // ["CALL", name, args]
        Object[] args = (Object[]) e[2];

        for (Object arg: args) {
          print("(Push)");
          generateExpr((Object[]) arg);
        }

        print("(Call #\""+e[1]+"[f"+args.length+"]\" "+args.length+")");
        break;
      case FETCH: // ["FETCH", pos]
        print("(Fetch "+e[1]+")");
        break;
      case LITERAL:
        print("(MakeVal "+e[1]+")");
        break;
      case IF: // ["IF", expr, expr, expr]
        
        int labelElse = newIntLabel();
        int labelEnd = newIntLabel();

        Object[] then = (Object[]) e[2];

        if ((e[1] instanceof Boolean)) {
          generateExpr(then);
          print("(Go _"+labelEnd+")");
          print("_"+labelElse+":");
          print("_"+labelEnd+":");
          return;
        }
        Object[] cond = (Object[]) e[1];

        generateJump(cond, 0, labelElse);
        generateExpr(then);

        print("(Go _"+labelEnd+")");
        print("_"+labelElse+":");

        Object[] els = (Object[]) e[3];
        if (els != null) {
          generateExpr(els);
        }

        print("_"+labelEnd+":");
        break;
      case WHILE: // ["WHILE", expr, expr]
        String labelStart = newLabel();
        String labelStop = newLabel();

        print("(Go "+labelStop+")");
        print(""+labelStart+":");

        generateBody((Object[]) e[2]);

        print(""+labelStop+":");

        generateExpr((Object[]) e[1]);
        print("(GoTrue "+labelStart+")");
        break;
      case BODY: // ["BODY", expr]
        generateBody(e);
        break;
      default:
        break;
    }
  }

  static void generateBody(Object[] e) {
    
    for (Object expr: (Object[]) e[1]) {
      generateExpr((Object[]) expr);
    }
  }

  private static void generateJump(Object[] e, int labelTrue, int labelFalse) {
    generateExpr(e);
    if (labelTrue != 0 ) print("(GoTrue _"+labelTrue+")");
    if (labelFalse != 0 ) print("(GoFalse _"+labelFalse+")");
  }
}