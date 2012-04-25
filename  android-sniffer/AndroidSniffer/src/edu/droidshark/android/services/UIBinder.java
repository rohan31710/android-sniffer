package edu.droidshark.android.services;

import android.content.Context;
import android.os.Binder;

/**
 * An Android Binder interface for the UIManager service. When Activities bind
 * to the UIManager service, they will get an instance of this class. This class
 * provides an Android Activity-Stack safe interface.
 * 
 * @author Ian Zieg
 * 
 */
public class UIBinder extends Binder
{

	protected UIManagerService uiman; // The parent service
	@SuppressWarnings("javadoc")
	public boolean wifiEnabled;
	//protected boolean fullscreenRequested; 
	
	/**
	 * @param req
	 * 			True to request full screen, false if not
	 *//*
	public void requestFullscreen(boolean req)
	{
		fullscreenRequested = req;
	}
	*/
	/**
	 * @return
	 * 		True if full screen requested, false if not
	 *//*
	public boolean isFullscreenRequested()
	{
		return fullscreenRequested;
	}
	*/
	
	/**
	 * Initializes fullscreenRequested
	 */
	public UIBinder()
	{
		//requestFullscreen(false);
	}

	/**
	 * @param context
	 * 			Application context
	 */
	public void onCreate(Context context)
	{
		uiman = (UIManagerService) context;
	}

	/**
	 * finish() Should be called by the bound Activity to tell us that it has
	 * lost focus or is stopping. Unregisters listeners used by the bound
	 * Activity.
	 */
	public void finish()
	{
		
	}

	/**
	 * @return The UPnP Manager
	 */
	//public UpnpManager getUpnpManager()
	//{
		//return uiman.getUpnpManager();
	//}
	
	/**
	 * @return The UPnP Service
	 */
	//public AndroidUpnpService getUpnpService()
	//{
		//return uiman.upnpService;
	//}

} // end class UIBinder
