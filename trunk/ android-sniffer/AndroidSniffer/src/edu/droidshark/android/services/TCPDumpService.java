/**
 * Created May 10, 2012
 */
package edu.droidshark.android.services;

import java.io.FileOutputStream;

import org.apache.commons.io.input.TeeInputStream;

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

		scannerThread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{	
				try
				{
					Thread.sleep(2000);
					
					FileOutputStream fos = new FileOutputStream(getExternalFilesDir(null)
							+ "/capture.pcap");
					final TeeInputStream tis = new TeeInputStream(tProcess.getInputStream(), fos, true);
					Decoder decoder = new Decoder(new RawformatInputStream(tis));
					Packet packet = null;
					while(!stopScanner && (packet = decoder.nextPacket()) != null) 
					{
						count++;
						if(tListener != null)
							tListener.packetReceived(count, packet);
					}
					if(SnifferConstants.DEBUG)
						Log.d(TAG, "Packet reading loop ended");
					decoder.close();
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
