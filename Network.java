import java.util.*;
import java.io.*;

/*
 * Network class
 * Contains the graph of network nodes and edges
 * Creates a graph of nodes and edges based on input filename
 * Network nodes are represented by Class Vertex.
 * Each vertex has a hash table of 'Class Edge' keyed to adjacent nodes
 * Each edge contains connection data, including prop delay and loading
 */
public class Network
{
	Comparator<Request> rCompare = new RequestComparator();
	HashMap<String, Vertex> nodes;
	PriorityQueue<Request> activeVirtualCircuits;
	public int successfullyRoutedCount;
	public int blockedCount;

	
	//Rubbish constructor
	public Network()
	{
		 nodes = new HashMap<String, Vertex>();
		 System.out.println("WOOO");
	}
	
	/*
	 * Main constructor. Initialises the network graph based on input filename
	 * Parses filename line by line to grab the edge data
	 */
	public Network (String filename) throws IOException
	{
		nodes = new HashMap<String, Vertex>();
		activeVirtualCircuits = new PriorityQueue<Request>(10, rCompare);
		BufferedReader instream = new BufferedReader(new FileReader(filename));
		String inputLine;
		
		successfullyRoutedCount = 0;
		blockedCount = 0;
		
		while(instream.ready())
		{
			inputLine = instream.readLine();
			parse(inputLine);
			//System.out.println(inputLine);
		}
		//get("A").get("B").print();
		
		instream.close();
	}
	
	/*
	 * parse line from input file to establish graph data
	 * Foreach line, parse source and dest and create edge.
	 * Store source, dest and edge in their respective hash tables
	 * Make sure each edge is associated with each node according to dest string
	 */
	public void parse (String line)
	{
		Edge thisEdge = new Edge();
		Vertex thisVertex;
		
		String params[] = line.split(" ");
	
		//Create a new edge based on the line data
		thisEdge.sourceName = params[0];
		thisEdge.destName = params[1];
		thisEdge.propDelay = Integer.parseInt(params[2]);
		thisEdge.vcCapacity = Integer.parseInt(params[3]);
		
		
		//See if source of the edge exists as a vertex already
		//If not, create one
		if(nodes.containsKey(thisEdge.sourceName))
		{
			thisVertex = nodes.get(thisEdge.sourceName);
			
		}
		else
		{
			thisVertex = new Vertex();
			thisVertex.name = thisEdge.sourceName;
		}
		
		//Place edge into the source node hash table based on destination name
		thisVertex.adjacentVertices.put(thisEdge.destName, thisEdge);
		nodes.put(thisEdge.sourceName, thisVertex);
		
		
		//Repeat the above process for the destination node
		if(nodes.containsKey(thisEdge.destName))
		{
			thisVertex = nodes.get(thisEdge.destName);
		}
		else
		{
			thisVertex = new Vertex();
			thisVertex.name = thisEdge.destName;
		}
		
		thisVertex.adjacentVertices.put(thisEdge.sourceName, thisEdge);
		nodes.put(thisEdge.destName, thisVertex);
	}
	
	/*
	 * Function that creates a circuit based on a request with a path
	 * Parses the path and activates all edges along the path
	 * If an edge is overfull, return -1 to indicate a blocked request
	 */
	public int createCircuit (Request request)
	{
		Edge currEdge;
		List<String> vertices = request.path;
		
		//First check list of active VCs to see if anything can be freed
		scrubObsoleteVCs(request.timestamp);
		
		for(int i=1; i<vertices.size(); i++)
		{
			//Get the edge using the associated hash between vertex[i-1] and vertex[i]
			currEdge = nodes.get(vertices.get(i-1)).adjacentGet(vertices.get(i));
			
			//With each edge, increment the number of active virtual circuits
			if (currEdge.activeVCs < currEdge.vcCapacity)
			{
				//System.out.println("Linking "+vertices.get(i-1) + " and " + vertices.get(i));
				//System.out.println("Active Links count: "+currEdge.activeVCs+" Capacity is: "+currEdge.vcCapacity );
				
				currEdge.activeVCs++;
			}
			else
			{
				//System.out.println("Blocking "+vertices.get(i-1) + " and " + vertices.get(i));
				//System.out.println("Active Links count: "+currEdge.activeVCs+" Capacity is: "+currEdge.vcCapacity );
				
				//If active VCs is at capacity, rollback all the conections already made for this request
				for(int j=i-1; j>0; j--)
				{
					currEdge = nodes.get(vertices.get(j-1)).adjacentGet(vertices.get(j));
					currEdge.activeVCs--;
				}
				//System.out.println("Blocked");

				//return non zero to indicate blocked
				blockedCount+=request.packets;
				return 1;
			}
			
		}
		
		//Activate the request and add it to the ative queue
		request.active = true;
		activeVirtualCircuits.add(request);
		successfullyRoutedCount+=request.packets;
		return 0;
	}
	/*
	 * Function that deletes all VCs that have already passed their end time
	 * Checks entire active list to see if there is anythin which is void
	 * Deletes from void list and frees up edge
	 */
	public void scrubObsoleteVCs(float time)
	{
		//System.out.println("Trying to srub at time "+ time);
		if(!activeVirtualCircuits.isEmpty())
		{
			Request currRequest = activeVirtualCircuits.peek();
			
			while(!activeVirtualCircuits.isEmpty() && time > currRequest.endtime())
			{
				currRequest = activeVirtualCircuits.remove();
//				System.out.println("Scrubbing VC between "+currRequest.source +" and "+ currRequest.dest +
//						" endtime: " +currRequest.endtime());
//				System.out.println("Current time is "+ time);
				deleteVC(currRequest);
				
				
			}
		}
	}
	
	/*
	 * Goes through each edge of the request and frees the connection for others to use
	 */
	public void deleteVC(Request request)
	{
		Edge currEdge;
		List<String> vertices = request.path;
		
		for(int i=1; i<vertices.size(); i++)
		{
			currEdge = nodes.get(vertices.get(i-1)).adjacentGet(vertices.get(i));		
			currEdge.activeVCs--;
		}
	}
	
	//calculate the propagation delay of a request, given an initiated path
	public float calculateCumPropDelay(Request request)
	{
		float totalPropDelay = 0;
		Edge currEdge;
		List <String> vertices = request.path;
		
		//Go through each edge in the path and sum up propagation delay
		for(int i=1; i<vertices.size(); i++)
		{
			currEdge = nodes.get(vertices.get(i-1)).adjacentGet(vertices.get(i));
			totalPropDelay+=currEdge.propDelay;
		}
		
		return totalPropDelay;
	}
	
	
	//calculate number of hops by looking at path size
  	//Take away 1 because of the fencepost problem i.e. (A-B) is only one hop
	public int numHops(Request request) 
	{
		return request.path.size();
	}
	
	public Vertex get(String key)
	{
		return nodes.get(key);
	}
	public Set<String> keyset()
	{
		return nodes.keySet();
	}
}


/*
 * Vertex class representing a network node
 * Contains the name of the node and a hashmap of edges
 * Edges are keyed by their destination node name (as a string)
 * adjacent.get(string s) returns the edge at string s
 */
class Vertex
{
	String name;
	Vertex previous;
	HashMap<String, Edge> adjacentVertices;
	double minDistance = Double.POSITIVE_INFINITY;
	public Vertex()
	{
		adjacentVertices  = new HashMap<String, Edge>();
		minDistance =Double.POSITIVE_INFINITY;
		name = "\0";
		previous = null;
	}
	public void print()
	{
		System.out.println("Node: "+name+" connected to "+ adjacentVertices.keySet());
	}
	public Edge adjacentGet(String s)
	{
		return adjacentVertices.get(s);
	}
}


/*
 * Edge class.
 * Contains data about propdelay and congestion in a particular network hop
 */
class Edge
{
	String destName;
	String sourceName;
	float propDelay;
	int vcCapacity;
	int activeVCs=0;
	
	public void print ()
	{
		System.out.println("Node1: " + destName +
			"|Node 2: " + sourceName + 
			"|propDelay: " + propDelay + 
			"|vcCapacity: " + vcCapacity);
	}
	public double load ()
	{
		float intermediateRes;
		intermediateRes = ((float)activeVCs/(float)vcCapacity);
		//System.out.println(intermediateRes);
		return (intermediateRes);
	}
}

//hash->hash->(name, delay, capacity)
