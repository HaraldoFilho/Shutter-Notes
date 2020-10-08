/*
 *  Copyright (c) 2020 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : Notebook.java
 *  Last modified : 10/8/20 1:29 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes.lists;

import android.content.Context;

import com.apps.mohb.shutternotes.Constants;
import com.apps.mohb.shutternotes.notes.FlickrNote;
import com.apps.mohb.shutternotes.notes.GearNote;
import com.apps.mohb.shutternotes.notes.SimpleNote;

import java.io.IOException;
import java.util.ArrayList;


public class Notebook {

    private static ArrayList<SimpleNote> simpleNotes;
    private static ArrayList<GearNote> gearNotes;
    private static ArrayList<FlickrNote> flickrNotes;
    private static SavedState notebookSavedState;

    private static Notebook notebook;

    private Notebook(Context context) {
        notebookSavedState = new SavedState(context, Constants.NOTEBOOK_SAVED_STATE);
        simpleNotes = new ArrayList<>();
        gearNotes = new ArrayList<>();
        flickrNotes = new ArrayList<>();
    }

    public static Notebook getInstance(Context context) {
        if (notebook == null) {
            notebook = new Notebook(context);
        }
        try {
            loadState();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return notebook;
    }

    private static void loadState() throws IOException {
        simpleNotes = notebookSavedState.getSimpleNotesState();
        gearNotes = notebookSavedState.getGearNotesState();
        flickrNotes = notebookSavedState.getFlickrNotesState();
    }

    public void saveState() throws IOException {
        notebookSavedState.setSimpleNotesState(simpleNotes);
        notebookSavedState.setGearNotesState(gearNotes);
        notebookSavedState.setFlickrNotesState(flickrNotes);
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
        flickrNotes.add(Constants.LIST_HEAD, note);
    }

    public void removeFlickrNote(int position) {
        flickrNotes.remove(position);
    }


}
