#!/bin/sh
javac *.java
java RoutingPerformance PACKET LLP topology.txt workload.txt 1 
