import java.io.*;
import java.net.*;
import java.util.*;


public class RoutingPerformance
{
   private static final double LOSS_RATE = 0.3;
   private static final int AVERAGE_DELAY = 100;  // milliseconds
   private static final int TIMEOUT = 1000; //msecs

   public static void main(String[] args) throws Exception
   {
      // Get command line argument.
      // if (args.length != 5) {
      //    System.out.println("Required arguments: NETWORK_SCHEME ROUTING_SCHEME TOPOLOGY_FILE WORKLOAD_FILE PACKET_RATE");
      //    return;
      // }

      Graph g = new Graph();
      // InetAddress IPAddr = InetAddress.getByName(args[0]);
      // int port = Integer.parseInt(args[1]);
   }
}

