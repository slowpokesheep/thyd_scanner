
## Make ##

Parser.class: NanoMorpho.class
	javac Parser.java
NanoMorpho.class: NanoMorpho.java
	javac NanoMorpho.java
NanoMorpho.java: nanomorpho.jflex
	java -jar jflex-full-1.7.0.jar nanomorpho.jflex

## Clean ##
clean:
	rm -rf *~ NanoMorpho.class NanoMorpho.java Parser.class

## Test ##

# Scanner

testScanner: testScanner1 testScanner2 testScanner3

testScanner1:
	java NanoMorpho tests/scanner/test1_correct.s | ../util/test_output.py

testScanner2:
	java NanoMorpho tests/scanner/test3_correct.s | ../util/test_output.py

testScanner3:
	java NanoMorpho tests/scanner/test3_error.s | ../util/test_output.py

# Parser

testParser: testParser1

testParser1:
	java Parser tests/parser/test1_correct.s | ../util/test_output.py
