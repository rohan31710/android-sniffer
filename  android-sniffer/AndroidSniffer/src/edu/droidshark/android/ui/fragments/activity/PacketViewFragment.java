package edu.droidshark.android.ui.fragments.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.voytechs.jnetstream.codec.Packet;

import edu.droidshark.R;

/**
 * A fragment that displays incoming packets from tcpdump
 * 
 * @author Sam SmithReams, Jayme Gibson
 *
 */
public class PacketViewFragment extends SherlockFragment
{
	private ListView packetList;
	private ArrayAdapter<String> adapter;
	private ArrayList<String> packets = new ArrayList<String>();
	private ArrayList<Packet> capturedPackets;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		adapter = new ArrayAdapter<String>(this.getActivity(), R.layout.packet_view_list, packets);
		
		capturedPackets = new ArrayList<Packet>();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		if (packetList != null)
			packetList.setAdapter(adapter);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.packetview_layout, container, false);
		
		packetList = (ListView) v.findViewById(R.id.packetListView);
		packetList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		
		packetList.setOnItemLongClickListener(new OnItemLongClickListener()
		{

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View v,
					int position, long id)
			{
				if (parent.getAdapter() instanceof ArrayAdapter)
				{
					PacketListLongClickDialogFragment df = new PacketListLongClickDialogFragment(capturedPackets.get(position));
					df.show(getFragmentManager(), "long click");
				}
				
				return true;
			}
			
		});
		
		return v;
	}
	
	public void updatePacketCount(int numPackets, Packet packet)
	{
		capturedPackets.add(packet);
		
		adapter.add(packet.getSummary());
	}
	
}
