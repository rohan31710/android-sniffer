/**
 * Created May 11, 2012
 */
package edu.droidshark.tcpdump;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import edu.droidshark.R;
import edu.droidshark.constants.SnifferConstants;

import android.content.Context;
import android.util.Log;

/**
 * Utilities for working with tcpdump
 * 
 * @author Sam SmithReams
 * 
 */
public class TCPDumpUtils
{
	private static final String TAG = "TCPDump";

	/**
	 * Creates tcpdump binary in data directory
	 * 
	 * @param context
	 *            Application context for getting data directories
	 */
	public static void createTCPDump(Context context)
	{
		try
		{
			// Create tcpdump
			InputStream is = context.getResources().openRawResource(
					R.raw.tcpdump);
			FileOutputStream fos = context.openFileOutput("tcpdump",
					Context.MODE_PRIVATE);
			byte[] b = new byte[1801155];
			is.read(b);
			fos.write(b);

			is.close();
			fos.close();

			Runtime.getRuntime().exec(
					new String[] {
							"chmod",
							"755",
							context.getFilesDir().getAbsolutePath()
									+ "/tcpdump" });

		} catch (Exception ex)
		{
			Log.e(TAG, "Exception creating tcpdump, message=" + ex.getMessage());
		}
	}

	/**
	 * Checks to see if tcpdump is running
	 * 
	 * @param context
	 *            Application context for getting data directories
	 * 
	 * @return Whether tcpdump is running
	 */
	public static boolean isTCPDumpRunning()
	{
		return getTCPDumpPID() != null;
	}

	/**
	 * Finds the pid of tcpdump by using and parsing the ps command
	 * 
	 * @return the pid of tcpdump on the system
	 */
	public static String getTCPDumpPID()
	{
		try
		{
			// Run ps to get the process list
			Process proc = Runtime.getRuntime().exec("ps");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					proc.getInputStream()));
			
			//Finding the correct column for pid, this can vary with
			//different versions of ps
			String line;
			int pidCol = 1, i = 0;
			line = br.readLine();
			String[] columns = line.split("\\s+");
			for(String col : columns)
			{
				if(col.equalsIgnoreCase("PID"))
					pidCol = i;
				i++;
			}
			
			// Parse process list to find tcpdump entry
			while ((line = br.readLine()) != null)
			{
				if (line.contains("tcpdump") && line.contains("edu.droidshark")
						&& !line.contains("sh -c"))
				{
					if (SnifferConstants.DEBUG)
						Log.d(TAG, "ps entry=" + line);

					// Split the line by white space
					String pid[] = line.split("\\s+");
					// pid should be second string in line
					return pid[pidCol];
				}
			}
			br.close();
		} catch (IOException e)
		{
			Log.e(TAG, "Error killing tcpdump, msg=" + e.getMessage());
		}

		return null;
	}

	/**
	 * Starts tcpdump as root with given options
	 * 
	 * @param context
	 *            Application context for getting data directories
	 * @param tcpdumpOptions
	 *            The options to start tcpdump with
	 */
	public static Process startTCPDump(Context context, TCPDumpOptions tcpdumpOptions)
	{
		if(SnifferConstants.DEBUG)
			Log.d(TAG, "tcpdump options=" + tcpdumpOptions.toString());
		
		try
		{
			Process proc = Runtime.getRuntime().exec(
					new String[] {
							"su",
							"-c",
							context.getFilesDir().getAbsolutePath()
									+ "/tcpdump " + tcpdumpOptions.toString() });
			BufferedReader br = new BufferedReader(new InputStreamReader(
					proc.getErrorStream()));
			// Check for errors during the kill process
			String line = br.readLine();
			br.close();
			if(line.toLowerCase().contains("syntax error"))
				return null;
			else
				return proc;
		} catch (IOException e)
		{
			Log.e("tcpdump", "Error running tcpdump, msg=" + e.getMessage());
			return null;
		}
	}

	/**
	 * Finds and stops tcpdump started by this app
	 * 
	 * @param context
	 *            Application context for getting data directories
	 */
	public static void stopTCPDump()
	{
		try
		{
			String pid = getTCPDumpPID();
			if (SnifferConstants.DEBUG)
				Log.d(TAG, "Killing pid " + pid);
			// Kill tcpdump
			// NOTE: Took out the SIGTERM option on kill, reason for doing 
			// this is to prevent packets from getting interrupted and possibly
			// messing up the service, hopefully will kill it reliably still
			Process proc = Runtime.getRuntime().exec(
					new String[] { "su", "-c", "kill " + pid });
			BufferedReader br = new BufferedReader(new InputStreamReader(
					proc.getErrorStream()));
			// Check for errors during the kill process
			String line;
			while ((line = br.readLine()) != null)
			{
				Log.e(TAG, "kill error=" + line);
			}
			br.close();
		} catch (IOException e)
		{
			Log.e(TAG, "Error killing tcpdump, msg=" + e.getMessage());
		}
	}

	public static List<String> getDeviceList(Context context)
	{
		Process proc;
		try
		{
			proc = Runtime.getRuntime().exec(
					new String[] {
							"su",
							"-c",
							context.getFilesDir().getAbsolutePath()
									+ "/tcpdump -D" });
			String line;
			// Parse tcpdump input to get device list
			BufferedReader br = new BufferedReader(new InputStreamReader(
					proc.getInputStream()));
			List<String> devices = new ArrayList<String>();
			while ((line = br.readLine()) != null)
			{
				devices.add(line);
			}

			return devices;
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}
