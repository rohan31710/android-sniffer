/**
 * Created May 10, 2012
 */
package edu.droidshark.android.services;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
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
	private BufferedInputStream pcapStream;
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
		stopScanner = false;
		
		//TODO: This is just a bogus test, one line != one packet
		scannerThread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{	
				try
				{
					Thread.sleep(2000);
					
					pcapStream = new BufferedInputStream(new FileInputStream(getExternalFilesDir(null)
							+ "/capture.pcap"));
					
					while (!stopScanner)
					{						
						while(pcapStream.available() > 0)
						{
							char[] line = new char[1000];
							char ch;
							int counter = 0;
							while(pcapStream.available() > 0 
									&& (ch = (char) pcapStream.read()) != '\n')
							{
								line[counter] = ch;
								counter++;
							}
							count++;
							if(tListener != null)
								tListener.packetReceived(count, String.valueOf(line));
						}
						Thread.sleep(2000);
					}
					pcapStream.close();
				}
				catch(Exception e)
				{
					Log.e(TAG, "Error reading pcap file, msg=" + e.getMessage());
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
