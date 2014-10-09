public class Edge
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