/*
 *  Copyright (c) 2019 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : EditGearListDialogFragment.java
 *  Last modified : 8/17/19 12:08 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes.fragments.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.apps.mohb.shutternotes.Constants;
import com.apps.mohb.shutternotes.R;
import com.apps.mohb.shutternotes.notes.GearList;

import java.io.IOException;

public class EditGearListDialogFragment extends DialogFragment {

	public interface EditGearListDialogListener {
		void onEditGearListDialogPositiveClick(DialogFragment dialog);

		void onEditGearListDialogNegativeClick(DialogFragment dialog);
	}

	private EditGearListDialogListener mListener;
	private GearList gearList;
	private EditText text;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		gearList = new GearList();

		try {
			gearList.loadState(getContext(), Constants.GEAR_LIST_SAVED_STATE);
		} catch (IOException e) {
			e.printStackTrace();
		}

		final LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.fragment_edit_gear_list_dialog, null);

		text = view.findViewById(R.id.txtEditGear);
		final String editedText = gearList.getEditedGearItemText(getContext());
		final int itemPosition = gearList.getEditedGearItemPosition(getContext());

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(view);
		builder.setTitle(R.string.dialog_add_gear_title);

		if (!editedText.isEmpty()) {
			text.setText(editedText);
			builder.setTitle(R.string.dialog_edit_gear_title);
		}

		builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				String textString = text.getText().toString();
				if (!textString.isEmpty()) {
					if (editedText.isEmpty()) {
						gearList.add(textString);
					} else if (itemPosition != Constants.NULL_POSITION) {
						gearList.setEditedGearItemText(getContext(), textString);
						gearList.setEditedGearItemPosition(getContext(), itemPosition);
					}
					try {
						gearList.saveState(getContext(), Constants.GEAR_LIST_SAVED_STATE);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				mListener.onEditGearListDialogPositiveClick(EditGearListDialogFragment.this);
			}
		})
				.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						mListener.onEditGearListDialogNegativeClick(EditGearListDialogFragment.this);
					}
				});

		return builder.create();

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the BookmarkEditDialogListener so we can send events to the host
			mListener = (EditGearListDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement AddGearDialogListener");
		}
	}

}
