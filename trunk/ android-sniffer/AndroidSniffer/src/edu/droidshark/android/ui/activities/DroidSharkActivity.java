package edu.droidshark.android.ui.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.voytechs.jnetstream.codec.Packet;

import edu.droidshark.R;
import edu.droidshark.android.services.TCPDumpBinder;
import edu.droidshark.android.services.TCPDumpService;
import edu.droidshark.android.ui.fragments.activity.PacketViewFragment;
import edu.droidshark.android.ui.fragments.activity.SaveFragment;
import edu.droidshark.android.ui.fragments.activity.SnifferFragment;
import edu.droidshark.constants.SnifferConstants;
import edu.droidshark.tcpdump.FilterDatabase;
import edu.droidshark.tcpdump.TCPDumpListener;
import edu.droidshark.tcpdump.TCPDumpOptions;
import edu.droidshark.tcpdump.TCPDumpUtils;

public class DroidSharkActivity extends SherlockFragmentActivity
{
	// public static final int EXIT_MENU_ID = 0x8;
	private static final String TAG = "DroidSharkActivity";
	private int currPane = SnifferConstants.SNIFFERPANE;
	private SnifferFragment snifferFragment;
	private PacketViewFragment packetViewFragment;
	private FrameLayout firstPane, secondPane;
	private ActionBar.Tab mSnifTab, mPVTab;
	private ActionBar mActionBar;
	public boolean tcpdumpIsRunning, isBound;
	private TCPDumpService tService;
	private Process tProcess;
	public FilterDatabase filterDB;

	private ServiceConnection sConn = new ServiceConnection()
	{
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.content.ServiceConnection#onServiceConnected(android.content
		 * .ComponentName, android.os.IBinder)
		 */
		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			tService = ((TCPDumpBinder) service).getService();
			tService.settListener(new TCPDumpCallbacks());
			isBound = true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.content.ServiceConnection#onServiceDisconnected(android.content
		 * .ComponentName)
		 */
		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			isBound = false;
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Check to see if tcpdump is present
		try
		{
			FileInputStream fis = openFileInput("tcpdump");
			fis.close();
		} catch (FileNotFoundException e)
		{
			// If file not found need to create
			TCPDumpUtils.createTCPDump(this);
		} catch (IOException e)
		{
			Log.e(getClass().getSimpleName(),
					"IOException, message=" + e.getMessage());
		}

		setContentView(R.layout.main);

		firstPane = (FrameLayout) findViewById(R.id.first_pane);
		secondPane = (FrameLayout) findViewById(R.id.second_pane);

		if (SnifferConstants.DEBUG)
			Log.d(TAG, "Running on device: " + android.os.Build.MODEL);

		// Fragments are saved in the instance state by default, so they don't
		// have to be recreated.
		if (savedInstanceState == null)
		{
			snifferFragment = new SnifferFragment();
			packetViewFragment = new PacketViewFragment();
			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();

			transaction.add(R.id.first_pane, snifferFragment, "sniffer");
			transaction.add(R.id.second_pane, packetViewFragment, "packetView");
			transaction.commit();
		} else
		{
			snifferFragment = (SnifferFragment) getSupportFragmentManager()
					.findFragmentByTag("sniffer");
			packetViewFragment = (PacketViewFragment) getSupportFragmentManager()
					.findFragmentByTag("packetView");
			currPane = savedInstanceState.getInt("currPane");
		}

		// BEGIN FRAGMENT STUFF
		if (currPane == SnifferConstants.SNIFFERPANE)
			showSniffer();
		else if (currPane == SnifferConstants.PACKETVIEWPANE)
			showPacketView();

		// Start service onCreate(), so it is not destroyed when activity
		// unbinds.
		startService(new Intent(this, TCPDumpService.class));

		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(false);
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mSnifTab = mActionBar
				.newTab()
				.setText(R.string.sniffer)
				.setTabListener(
						new ActionTabListener<SnifferFragment>(this, "Snif",
								SnifferFragment.class));
		if (currPane == SnifferConstants.SNIFFERPANE)
			mActionBar.addTab(mSnifTab, true);
		else
			mActionBar.addTab(mSnifTab, false);

		mPVTab = mActionBar
				.newTab()
				.setText(R.string.packet_view)
				.setTabListener(
						new ActionTabListener<PacketViewFragment>(this, "PV",
								PacketViewFragment.class));
		if (currPane == SnifferConstants.PACKETVIEWPANE)
			mActionBar.addTab(mPVTab, true);
		else
			mActionBar.addTab(mPVTab, false);

		// Get database of filters
		filterDB = new FilterDatabase(this);
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		Log.d(TAG, "Binding TCPDumpService");
		bindService(new Intent(this, TCPDumpService.class), sConn,
				Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		tcpdumpIsRunning = TCPDumpUtils.isTCPDumpRunning();

		if (SnifferConstants.DEBUG)
			Log.d(TAG, "tcpdumpRunning=" + tcpdumpIsRunning);
	}

	@Override
	public void onStop()
	{
		super.onStop();
		// Unbind the service
		if (isBound)
		{
			tService.settListener(null);
			if (SnifferConstants.DEBUG)
				Log.d(TAG, "Unbinding TCPDumpService");
			unbindService(sConn);
		}

		filterDB.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = this.getSupportMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		// Calling super after populating the menu is necessary here to ensure
		// that the action bar helpers have a chance to handle this event.
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);

		outState.putInt("currPane", currPane);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle item selection
		switch (item.getItemId())
		{
		case R.id.exit:
			stopService(new Intent(this, TCPDumpService.class));
			finish();
			return true;
		case R.id.save:
			if (tcpdumpIsRunning)
				Toast.makeText(this, "Sniffer must be stopped",
						Toast.LENGTH_SHORT).show();
			else
				openSaveDialog();
			return true;
		case R.id.start:
			if (tcpdumpIsRunning)
				Toast.makeText(this, "Sniffer already running",
						Toast.LENGTH_SHORT).show();
			else
				startSniffer();
			return true;
		case R.id.stop:
			if (tcpdumpIsRunning)
				stopSniffer();
			else
				Toast.makeText(this, "Sniffer not running", Toast.LENGTH_SHORT).show();
			return true;
		default:
			return false;
		}
	}

	/**
	 * Opens save prompt
	 */
	private void openSaveDialog()
	{
		if (new File(getExternalFilesDir(null) + "/capture.pcap").exists())
			new SaveFragment(Environment.getExternalStorageDirectory()
					.getPath() + "/Capture").show(getSupportFragmentManager(),
					"save");
		else
			Toast.makeText(this, "No capture file found", Toast.LENGTH_SHORT).show();
	}

	/**
	 * Saves the last capture file with specified name
	 * 
	 * @param filename
	 *            The filename to be saved
	 */
	public void saveCaptureFile(String filename)
	{
		File path = Environment.getExternalStorageDirectory();
		File capDir = new File(path.getPath() + "/Capture");
		File saveFile = new File(capDir.getPath() + "/" + filename);

		try
		{
			// Make sure the Pictures directory exists.
			capDir.mkdirs();
			File captureFile = new File(getExternalFilesDir(null)
					+ "/capture.pcap");
			FileUtils.copyFile(captureFile, saveFile);
		} catch (IOException e)
		{
			Log.e(TAG, "Error writing file, msg=" + e.getMessage());
		}

	}

	/**
	 * Starts the sniffer
	 */
	private void startSniffer()
	{
		try
		{
			tcpdumpIsRunning = true;
			EditText packetLenLimEditText = snifferFragment
					.getPacketLenLimEditText();
			TCPDumpOptions tcpdumpOptions = snifferFragment.getTCPDumpOptions();
			closeIME(packetLenLimEditText.getWindowToken());
			tcpdumpOptions.setPacketLenLim(Integer.valueOf(packetLenLimEditText
					.getText().toString()));
			Process proc = TCPDumpUtils.startTCPDump(this, tcpdumpOptions);
			if (proc == null)
			{
				Toast.makeText(this,
						"Syntax error occurred, check your filter",
						Toast.LENGTH_SHORT).show();
				tcpdumpIsRunning = false;
			} else
			{
				tProcess = proc;
				tService.openFileStream(tProcess);
			}
		} catch (NumberFormatException e)
		{
			Toast.makeText(this, "Invalid Packet Length Limit",
					Toast.LENGTH_SHORT).show();
			tcpdumpIsRunning = false;
		}

		snifferFragment.setRunningText(tcpdumpIsRunning);
	}

	/**
	 * Stops the sniffer
	 */
	private void stopSniffer()
	{
		closeIME(snifferFragment.getPacketLenLimEditText().getWindowToken());
		TCPDumpUtils.stopTCPDump();
		tService.closeFileStream();
		tcpdumpIsRunning = false;
		snifferFragment.setRunningText(tcpdumpIsRunning);
	}

	/**
	 * Closes the soft keyboard
	 * 
	 * @param windowToken
	 *            The field's(ie EditText) window token (use getWindowToken())
	 *            that currently is using the soft keyboard.
	 */
	public void closeIME(IBinder windowToken)
	{
		InputMethodManager mgr = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(windowToken, 0);
	}

	public void showSniffer()
	{
		firstPane.setVisibility(View.VISIBLE);
		secondPane.setVisibility(View.GONE);
		currPane = SnifferConstants.SNIFFERPANE;

		if (mSnifTab != null)
			mActionBar.setSelectedNavigationItem(mSnifTab.getPosition());
	}

	public void showPacketView()
	{
		firstPane.setVisibility(View.GONE);
		secondPane.setVisibility(View.VISIBLE);
		currPane = SnifferConstants.PACKETVIEWPANE;

		if (mPVTab != null)
			mActionBar.setSelectedNavigationItem(mPVTab.getPosition());
	}

	/**
	 * Adds a filter to the database
	 * 
	 * @param name
	 *            Name of the filter
	 * @param filter
	 *            Filter string
	 */
	public void addFilter(String name, String filter)
	{
		ContentValues cv = new ContentValues();
		cv.put("name", name);
		cv.put("filter", filter);
		filterDB.getWritableDatabase().insert("filters", "name", cv);
		snifferFragment.addFilter(name, filter);
	}

	/**
	 * Edits a filter to the database
	 * 
	 * @param name
	 *            Name of the filter
	 * @param filter
	 *            Filter string
	 */
	public void editFilter(int id, String name, String filter)
	{
		ContentValues cv = new ContentValues();
		cv.put("name", name);
		cv.put("filter", filter);
		filterDB.getWritableDatabase().update("filters", cv, "_id=?",
				new String[] { String.valueOf(id) });
		snifferFragment.updateFilter(id, name, filter);
	}

	/**
	 * A class for doing something with callbacks from TCPDumpService
	 * 
	 * @author Sam SmithReams
	 * 
	 */
	public class TCPDumpCallbacks implements TCPDumpListener
	{

		/*
		 * (non-Javadoc)
		 * 
		 * @see edu.droidshark.tcpdump.TCPDumpListener#packetReceived(int)
		 */
		@Override
		public void packetReceived(final int numPackets, Packet packet)
		{
			DroidSharkActivity.this.runOnUiThread(new Runnable()
			{

				@Override
				public void run()
				{
					packetViewFragment.updatePacketCount(numPackets);
				}

			});
		}
	}

	public class ActionTabListener<T extends SherlockFragment> implements
			TabListener
	{
		private final Activity mActivity;
		private final String mTag;

		// private final Class<T> mClass;

		/**
		 * @param activity
		 * @param tag
		 * @param clz
		 */
		public ActionTabListener(Activity activity, String tag, Class<T> clz)
		{
			mActivity = activity;
			mTag = tag;
			// mClass = clz;
		}

		public void onTabReselected(Tab tab, FragmentTransaction unused)
		{
			// User selected the already selected tab. do nothing

		}

		public void onTabSelected(Tab tab, FragmentTransaction unused)
		{
			// Check if fragment is already initialized
			if (mTag == "Snif")
			{
				((DroidSharkActivity) mActivity).showSniffer();
			} else if (mTag == "PV")
			{
				((DroidSharkActivity) mActivity).showPacketView();
			}
		}

		public void onTabUnselected(Tab tab, FragmentTransaction unused)
		{
			// do nothing
		}
	}
}