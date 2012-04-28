package edu.droidshark.android.ui.fragments.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.os.Bundle;
import android.support.v4.app.SupportActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import edu.droidshark.R;
import edu.droidshark.android.ui.activities.DroidSharkActivity;

public class SnifferFragment extends FragmentWithBinder
{
	protected final String LOG_TAG = getClass().getSimpleName();
	protected DroidSharkActivity droidSharkActivity;
	private ListView packetlist;
	//private SnifferAdapter snifferAdapter;
	private static final String[] items = {"packet 1", "packet 2"};
	private Process proc;
	
	@Override
	public void onAttach(SupportActivity activity)
	{
		super.onAttach(activity);
		droidSharkActivity = (DroidSharkActivity) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//snifferAdapter = new SnifferAdapter(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.sniffer_layout, container,
				false);
		
		packetlist = (ListView) v.findViewById(R.id.packetlist);
		packetlist.setAdapter(new ArrayAdapter<String>(droidSharkActivity, android.R.layout.simple_list_item_1, items));
		
		((ImageButton) v.findViewById(R.id.startButton)).
				setOnClickListener(new OnClickListener()
				{
					public void onClick(View v)
					{							
						Thread captureThread = new Thread(new Runnable()
						{

							@Override
							public void run() 
							{
								try
								{
									proc = Runtime.getRuntime().exec(new String[] {"su", "-c", getActivity().getFilesDir().getAbsolutePath() + "/tcpdump -X -n -s 0 -w "
											+ getActivity().getExternalFilesDir(null) + "/capture.pcap"});
								} catch (IOException e)
								{
									Log.e("tcpdump", "Error running tcpdump, msg=" + e.getMessage());
								}
								
							}
							
						});
						captureThread.run();
					}
				});
		((ImageButton) v.findViewById(R.id.stopButton)).
				setOnClickListener(new OnClickListener()
				{
					public void onClick(View v)
					{
						try 
						{
							Process proc = Runtime.getRuntime().exec(new String[] {"su", "-c", "kill -9 $(busybox pidof tcpdump)"});
							BufferedReader br = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
							String line;
							while((line = br.readLine()) != null)
							{
								Log.e("kill", line);
							}
							br.close();
						} catch (IOException e)
						{
							Log.e("tcpdump", "Error killing tcpdump, msg=" + e.getMessage());
						}
					}
				});
		
		//SharedPreferences prefs = PreferenceManager
				//.getDefaultSharedPreferences(droidSharkActivity);
		
		return v;
	}

	@Override
	public void onResume()
	{
		super.onResume();
	}

	@Override
	public void onStart()
	{
		super.onStart();
	}

	@Override
	public void onPause()
	{
		super.onPause();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	protected void doAfterBind()
	{
		// TODO Auto-generated method stub
		
	}

}
