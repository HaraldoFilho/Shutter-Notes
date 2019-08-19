/*
 *  Copyright (c) 2019 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : GearNotesListActivity.java
 *  Last modified : 8/17/19 11:08 PM
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

import com.apps.mohb.shutternotes.adapters.GearNotesListAdapter;
import com.apps.mohb.shutternotes.fragments.dialogs.ArchiveAllNotesAlertFragment;
import com.apps.mohb.shutternotes.fragments.dialogs.DeleteAllNotesAlertFragment;
import com.apps.mohb.shutternotes.fragments.dialogs.NoteDeleteAlertFragment;
import com.apps.mohb.shutternotes.notes.Archive;
import com.apps.mohb.shutternotes.notes.GearNote;
import com.apps.mohb.shutternotes.notes.Notebook;
import com.apps.mohb.shutternotes.views.GridViewWithHeaderAndFooter;
import com.apps.mohb.shutternotes.views.Toasts;

import java.io.IOException;
import java.util.ArrayList;


public class GearNotesListActivity extends AppCompatActivity implements
		NoteDeleteAlertFragment.NoteDeleteDialogListener,
		DeleteAllNotesAlertFragment.DeleteAllNotesAlertDialogListener,
		ArchiveAllNotesAlertFragment.ArchiveAllNotesAlertDialogListener {

	private Notebook notebook;
	private Archive archive;
	private GridViewWithHeaderAndFooter notesListGridView;
	private GearNotesListAdapter notesAdapter;
	private View listHeader;
	private View listFooter;

	private AdapterView.AdapterContextMenuInfo menuInfo;
	private MenuItem menuItemArchiveAll;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gear_notes_list);

		// Create list header and footer, that will insert spaces on top and bottom of the
		// list to make material design effect elevation and shadow
		listHeader = getLayoutInflater().inflate(R.layout.list_header, notesListGridView);
		listFooter = getLayoutInflater().inflate(R.layout.list_footer, notesListGridView);

		notesListGridView = findViewById(R.id.gearNotesList);
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
			if (notebook.getGearNotes().size() > 0) { // Fix java.lang.IndexOutOfBoundsException: Invalid index 0, size is 0
				String textString = notebook.getGearNotes().get(i).getGearList();
				Intent intent = new Intent(getBaseContext(), FullscreenNoteActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt(Constants.KEY_CALLER_ACTIVITY, Constants.ACTIVITY_LISTS);
				bundle.putString(Constants.KEY_FULL_SCREEN_TEXT, textString);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();

		try {
			notebook.loadState(this);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			archive.loadState(this);
		} catch (IOException e) {
			e.printStackTrace();
		}

		ArrayList<GearNote> gearNotesList = notebook.getGearNotes();
		notesAdapter = new GearNotesListAdapter(getApplicationContext(), gearNotesList);
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
				archive.addNote(notebook.getGearNotes().get(getCorrectPosition(menuInfo.position)));
				notebook.removeGearNote(getCorrectPosition(menuInfo.position));
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
		getMenuInflater().inflate(R.menu.options_notes, menu);
		menuItemArchiveAll = menu.findItem(R.id.action_archive_all);
		menuItemArchiveAll.setEnabled(true);
		if (notebook.getGearNotes().isEmpty()) {
			menuItemArchiveAll.setEnabled(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		switch (id) {

			// Archive all notes
			case R.id.action_archive_all: {
				ArchiveAllNotesAlertFragment dialogArchive = new ArchiveAllNotesAlertFragment();
				dialogArchive.show(getSupportFragmentManager(), "ArchiveAllNotesAlertFragment");
				break;
			}

		}

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
		Toasts.cancelAllNotesArchived();
	}

	@Override
	public void onNoteDeleteDialogPositiveClick(DialogFragment dialog) {
		notebook.removeGearNote(getCorrectPosition(menuInfo.position));
		notesListGridView.invalidateViews();
	}

	@Override
	public void onNoteDeleteDialogNegativeClick(DialogFragment dialog) {
		dialog.dismiss();
	}

	@Override
	public void onDeleteAllNotesDialogPositiveClick(DialogFragment dialog) {
		notebook.getGearNotes().clear();
		notesListGridView.invalidateViews();
		menuItemArchiveAll.setEnabled(false);
	}

	@Override
	public void onDeleteAllNotesDialogNegativeClick(DialogFragment dialog) {
		dialog.dismiss();
	}


	@Override
	public void onArchiveAllNotesDialogPositiveClick(DialogFragment dialog) {
		for (int i = notebook.getGearNotes().size() - 1; i >= 0; i--) {
			archive.addNote(notebook.getGearNotes().get(i));
			notebook.getGearNotes().remove(i);
		}
		notesListGridView.invalidateViews();
		menuItemArchiveAll.setEnabled(false);
		Toasts.setContext(getApplicationContext());
		Toasts.createAllNotesArchived();
		Toasts.showAllNotesArchived();
	}

	@Override
	public void onArchiveAllNotesDialogNegativeClick(DialogFragment dialog) {
		dialog.dismiss();
	}


	// CLASS METHODS

	private int getCorrectPosition(int position) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			position = position - Constants.LIST_HEADER_POSITION;
		}
		return position;
	}

}
