import java.util.*;

public class Graph
{
	HashMap nodes;
	public Graph()
	{
		 nodes = new HashMap<String, HashMap<String, Edge>>();
		 System.out.println("WOOO");
	}
	public Graph(String inputfilename)
	{
		System.out.println(inputfilename);
	}
}






//hash->hash->(name, delay, capacity)
