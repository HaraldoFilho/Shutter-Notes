/*
 *  Copyright (c) 2019 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : GearDeleteAlertFragment.java
 *  Last modified : 8/17/19 12:08 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes.fragments.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.apps.mohb.shutternotes.R;


public class GearDeleteAlertFragment extends DialogFragment {

	public interface GearDeleteDialogListener {
		void onGearDeleteDialogPositiveClick(DialogFragment dialog);

		void onGearDeleteDialogNegativeClick(DialogFragment dialog);
	}

	private GearDeleteDialogListener mListener;

	@Override
	public AlertDialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.alert_title_delete_gear).setMessage(R.string.alert_message_no_undone)
				.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						mListener.onGearDeleteDialogPositiveClick(GearDeleteAlertFragment.this);
					}
				})
				.setNegativeButton(R.string.alert_button_no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						mListener.onGearDeleteDialogNegativeClick(GearDeleteAlertFragment.this);
					}
				});

		return builder.create();

	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the NetworkDeleteDialogListener so we can send events to the host
			mListener = (GearDeleteDialogListener) context;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(context.toString()
					+ " must implement NetworkDeleteDialogListener");
		}
	}

}