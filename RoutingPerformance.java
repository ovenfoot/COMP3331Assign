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
   private static Workload workload;
   private static int packetRate;
   private static SHP shortHopSearch;
   public static void main(String[] args) throws Exception
   {
      // Get command line argument.
      // if (args.length != 5) {
      //    System.out.println("Required arguments: NETWORK_SCHEME ROUTING_SCHEME TOPOLOGY_FILE WORKLOAD_FILE PACKET_RATE");
      //    return;
      // }
   	Request currRequest;
   	network_topology = new Graph(args[0]);
   	workload = new Workload(args[1]);
   	packetRate = Integer.parseInt(args[2]);
   	//System.out.println("Packt Rate: "+packetRate);
   	//workload.element().print();
   	
      //network_topology.get("A").adjacentVertices.get("B").print();
      //network_topology.get("J").adjacentVertices.get("K").print();
   	shortHopSearch = new SHP(network_topology);
   	while(!workload.isEmpty())
      {
      	currRequest = workload.remove();
      	System.out.println("Computing: " +currRequest.source + " to " +currRequest.dest);
      	System.out.println(shortHopSearch.computeSHPath(currRequest.source, currRequest.dest));
      	
      }
   	
   	//System.out.println(shortHopSearch.computeSHPath("B","K"));
   	
   	//System.out.println(shortHopSearch.computeSHPath("F","E"));
   	
   	//network_topology.get("K").print();
   }
}


//Simple dijkstras
class SHP
{	
   private static Graph network_topology;
   public SHP(Graph netIn)
   {
   	network_topology = netIn;
   }
	public void computeAllPaths(String source)
	{
		Vertex sourceVertex = network_topology.get(source);
		Vertex u,v;
		double distuv;

		Queue<Vertex> vertexQueue = new LinkedList<Vertex>();
		
		sourceVertex.minDistance = 0;
		vertexQueue.add(sourceVertex);
		
		
		while(!vertexQueue.isEmpty())
		{
			u = vertexQueue.remove();
			//System.out.println("visiting everynode from " + u.name);
			for (String key : u.adjacentVertices.keySet())
			{
				v = network_topology.get(key);
				//vertexQueue.remove(v);
				distuv = u.minDistance + 1;
				//System.out.println("touching " + v.name);
				if(distuv < v.minDistance)
				{
					v.minDistance = distuv;
					v.previous = u;
					vertexQueue.add(v);
				}
				//System.out.println("complete");
				
				//System.out.println(key);
			}	
		}
	}
	public List<String> computeSHPath(String source, String dest)
	{
		Vertex v,u;
		List<String>shortestPath = new LinkedList<String>();
		computeAllPaths(source);
		for (v= network_topology.get(dest); v != null; v = v.previous)
		{   
			shortestPath.add(v.name);
		}
		Collections.reverse(shortestPath);
		for(String key: network_topology.keyset())
		{
			//System.out.println("Clearing : "+ key);
			network_topology.get(key).previous=null;
			network_topology.get(key).minDistance = Double.POSITIVE_INFINITY;
		}
		
		
		//Clear out path terminations
		return shortestPath;
		
	}
   
	
}
