package edu.droidshark.constants;

import com.dropbox.client2.session.Session.AccessType;

public interface SnifferConstants
{
	public static final boolean DEBUG = true; // Enable debug Log messages
	public static final int SNIFFERPANE = 0x01, PACKETVIEWPANE = 0x04;
	public static final String TCPDUMP_OPTIONS = "TCPDUMP_OPTIONS";
	public static final String DROPBOX_APP_KEY = "xxxx";
	public static final String DROPBOX_APP_SECRET = "xxxx";
	public static final AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
	public static final String DROPBOX_KEY_NAME = "DROPBOX_KEY";
	public static final String DROPBOX_SECRET_NAME = "DROPBOX_SECRET";
	//NOWPLAYINGPANE = 0x08;
}
