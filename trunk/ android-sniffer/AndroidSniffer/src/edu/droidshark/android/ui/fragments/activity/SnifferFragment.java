package edu.droidshark.android.ui.fragments.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

import edu.droidshark.R;
import edu.droidshark.android.ui.activities.DroidSharkActivity;
import edu.droidshark.constants.SnifferConstants;
import edu.droidshark.tcpdump.TCPDumpFilter;
import edu.droidshark.tcpdump.TCPDumpOptions;
import edu.droidshark.tcpdump.TCPDumpUtils;

public class SnifferFragment extends SherlockFragment implements
		OnItemSelectedListener, OnCheckedChangeListener
{
	protected final String LOG_TAG = getClass().getSimpleName();
	protected DroidSharkActivity dsActivity;
	private static final String TAG = "SnifferFragment";
	private ImageButton startButton, stopButton;
	private Spinner deviceSpinner, filterSpinner;
	private List<TCPDumpFilter> filters;
	private ArrayAdapter<TCPDumpFilter> filterAdapter;
	private EditText packetLenLimEditText;
	private TCPDumpOptions tcpdumpOptions;

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		dsActivity = (DroidSharkActivity) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		tcpdumpOptions = new TCPDumpOptions();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.sniffer_layout, container, false);

		// Buttons
		startButton = (ImageButton) v.findViewById(R.id.startButton);
		stopButton = (ImageButton) v.findViewById(R.id.stopButton);
		Button editFilterButton = (Button) v
				.findViewById(R.id.editFilterButton);

		// Start button action, start tcpdump and service monitoring.
		startButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				try
				{
					dsActivity.closeIME(packetLenLimEditText.getWindowToken());
					tcpdumpOptions.setPacketLenLim(Integer
							.valueOf(packetLenLimEditText.getText().toString()));
					Process proc = TCPDumpUtils.startTCPDump(getActivity(),
							tcpdumpOptions);
					if (proc == null)
						Toast.makeText(dsActivity,
								"Syntax error occurred, check your filter",
								Toast.LENGTH_SHORT).show();
					else
					{
						dsActivity.settProcess(proc);
						dsActivity.openFileStream();
						startButton.setEnabled(false);
						stopButton.setEnabled(true);
					}
				} catch (NumberFormatException e)
				{
					Toast.makeText(dsActivity, "Invalid Packet Length Limit",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		// Stop button action, stop tcpdump and service monitoring.
		stopButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				dsActivity.closeIME(packetLenLimEditText.getWindowToken());
				TCPDumpUtils.stopTCPDump();
				dsActivity.closeFileStream();
				startButton.setEnabled(true);
				stopButton.setEnabled(false);
			}
		});

		editFilterButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				TCPDumpFilter filter = (TCPDumpFilter) filterSpinner.getSelectedItem();
				new FilterEditFragment(filter)
						.show(dsActivity.getSupportFragmentManager(),
								"editFilter");
			}
		});

		// Spinners
		deviceSpinner = (Spinner) v.findViewById(R.id.deviceSpinner);
		filterSpinner = (Spinner) v.findViewById(R.id.filterSpinner);
		Spinner verboseSpinner = (Spinner) v.findViewById(R.id.verboseSpinner);
		Spinner dataSpinner = (Spinner) v.findViewById(R.id.dataSpinner);
		deviceSpinner.setOnItemSelectedListener(this);
		filterSpinner.setOnItemSelectedListener(this);
		verboseSpinner.setOnItemSelectedListener(this);
		dataSpinner.setOnItemSelectedListener(this);

		// Query filter database and populate spinner
		Cursor cursor = dsActivity.filterDB.getReadableDatabase().rawQuery(
				"SELECT _id, name, filter FROM filters", null);
		filters = new ArrayList<TCPDumpFilter>();
		cursor.moveToFirst();
		do
		{
			filters.add(new TCPDumpFilter(cursor.getInt(0),
					cursor.getString(1), cursor.getString(2)));
			Log.d(TAG, "Adding filter name=" + cursor.getString(0));
		} while (cursor.moveToNext());
		cursor.close();
		filterAdapter = new ArrayAdapter<TCPDumpFilter>(dsActivity,
				android.R.layout.simple_spinner_item, filters);
		filterAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		filterSpinner.setAdapter(filterAdapter);

		// Populate verbose output spinner
		ArrayAdapter<CharSequence> vaa = ArrayAdapter.createFromResource(
				dsActivity, R.array.verbose_mode,
				android.R.layout.simple_spinner_item);
		vaa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		verboseSpinner.setAdapter(vaa);

		// Populate data output spinner
		ArrayAdapter<CharSequence> daa = ArrayAdapter.createFromResource(
				dsActivity, R.array.data_mode,
				android.R.layout.simple_spinner_item);
		daa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		dataSpinner.setAdapter(daa);

		// Checkboxes
		CheckBox hostsCheckBox = (CheckBox) v.findViewById(R.id.hostsCheckBox);
		CheckBox timestampCheckBox = (CheckBox) v
				.findViewById(R.id.timestampCheckBox);
		hostsCheckBox.setOnCheckedChangeListener(this);
		timestampCheckBox.setOnCheckedChangeListener(this);

		packetLenLimEditText = (EditText) v
				.findViewById(R.id.packetLenLimTextBox);

		// Setting values from preferences
		SharedPreferences prefs = dsActivity.getPreferences(0);
		deviceSpinner.setSelection(prefs.getInt("device", 0));
		filterSpinner.setSelection(prefs.getInt("filter", 0));
		verboseSpinner.setSelection(prefs.getInt("verboseMode", 2));
		dataSpinner.setSelection(prefs.getInt("dataMode", 2));
		boolean hosts = prefs.getBoolean("hosts", false);
		tcpdumpOptions.setnoHostNames(!hosts);
		hostsCheckBox.setChecked(hosts);
		boolean timestamp = prefs.getBoolean("timestamp", true);
		tcpdumpOptions.setnoTimeStamp(!timestamp);
		timestampCheckBox.setChecked(timestamp);
		String packetLenLim = prefs.getString("packetLenLim", "0");
		tcpdumpOptions.setPacketLenLim(Integer.valueOf(packetLenLim));
		packetLenLimEditText.setText(packetLenLim);

		return v;
	}

	@Override
	public void onResume()
	{
		super.onResume();

		// Populate spinner with device list, doing this in onResume
		// so if network changes it gets updated
		List<String> devices = TCPDumpUtils.getDeviceList(getActivity());

		ArrayAdapter<String> aa = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, devices);

		aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		deviceSpinner.setAdapter(aa);
	}

	@Override
	public void onStop()
	{
		super.onStop();

		// Save preferences
		SharedPreferences.Editor editor = dsActivity.getPreferences(0).edit();
		editor.putInt("device", tcpdumpOptions.getDeviceId() - 1);
		editor.putInt("filter", tcpdumpOptions.getFilter().getId() - 1);
		editor.putInt("verboseMode", tcpdumpOptions.getVerboseMode());
		editor.putInt("dataMode", tcpdumpOptions.getDataMode());
		editor.putBoolean("hosts", !tcpdumpOptions.isnoHostNames());
		editor.putBoolean("timestamp", !tcpdumpOptions.isnoTimeStamp());
		editor.putString("packetLenLim",
				String.valueOf(tcpdumpOptions.getPacketLenLim()));
		editor.commit();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position,
			long id)
	{
		if (parent.getId() == R.id.deviceSpinner)
		{
			if (SnifferConstants.DEBUG)
				Log.d(TAG,
						"setting deviceId to " + String.valueOf(position + 1));

			tcpdumpOptions.setDeviceId(position + 1);
		} else if (parent.getId() == R.id.filterSpinner)
		{
			TCPDumpFilter filter = (TCPDumpFilter) parent
					.getItemAtPosition(position);

			if (SnifferConstants.DEBUG)
				Log.d(TAG,
						"setting filter to " + filter.getId() + "-"
								+ filter.toString());

			tcpdumpOptions.setFilter(filter);
		} else if (parent.getId() == R.id.verboseSpinner)
		{
			if (SnifferConstants.DEBUG)
				Log.d(TAG,
						"setting verbose_mode to " + String.valueOf(position));

			tcpdumpOptions.setVerboseMode(position);
		} else if (parent.getId() == R.id.dataSpinner)
		{
			if (SnifferConstants.DEBUG)
				Log.d(TAG, "setting data_mode to " + String.valueOf(position));

			tcpdumpOptions.setDataMode(position);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent)
	{
		// Should not ever happen
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged
	 * (android.widget.CompoundButton, boolean)
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		if (buttonView.getId() == R.id.hostsCheckBox)
		{
			if (SnifferConstants.DEBUG)
				Log.d(TAG, "setting host names to " + String.valueOf(isChecked));

			tcpdumpOptions.setnoHostNames(!isChecked);
		}

		if (buttonView.getId() == R.id.timestampCheckBox)
		{
			if (SnifferConstants.DEBUG)
				Log.d(TAG, "setting timestamp to " + String.valueOf(isChecked));

			tcpdumpOptions.setnoTimeStamp(!isChecked);
		}
	}

	/**
	 * Adds a filter to the array adapter
	 * 
	 * @param name
	 *            Name of the filter
	 * @param filter
	 *            Filter string
	 */
	public void addFilter(String name, String filter)
	{
		filters.add(new TCPDumpFilter(filters.size() + 1, name,
				filter));
		filterAdapter.notifyDataSetChanged();
	}
	
	/**
	 * Edits a filter in the array adapter
	 * 
	 * @param id
	 *			Id of the filter
	 * @param name
	 * 			Name of the filter
	 * @param filter
	 *          Filter string
	 */
	public void updateFilter(int id, String name, String filter)
	{
		TCPDumpFilter editFilter = filters.get(id - 1);
		editFilter.setName(name);
		editFilter.setFilter(filter);
		filterAdapter.notifyDataSetChanged();
	}
}
