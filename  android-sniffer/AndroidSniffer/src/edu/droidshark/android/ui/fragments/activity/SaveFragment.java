/**
 * Created May 27, 2012
 */
package edu.droidshark.android.ui.fragments.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Dialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;

import edu.droidshark.R;
import edu.droidshark.android.ui.activities.DroidSharkActivity;

/**
 * Dialog for saving pcap file
 * 
 * @author Sam SmithReams
 * 
 */
public class SaveFragment extends SherlockDialogFragment
{	
	private String savePath;
	
	/**
	 * @param savePath
	 * 			Path file is saved at
	 */
	public SaveFragment(String savePath)
	{
		this.savePath = savePath;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		Dialog dialog = new Dialog(this.getActivity());

		dialog.setContentView(R.layout.save_layout);
		dialog.setTitle("Save Capture File");

		TextView pathTextView = (TextView) dialog.findViewById(R.id.pathTextView);
		pathTextView.setText("Directory - " + savePath);
		
		final EditText fileNameEditText = (EditText) dialog
				.findViewById(R.id.fileNameEditText);
		SimpleDateFormat format = new SimpleDateFormat("MMddyy-HHmm.ss");
		fileNameEditText.setText(format.format(new Date()));
		// Filter for acceptable filename characters
		InputFilter filter = new InputFilter()
		{
			@Override
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend)
			{
				for (int i = start; i < end; i++)
				{
					Character c = source.charAt(i);
					if (!Character.isLetterOrDigit(c) && c != '-' && c != '_'
							&& c != '.')
					{
						return "";
					}
				}
				return null;
			}
		};
		fileNameEditText.setFilters(new InputFilter[] { filter });

		Button cancelButton = (Button) dialog
				.findViewById(R.id.saveCancelButton);
		cancelButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				SaveFragment.this.dismiss();
			}

		});

		Button saveButton = (Button) dialog.findViewById(R.id.saveButton);
		saveButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				DroidSharkActivity dsActivity = (DroidSharkActivity) SaveFragment.this
						.getActivity();
				dsActivity.saveCaptureFile(fileNameEditText.getText()
						.toString() + ".pcap");
				SaveFragment.this.dismiss();
			}
		});

		return dialog;
	}
}
