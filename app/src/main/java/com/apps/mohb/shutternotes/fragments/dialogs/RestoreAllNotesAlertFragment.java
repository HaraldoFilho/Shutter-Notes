/*
 *  Copyright (c) 2019 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : RestoreAllNotesAlertFragment.java
 *  Last modified : 8/17/19 12:08 PM
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


public class RestoreAllNotesAlertFragment extends DialogFragment {

	public interface RestoreAllNotesAlertDialogListener {
		void onRestoreAllNotesDialogPositiveClick(DialogFragment dialog);

		void onRestoreAllNotesDialogNegativeClick(DialogFragment dialog);
	}

	private RestoreAllNotesAlertDialogListener mListener;


	@Override
	public AlertDialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.alert_title_restore_all_notes).setMessage(R.string.alert_message_restore_all_notes)
				.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						mListener.onRestoreAllNotesDialogPositiveClick(RestoreAllNotesAlertFragment.this);
					}
				})
				.setNegativeButton(R.string.alert_button_no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						mListener.onRestoreAllNotesDialogNegativeClick(RestoreAllNotesAlertFragment.this);
					}
				});

		return builder.create();

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the RestoreAllNotesDialogListener so we can send events to the host
			mListener = (RestoreAllNotesAlertDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement RestoreAllNotesDialogListener");
		}
	}

}
