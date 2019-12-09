/*
 *  Copyright (c) 2019 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : MaterialIconsDialogFragment.java
 *  Last modified : 8/17/19 12:08 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes.fragments.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.apps.mohb.shutternotes.R;

import java.util.Objects;


public class MaterialIconsDialogFragment extends DialogFragment {

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		LayoutInflater layoutInflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
		View view = layoutInflater.inflate(R.layout.fragment_about_dialog, null);

		TextView textViewTitle = (TextView) view.findViewById(R.id.txtTitle);
		TextView textView = (TextView) view.findViewById(R.id.txtText);

		textViewTitle.setText(getText(R.string.action_material_icons));
		textView.setText(getText(R.string.html_graphic_resources_attribution));

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
		alertDialogBuilder.setView(view);

		return alertDialogBuilder.create();

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

}
