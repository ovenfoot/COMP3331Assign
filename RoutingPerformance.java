import java.util.*;

import sun.net.www.protocol.gopher.GopherClient;


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
   public static int numHops;
   public static float cumPropagationDelay;
   public static int totalRequests;
   
   public static void main(String[] args) throws Exception
   {
       //Get command line argument.
   if (args.length != 5) {
      System.out.println("Required arguments: NETWORK_SCHEME ROUTING_SCHEME TOPOLOGY_FILE WORKLOAD_FILE PACKET_RATE");
      return;
   }
   	Request currRequest;
   	
   	//Initialise Network topology and workload based on input files
   	
   	
   	network_topology = new Network(args[TOPOLOGY_FILE]);
   	workload = new Workload(args[WORKLOAD_FILE], Integer.parseInt(args[PACKET_RATE]), args[NETWORKING_SCHEME]);
   	packetRate = Float.parseFloat(args[PACKET_RATE]);
   	numHops = 0;
   	cumPropagationDelay = 0;
   	
   	
   	//Initialise the dijkstra's processor and set the routing scheme
   	router = new RoutingProcessor(network_topology);
   	router.setMethod(args[ROUTING_SCHEME]);
   	
   	
   	
   	//go throug hthe workload and process each request
   	
   	
   	totalRequests = workload.size();
   	while(!workload.isEmpty())
   	{
   		//Pop off the top of the list
      	currRequest = workload.remove();

//      	System.out.println("Packets: "+ currRequest.packets);
//      	System.out.println("Computing: " +currRequest.source + " to " +currRequest.dest + " currtime is: " + currRequest.timestamp + 
//      			" endtime is: " + currRequest.endtime());
      	
      	//Calculate the path using the routing processor
      	currRequest.path = router.computeBestPath(currRequest.source, currRequest.dest);
      	
      	
      	//System.out.println(currRequest.path);
      	
      	//Sum up the number of hops and propagation delay on the path
      	numHops+= network_topology.numHops(currRequest);
      	cumPropagationDelay+=network_topology.calculateCumPropDelay(currRequest);
      	
      	
      	
      	//createCircuit returns 0 if path is successful. nonzero if blocked
      	if(network_topology.createCircuit(currRequest)==0)
      	{
      		//System.out.println("Success!");
      	}
      	else
      	{
      		//System.out.println("BLOCKED");
      	}
      	
      }
   	
   	
   	//Print out all analytics
    int totVirtualCircuitRequests = workload.vcRequestCount;
    int totPackets = workload.packetRequestCount;
    int successfulPackets = network_topology.successfullyRoutedCount;
    float percentageSuccessPackets = (float) (((float) successfulPackets/(float) totPackets) * 100.0);
    int blockedPackets = network_topology.blockedCount;
    float perecentageBlockedPackets = (float) (((float) blockedPackets/ (float) totPackets) * 100.0);
    float averageNumHops = (float) numHops/ (float) totalRequests;
    float averageCumPropDelay = (float) cumPropagationDelay/ (float) totalRequests;
    
    System.out.println("total number of virtual circuit requests: " + totVirtualCircuitRequests);
    System.out.println("total number of packets: " + totPackets);
    System.out.println("number of successfully routed packets: " + successfulPackets);
    System.out.format("percentage of successfully routed packets: %.2f\n", percentageSuccessPackets);
    System.out.println("number of blocked packets: " + blockedPackets);
    System.out.format("percentage of blocked packets: %.2f\n", perecentageBlockedPackets);
    System.out.format("average number of hops per circuit: %.2f\n" , averageNumHops);
    System.out.format("average cumulative propagation delay per circuit: %.2f\n" , averageCumPropDelay);
    
    
   
   }
   
   
}


//Simple dijkstras
class RoutingProcessor
{	
   private static Network network_topology;
   private static String routing_method;
   
   /*
    * Main constructor . Takes in the network topology as an input 
    */
   public RoutingProcessor(Network netIn)
   {
	   network_topology = netIn;
	   routing_method="SHP";
	   
   }
   
   /*
    * Sets the routing method
    * Takes string as input
    * "SHP", "SDP", "LLP"
    */
   public void setMethod(String method)
   {
	   routing_method=method;
   }
   
   
   /*
    * Compute best path between two nodes as specified by their string name
    */
   public List<String> computeBestPath(String source, String dest)
   {
		Vertex v;
		List<String>shortestPath = new LinkedList<String>();
		
		//First, relax all nodes
		computeAllPaths(source);
		
		//Then go through each onde and return the path as a list of strings
		for (v= network_topology.get(dest); v != null; v = v.previous)
		{   
			shortestPath.add(v.name);
		}
		
		Collections.reverse(shortestPath);
		
		//Clear the relaxed values so that they can be recalculated with each term
		clearPathData();
		
		return shortestPath;
		
	}
   
   /*
    * First step in dijkstras is to relax each node from a source
    * Switches between methods based on routng_method
    */
	public void computeAllPaths(String source)
	{
		Vertex sourceVertex = network_topology.get(source);
		Vertex u,v;
		double distuv;

		Queue<Vertex> vertexQueue = new LinkedList<Vertex>();
		
		
		//Switch between methods and decide what to do
		if(routing_method.equals("SHP"))
		{
			//SHP, weight of each edge is 1
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
			
			//SDP, weight of each edge is it's propagation delay
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
			//LLP, weight each edge is activeCircuits/AvailableCircuits
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
	
	//Delete all shortest path data
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
