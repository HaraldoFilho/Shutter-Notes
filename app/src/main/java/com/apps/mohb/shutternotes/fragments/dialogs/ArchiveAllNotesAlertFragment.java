/*
 *  Copyright (c) 2019 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : ArchiveAllNotesAlertFragment.java
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


public class ArchiveAllNotesAlertFragment extends DialogFragment {

	public interface ArchiveAllNotesAlertDialogListener {
		void onArchiveAllNotesDialogPositiveClick(DialogFragment dialog);

		void onArchiveAllNotesDialogNegativeClick(DialogFragment dialog);
	}

	private ArchiveAllNotesAlertDialogListener mListener;


	@Override
	public AlertDialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.alert_title_archive_all_notes).setMessage(R.string.alert_message_can_be_restored)
				.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						mListener.onArchiveAllNotesDialogPositiveClick(ArchiveAllNotesAlertFragment.this);
					}
				})
				.setNegativeButton(R.string.alert_button_no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						mListener.onArchiveAllNotesDialogNegativeClick(ArchiveAllNotesAlertFragment.this);
					}
				});

		return builder.create();

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the ArchiveAllNotesDialogListener so we can send events to the host
			mListener = (ArchiveAllNotesAlertDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement ArchiveAllNotesDialogListener");
		}
	}

}
