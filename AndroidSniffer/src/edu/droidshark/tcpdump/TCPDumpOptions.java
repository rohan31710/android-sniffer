/**
 * Created May 11, 2012
 */
package edu.droidshark.tcpdump;

/**
 * A class for maintaining tcpdump options
 * 
 * @author Sam SmithReams
 *
 */
public class TCPDumpOptions
{
	private TCPDumpFilter filter;
	private boolean noHostNames, noTimeStamp;
	private int deviceId, dataMode, verboseMode, packetLenLim;
	private final static int DATAMODE_HEX = 1, DATAMODE_ASCII = 2;
	private final static int VMODE_V = 1, VMODE_VV = 2, VMODE_VVV = 3;
	
	/**
	 * @return the filter
	 */
	public TCPDumpFilter getFilter()
	{
		return filter;
	}
	
	/**
	 * @param filter the filter to set
	 */
	public void setFilter(TCPDumpFilter filter)
	{
		this.filter = filter;
	}

	/**
	 * @return the noHostNames
	 */
	public boolean isnoHostNames()
	{
		return noHostNames;
	}

	/**
	 * @param noHostNames the noHostNames to set
	 */
	public void setnoHostNames(boolean noHostNames)
	{
		this.noHostNames = noHostNames;
	}

	/**
	 * @return the noTimeStamp
	 */
	public boolean isnoTimeStamp()
	{
		return noTimeStamp;
	}

	/**
	 * @param noTimeStamp the noTimeStamp to set
	 */
	public void setnoTimeStamp(boolean noTimeStamp)
	{
		this.noTimeStamp = noTimeStamp;
	}

	/**
	 * @return the dataMode
	 */
	public int getDataMode()
	{
		return dataMode;
	}

	/**
	 * @param dataMode the dataMode to set
	 */
	public void setDataMode(int dataMode)
	{
		this.dataMode = dataMode;
	}

	/**
	 * @return the verboseMode
	 */
	public int getVerboseMode()
	{
		return verboseMode;
	}

	/**
	 * @param verboseMode the verboseMode to set
	 */
	public void setVerboseMode(int verboseMode)
	{
		this.verboseMode = verboseMode;
	}

	/**
	 * @return the packetLenLim
	 */
	public int getPacketLenLim()
	{
		return packetLenLim;
	}

	/**
	 * @param packetLenLim the packetLenLim to set
	 */
	public void setPacketLenLim(int packetLenLim)
	{
		this.packetLenLim = packetLenLim;
	}
	
	/**
	 * @return the deviceId
	 */
	public int getDeviceId()
	{
		return deviceId;
	}

	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(int deviceId)
	{
		this.deviceId = deviceId;
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();		
		
		builder.append("-i " + deviceId + " ");
		
		switch(verboseMode)
		{
			case VMODE_V:
				builder.append("-v "); break;
			case VMODE_VV:
				builder.append("-vv "); break;
			case VMODE_VVV:
				builder.append("-vvv "); break;
		}
		
		switch(dataMode)
		{
			case DATAMODE_HEX:
				builder.append("-x "); break;
			case DATAMODE_ASCII:
				builder.append("-X "); break;
		}
		
		if(noHostNames)
			builder.append("-n ");
		
		if(noTimeStamp)
			builder.append("-t ");
		
		builder.append("-s " + packetLenLim + " -U -w - ");
		
		builder.append(filter.getFilter());
		
		return builder.toString();
	}
}
