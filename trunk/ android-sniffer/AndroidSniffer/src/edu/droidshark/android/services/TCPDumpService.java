/**
 * Created May 10, 2012
 */
package edu.droidshark.android.services;

import java.util.Scanner;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import edu.droidshark.tcpdump.TCPDumpListener;

/**
 * Service for running tcpdump
 * 
 * @author Sam SmithReams
 * 
 */
public class TCPDumpService extends Service
{
	private TCPDumpBinder binder;
	private Scanner pcapStream;
	private TCPDumpListener tListener;
	private int count;
	private final String TAG = "TCPDumpService";
	private Thread scannerThread;
	private boolean stopScanner;

	@Override
	public void onCreate()
	{
		super.onCreate();

		binder = new TCPDumpBinder(this);
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return binder;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		
		closeFileStream();
	}
	
	/**
	 * Opens the pcap file that tcpdump is writing to and monitors it
	 */
	public void openFileStream()
	{
		pcapStream = new Scanner(getExternalFilesDir(null)
					+ "/capture.pcap");
		
		//TODO: This is just a bogus test, one line != one packet
		scannerThread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				while (!stopScanner && pcapStream.hasNext())
				{					
					count++;
					if(tListener != null)
						tListener.packetReceived(count);
				}
			}

		});
		scannerThread.start();
	}
	
	/**
	 * Closes the stream for the pcap file
	 */
	public void closeFileStream()
	{
		stopScanner = true;
		if(pcapStream != null)
			pcapStream.close();
	}

	/**
	 * @return the tListener
	 */
	public TCPDumpListener gettListener()
	{
		return tListener;
	}

	/**
	 * @param tListener the tListener to set
	 */
	public void settListener(TCPDumpListener tListener)
	{
		this.tListener = tListener;
	}
}
