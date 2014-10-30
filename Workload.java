import java.util.*;
import java.io.*;
//import java.util.Queue;

/*
 * Workload class
 * Creates and contains all data on the workload of the network
 * Workload is represented by a queue of 'Requests' sorted by
 * timestamp. Each request can be processed by 'Network' class
 */
public class Workload
{
    Comparator<Request> rCompare = new RequestComparator();
    private PriorityQueue<Request> allRequests;
    private static String networkingScheme;
    private double packetDuration;
    public int vcRequestCount;
    public int packetRequestCount;
    
    /*
     * Main constructor
     * Takes in filename, packet rate and networking scheme as arguments
     * Creates an ordered queue of requests according to the networking scheme
     * Each line of the file is a virtual circuit connection
     * if networking scheme is 'PACKET', then one virtual circuit connection has n 'requests'
     * 
     */
    public Workload (String filename, double packetRate, String _networkingScheme) throws IOException
    {
        allRequests = new PriorityQueue<Request>(100, rCompare);
        BufferedReader instream = new BufferedReader(new FileReader(filename));
        String inputLine;
        Request currRequest;
        networkingScheme = _networkingScheme;
        vcRequestCount = 0;
        packetRequestCount = 0;
        packetDuration = ((double) 1)/packetRate;
        
        //Switch between networking schemes
        if(networkingScheme.equals("CIRCUIT"))
        {
            while(instream.ready())
            {
                // Parse each line individually and create a request
                inputLine = instream.readLine();
                currRequest = parseCircuits(inputLine, packetRate);
                allRequests.add(currRequest);
                vcRequestCount++;
                packetRequestCount += currRequest.packets;
            }
        }   
        else if(networkingScheme.equals("PACKET"))
        {
            while(instream.ready())
            {
                //Parse each line and create a series of requests based on input line
                inputLine = instream.readLine();
                parsePackets(inputLine, packetDuration);
                vcRequestCount++;
            }
        }   

        instream.close();
    }
    public Request parseCircuits (String line, double packetRate)
    {
        Request request = new Request();
        
        String params[] = line.split(" ");

        double currTime = Double.parseDouble(params[0]);
        double duration = Double.parseDouble(params[3]);

        request.timestamp = currTime;
        request.source    = params[1];
        request.dest      = params[2];
        request.duration  = duration;// - Double.MIN_VALUE;
        request.packets = (int) Math.ceil(duration*(double)packetRate);
        request.packetDuration = ((double) 1)/packetRate;
        
        return request;
    }
    
    /*
     * Takes an input line and packet duration and creates n-requests
     * where n is the number of packets during the active connection
     * Adds the requests to the sorted queue
     */
    public void parsePackets (String line, double packetDuration)
    {
        Request currRequest;
        double currTime;
        double endTime;
        double duration;
        String source, dest;
        String params[] = line.split(" ");
        
        source = params[1];
        dest = params[2];
        currTime = Double.parseDouble(params[0]);
        duration = Double.parseDouble(params[3]);
        endTime = currTime + duration;
        
        // While the time is < endtime, create more packets and increment currtime
        for (; currTime < endTime; currTime += packetDuration) {
            currRequest = new Request();
            currRequest.timestamp = currTime;
            currRequest.source = source;
            currRequest.dest = dest;
            currRequest.packets = 1;
            if((currTime + packetDuration)> endTime)
            {
                currRequest.duration = endTime - currTime;
            }
            else
            {
            	// Que?
                currRequest.duration = packetDuration;//-Double.MIN_VALUE;
            }
            packetRequestCount++;
            allRequests.add(currRequest);
        }
    }
    
    // Interface functions to add, remove and poll requests from outside class
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
    public int size()
    {
        return allRequests.size();
    }
        
}

/*
 * Request class. Contains information on source, dest, begin time and duration
 * of a network connection request
 * Initialised with an empty path that will be filled in the main routing 
 * processor
 */
class Request
{
    double timestamp;
    String dest;
    String source;
    double duration;
    int packets;
    double packetDuration;
    
    Boolean active = false;
    List<String> path;
    
    public Request ()
    {
    	
    }
    public Request (Request oldRequest)
    {
    	System.out.println("creating new request from");
    	oldRequest.print();
    	timestamp = oldRequest.timestamp;
        dest = oldRequest.dest;
        source = oldRequest.source;
        duration = oldRequest.duration;
        packets = oldRequest.packets;
        packetDuration = oldRequest.packetDuration;
        path = oldRequest.path;
        active = oldRequest.active;
    }
    
    public void print()
    {
        System.out.println("Request Timestamp: " + timestamp +
                "|Source: " + source +
                "|Dest: " + dest + 
                "|Endtime: "+ endtime());
    }
    
    public double endtime()
    {
        if (packets > 1) {
            return timestamp + packetDuration;
        }
        return (timestamp + duration);
    }
    
    

    public int getPackets()
    {
        return packets;
    }

    public boolean hasPath() {
        if(path == null) {
        	return false;
        }
        
        if(path.isEmpty()) {
        	return false;
        }
        return true;
    }
}

//Comparator class for sorting requests by timestamp
class RequestComparator implements Comparator<Request>
{
    @Override
    public int compare (Request r1, Request r2)
    {
    	
    	return Double.compare(r1.timestamp, r2.timestamp);
    }
}
//hash->hash->(name, delay, capacity)
