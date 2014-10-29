.java.class:
	javac -g $*.java

default: router

router: RoutingPerformance.class
RoutingPerformance.class: RoutingPerofrmance.java Workload.class Network.class
Workload.class: Workload.java
Network.class: Network.java

clean:
	$(RM) *.class