

## Nanomorpho

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
