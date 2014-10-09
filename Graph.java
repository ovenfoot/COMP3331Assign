import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.Charset;

public class Graph
{
	HashMap<String, HashMap<String, Edge>> nodes;
	
	public Graph()
	{
		 nodes = new HashMap<String, HashMap<String, Edge>>();
		 System.out.println("WOOO");
	}
	
	public Graph (String filename) throws IOException
	{
		nodes = new HashMap<String, HashMap<String, Edge>>();
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
		HashMap<String, Edge> destMap;
		
		String params[] = line.split(" ");
	
		thisEdge.sourceName = params[0];
		thisEdge.destName = params[1];
		thisEdge.propDelay = Integer.parseInt(params[2]);
		thisEdge.vcCapacity = Integer.parseInt(params[3]);
		
		if(nodes.containsKey(thisEdge.sourceName))
		{
			destMap = nodes.get(thisEdge.sourceName);
		}
		else
		{
			destMap = new HashMap<String,Edge>();
		}
		
		destMap.put(thisEdge.destName, thisEdge);
		nodes.put(thisEdge.sourceName, destMap);
		
		if(nodes.containsKey(thisEdge.destName))
		{
			destMap = nodes.get(thisEdge.destName);
		}
		else
		{
			destMap = new HashMap<String,Edge>();
		}
		
		destMap.put(thisEdge.sourceName, thisEdge);
		nodes.put(thisEdge.destName, destMap);
	}
	public HashMap<String,Edge> get(String key)
	{
		return nodes.get(key);
	}
}






//hash->hash->(name, delay, capacity)
