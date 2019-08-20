/*
 *  Copyright (c) 2019 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : FullscreenTipAlertFragment.java
 *  Last modified : 8/19/19 10:20 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes.fragments.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.apps.mohb.shutternotes.R;


public class FullscreenTipAlertFragment extends DialogFragment {

	public interface FullscreenTipDialogListener {
		void onFullscreenTipDialogPositiveClick(DialogFragment dialog);
	}

	private FullscreenTipDialogListener mListener;


	@Override
	public AlertDialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
		alertDialogBuilder.setTitle(R.string.dialog_instruction_title).setMessage(R.string.dialog_instruction_fullscreen_message)
				.setPositiveButton(R.string.dialog_warning_button_ok, (dialog, id) -> mListener.onFullscreenTipDialogPositiveClick(FullscreenTipAlertFragment.this));

		return alertDialogBuilder.create();

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the FullscreenTipDialogListener so we can send events to the host
			mListener = (FullscreenTipDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement FullscreenTipDialogListener");
		}
	}


}
