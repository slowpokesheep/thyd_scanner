NanoMorpho.class: NanoMorpho.java
	javac NanoMorpho.java
NanoMorpho.java: nanomorpho.jflex
	java -jar jflex-full-1.7.0.jar nanomorpho.jflex
clean:
	rm -rf *~ NanoMorpho.class NanoMorpho.java
tests: test1
test1: NanoMorpho.class test.s
	java NanoMorpho test.s
