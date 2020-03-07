
## Make ##

Parser.class: NanoMorpho.class
	javac Parser.java
NanoMorpho.class: NanoMorpho.java
	javac NanoMorpho.java
NanoMorpho.java: nanomorpho.jflex
	java -jar jflex-full-1.7.0.jar nanomorpho.jflex

## Make new ##
new: new2
	javac nanomorpho/NanoMorpho2.java
new2:
	java -jar jflex-full-1.7.0.jar nanomorpho/nanomorpho2.flex

## Maker newclean ##
newclean:
	rm -rf *~ nanomorpho/NanoMorpho2.class Utils/*.class

## Make dev ##
dev:
	javac Parser.java

## Clean ##
clean:
	rm -rf *~ NanoMorpho.class NanoMorpho.java Parser.class

## Test ##

# Scanner

testScanner: testScanner1 testScanner2 testScanner3

testScanner1:
	java NanoMorpho tests/scanner/test1_correct.s | ../util/scanner_output.py
testScanner2:
	java NanoMorpho tests/scanner/test2_correct.s | ../util/scanner_output.py
testScanner3:
	java NanoMorpho tests/scanner/test3_error.s | ../util/scanner_output.py

# Parser

testParser: testParserInvalid1 testParserInvalid2 testParserInvalid3 testParserInvalid4 testParserInvalid5 testParserInvalid6 testParserValid 

testParserInvalid1:
	java Parser tests/parser/test_invalid1.s 2>&1 | ../util/parser_output.py
testParserInvalid2:
	java Parser tests/parser/test_invalid2.s 2>&1 | ../util/parser_output.py
testParserInvalid3:
	java Parser tests/parser/test_invalid3.s 2>&1 | ../util/parser_output.py
testParserInvalid4:
	java Parser tests/parser/test_invalid4.s 2>&1 | ../util/parser_output.py
testParserInvalid5:
	java Parser tests/parser/test_invalid5.s 2>&1 | ../util/parser_output.py
testParserInvalid6:
	java Parser tests/parser/test_invalid6.s 2>&1 | ../util/parser_output.py

testParserValid:
	java Parser tests/parser/test_valid.s 2>&1 | ../util/parser_output.py