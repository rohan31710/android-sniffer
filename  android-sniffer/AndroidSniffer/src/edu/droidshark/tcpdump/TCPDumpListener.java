/**
 * Created May 11, 2012
 */
package edu.droidshark.tcpdump;

/**
 * An interface for communicating with TCPDumpService
 * 
 * @author Sam SmithReams
 *
 */
public interface TCPDumpListener
{
	void packetReceived(int numPackets); //Will need a packet as well eventually
}
