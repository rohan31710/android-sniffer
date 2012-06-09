/**
 * Created May 26, 2012
 */
package edu.droidshark.tcpdump;

/**
 * Simple class for storing a tcpdump filter with a name(alias)
 * 
 * @author Sam SmithReams
 *
 */
public class TCPDumpFilter
{
	private String name, filter;
	private int id;
	
	/**
	 * @param name
	 * 			Name of the filter (alias)
	 * @param filter
	 * 			The filter string
	 */
	public TCPDumpFilter(int id, String name, String filter)
	{
		this.id = id;
		this.name = name;
		this.filter = filter;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the filter
	 */
	public String getFilter()
	{
		return filter;
	}

	/**
	 * @param filter the filter to set
	 */
	public void setFilter(String filter)
	{
		this.filter = filter;
	}

	@Override
	public String toString()
	{
		return name;
	}

	/**
	 * @return the id
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id)
	{
		this.id = id;
	}
}
