import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.Charset;

public class Graph
{
	HashMap<String, Vertex> nodes;
	public Graph()
	{
		 nodes = new HashMap<String, Vertex>();
		 System.out.println("WOOO");
	}
	
	public Graph (String filename) throws IOException
	{
		nodes = new HashMap<String, Vertex>();
		BufferedReader instream = new BufferedReader(new FileReader(filename));
		String inputLine;
		
		while(instream.ready())
		{
			inputLine = instream.readLine();
			parse(inputLine);
			//System.out.println(inputLine);
		}
		//get("A").get("B").print();
		
		instream.close();
	}
	public void parse (String line)
	{
		Edge thisEdge = new Edge();
		Vertex thisVertex;
		
		String params[] = line.split(" ");
	
		thisEdge.sourceName = params[0];
		thisEdge.destName = params[1];
		thisEdge.propDelay = Integer.parseInt(params[2]);
		thisEdge.vcCapacity = Integer.parseInt(params[3]);
		
		if(nodes.containsKey(thisEdge.sourceName))
		{
			thisVertex = nodes.get(thisEdge.sourceName);
			
		}
		else
		{
			thisVertex = new Vertex();
			thisVertex.name = thisEdge.sourceName;
		}
		
		thisVertex.adjacentVertices.put(thisEdge.destName, thisEdge);
		nodes.put(thisEdge.sourceName, thisVertex);
		
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
	public Vertex get(String key)
	{
		return nodes.get(key);
	}
	public Set<String> keyset()
	{
		return nodes.keySet();
	}
}


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
}


class Edge
{
	String destName;
	String sourceName;
	int propDelay;
	int vcCapacity;
	
	public void print ()
	{
		System.out.println("Node1: " + destName +
			"|Node 2: " + sourceName + 
			"|propDelay: " + propDelay + 
			"|vcCapacity: " + vcCapacity);
	}
}

//hash->hash->(name, delay, capacity)
