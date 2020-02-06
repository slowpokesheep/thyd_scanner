NanoMorpho.class: NanoMorpho.java
	javac NanoMorpho.java
NanoMorpho.java: nanomorpho.jflex
	java -jar jflex-full-1.7.0.jar nanomorpho.jflex

clean:
	rm -rf *~ NanoMorpho.class NanoMorpho.java

test: test1

test1: NanoMorpho.class tests/test1.s
	java NanoMorpho tests/test1.s