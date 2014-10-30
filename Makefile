.java.class:
	javac -g $*.java

default:
	javac -g RoutingPerfomance.java Workload.java Network.java
clean:
	$(RM) *.class
