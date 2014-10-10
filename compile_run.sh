#!/bin/sh
javac *.java
java RoutingPerformance topology.txt workload.txt 2
