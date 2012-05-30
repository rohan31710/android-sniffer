/**
 * Created May 30, 2012
 */
package edu.droidshark.android.ui.fragments.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;

import edu.droidshark.R;

/**
 * Shows info about the application
 * 
 * @author Sam SmithReams
 *
 */
public class AboutFragment extends SherlockDialogFragment
{
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		Dialog dialog = new Dialog(getActivity());
		
		dialog.setContentView(R.layout.aboutbox);
		dialog.setTitle("About " + getActivity().getString(R.string.app_name));
		
		// Setup text label for current value
		TextView aboutTextView = (TextView) dialog.findViewById(R.id.aboutText);
		SpannableString aboutText = new SpannableString("Version "
				+ getVersionName(getActivity()) + "\n"
				+ getActivity().getString(R.string.about_text) + "\n");
		
		aboutTextView.setText(aboutText);		
		Linkify.addLinks(aboutTextView, Linkify.ALL);
		
		TextView libTextView = (TextView) dialog.findViewById(R.id.libraryText);
		String libString = "";
		for(String lib : getActivity().getResources().getStringArray(R.array.libraries))
		{
			libString += lib + "\n";
		}
		SpannableString libText = new SpannableString(libString);
		libTextView.setText(libText);
		Linkify.addLinks(libTextView, Linkify.ALL);
		
		Button okButton = (Button) dialog.findViewById(R.id.aboutOk);
		okButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AboutFragment.this.dismiss();				
			}			
		});
		
		return dialog;
	}

	private String getVersionName(Context context)
	{
		try
		{
			return context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e)
		{
			return "Unknown";
		}
	}
}
