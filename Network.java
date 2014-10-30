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
    Comparator<Request> rCompare = new RequestEndComparator();
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
    
        // Create a new edge based on the line data
        thisEdge.sourceName = params[0];
        thisEdge.destName = params[1];
        thisEdge.propDelay = Integer.parseInt(params[2]);
        thisEdge.vcCapacity = Integer.parseInt(params[3]);
        
        
        // See if source of the edge exists as a vertex already
        // If not, create one
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
     * If an edge is over-full, return 1 to indicate a blocked request
     */
    public int createCircuit (Request request)
    {
        Edge currEdge;
        List<String> vertices = request.path;
        
        // First check list of active VCs to see if anything can be freed
        scrubObsoleteVCs(request.timestamp);
        
        for(int i = 1; i < vertices.size(); i++)
        {
            // Get the edge using the associated hash between vertex[i-1] and vertex[i]
            currEdge = nodes.get(vertices.get(i-1)).adjacentGet(vertices.get(i));
            
            // With each edge, increment the number of active virtual circuits
            if (currEdge.activeVCs < currEdge.vcCapacity)
            {                
                if(!request.active) {
                	currEdge.activeVCs++;
                }
            }
            else
            {              
                // If active VCs is at capacity, roll back all the connections already made for this request
                for(int j = i-1; j > 0; j--)
                {
                    currEdge = nodes.get(vertices.get(j-1)).adjacentGet(vertices.get(j));
                    currEdge.activeVCs--;
                }

                // Return non zero to indicate blocked
                blockedCount++;
                return 1;
            }
            
        }
        
        // Activate the request and add it to the active queue
        request.active = true;
        if(!activeVirtualCircuits.contains(request)) {
        	activeVirtualCircuits.add(request);
        }
        successfullyRoutedCount++;
        return 0;
    }
    /*
     * Function that deletes all VCs that have already passed their end time
     * Checks entire active list to see if there is anything which is void
     * Deletes from void list and frees up edge
     */
    public void scrubObsoleteVCs(double time)
    {
        if(!activeVirtualCircuits.isEmpty())
        {
            Request currRequest = activeVirtualCircuits.peek();
            
            while(!activeVirtualCircuits.isEmpty() && time > currRequest.endtime())
            {
                currRequest = activeVirtualCircuits.remove();
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
        
        for(int i = 1; i < vertices.size(); i++)
        {
            currEdge = nodes.get(vertices.get(i-1)).adjacentGet(vertices.get(i));       
            currEdge.activeVCs--;
        }
    }
    
    // Calculate the propagation delay of a request, given an initiated path
    public double calculateCumPropDelay(Request request)
    {
        double totalPropDelay = 0;
        Edge currEdge;
        List <String> vertices = request.path;
        
        // Go through each edge in the path and sum up propagation delay
        for(int i = 1; i < vertices.size(); i++)
        {
            currEdge = nodes.get(vertices.get(i-1)).adjacentGet(vertices.get(i));
            totalPropDelay += currEdge.propDelay;
        }
        
        return totalPropDelay;
    }
    
    
    // Calculate number of hops by looking at path size
    // Take away 1 because of the fence post problem i.e. (A-B) is only one hop
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
class Vertex implements Comparable<Vertex>
{
    String name;
    Vertex previous;
    HashMap<String, Edge> adjacentVertices;
    double minDistance = Double.POSITIVE_INFINITY;
    public Vertex()
    {
        adjacentVertices  = new HashMap<String, Edge>();
        minDistance = Double.POSITIVE_INFINITY;
        name = "\0";
        previous = null;
    }
    public void print()
    {
        System.out.println("Node: " + name + " connected to " + adjacentVertices.keySet());
    }
    public Edge adjacentGet(String s)
    {
        return adjacentVertices.get(s);
    }
    public int compareTo(Vertex otherVertex)
    {
    	return Double.compare(minDistance, otherVertex.minDistance);
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
    double propDelay;
    int vcCapacity;
    int activeVCs = 0;
    
    public Edge ()
    {
    	activeVCs = 0;
    }
    public void print ()
    {
        System.out.println("Node1: " + destName +
            "|Node 2: " + sourceName + 
            "|propDelay: " + propDelay + 
            "|vcCapacity: " + vcCapacity);
    }
    public double load ()
    {
        double intermediateRes;
        intermediateRes = ((double)activeVCs/(double)vcCapacity);
        //System.out.println(intermediateRes);
        return (intermediateRes);
    }
}
class RequestEndComparator implements Comparator<Request>
{
    @Override
    public int compare (Request r1, Request r2)
    {
    	
    	return Double.compare(r1.endtime(), r2.endtime());
    }
}

//hash->hash->(name, delay, capacity)
