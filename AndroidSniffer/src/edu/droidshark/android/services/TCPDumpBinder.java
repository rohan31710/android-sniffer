/**
 * Created May 11, 2012
 */
package edu.droidshark.android.services;

import android.os.Binder;

/**
 * Binder for TCPDumpService
 * 
 * @author Sam SmithReams
 *
 */
public class TCPDumpBinder extends Binder
{
	private TCPDumpService tService;
	
	public TCPDumpBinder(TCPDumpService tService)
	{
		this.tService = tService;
	}
	
	public TCPDumpService getService()
	{
		return tService;
	}
}