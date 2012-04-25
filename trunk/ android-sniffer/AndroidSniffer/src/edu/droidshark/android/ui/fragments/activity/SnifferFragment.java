package edu.droidshark.android.ui.fragments.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.SupportActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import edu.droidshark.R;
import edu.droidshark.android.ui.activities.DroidSharkActivity;
import edu.droidshark.android.ui.adapters.SnifferAdapter;

public class SnifferFragment extends FragmentWithBinder
{
	protected final String LOG_TAG = getClass().getSimpleName();
	protected DroidSharkActivity droidSharkActivity;
	private ListView packetlist;
	//private SnifferAdapter snifferAdapter;
	private static final String[] items = {"packet 1", "packet 2"};
	
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
		/*
		((ImageButton) v.findViewById(R.id.startButton)).
				setOnClickListener(new OnClickListener()
				{
					public void onClick(View v)
					{
						// DO NOTHING FOR NOW
					}
				});
		((ImageButton) v.findViewById(R.id.stopButton)).
				setOnClickListener(new OnClickListener()
				{
					public void onClick(View v)
					{
						// DO NOTHING FOR NOW
					}
				});
		*/
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
