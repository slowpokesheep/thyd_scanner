
## Make ##
main: flex
	javac NanoMorphoLexer.java NanoMorphoParser.java NanoMorphoParserVal.java
flex: compiler
	java -jar ../bin/jflex-full-1.7.0.jar nanomorpho.flex
compiler:
	byaccj -Jclass=NanoMorphoParser nanomorpho.byaccj

## Maker clean ##
clean:
	rm -rf *.java *~ *.class

test:
	java NanoMorphoParser ignore_test.s
