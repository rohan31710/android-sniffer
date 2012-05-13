package edu.droidshark.android.ui.fragments.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockFragment;

import edu.droidshark.R;
import edu.droidshark.android.ui.activities.DroidSharkActivity;
import edu.droidshark.tcpdump.TCPDumpUtils;

public class SnifferFragment extends SherlockFragment implements
		OnItemSelectedListener
{
	protected final String LOG_TAG = getClass().getSimpleName();
	protected DroidSharkActivity droidSharkActivity;
	private static final String TAG = "SnifferFragment";
	private ImageButton startButton, stopButton;
	private int deviceId = 1;

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		droidSharkActivity = (DroidSharkActivity) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.sniffer_layout, container, false);

		startButton = (ImageButton) v.findViewById(R.id.startButton);
		stopButton = (ImageButton) v.findViewById(R.id.stopButton);

		// Start button action, start tcpdump and service monitoring.
		((ImageButton) v.findViewById(R.id.startButton))
				.setOnClickListener(new OnClickListener()
				{
					public void onClick(View v)
					{
						droidSharkActivity.settProcess(TCPDumpUtils
								.startTCPDump(getActivity(), "-i " + deviceId
//										+ " -X -n -s 0 -w " + 
//										+ getActivity().getExternalFilesDir(null)
//										+ "/capture.pcap" )); //write to file
										+ " -X -n -s 0")); //write to std out
						droidSharkActivity.openFileStream();
						startButton.setEnabled(false);
						stopButton.setEnabled(true);
					}
				});

		// Stop button action, stop tcpdump and service monitoring.
		((ImageButton) v.findViewById(R.id.stopButton))
				.setOnClickListener(new OnClickListener()
				{
					public void onClick(View v)
					{
						TCPDumpUtils.stopTCPDump();
						droidSharkActivity.closeFileStream();
						startButton.setEnabled(true);
						stopButton.setEnabled(false);
					}
				});

		// Populate spinner with device list
		List<String> devices = TCPDumpUtils.getDeviceList(getActivity());

		Spinner deviceSpinner = (Spinner) v.findViewById(R.id.deviceSpinner);
		deviceSpinner.setOnItemSelectedListener(this);

		ArrayAdapter<String> aa = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, devices);

		aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		deviceSpinner.setAdapter(aa);

		// SharedPreferences prefs = PreferenceManager
		// .getDefaultSharedPreferences(droidSharkActivity);

		return v;
	}

	@Override
	public void onResume()
	{
		super.onResume();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position,
			long id)
	{
		Log.d(TAG, "setting deviceId to " + String.valueOf(position + 1));
		deviceId = position + 1;
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent)
	{

	}

}
