/*
 *  Copyright (c) 2019 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : GearList.java
 *  Last modified : 8/17/19 12:08 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes.notes;

import android.content.Context;
import android.content.SharedPreferences;

import com.apps.mohb.shutternotes.Constants;

import java.io.IOException;
import java.util.ArrayList;


public class GearList {

	private ArrayList<Gear> list;
	private SavedState gearListSavedState;
	private SharedPreferences gearTextEdit;

	public GearList() {
		list = new ArrayList<>();
	}

	public void loadState(Context context, String dataType) throws IOException {
		gearListSavedState = new SavedState(context, dataType);
		list = gearListSavedState.getGearListState();
	}

	public void saveState(Context context, String dataType) throws IOException {
		gearListSavedState = new SavedState(context, dataType);
		gearListSavedState.setGearListState(list);
	}

	public ArrayList<Gear> getList() {
		return list;
	}

	public void add(String gear) {
		Gear gearItem = new Gear(gear);
		list.add(gearItem);
	}

	public void add(int position, String gear) {
		Gear gearItem = new Gear(gear);
		list.add(position, gearItem);
	}

	public Gear get(int position) {
		return list.get(position);
	}

	public String getGearItem(int position) {
		return list.get(position).getGearItem();
	}

	public void setGearItem(int position, String gear) {
		list.get(position).setGearItem(gear);
	}

	public void moveToBottom(int position) {
		Gear gear = list.get(position);
		list.remove(position);
		list.add(gear);
	}

	public void moveToBottomOfLastSelected(int position) {
		Gear gear = list.get(position);
		list.remove(position);
		for (int i = list.size() - 1; i >= 0; i--) {
			if (list.get(i).isSelected()) {
				if (i < list.size() - 1) {
					list.add(i + 1, gear);
					return;
				} else {
					list.add(gear);
					return;
				}
			}
		}
		list.add(Constants.LIST_HEAD, gear);
		return;

	}

	public String getFlickrTags() {
		String tags = Constants.EMPTY;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).isSelected()) {
				tags = tags.concat(Constants.QUOTE + list.get(i).getGearItem() + Constants.QUOTE + Constants.SPACE);
			}
		}
		return tags;
	}

	public String getGearListText() {
		String textString = Constants.EMPTY;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).isSelected()) {
				textString = textString.concat(list.get(i).getGearItem() + Constants.NEW_LINE);
			}
		}
		return textString;
	}

	public void remove(int position) {
		list.remove(position);
	}

	public int size() {
		return list.size();
	}

	public String getEditedGearItemText(Context context) {
		gearTextEdit = context.getSharedPreferences(Constants.EDIT_GEAR_TEXT, Constants.PRIVATE_MODE);
		String editedText = gearTextEdit.getString(Constants.GEAR_EDITED_TEXT, Constants.EMPTY);
		return editedText;
	}

	public void setEditedGearItemText(Context context, String textString) {
		gearTextEdit = context.getSharedPreferences(Constants.EDIT_GEAR_TEXT, Constants.PRIVATE_MODE);
		SharedPreferences.Editor editor = gearTextEdit.edit();
		editor.putString(Constants.GEAR_EDITED_TEXT, textString);
		editor.commit();
	}

	public int getEditedGearItemPosition(Context context) {
		gearTextEdit = context.getSharedPreferences(Constants.EDIT_GEAR_TEXT, Constants.PRIVATE_MODE);
		int itemPosition = gearTextEdit.getInt(Constants.GEAR_ITEM_POSITION, Constants.NULL_POSITION);
		return itemPosition;
	}

	public void setEditedGearItemPosition(Context context, int itemPosition) {
		gearTextEdit = context.getSharedPreferences(Constants.EDIT_GEAR_TEXT, Constants.PRIVATE_MODE);
		SharedPreferences.Editor editor = gearTextEdit.edit();
		editor.putInt(Constants.GEAR_ITEM_POSITION, itemPosition);
		editor.commit();
	}

}
