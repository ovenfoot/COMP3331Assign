import java.util.*;
import java.io.*;
//import java.util.Queue;
public class Workload
{
	Comparator<Request> rCompare = new RequestComparator();
	private PriorityQueue<Request> allRequests;
	private static String networkingScheme;
	private float packetDuration;
	
	public Workload()
	{
		 System.out.println("WOOO");
	}
	
	public Workload (String filename, float packetRate, String _networkingScheme) throws IOException
	{
		allRequests = new PriorityQueue<Request>(100, rCompare);
		BufferedReader instream = new BufferedReader(new FileReader(filename));
		String inputLine;
		Request currRequest;
		networkingScheme = _networkingScheme;
		if(networkingScheme.equals("CIRCUIT"))
		{
			while(instream.ready())
			{
				inputLine = instream.readLine();
				currRequest = parseCircuits(inputLine);
				currRequest.packets = (int) Math.round(currRequest.duration*packetRate);
				allRequests.add(currRequest);
			}
		}	
		else if(networkingScheme.equals("PACKET"))
		{
			while(instream.ready())
			{
				inputLine = instream.readLine();
				packetDuration = 1/packetRate;
				parsePackets(inputLine, packetDuration);
			}
		}	
			
			
		

		
		instream.close();
	}
	public Request parseCircuits (String line)
	{
		Request request = new Request();
		
		String params[] = line.split(" ");
	
		request.timestamp = Float.parseFloat(params[0]);
		request.source = params[1];
		request.dest = params[2];
		request.duration = Float.parseFloat(params[3]);
		
		return request;
	}
	public void parsePackets (String line, float packetDuration)
	{
		Request currRequest;
		float currTime;
		float endTime;
		float duration;
		String source, dest;
		String params[] = line.split(" ");
		
		source = params[1];
		dest = params[2];
		currTime = Float.parseFloat(params[0]);
		duration = Float.parseFloat(params[3]);
		endTime = currTime + duration;
		for (currTime = Float.parseFloat(params[0]); 
				currTime < endTime; 
				currTime+=packetDuration)
		{
			currRequest = new Request();
			currRequest.timestamp = currTime;
			currRequest.source = source;
			currRequest.dest = dest;
			if((currTime + packetDuration)> endTime)
			{
				currRequest.duration = endTime - currTime;
			}
			else
			{
				currRequest.duration = packetDuration-(float)0.0001;
			}
			
			allRequests.add(currRequest);
		}
		
		
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
	int packets;
	Boolean active = false;
	List<String> path;
	
	public void print()
	{
		System.out.println("Request Timestamp: " + timestamp +
				"|Source: " + source +
				"|Dest: " + dest + 
				"|Duration: "+ duration);
	}
	public float endtime()
	{
		return (timestamp+duration);
	}
}

class RequestComparator implements Comparator<Request>
{
	@Override
	public int compare (Request r1, Request r2)
	{
		if(r1.timestamp > r2.timestamp)
		{
			return 1;
		}
		else
		{
			return -1;
		}
		//return (int)(r1.timestamp - r2.timestamp);
	}
}
//hash->hash->(name, delay, capacity)
