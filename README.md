

## Nanomorpho

* [Scanner](nanomorpho/scanner)
  * [pdf](nanomorpho/scanner/latex/scanner.pdf)
* [Parser](nanomorpho/parser)
  * [pdf](nanomorpho/parser/latex/parser.pdf)

## Running the programs

### Makefile

**Compile**
```shell
make
```
**Remove**
```shell
make clean
```
**Run Tests**
```shell
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
