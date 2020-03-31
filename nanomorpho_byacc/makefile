
OUTPUT_DIR=${PWD}/output

PARSER=NanoMorphoParser
TEST_FILE=test
TEST_EXE=${TEST_FILE}.mexe

MORPHO_JAR=../bin/morpho.jar

# Util
printline=$(shell INDEX=0; while [ $${INDEX} -le 80 ]; do printf "-"; INDEX=`expr $$INDEX + 1`; done; printf "\n")

# Colours
blue=\033[0;34m
none=\033[0;m

.PHONY: all main classes flex compiler move clean test

all: main move

main: classes
	@printf "${blue}Making directory <${OUTPUT_DIR}> if it doesn't exist${none}\n"
	if [ ! -d ${OUTPUT_DIR} ]; then mkdir ${OUTPUT_DIR}; fi
	@echo ${printline}

classes: flex
	@printf "${blue}Compiling Lexer, Parser and ParserVal classes${none}\n"
	javac -d output NanoMorphoLexer.java NanoMorphoParser.java NanoMorphoParserVal.java
	@echo ${printline}

flex: compiler
	@printf "${blue}Compiling flex file${none}\n"
	java -jar ../bin/jflex-full-1.7.0.jar nanomorpho.flex
	@echo ${printline}

compiler:
	@printf "${blue}Byacc${none}\n"
	byaccj -Jclass=NanoMorphoParser nanomorpho.byaccj
	@echo ${printline}

move:
	@mv *.java ${OUTPUT_DIR}
	@if [ -f *.java~ ]; then mv *.java~ ${OUTPUT_DIR}; fi

clean:
	rm -rf output

# Testing, generating and running
run: gen # Run .mexe
	@printf "${blue}Running ${TEST_FILE}.mexe${none}\n"
	java -jar ${MORPHO_JAR} ${OUTPUT_DIR}/${TEST_FILE}
	@echo ${printline}
gen: test # Generate .mexe
	@printf "${blue}Compiling ${TEST_FILE}.masm and generating ${TEST_FILE}.mexe${none}\n"
	java -jar ${MORPHO_JAR} -c ${OUTPUT_DIR}/${TEST_FILE}.masm
	@mv ../${TEST_EXE} ${OUTPUT_DIR}/${TEST_EXE}
	@echo ${printline}
test: # Generetae .masm
	@printf "${blue}Testing ${TEST_FILE}.s and generating ${TEST_FILE}.masm${none}\n"
	cd ${OUTPUT_DIR} && java ${PARSER} ../${TEST_FILE}.s
	@mv ${TEST_FILE}.masm ${OUTPUT_DIR}
	@echo ${printline}
