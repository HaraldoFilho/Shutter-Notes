/*
 *  Copyright (c) 2019 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : GearNoteAdapter.java
 *  Last modified : 8/17/19 12:08 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.apps.mohb.shutternotes.Constants;
import com.apps.mohb.shutternotes.R;
import com.apps.mohb.shutternotes.notes.Gear;

import java.util.ArrayList;


public class GearNoteAdapter extends ArrayAdapter {


	public GearNoteAdapter(@NonNull Context context, ArrayList<Gear> note) {
		super(context, Constants.LIST_ADAPTER_RESOURCE_ID, note);
	}

	@NonNull
	@Override
	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

		Gear item = (Gear) getItem(position);

		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.gear_item, parent, false);
		}

		TextView txtItem = convertView.findViewById(R.id.gearView);
		txtItem.setText(item.getGearItem());

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
		String prefKey = settings.getString(Constants.PREF_KEY_FONT_SIZE, Constants.PREF_FONT_SIZE_MEDIUM);

		switch (prefKey) {

			case Constants.PREF_FONT_SIZE_SMALL:
				txtItem.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.FONT_SIZE_SMALL_MEDIUM);
				break;

			case Constants.PREF_FONT_SIZE_MEDIUM:
				txtItem.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.FONT_SIZE_MEDIUM_MEDIUM);
				break;

			case Constants.PREF_FONT_SIZE_LARGE:
				txtItem.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.FONT_SIZE_LARGE_MEDIUM);
				break;

		}

		if (item.isSelected()) {
			txtItem.setTextColor(getContext().getResources().getColor(R.color.colorBlackText));
		} else {
			txtItem.setTextColor(getContext().getResources().getColor(R.color.colorGreyText));
		}

		return convertView;

	}
}
