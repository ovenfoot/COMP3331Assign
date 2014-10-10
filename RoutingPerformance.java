import java.util.*;


public class RoutingPerformance
{
   //private static Path topology_file;
   private static Network network_topology;
   private static Workload workload;
   private static int packetRate;
   private static RoutingProcessor shortHopSearch;
   public static void main(String[] args) throws Exception
   {
      // Get command line argument.
      // if (args.length != 5) {
      //    System.out.println("Required arguments: NETWORK_SCHEME ROUTING_SCHEME TOPOLOGY_FILE WORKLOAD_FILE PACKET_RATE");
      //    return;
      // }
   	Request currRequest;
   	network_topology = new Network(args[0]);
   	workload = new Workload(args[1], Integer.parseInt(args[2]));
   	packetRate = Integer.parseInt(args[2]);
   	//System.out.println("Packt Rate: "+packetRate);
   	//workload.element().print();
   	
      //network_topology.get("A").adjacentVertices.get("B").print();
      //network_topology.get("J").adjacentVertices.get("K").print();
   	shortHopSearch = new RoutingProcessor(network_topology);
   	shortHopSearch.setMethod("SDP");
   	while(!workload.isEmpty())
      {
      	currRequest = workload.remove();
      	//System.out.println("Packets: "+ currRequest.packets);
      	System.out.println("Computing: " +currRequest.source + " to " +currRequest.dest);
      	currRequest.path = shortHopSearch.computeBestPath(currRequest.source, currRequest.dest);
      	System.out.println(currRequest.path);
      	if(network_topology.createCircuit(currRequest)==0)
      	{
      		System.out.println("Success!");
      	}
      	else
      	{
      		System.out.println("BLOCKED");
      	}
      	
      }
   	
   	//System.out.println(shortHopSearch.computeSHPath("B","K"));
   	
   	//System.out.println(shortHopSearch.computeSHPath("F","E"));
   	
   	//network_topology.get("K").print();
   }
}


//Simple dijkstras
class RoutingProcessor
{	
   private static Network network_topology;
   private static String routing_method;
   public RoutingProcessor(Network netIn)
   {
	   network_topology = netIn;
	   routing_method="SHP";
	   
   }
   public void setMethod(String method)
   {
	   routing_method=method;
   }
	public void computeAllPaths(String source)
	{
		Vertex sourceVertex = network_topology.get(source);
		Vertex u,v;
		double distuv;

		Queue<Vertex> vertexQueue = new LinkedList<Vertex>();
		
		switch (routing_method)
		{
			case "SHP":
				sourceVertex.minDistance = 0;
				vertexQueue.add(sourceVertex);
				while(!vertexQueue.isEmpty())
				{
					u = vertexQueue.remove();
					for (String key : u.adjacentVertices.keySet())
					{
						v = network_topology.get(key);
						distuv = u.minDistance + 1;
						if(distuv < v.minDistance)
						{
							v.minDistance = distuv;
							v.previous = u;
							vertexQueue.add(v);
						}
					}
						
				}
				
				break;
				
			case "SDP":
				sourceVertex.minDistance = 0;
				vertexQueue.add(sourceVertex);
				while(!vertexQueue.isEmpty())
				{
					u = vertexQueue.remove();
					for (String key : u.adjacentVertices.keySet())
					{
						v = network_topology.get(key);
						distuv = u.minDistance + u.adjacentVertices.get(key).propDelay;
						if(distuv < v.minDistance)
						{
							v.minDistance = distuv;
							v.previous = u;
							vertexQueue.add(v);
						}
					}
						
				}
				break;
		}
		
		
		
		
	}
	public List<String> computeBestPath(String source, String dest)
	{
		Vertex v;
		List<String>shortestPath = new LinkedList<String>();
		
		computeAllPaths(source);
		
		for (v= network_topology.get(dest); v != null; v = v.previous)
		{   
			shortestPath.add(v.name);
		}
		
		Collections.reverse(shortestPath);
		
		clearPathData();
		
		return shortestPath;
		
	}
	public void clearPathData()
	{
		//Clear out all paths
		for(String key: network_topology.keyset())
		{
			//System.out.println("Clearing : "+ key);
			network_topology.get(key).previous=null;
			network_topology.get(key).minDistance = Double.POSITIVE_INFINITY;
		}
	}
   
	
}
