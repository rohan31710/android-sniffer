/**
 * Created May 27, 2012
 */
package edu.droidshark.android.ui.fragments.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockDialogFragment;

import edu.droidshark.R;
import edu.droidshark.android.ui.activities.DroidSharkActivity;
import edu.droidshark.tcpdump.TCPDumpFilter;

/**
 * A fragment for creating/editing filters
 * 
 * @author Sam SmithReams
 * 
 */
public class FilterEditFragment extends SherlockDialogFragment
{
	private TCPDumpFilter filter;
	
	/**
	 * @param index
	 * 			Index of the selected filter
	 */
	public FilterEditFragment(TCPDumpFilter filter)
	{
		this.filter = filter;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		Dialog dialog = new Dialog(this.getActivity());
		setRetainInstance(true);

		dialog.setContentView(R.layout.filter_edit_layout);
		dialog.setTitle("Edit Filter");

		final EditText nameEditText = (EditText) dialog
				.findViewById(R.id.filterNameEditText);
		nameEditText.setText(filter.getName());
		final EditText filterEditText = (EditText) dialog
				.findViewById(R.id.filterFilterEditText);
		filterEditText.setText(filter.getFilter());

		Button cancelButton = (Button) dialog.findViewById(R.id.filterCancel);
		cancelButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				FilterEditFragment.this.dismiss();
			}

		});

		Button newButton = (Button) dialog.findViewById(R.id.filterNew);
		newButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				DroidSharkActivity dsActivity = (DroidSharkActivity) FilterEditFragment.this
						.getActivity();
				dsActivity.addFilter(nameEditText.getText().toString(),
						filterEditText.getText().toString());
				FilterEditFragment.this.dismiss();
			}
		});
		
		Button editButton = (Button) dialog.findViewById(R.id.filterEdit);
		editButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				DroidSharkActivity dsActivity = (DroidSharkActivity) FilterEditFragment.this
						.getActivity();
				dsActivity.editFilter(filter.getId(), nameEditText.getText().toString(),
						filterEditText.getText().toString());
				FilterEditFragment.this.dismiss();
			}
		});

		return dialog;
	}
}
