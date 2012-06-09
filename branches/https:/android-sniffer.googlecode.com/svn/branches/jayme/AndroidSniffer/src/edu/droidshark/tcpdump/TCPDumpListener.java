/**
 * Created May 11, 2012
 */
package edu.droidshark.tcpdump;

import com.voytechs.jnetstream.codec.Packet;

/**
 * An interface for communicating with TCPDumpService
 * 
 * @author Sam SmithReams
 *
 */
public interface TCPDumpListener
{
	void packetReceived(int numPackets, Packet packet);
}
