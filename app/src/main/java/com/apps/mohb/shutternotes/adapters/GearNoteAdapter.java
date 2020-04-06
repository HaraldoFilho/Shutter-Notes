/*
 *  Copyright (c) 2020 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : GearNoteAdapter.java
 *  Last modified : 4/6/20 7:31 PM
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
import java.util.Objects;


public class GearNoteAdapter extends ArrayAdapter {


	@SuppressWarnings("unchecked")
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
		txtItem.setText(Objects.requireNonNull(item).getGearItem());

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
		String prefKey = settings.getString(Constants.PREF_KEY_FONT_SIZE, Constants.PREF_FONT_SIZE_MEDIUM);

		switch (Objects.requireNonNull(prefKey)) {

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
			convertView.setBackground(getContext().getResources().getDrawable(R.drawable.gear_item_tile_selected, null));
			txtItem.setTextColor(getContext().getResources().getColor(R.color.colorBlackText, null));
		} else {
			convertView.setBackground(getContext().getResources().getDrawable(R.drawable.gear_item_tile, null));
			txtItem.setTextColor(getContext().getResources().getColor(R.color.colorGreyText, null));
		}

		return convertView;

	}
}
