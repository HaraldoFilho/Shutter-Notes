/*
 *  Copyright (c) 2019 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : AuthenticationNeededAlertFragment.java
 *  Last modified : 12/20/19 12:52 AM
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


public class AuthenticationNeededAlertFragment extends DialogFragment {

	public interface AuthenticationNeededAlertDialogListener {
		void onAuthenticationNeededDialogPositiveClick(DialogFragment dialog);
		void onAuthenticationNeededDialogNegativeClick(DialogFragment dialog);
	}

	private AuthenticationNeededAlertDialogListener mListener;


	@NonNull
	@Override
	public AlertDialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.alert_title_authenticate).setMessage(R.string.alert_message_authenticate)
				.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						mListener.onAuthenticationNeededDialogPositiveClick(AuthenticationNeededAlertFragment.this);
					}
				})
				.setNegativeButton(R.string.alert_button_no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						mListener.onAuthenticationNeededDialogNegativeClick(AuthenticationNeededAlertFragment.this);
					}
				});

		return builder.create();

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the AuthenticationNeededDialogListener so we can send events to the host
			mListener = (AuthenticationNeededAlertDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement AuthenticationNeededDialogListener");
		}
	}

}
