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
   	network_topology = new Graph(args[0]);
   	workload = new Workload(args[1]);
   	packetRate = Integer.parseInt(args[2]);
   	//System.out.println("Packt Rate: "+packetRate);
   	//workload.element().print();
   	
      //network_topology.get("A").adjacentVertices.get("B").print();
      //network_topology.get("J").adjacentVertices.get("K").print();
      
      shortHopSearch = new SHP(network_topology);
      System.out.println(shortHopSearch.computeSHPath("A", "B"));
      
   }
}


//Simple dijkstras
class SHP
{	
   private static Graph network_topology;
   private static Workload workload;
   private static int packetRate;
   private static Queue<Vertex> vertexQueue;
   public List<String> shortestPath; 
   public SHP(Graph netIn)
   {
   	network_topology = netIn;
   }
	public void computeAllPaths(String source)
	{
		Vertex sourceVertex = network_topology.get(source);
		Vertex u,v;
		double distuv;

		vertexQueue = new LinkedList<Vertex>();
		
		sourceVertex.minDistance = 0;
		vertexQueue.add(sourceVertex);
		
		while(!vertexQueue.isEmpty())
		{
			u = vertexQueue.poll();
			//System.out.println("visiting everynode from " + u.name);
			for (String key : u.adjacentVertices.keySet())
			{
				v = network_topology.get(key);
				
				distuv = u.minDistance + 1;
				//System.out.println("touching " + v.name);
				if(distuv < v.minDistance)
				{
					vertexQueue.remove(v);
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
		Vertex v;
		shortestPath = new LinkedList<String>();
		computeAllPaths(source);
		for (v= network_topology.get(dest); v != null; v = v.previous)
		{   
			shortestPath.add(v.name);
		}
		Collections.reverse(shortestPath);
		return shortestPath;
		
	}
   
	
}

/*
public class Dijkstra
{
    public static void computePaths(Vertex source)
    {
        source.minDistance = 0.;
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
      	vertexQueue.add(source);

	while (!vertexQueue.isEmpty()) {
	    Vertex u = vertexQueue.poll();

            // Visit each edge exiting u
            for (Edge e : u.adjacencies)
            {
                Vertex v = e.target;
                double weight = e.weight;
                double distanceThroughU = u.minDistance + weight;
		if (distanceThroughU < v.minDistance) {
		    vertexQueue.remove(v);
		    v.minDistance = distanceThroughU ;
		    v.previous = u;
		    vertexQueue.add(v);
		}
            }
        }
    }

    public static List<Vertex> getShortestPathTo(Vertex target)
    {
        List<Vertex> path = new ArrayList<Vertex>();
        for (Vertex vertex = target; vertex != null; vertex = vertex.previous)
            path.add(vertex);
        Collections.reverse(path);
        return path;
    }

    public static void main(String[] args)
    {
        Vertex v0 = new Vertex("Redvile");
	Vertex v1 = new Vertex("Blueville");
	Vertex v2 = new Vertex("Greenville");
	Vertex v3 = new Vertex("Orangeville");
	Vertex v4 = new Vertex("Purpleville");

	v0.adjacencies = new Edge[]{ new Edge(v1, 5),
	                             new Edge(v2, 10),
                               new Edge(v3, 8) };
	v1.adjacencies = new Edge[]{ new Edge(v0, 5),
	                             new Edge(v2, 3),
	                             new Edge(v4, 7) };
	v2.adjacencies = new Edge[]{ new Edge(v0, 10),
                               new Edge(v1, 3) };
	v3.adjacencies = new Edge[]{ new Edge(v0, 8),
	                             new Edge(v4, 2) };
	v4.adjacencies = new Edge[]{ new Edge(v1, 7),
                               new Edge(v3, 2) };
	Vertex[] vertices = { v0, v1, v2, v3, v4 };
        computePaths(v0);
        for (Vertex v : vertices)
	{
	    System.out.println("Distance to " + v + ": " + v.minDistance);
	    List<Vertex> path = getShortestPathTo(v);
	    System.out.println("Path: " + path);
	}
    }
}*/