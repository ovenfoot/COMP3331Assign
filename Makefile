.java.class:
	javac -g $*.java

default:
	javac -g RoutingPerformance.java Workload.java Network.java
clean:
	$(RM) *.class
