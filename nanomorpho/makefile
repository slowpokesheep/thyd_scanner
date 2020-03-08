
## Make ##
main: parser
	javac nanomorpho/NanoMorphoCompiler.java
parser: scanner
	javac nanomorpho/NanoMorphoParser.java
scanner: flex
	javac nanomorpho/NanoMorpho.java
flex:
	java -jar jflex-full-1.7.0.jar nanomorpho/nanomorpho.flex

## Maker clean ##
clean:
	rm -rf nanomorpho/NanoMorpho.java nanomorpho/*~ nanomorpho/*.class

## Test ##

test:
	java nanomorpho/NanoMorpho tests/valid.s

errortest: test1 test2 test3 test4 test5 test6

test1:
	-java nanomorpho/NanoMorpho tests/invalid01.s
test2:
	-java nanomorpho/NanoMorpho tests/invalid02.s
test3:
	-java nanomorpho/NanoMorpho tests/invalid03.s
test4:
	-java nanomorpho/NanoMorpho tests/invalid04.s
test5:
	-java nanomorpho/NanoMorpho tests/invalid05.s
test6:
	-java nanomorpho/NanoMorpho tests/invalid06.s