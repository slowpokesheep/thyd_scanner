

## Nanomorpho

* [Scanner](nanomorpho/nanomorpho.jflex)
  * [pdf](nanomorpho/latex/scanner/scanner.pdf)
* [Parser](nanomorpho/Parser.java)
  * [pdf](nanomorpho/latex/parser/parser.pdf)

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
make testScanner
```
```
make testParser
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
