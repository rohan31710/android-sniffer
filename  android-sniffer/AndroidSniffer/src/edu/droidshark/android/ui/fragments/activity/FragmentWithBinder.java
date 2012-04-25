/****************************************************************
 * Copyright (c) 2011, 2012 MyDogMedia
 ****************************************************************/
package edu.droidshark.android.ui.fragments.activity;

import edu.droidshark.android.services.UIBinder;
import android.support.v4.app.Fragment;

/**
 * This is an abstract class that takes care of setting the UIB in a fragment.
 * 
 * @author Sam SmithReams
 * 
 */
public abstract class FragmentWithBinder extends Fragment
{
	protected UIBinder uib;

	@Override
	public void onResume()
	{
		super.onResume();
		// The purpose of this is when a fragment is dynamically created and we
		// set the uib before
		// onCreateView() was executed we can now doAfterBind() appropriately.
		if (uib != null)
			doAfterBind();
	}

	/**
	 * Method called by parent activity when it binds to the UI Binder.
	 * 
	 * @param uib
	 */
	public void setBinder(UIBinder uib)
	{
		this.uib = uib;
		// This is done so that when fragment is dynamically added we don't
		// doAfterBind() if this view
		// hasn't been created.
		if (this.getView() != null)
			doAfterBind();
	}

	protected abstract void doAfterBind();
}
