package edu.droidshark.android.ui.fragments.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

import edu.droidshark.R;

/**
 * A fragment that displays incoming packets from tcpdump
 * 
 * @author Sam SmithReams, Jayme Gibson
 *
 */
public class PacketViewFragment extends SherlockFragment
{
	private TextView numPacketsTextView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.packetview_layout, container, false);
		
		numPacketsTextView = (TextView) v.findViewById(R.id.numPacketsTextView);
		
		return v;
	}
	
	public void updatePacketCount(int numPackets)
	{
		numPacketsTextView.setText(numPackets + " packets");
	}
	
}
