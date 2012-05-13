/**
 * Created May 10, 2012
 */
package edu.droidshark.android.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.voytechs.jnetstream.codec.Decoder;
import com.voytechs.jnetstream.codec.Packet;
import com.voytechs.jnetstream.io.RawformatInputStream;

import edu.droidshark.constants.SnifferConstants;
import edu.droidshark.tcpdump.TCPDumpListener;

/**
 * Service for reading output of tcpdump
 * 
 * @author Sam SmithReams
 * 
 */
public class TCPDumpService extends Service
{
	private TCPDumpBinder binder;
//	private BufferedInputStream pcapStream;
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
	public void openFileStream(final Process tProcess)
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
					
//					TODO: I'm not sure how we are going to handle this sort of thing. We may need to create our own custom input stream
//					that blocks on the read command. This is in thinking about using jnetstream, the problem with providing a file
//					stream directly is that it's going to get to the end of the file and stop reading. The code commented out below is
//					a way to read from a file stream without getting to the end. Another option might be to have tcpdump running twice, 
//					one outputting to file stream and the other to stdout.
					
					//TODO: So this seems to work for reading the input stream. Still haven't resolved issue of writing to file.
					//Will deal with later. A thought is to take the input stream and write it to file as we go in another thread.
					
					Decoder decoder = new Decoder(new RawformatInputStream(tProcess.getInputStream()));
					Packet packet = null;
					while(!stopScanner && (packet = decoder.nextPacket()) != null) 
					{
						count++;
						if(tListener != null)
							tListener.packetReceived(count, packet);
					}
					
					decoder.close();
					
//					Read from the input stream of tcpdump
//					BufferedReader br = new BufferedReader(new InputStreamReader(
//							tProcess.getInputStream()));
//					String line;
//					if(SnifferConstants.DEBUG)
//						Log.d(TAG, "waiting for stdout");
//					while ((line = br.readLine()) != null)
//					{
//						if(SnifferConstants.DEBUG)
//							Log.d(TAG, "reading from stdout");
//						count++;
//						if(tListener != null)
//							tListener.packetReceived(count, line);
//					}
					
//					Continually check the file stream
//					pcapStream = new BufferedInputStream(new FileInputStream(getExternalFilesDir(null)
//							+ "/capture.pcap"));
//					
//					while (!stopScanner)
//					{	
//						if(SnifferConstants.DEBUG)
//							Log.d(TAG, "avail bytes in stream=" + pcapStream.available());
//						
//						while(pcapStream.available() > 0)
//						{
//							char[] line = new char[1000];
//							char ch;
//							int counter = 0;
//							while(pcapStream.available() > 0 
//									&& (ch = (char) pcapStream.read()) != '\n')
//							{
//								line[counter] = ch;
//								counter++;
//							}
//							count++;
//							if(tListener != null)
//								tListener.packetReceived(count, String.valueOf(line));
//						}
//						Thread.sleep(2000);
//					}
//					pcapStream.close();
				}
				catch(Exception e)
				{
					Log.e(TAG, "Error reading pcap file, msg=" + e.getMessage());
				}
			}

		});
		
		//Check error output for tcpdump
		if(SnifferConstants.DEBUG)
			new Thread(new Runnable()
			{
	
				@Override
				public void run()
				{
					BufferedReader br = new BufferedReader(new InputStreamReader(
							tProcess.getErrorStream()));
					String line;
					try
					{
						while ((line = br.readLine()) != null)
						{
							Log.d(TAG, "tcpdump stderr msg=" + line);
						}
					} catch (IOException e)
					{
						Log.e(TAG, e.getMessage());
					}				
				}
				
			}).start();
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
