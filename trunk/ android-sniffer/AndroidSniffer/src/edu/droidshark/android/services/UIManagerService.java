package edu.droidshark.android.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Build;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import edu.droidshark.R;
import edu.droidshark.android.ui.activities.DroidSharkActivity;

public class UIManagerService extends Service
{
	protected static final String TAG = "UIManagerService";

	// Telephony Listener
	PhoneStateListener phoneStateListener = new PhoneStateListener()
	{
		@Override
		public void onCallStateChanged(int state, String incomingNumber)
		{
			if (state == TelephonyManager.CALL_STATE_RINGING)
			{
				// Incoming call: Pause music
				//if (upnpManager != null && upnpManager.getAvt() != null)
					//upnpManager.getAvt().pause();
			} else if (state == TelephonyManager.CALL_STATE_IDLE)
			{
				// Not in call: Play music
			} else if (state == TelephonyManager.CALL_STATE_OFFHOOK)
			{
				// A call is dialing, active or on hold
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	};

	// User Notification
	protected NotificationManager notMan;
	//protected int NOTIFICATION = R.string.uimanager_service_started;


	private String serverAddress, serverPort;

	// Receiver for wifi state changes.
	BroadcastReceiver wifiReceiver;
	
	private WifiLock globalLock;

	// Binder interface for the Activities.
	//@SuppressWarnings("javadoc")
	public final UIBinder uib = new UIBinder();

	// The http server
	//private MediaFileServer t;

	// UPnP Service
	//protected UpnpManager upnpManager;
	//protected AndroidUpnpService upnpService;
	protected ServiceConnection servConn = new ServiceConnection()
	{
		public void onServiceConnected(ComponentName className,
				IBinder service)
		{
			//upnpService = (AndroidUpnpService) service;
			//upnpManager.serviceConnected(upnpService);
		} // end onServiceConnected()

		public void onServiceDisconnected(ComponentName className)
		{
			//upnpService = null;
			//upnpManager.serviceDisconnected();
		} // end onServiceDisconnected()
	}; // end servConn

	/**
	 * @return Formatted http address string
	 */
	public String getAddressString()
	{
		return "http://" + serverAddress + ":" + serverPort + "/";
	}

	/******** ANDROID SERVICE CYCLE ********/
	@Override
	public void onCreate()
	{
		notMan = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		//upnpManager = new UpnpManager(this);
		showNotification();

		//initUpnpService();
		initWifiReceiver();
		//initServer();
		Log.i(TAG, "Service started.");
		
		// Register the phone call listener
		TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		if(mgr != null) {
		    mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.i(TAG, "Received start id " + startId + ": " + intent);
		return START_STICKY;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Log.i(TAG, "Shutting down...");
		shutdown();
		Log.i(TAG, "Service Stopped.");
	}

	/******** ANDROID SERVICE CYCLE ********/

	protected void initUpnpService()
	{
		// startService(new Intent(this, AndroidUpnpServiceImpl.class));
		//bindService(new Intent(this, UpnpMediaService.class),
				//servConn, Context.BIND_AUTO_CREATE);
	} // end initUpnpService()

	/**
	 * Check initial wifi state and register receiver for when it changes.
	 */
	protected void initWifiReceiver()
	{/*
		final WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		globalLock = wifiManager.createWifiLock("globalLock");
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int address = 0;
		uib.wifiEnabled = wifiManager.isWifiEnabled();
		if (uib.wifiEnabled)
			address = wifiInfo.getIpAddress();

		setServerAddress(address);

		wifiReceiver = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent)
			{
				int wifiState = intent.getIntExtra(
						WifiManager.EXTRA_WIFI_STATE, -1);

				int address = 0;
				if (wifiState == WifiManager.WIFI_STATE_ENABLED)
				{
					uib.wifiEnabled = true;
					address = wifiManager.getConnectionInfo().getIpAddress();
				}
				else
					uib.wifiEnabled = false;

				setServerAddress(address);
			}
		};
		this.registerReceiver(wifiReceiver, new IntentFilter(
				WifiManager.WIFI_STATE_CHANGED_ACTION));*/
	} // end initWifiReceiver

	/**
	 * Sets the address of the device depending on wifi state.
	 * 
	 * @param address
	 * @see #WifiInfo.getIpAddress()
	 *//*
	private void setServerAddress(int address)
	{
		if ("sdk".equals(Build.MODEL))
			serverAddress = "127.0.0.1";
		else
		{
			if (uib.wifiEnabled)
				serverAddress = Formatter
						.formatIpAddress(address);
			else
				serverAddress = "127.0.0.1";
		}

		Log.i(getClass().getSimpleName(), "Setting server address="
				+ serverAddress);
	}
	*/
	/**
	 * Starts the http server for serving media files.
	 *//*
	protected void initServer()
	{
		try
		{
			t = new MediaFileServer(this, getApplicationContext()
					.getContentResolver(),
					(WifiManager) getSystemService(Context.WIFI_SERVICE));
			serverPort = t.getPort();
			t.setDaemon(false);
			t.start();
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	*/
	/**
	 * Acquire global wifilock.
	 */
	public void acquireGlobalWifiLock()
	{
		if(!globalLock.isHeld())
			globalLock.acquire();		
	}
	
	/**
	 * Release global wifilock.
	 */
	public void releaseGlobalWifiLock()
	{
		if(globalLock.isHeld())
			globalLock.release();
	}
	
	/**
	 * Method called by onDestroy() to properly shutdown the service.
	 */
	protected void shutdown()
	{
		// *** Make sure all sate is saved here ***
		//upnpManager.finish();

		this.unregisterReceiver(wifiReceiver);
		releaseGlobalWifiLock();
		unbindService(servConn);
		// I had this after stopping the service before but I think in some
		// cases this thread
		// might get terminated really fast and cause a null point after
		// stopping service.
		// Honestly not sure if this is even necessary.
		//if (t != null)
			//t.close();
		//stopService(new Intent(this, UpnpMediaService.class));
		notMan.cancelAll();
		
		// Un-register our phone state listener
		TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		if(mgr != null) {
		    mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
		}
	}

	/**
	 * Shows a notification in the notification bar.
	 */
	protected void showNotification()
	{/*
		CharSequence textMajor = getText(NOTIFICATION);
		CharSequence textMinor = getText(R.string.uimanager_service_launcher);
		Notification note = new Notification(R.drawable.ic_launcher, textMajor,
				System.currentTimeMillis());
		
		note.flags = Notification.FLAG_ONGOING_EVENT;
		
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, MainActivity.class), 0);

		note.setLatestEventInfo(this, textMinor, textMajor, contentIntent);

		notMan.notify(NOTIFICATION, note);*/
	}

	/**
	 * @return the UpnpManager object
	 */
	//public UpnpManager getUpnpManager()
	//{
		//return upnpManager;
	//}

	@Override
	public IBinder onBind(Intent intent)
	{
		uib.onCreate(this);
		return uib;
	}
} // end class UIManager
