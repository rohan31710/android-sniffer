package edu.droidshark.android.ui.fragments.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.voytechs.jnetstream.codec.Field;
import com.voytechs.jnetstream.codec.Header;
import com.voytechs.jnetstream.codec.Packet;

import edu.droidshark.R;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

public class PacketListLongClickDialogFragment extends DialogFragment
{
	private ExpandableListView ExpListView;
	private ExpandableListAdapter adapter;
	private Packet packet;
	private Header header;
	private Field field;
	private static final String NAME = "NAME";
    private static final String INFO = "INFO";
    
    public PacketListLongClickDialogFragment(Packet packet)
    {
    	this.packet = packet;
    }
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		Dialog dialog = new Dialog(getActivity());
		dialog.setContentView(R.layout.packet_dialog_layout);
		
		ExpListView = (ExpandableListView) dialog.findViewById(R.id.expandableListView1);
		
		List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
        List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
        
        for (int i = 0; i < packet.getHeaderCount(); i++)
        {
        	header = packet.getHeader(i);
        	
            Map<String, String> curGroupMap = new HashMap<String, String>();
            groupData.add(curGroupMap);
            curGroupMap.put(NAME, header.getName());
            curGroupMap.put(INFO, header.toString());

            List<Map<String, String>> children = new ArrayList<Map<String, String>>();
            for (int j = 0; j < header.getFieldCount(); j++)
            {
            	field = header.getField(j);
            	
                Map<String, String> curChildMap = new HashMap<String, String>();
                children.add(curChildMap);
                curChildMap.put(NAME, field.getName());
                curChildMap.put(INFO, field.toString());
            }
            childData.add(children);
        }
        
        adapter = new SimpleExpandableListAdapter(
                getActivity(),
                groupData,
                //R.layout.child_layout,
                android.R.layout.simple_expandable_list_item_1,
                new String[] { NAME, INFO },
                new int[] { android.R.id.text1, android.R.id.text2 },
                childData,
                R.layout.child_layout,
                //android.R.layout.simple_expandable_list_item_2,
                new String[] { NAME, INFO },
                new int[] { android.R.id.text1, android.R.id.text2 }
                );
        ExpListView.setAdapter(adapter);
        
		return dialog;
	}
}
