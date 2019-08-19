/*
 *  Copyright (c) 2019 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : FlickrNoteTipAlertFragment.java
 *  Last modified : 8/17/19 10:53 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes.fragments.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.apps.mohb.shutternotes.R;


public class FlickrNoteTipAlertFragment extends DialogFragment {

	public interface FlickrNoteTipDialogListener {
		void onFlickrNoteTipDialogPositiveClick(DialogFragment dialog);
	}

	private FlickrNoteTipDialogListener mListener;


	@Override
	public AlertDialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
		alertDialogBuilder.setTitle(R.string.dialog_warning_title).setMessage(R.string.dialog_warning_flickr_note_message)
				.setPositiveButton(R.string.dialog_warning_button_ok, (dialog, id) -> mListener.onFlickrNoteTipDialogPositiveClick(FlickrNoteTipAlertFragment.this));

		return alertDialogBuilder.create();

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the FlickrNoteTipDialogListener so we can send events to the host
			mListener = (FlickrNoteTipDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement FlickrNoteTipDialogListener");
		}
	}


}
