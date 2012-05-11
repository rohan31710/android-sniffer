package edu.droidshark.android.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import edu.droidshark.constants.SnifferConstants;

/**
 * Service for running tcpdump
 * 
 * @author Sam SmithReams
 *
 */
public class TCPDumpService extends Service
{
	private final String TAG = "TCPDumpService";

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		// The options to run tcpdump with
		final String opts = intent
				.getStringExtra(SnifferConstants.TCPDUMP_OPTIONS);

		// Start tcpdump
		try
		{
			Runtime.getRuntime().exec(
					new String[] {
							"su",
							"-c",
							getFilesDir().getAbsolutePath() + "/tcpdump "
									+ opts + getExternalFilesDir(null)
									+ "/capture.pcap" });
		} catch (IOException e)
		{
			Log.e("tcpdump", "Error running tcpdump, msg=" + e.getMessage());
		}

		return (START_NOT_STICKY);
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onDestroy()
	{
		try
		{
			//Run ps to get the process list
			Process proc = Runtime.getRuntime().exec("ps");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					proc.getInputStream()));
			String line;
			//Parse process list to find tcpdump pid
			while ((line = br.readLine()) != null)
			{
				if (line.contains("tcpdump") && line.contains("edu.droidshark"))
				{
					String pid[] = line.split("\\s+");
					
					if (SnifferConstants.DEBUG)
						Log.d(TAG, "About to kill pid " + pid[1]);

					//Kill tcpdump
					Process proc2 = Runtime.getRuntime().exec(
							new String[] { "su", "-c", "kill -9 " + pid[1] });
					BufferedReader br2 = new BufferedReader(
							new InputStreamReader(proc2.getErrorStream()));
					
					//Check for errors during the kill process
					String line2;
					while ((line2 = br2.readLine()) != null)
					{
						Log.e(TAG, "kill error=" + line2);
					}
					br2.close();
				}
			}
			br.close();
			
			if (SnifferConstants.DEBUG)
				Log.d(TAG, "Done with killing tcpdump");
		} catch (IOException e)
		{
			Log.e(TAG, "Error killing tcpdump, msg=" + e.getMessage());
		}
		super.onDestroy();
	}

}
