/*
 *  Copyright (c) 2020 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : GearNotesListActivity.java
 *  Last modified : 10/8/20 6:00 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.apps.mohb.shutternotes.adapters.GearNotesListAdapter;
import com.apps.mohb.shutternotes.fragments.dialogs.ArchiveAllNotesAlertFragment;
import com.apps.mohb.shutternotes.fragments.dialogs.DeleteAllNotesAlertFragment;
import com.apps.mohb.shutternotes.fragments.dialogs.NoteDeleteAlertFragment;
import com.apps.mohb.shutternotes.lists.Archive;
import com.apps.mohb.shutternotes.lists.Notebook;
import com.apps.mohb.shutternotes.notes.GearNote;
import com.apps.mohb.shutternotes.views.GridViewWithHeaderAndFooter;

import java.io.IOException;
import java.util.ArrayList;


public class GearNotesListActivity extends AppCompatActivity implements
        NoteDeleteAlertFragment.NoteDeleteDialogListener,
        DeleteAllNotesAlertFragment.DeleteAllNotesAlertDialogListener,
        ArchiveAllNotesAlertFragment.ArchiveAllNotesAlertDialogListener {

    private Notebook notebook;
    private Archive archive;

    private GridViewWithHeaderAndFooter notesListGridView;

    private AdapterView.AdapterContextMenuInfo menuInfo;
    private MenuItem menuItemArchiveAll;

    private Toast allNotesArchived;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gear_notes_list);

        // Create list header and footer, that will insert spaces on top and bottom of the
        // list to make material design effect elevation and shadow
        View listHeader = getLayoutInflater().inflate(R.layout.list_header, notesListGridView);
        View listFooter = getLayoutInflater().inflate(R.layout.list_footer, notesListGridView);

        notesListGridView = findViewById(R.id.gearNotesList);
        registerForContextMenu(notesListGridView);

        notesListGridView.addHeaderView(listHeader);
        notesListGridView.addFooterView(listFooter);
        listHeader.setClickable(false);
        listFooter.setClickable(false);

        notebook = Notebook.getInstance(getApplicationContext());
        archive = Archive.getInstance(getApplicationContext());

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

        // Define form factor of notes items accorging to screen height in pixels
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int listItemHeight = (int) (metrics.heightPixels / Constants.LIST_ITEM_HEIGHT_FACTOR);

        ArrayList<GearNote> gearNotesList = notebook.getGearNotes();
        GearNotesListAdapter notesAdapter = new GearNotesListAdapter(this, gearNotesList, listItemHeight);
        notesListGridView.setAdapter(notesAdapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            archive.saveState();
            notebook.saveState();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        MenuItem menuHelp = menu.findItem(R.id.action_help);
        menuHelp.setEnabled(false);
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

            // Help
            case R.id.action_help: {
                Intent intent = new Intent(this, HelpActivity.class);
                intent.putExtra(Constants.KEY_URL, getString(R.string.url_help_gear_notes));
                startActivity(intent);
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            allNotesArchived.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        allNotesArchived = Toast.makeText(this, R.string.toast_all_notes_archived, Toast.LENGTH_SHORT);
        allNotesArchived.show();
    }

    @Override
    public void onArchiveAllNotesDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }


    // CLASS METHODS

    private int getCorrectPosition(int position) {
        return position - Constants.GRID_HEADER_POSITION;
    }

}
