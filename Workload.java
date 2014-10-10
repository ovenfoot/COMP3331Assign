import java.util.*;
import java.io.*;
//import java.util.Queue;
public class Workload
{
	private Queue<Request> allRequests;

	public Workload()
	{
		 System.out.println("WOOO");
	}
	
	public Workload (String filename) throws IOException
	{
		allRequests = new LinkedList<Request>();
		BufferedReader instream = new BufferedReader(new FileReader(filename));
		String inputLine;
		Request currRequest;
		while(instream.ready())
		{
			inputLine = instream.readLine();
			currRequest = parse(inputLine);
			allRequests.add(currRequest);
		}
		
		instream.close();
	}
	public Request parse (String line)
	{
		Request request = new Request();
		
		String params[] = line.split(" ");
	
		request.timestamp = Float.parseFloat(params[0]);
		request.source = params[1];
		request.dest = params[2];
		request.duration = Float.parseFloat(params[3]);
		
		return request;
	}
	
	public void add(Request _request)
	{
		allRequests.add(_request);
	}
	public Request remove()
	{
		return allRequests.remove();
	}
	public Request poll()
	{
		return allRequests.poll();
	}
	public Request element()
	{
		return allRequests.element();
	}
	public Boolean isEmpty()
	{
		return allRequests.isEmpty();
	}
		
}
class Request
{
	float timestamp;
	String dest;
	String source;
	float duration;
	
	public void print()
	{
		System.out.println("Request Timestamp: " + timestamp +
				"|Source: " + source +
				"|Dest: " + dest + 
				"|Duration: "+ duration);
	}
}
//hash->hash->(name, delay, capacity)
