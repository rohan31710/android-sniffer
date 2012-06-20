/**
 * Created Jun 20, 2012
 */
package edu.droidshark.android.ui.fragments.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Dialog;
import android.os.Bundle;
import android.webkit.WebView;

import com.actionbarsherlock.app.SherlockDialogFragment;

import edu.droidshark.R;

/**
 * Dialog for displaying help
 * 
 * @author Sam SmithReams
 *
 */
public class HelpFragment extends SherlockDialogFragment
{

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		Dialog dialog = new Dialog(getActivity());
		
		dialog.setContentView(R.layout.helpdialog);
		dialog.setTitle(getActivity().getString(R.string.app_name) + " Help");
		
		WebView helpText = (WebView) dialog.findViewById(R.id.helptext);
		
		// Read the help file
		helpText.loadData(readFromHTMLResource(R.raw.help),"text/html" , "UTF-8");
		
		return dialog;
	}
	
	private String readFromHTMLResource(int resId)
	{
		InputStream inStream = getActivity().getResources().openRawResource(resId);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		
		int i;
		try
		{
			i = inStream.read();
			while(i != -1)
			{
				outStream.write(i);
				i = inStream.read();
			}
			inStream.close();
		}
		catch (IOException ex)
		{
			// no nothing for now
		}
		return outStream.toString();
	}
	
	@Override
	public void onDestroyView() 
	{
	  if (getDialog() != null && getRetainInstance())
	    getDialog().setOnDismissListener(null);
	  super.onDestroyView();
	}
}
