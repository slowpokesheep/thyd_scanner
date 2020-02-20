NanoMorpho.class: NanoMorpho.java
	javac NanoMorpho.java
NanoMorpho.java: nanomorpho.jflex
	java -jar jflex-full-1.7.0.jar nanomorpho.jflex

Parser.class: Parser.java
	javac Parser.java

clean:
	rm -rf *~ NanoMorpho.class NanoMorpho.java

test: test1 test2 test3

test1:
	java NanoMorpho tests/test1.s | ../../util/test_output.py

test2:
	java NanoMorpho tests/test2.s | ../../util/test_output.py

test3:
	java NanoMorpho tests/test3.s | ../util/test_output.py

testparser1:
	java Parser tests/testparser_correct1.s | ../util/test_output.py
