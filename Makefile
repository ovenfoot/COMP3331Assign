.java.class:
	javac -g $*.java

default:
	javac -g *.java
clean:
	$(RM) *.class
