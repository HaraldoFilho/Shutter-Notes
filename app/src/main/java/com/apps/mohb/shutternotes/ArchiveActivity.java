/*
 *  Copyright (c) 2019 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : ArchiveActivity.java
 *  Last modified : 8/17/19 11:08 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.apps.mohb.shutternotes.adapters.FlickrNotesListAdapter;
import com.apps.mohb.shutternotes.adapters.GearNotesListAdapter;
import com.apps.mohb.shutternotes.adapters.SimpleNotesListAdapter;
import com.apps.mohb.shutternotes.fragments.dialogs.DeleteAllNotesAlertFragment;
import com.apps.mohb.shutternotes.fragments.dialogs.NoteDeleteAlertFragment;
import com.apps.mohb.shutternotes.fragments.dialogs.RestoreAllNotesAlertFragment;
import com.apps.mohb.shutternotes.notes.Archive;
import com.apps.mohb.shutternotes.notes.FlickrNote;
import com.apps.mohb.shutternotes.notes.GearNote;
import com.apps.mohb.shutternotes.notes.Notebook;
import com.apps.mohb.shutternotes.notes.SimpleNote;
import com.apps.mohb.shutternotes.views.GridViewWithHeaderAndFooter;
import com.apps.mohb.shutternotes.views.Toasts;

import java.io.IOException;
import java.util.ArrayList;


public class ArchiveActivity extends AppCompatActivity implements
		NoteDeleteAlertFragment.NoteDeleteDialogListener,
		DeleteAllNotesAlertFragment.DeleteAllNotesAlertDialogListener,
		RestoreAllNotesAlertFragment.RestoreAllNotesAlertDialogListener {

	private Notebook notebook;
	private Archive archive;
	private BottomNavigationView botNavView;
	private GridViewWithHeaderAndFooter notesListGridView;
	private SimpleNotesListAdapter simpleNotesAdapter;
	private GearNotesListAdapter gearNotesAdapter;
	private FlickrNotesListAdapter flickrNotesAdapter;
	private View listHeader;
	private View listFooter;

	private ArrayList<SimpleNote> simpleNotesList;
	private ArrayList<GearNote> gearNotesList;
	private ArrayList<FlickrNote> flickrNotesList;

	private AdapterView.AdapterContextMenuInfo menuInfo;
	private MenuItem menuItemDeleteAll;
	private MenuItem menuItemRestoreAll;


	private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
			= new BottomNavigationView.OnNavigationItemSelectedListener() {

		@Override
		public boolean onNavigationItemSelected(@NonNull MenuItem item) {
			switch (item.getItemId()) {
				case R.id.bot_nav_simple:
					notesListGridView.setAdapter(simpleNotesAdapter);
					return true;
				case R.id.bot_nav_gear:
					notesListGridView.setAdapter(gearNotesAdapter);
					return true;
				case R.id.bot_nav_flickr:
					notesListGridView.setAdapter(flickrNotesAdapter);
					return true;
			}
			return false;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_archive);
		botNavView = findViewById(R.id.botNavView);
		botNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

		// Create list header and footer, that will insert spaces on top and bottom of the
		// list to make material design effect elevation and shadow
		listHeader = getLayoutInflater().inflate(R.layout.list_header, notesListGridView);
		listFooter = getLayoutInflater().inflate(R.layout.archive_footer, notesListGridView);

		notesListGridView = findViewById(R.id.archivedNotesList);
		registerForContextMenu(notesListGridView);

		// Insert header and footer if version is Lollipop (5.x) or higher
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			notesListGridView.addHeaderView(listHeader);
			notesListGridView.addFooterView(listFooter);
			listHeader.setClickable(false);
			listFooter.setClickable(false);
		}

		if (archive == null) {
			archive = new Archive();
		}

		if (notebook == null) {
			notebook = new Notebook();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		try {
			archive.loadState(this);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			notebook.loadState(this);
		} catch (IOException e) {
			e.printStackTrace();
		}

		simpleNotesList = archive.getSimpleNotes();
		simpleNotesAdapter = new SimpleNotesListAdapter(getApplicationContext(), simpleNotesList);
		notesListGridView.setAdapter(simpleNotesAdapter);

		gearNotesList = archive.getGearNotes();
		gearNotesAdapter = new GearNotesListAdapter(getApplicationContext(), gearNotesList);

		flickrNotesList = archive.getFlickrNotes();
		flickrNotesAdapter = new FlickrNotesListAdapter(getApplicationContext(), flickrNotesList);

	}

	// CONTEXT MENU

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_archived, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

		switch (item.getItemId()) {

			// Restore
			case R.id.restore:
				restoreNote(getCorrectPosition(menuInfo.position));
				setAdapter(notesListGridView);
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
		getMenuInflater().inflate(R.menu.options_archive, menu);
		menuItemDeleteAll = menu.findItem(R.id.action_delete_all);
		menuItemRestoreAll = menu.findItem(R.id.action_archive_all);
		menuItemRestoreAll.setTitle(R.string.action_restore_all);
		menuItemRestoreAll.setIcon(R.drawable.ic_unarchive_white_24dp);
		menuItemDeleteAll.setEnabled(true);
		menuItemRestoreAll.setEnabled(true);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		switch (id) {

			// Delete all notes
			case R.id.action_delete_all: {
				if (!isCurrentListEmpty()) {
					DialogFragment dialog = new DeleteAllNotesAlertFragment();
					dialog.show(getSupportFragmentManager(), "DeleteAllNotesAlertFragment");
				}
				break;
			}

			// Restore all notes
			case R.id.action_archive_all: {
				if (!isCurrentListEmpty()) {
					DialogFragment dialog = new RestoreAllNotesAlertFragment();
					dialog.show(getSupportFragmentManager(), "RestoreAllNotesAlertFragment");
				}
				break;
			}

		}

		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onNoteDeleteDialogPositiveClick(DialogFragment dialog) {
		removeNote(getCorrectPosition(menuInfo.position));
		setAdapter(notesListGridView);
	}

	@Override
	public void onNoteDeleteDialogNegativeClick(DialogFragment dialog) {
		dialog.dismiss();
	}

	@Override
	public void onDeleteAllNotesDialogPositiveClick(DialogFragment dialog) {
		deleteAllNotes();
		setAdapter(notesListGridView);
	}

	@Override
	public void onDeleteAllNotesDialogNegativeClick(DialogFragment dialog) {
		dialog.dismiss();
	}

	@Override
	public void onRestoreAllNotesDialogPositiveClick(DialogFragment dialog) {
		restoreAllNotes();
		setAdapter(notesListGridView);
	}

	@Override
	public void onRestoreAllNotesDialogNegativeClick(DialogFragment dialog) {

	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			archive.saveState(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			notebook.saveState(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Toasts.cancelAllNotesRestored();
	}

	// CLASS METHODS

	/*
		 Set the correct adapter
	 */
	private void setAdapter(GridViewWithHeaderAndFooter notesListGridView) {
		switch (botNavView.getSelectedItemId()) {

			case R.id.bot_nav_simple:
				notesListGridView.setAdapter(simpleNotesAdapter);
				break;

			case R.id.bot_nav_gear:
				notesListGridView.setAdapter(gearNotesAdapter);
				break;

			case R.id.bot_nav_flickr:
				notesListGridView.setAdapter(flickrNotesAdapter);
				break;

		}
	}

	/*
		 Remove note from the correct type
	 */
	private void removeNote(int position) {
		switch (botNavView.getSelectedItemId()) {

			case R.id.bot_nav_simple:
				archive.removeSimpleNote(position);
				break;

			case R.id.bot_nav_gear:
				archive.removeGearNote(position);
				break;

			case R.id.bot_nav_flickr:
				archive.removeFlickrNote(position);
				break;

		}
	}

	/*
		 Restore note from the correct type
	 */
	private void restoreNote(int position) {
		switch (botNavView.getSelectedItemId()) {

			case R.id.bot_nav_simple:
				notebook.addNote(archive.getSimpleNotes().get(getCorrectPosition(menuInfo.position)));
				removeNote(getCorrectPosition(menuInfo.position));
				break;

			case R.id.bot_nav_gear:
				notebook.addNote(archive.getGearNotes().get(getCorrectPosition(menuInfo.position)));
				removeNote(getCorrectPosition(menuInfo.position));
				break;

			case R.id.bot_nav_flickr:
				notebook.addNote(archive.getFlickrNotes().get(getCorrectPosition(menuInfo.position)));
				removeNote(getCorrectPosition(menuInfo.position));
				break;

		}
	}

	/*
		 Delete all notes from the correct type
	 */
	private void deleteAllNotes() {

		switch (botNavView.getSelectedItemId()) {

			case R.id.bot_nav_simple:
				archive.getSimpleNotes().clear();
				break;

			case R.id.bot_nav_gear:
				archive.getGearNotes().clear();
				break;

			case R.id.bot_nav_flickr:
				archive.getFlickrNotes().clear();
				break;

		}

	}

	/*
		 Restore all notes
	*/
	private void restoreAllNotes() {

		switch (botNavView.getSelectedItemId()) {

			case R.id.bot_nav_simple:
				for (int i = archive.getSimpleNotes().size() - 1; i >= 0; i--) {
					notebook.addNote(archive.getSimpleNotes().get(i));
					archive.getSimpleNotes().remove(i);
				}
				break;

			case R.id.bot_nav_gear:
				for (int i = archive.getGearNotes().size() - 1; i >= 0; i--) {
					notebook.addNote(archive.getGearNotes().get(i));
					archive.getGearNotes().remove(i);
				}
				break;

			case R.id.bot_nav_flickr:
				for (int i = archive.getFlickrNotes().size() - 1; i >= 0; i--) {
					notebook.addNote(archive.getFlickrNotes().get(i));
					archive.getFlickrNotes().remove(i);
				}
				break;

		}

		Toasts.setContext(getApplicationContext());
		Toasts.createAllNotesRestored();
		Toasts.showAllNotesRestored();
	}

	/*
		 Set the correct menu item status
	 */
	private boolean isCurrentListEmpty() {

		switch (botNavView.getSelectedItemId()) {

			case R.id.bot_nav_simple:
				if (archive.getSimpleNotes().isEmpty()) {
					return true;
				}
				break;

			case R.id.bot_nav_gear:
				if (archive.getGearNotes().isEmpty()) {
					return true;
				}
				break;

			case R.id.bot_nav_flickr:
				if (archive.getFlickrNotes().isEmpty()) {
					return true;
				}
				break;

		}

		return false;

	}

	/*
		 List item position correction due to header
	*/
	private int getCorrectPosition(int position) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			position = position - Constants.LIST_HEADER_POSITION;
		}
		return position;
	}

}

