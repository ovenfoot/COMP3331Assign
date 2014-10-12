import java.util.*;


public class RoutingPerformance
{
   //private static Path topology_file;
   private static Network network_topology;
   private static Workload workload;
   private static float packetRate;
   private static RoutingProcessor router;
   private static int NETWORKING_SCHEME = 0;
   private static int ROUTING_SCHEME = 1;
   private static int TOPOLOGY_FILE = 2;
   private static int WORKLOAD_FILE = 3;
   private static int PACKET_RATE = 4;
   
   public static void main(String[] args) throws Exception
   {
       //Get command line argument.
   if (args.length != 5) {
      System.out.println("Required arguments: NETWORK_SCHEME ROUTING_SCHEME TOPOLOGY_FILE WORKLOAD_FILE PACKET_RATE");
      return;
   }
   	Request currRequest;
   	network_topology = new Network(args[TOPOLOGY_FILE]);
   	workload = new Workload(args[WORKLOAD_FILE], Integer.parseInt(args[PACKET_RATE]), args[NETWORKING_SCHEME]);
   	packetRate = Float.parseFloat(args[PACKET_RATE]);
   	//System.out.println("Packt Rate: "+packetRate);
   	//workload.element().print();
   	
      //network_topology.get("A").adjacentVertices.get("B").print();
      //network_topology.get("J").adjacentVertices.get("K").print();
   	router = new RoutingProcessor(network_topology);
   	router.setMethod(args[ROUTING_SCHEME]);
   	while(!workload.isEmpty())
      {
      	currRequest = workload.remove();
      	//System.out.println("Packets: "+ currRequest.packets);
      	System.out.println("Computing: " +currRequest.source + " to " +currRequest.dest);
      	currRequest.path = router.computeBestPath(currRequest.source, currRequest.dest);
      	//System.out.println(currRequest.path);
      	if(network_topology.createCircuit(currRequest)==0)
      	{
      		//System.out.println("Success!");
      	}
      	else
      	{
      		//System.out.println("BLOCKED");
      	}
      	
      }
   	
   	//System.out.println(router.computeSHPath("B","K"));
   	
   	//System.out.println(router.computeSHPath("F","E"));
   	
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
		
		if(routing_method.equals("SHP"))
		{
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
				
		}
				
		else if (routing_method.equals("SDP"))
		{
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
		}
		else if (routing_method.equals("LLP"))
		{
			sourceVertex.minDistance = 0;
			vertexQueue.add(sourceVertex);
			while(!vertexQueue.isEmpty())
			{
				u = vertexQueue.remove();
				for (String key : u.adjacentVertices.keySet())
				{
					v = network_topology.get(key);
					distuv = Math.max(u.minDistance,u.adjacentGet(key).load());
					//System.out.println(u.adjacentGet(key).load());
					if(distuv < v.minDistance)
					{
						v.minDistance = distuv;
						v.previous = u;
						vertexQueue.add(v);
					}
				}
					
			}
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
