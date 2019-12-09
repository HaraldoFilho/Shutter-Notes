/*
 *  Copyright (c) 2019 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : Archive.java
 *  Last modified : 8/17/19 10:59 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes.notes;

import android.content.Context;

import com.apps.mohb.shutternotes.Constants;

import java.io.IOException;
import java.util.ArrayList;


public class Archive {

	private ArrayList<SimpleNote> simpleNotes;
	private ArrayList<GearNote> gearNotes;
	private ArrayList<FlickrNote> flickrNotes;
	private SavedState archiveSavedState;

	public Archive() {
		simpleNotes = new ArrayList<>();
		gearNotes = new ArrayList<>();
		flickrNotes = new ArrayList<>();
	}

	public void loadState(Context context) throws IOException {
		archiveSavedState = new SavedState(context, Constants.ARCHIVE_SAVED_STATE);
		simpleNotes = archiveSavedState.getSimpleNotesState();
		gearNotes = archiveSavedState.getGearNotesState();
		flickrNotes = archiveSavedState.getFlickrNotesState();
	}

	public void saveState(Context context) throws IOException {
		archiveSavedState = new SavedState(context, Constants.ARCHIVE_SAVED_STATE);
		archiveSavedState.setSimpleNotesState(simpleNotes);
		archiveSavedState.setGearNotesState(gearNotes);
		archiveSavedState.setFlickrNotesState(flickrNotes);
	}

	// Simple Notes methods

	public ArrayList<SimpleNote> getSimpleNotes() {
		return simpleNotes;
	}

	public void addNote(SimpleNote note) {
		simpleNotes.add(Constants.LIST_HEAD, note);
	}

	public void removeSimpleNote(int position) {
		simpleNotes.remove(position);
	}

	// Gear Notes methods

	public ArrayList<GearNote> getGearNotes() {
		return gearNotes;
	}

	public void addNote(GearNote note) {
		gearNotes.add(Constants.LIST_HEAD, note);
	}

	public void removeGearNote(int position) {
		gearNotes.remove(position);
	}

	// Flickr Notes methods

	public ArrayList<FlickrNote> getFlickrNotes() {
		return flickrNotes;
	}

	public void addNote(FlickrNote note) {
		note.setSelected(false);
		flickrNotes.add(Constants.LIST_HEAD, note);
	}

	public void removeFlickrNote(int position) {
		flickrNotes.remove(position);
	}


}
