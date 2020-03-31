

## Nanomorpho Byacc - Bot-Up
* [Jflex](nanomorpho_byacc/nanomorpho.flex)
* [byacc](nanomorpho_byacc/nanomorpho.byaccj)
* [test](nanomorpho_byacc/test.s)
* [makefile](nanomorpho_byacc/makefile)
* [pdf](nanomorpho_byacc/latex/nanomorpho_byacc.pdf)

## Nanomorpho - Top-down

* [Jflex](nanomorpho/nanomorpho/nanomorpho.flex)
* [Lexer](nanomorpho/nanomorpho/NanoMorphoLexer.java)
  * [pdf](nanomorpho/latex/scanner/scanner.pdf)
* [Parser](nanomorpho/nanomorpho/NanoMorphoParser.java)
  * [pdf](nanomorpho/latex/parser/parser.pdf)
* [Compiler](nanomorpho/nanomorpho/NanoMorphoCompiler.java)
  * [pdf](nanomorpho/latex/compiler/compiler.pdf)

## Running the program

### Makefile

**Compile**
```
make
```
**Run** (Only for nanomorpho_byacc)
```
make run
```
**Remove**
```
make clean
```
**Run Tests**
```
make test
```

### Manual

**Compile**
```java
java -jar JFlex-full-1.7.0.jar nanomorpho.jflex
javac NanoMorpho.java
```

**Run**
```java
java NanoMorpho <input_file> > <output_file>
```
