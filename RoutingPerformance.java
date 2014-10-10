import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RoutingPerformance
{
   //private static Path topology_file;
   private static Graph network_topology;
   public static void main(String[] args) throws Exception
   {
      // Get command line argument.
      // if (args.length != 5) {
      //    System.out.println("Required arguments: NETWORK_SCHEME ROUTING_SCHEME TOPOLOGY_FILE WORKLOAD_FILE PACKET_RATE");
      //    return;
      // }
   	network_topology = new Graph(args[0]);
      network_topology.get("A").get("B").print();
      network_topology.get("J").get("K").print();
   }
}

