import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.Charset;

public class Graph
{
	HashMap nodes;
	
	public Graph()
	{
		 nodes = new HashMap<String, HashMap<String, Edge>>();
		 System.out.println("WOOO");
	}
	public Graph(Path inputfilename)
	{
		Charset charset = Charset.forName("US-ASCII");
		try {
			BufferedReader reader = Files.newBufferedReader(inputfilename, charset);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println("FUCK YEAH");
		
	}
}






//hash->hash->(name, delay, capacity)
