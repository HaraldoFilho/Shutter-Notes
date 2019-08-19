/*
 *  Copyright (c) 2019 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : FlickrNotesListActivity.java
 *  Last modified : 8/18/19 12:26 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.apps.mohb.shutternotes.adapters.FlickrNotesListAdapter;
import com.apps.mohb.shutternotes.fragments.dialogs.ArchiveSelectedNotesAlertFragment;
import com.apps.mohb.shutternotes.fragments.dialogs.NoteDeleteAlertFragment;
import com.apps.mohb.shutternotes.notes.Archive;
import com.apps.mohb.shutternotes.notes.FlickrNote;
import com.apps.mohb.shutternotes.notes.Notebook;
import com.apps.mohb.shutternotes.views.GridViewWithHeaderAndFooter;
import com.apps.mohb.shutternotes.views.Toasts;

import java.io.IOException;
import java.util.ArrayList;


public class FlickrNotesListActivity extends AppCompatActivity implements
		NoteDeleteAlertFragment.NoteDeleteDialogListener,
		ArchiveSelectedNotesAlertFragment.ArchiveSelectedNotesAlertDialogListener {

	private Notebook notebook;
	private Archive archive;
	private GridViewWithHeaderAndFooter notesListGridView;
	private FlickrNotesListAdapter notesAdapter;
	private View listHeader;
	private View listFooter;

	private AdapterView.AdapterContextMenuInfo menuInfo;
	private MenuItem menuItemUploadToFlickr;
	private MenuItem menuItemArchiveAll;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flickr_notes_list);

		// Create list header and footer, that will insert spaces on top and bottom of the
		// list to make material design effect elevation and shadow
		listHeader = getLayoutInflater().inflate(R.layout.list_header, notesListGridView);
		listFooter = getLayoutInflater().inflate(R.layout.list_footer, notesListGridView);

		notesListGridView = findViewById(R.id.flickrNotesList);
		registerForContextMenu(notesListGridView);

		// Insert header and footer if version is Lollipop (5.x) or higher
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			notesListGridView.addHeaderView(listHeader);
			notesListGridView.addFooterView(listFooter);
			listHeader.setClickable(false);
			listFooter.setClickable(false);
		}

		if (notebook == null) {
			notebook = new Notebook();
		}

		if (archive == null) {
			archive = new Archive();
		}

		notesListGridView.setOnItemClickListener((adapterView, view, i, l) -> {
			if (notebook.getFlickrNotes().size() > 0) { // Fix java.lang.IndexOutOfBoundsException: Invalid index 0, size is 0
				if (!notebook.getFlickrNotes().get(i).isSelected()) {
					notebook.getFlickrNotes().get(i).setSelected(true);
				} else {
					notebook.getFlickrNotes().get(i).setSelected(false);
				}
				notesListGridView.invalidateViews();
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			notebook.loadState(this);
			unselectAllNotes();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			archive.loadState(this);
		} catch (IOException e) {
			e.printStackTrace();
		}

		ArrayList<FlickrNote> flickrNotesList = notebook.getFlickrNotes();
		notesAdapter = new FlickrNotesListAdapter(getApplicationContext(), flickrNotesList);
		notesListGridView.setAdapter(notesAdapter);

	}

	// CONTEXT MENU

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_notes, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

		switch (item.getItemId()) {

			// Archive
			case R.id.archive:
				archive.addNote(notebook.getFlickrNotes().get(getCorrectPosition(menuInfo.position)));
				notebook.removeFlickrNote(getCorrectPosition(menuInfo.position));
				notesListGridView.invalidateViews();
				return true;

			// Delete
			case R.id.delete:
				NoteDeleteAlertFragment dialogDelete = new NoteDeleteAlertFragment();
				dialogDelete.show(getSupportFragmentManager(), "NoteDeleteDialogFragment");
				return true;

			default:
				return super.onContextItemSelected(item);

		}
	}

	// OPTIONS MENU

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.options_flickr_notes, menu);
		menuItemUploadToFlickr = menu.findItem(R.id.action_upload_to_flickr);
		menuItemArchiveAll = menu.findItem(R.id.action_archive_all);
		menuItemUploadToFlickr.setEnabled(true);
		menuItemArchiveAll.setEnabled(true);
		if (notebook.getFlickrNotes().isEmpty()) {
			menuItemUploadToFlickr.setEnabled(false);
			menuItemArchiveAll.setEnabled(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		Toasts.setContext(getApplicationContext());
		Toasts.createMustSelect();

		boolean noNoteSelected = true;
		for (int i = notebook.getFlickrNotes().size() - 1; i >= 0; i--) {
			FlickrNote note = notebook.getFlickrNotes().get(i);
			if (note.isSelected()) {
				noNoteSelected = false;
				break;
			}
		}

		switch (id) {

			// Upload to Flickr
			case R.id.action_upload_to_flickr: {
				if (noNoteSelected) {
					Toasts.showMustSelect();
				} else {
					try {
						notebook.saveState(this);
					} catch (IOException e) {
						e.printStackTrace();
					}
					Intent intent = new Intent(this, FlickrPhotosetsListActivity.class);
					startActivity(intent);
				}
				break;
			}

			// Archive all notes
			case R.id.action_archive_all: {
				if (noNoteSelected) {
					Toasts.showMustSelect();
				} else {
					ArchiveSelectedNotesAlertFragment dialogArchive = new ArchiveSelectedNotesAlertFragment();
					dialogArchive.show(getSupportFragmentManager(), "ArchiveSelectedNotesAlertFragment");
				}
				break;
			}

			// Select all notes
			case R.id.action_select_all: {
				selectAllNotes();
				break;
			}

			// Unselect all notes
			case R.id.action_unselect_all: {
				unselectAllNotes();
				break;
			}
		}

		notesListGridView.invalidateViews();

		return super.onOptionsItemSelected(item);
	}


	@Override
	protected void onPause() {
		super.onPause();
		try {
			notebook.saveState(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			archive.saveState(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Toasts.cancelMustSelect();
		Toasts.cancelAllNotesArchived();
	}

	@Override
	public void onNoteDeleteDialogPositiveClick(DialogFragment dialog) {
		notebook.removeFlickrNote(getCorrectPosition(menuInfo.position));
		notesListGridView.invalidateViews();
	}

	@Override
	public void onNoteDeleteDialogNegativeClick(DialogFragment dialog) {
		dialog.dismiss();
	}

	@Override
	public void onArchiveSelectedNotesDialogPositiveClick(DialogFragment dialog) {
		for (int i = notebook.getFlickrNotes().size() - 1; i >= 0; i--) {
			FlickrNote note = notebook.getFlickrNotes().get(i);
			if (note.isSelected()) {
				archive.addNote(note);
				notebook.getFlickrNotes().remove(i);
			}
		}
		notesListGridView.invalidateViews();
		Toasts.setContext(getApplicationContext());
		Toasts.createAllNotesArchived(R.string.toast_selected_notes_archived);
		Toasts.showAllNotesArchived();
		if (notebook.getFlickrNotes().isEmpty()) {
			menuItemUploadToFlickr.setEnabled(false);
			menuItemArchiveAll.setEnabled(false);
		}
	}

	@Override
	public void onArchiveSelectedNotesDialogNegativeClick(DialogFragment dialog) {
		dialog.dismiss();
	}


	// CLASS METHODS

	private int getCorrectPosition(int position) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			position = position - Constants.LIST_HEADER_POSITION;
		}
		return position;
	}

	private void selectAllNotes() {
		for (int i = 0; i < notebook.getFlickrNotes().size(); i++) {
			notebook.getFlickrNotes().get(i).setSelected(true);
		}
	}

	private void unselectAllNotes() {
		for (int i = 0; i < notebook.getFlickrNotes().size(); i++) {
			notebook.getFlickrNotes().get(i).setSelected(false);
		}

	}

}