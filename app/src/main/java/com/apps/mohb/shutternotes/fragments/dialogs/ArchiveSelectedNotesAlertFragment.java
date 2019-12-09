/*
 *  Copyright (c) 2019 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : ArchiveSelectedNotesAlertFragment.java
 *  Last modified : 8/17/19 12:08 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes.fragments.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.apps.mohb.shutternotes.R;


public class ArchiveSelectedNotesAlertFragment extends DialogFragment {

	public interface ArchiveSelectedNotesAlertDialogListener {
		void onArchiveSelectedNotesDialogPositiveClick(DialogFragment dialog);

		void onArchiveSelectedNotesDialogNegativeClick(DialogFragment dialog);
	}

	private ArchiveSelectedNotesAlertDialogListener mListener;


	@NonNull
	@Override
	public AlertDialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.alert_title_archive_selected_notes).setMessage(R.string.alert_message_can_be_restored)
				.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						mListener.onArchiveSelectedNotesDialogPositiveClick(ArchiveSelectedNotesAlertFragment.this);
					}
				})
				.setNegativeButton(R.string.alert_button_no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						mListener.onArchiveSelectedNotesDialogNegativeClick(ArchiveSelectedNotesAlertFragment.this);
					}
				});

		return builder.create();

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the ArchiveSelectedNotesDialogListener so we can send events to the host
			mListener = (ArchiveSelectedNotesAlertDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement ArchiveSelectedNotesDialogListener");
		}
	}

}
