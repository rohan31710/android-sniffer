/**
 * Created May 27, 2012
 */
package edu.droidshark.android.ui.fragments.activity;

import java.io.File;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockDialogFragment;

import edu.droidshark.android.ui.activities.DroidSharkActivity;

/**
 * Dialog fragment for selecting a file to upload
 * 
 * @author Sam SmithReams
 * 
 */
public class UploadFragment extends SherlockDialogFragment
{
	private File path;

	/**
	 * @param path
	 *            The path to look for files
	 */
	public UploadFragment(File path)
	{
		this.path = path;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		setRetainInstance(true);
		
		final File[] files = path.listFiles();

		final CharSequence[] items = new CharSequence[files.length];
		for (int i = 0; i < files.length; i++)
			items[i] = files[i].getName();

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Select File");
		builder.setItems(items, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int item)
			{
				DroidSharkActivity dsActivity = (DroidSharkActivity) UploadFragment.this
						.getActivity();
				dsActivity.uploadCaptureFile(files[item]);
			}
		});

		return builder.create();
	}
}
