/*
 *  Copyright (c) 2020 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : FlickrNoteActivity.java
 *  Last modified : 4/5/20 12:46 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.apps.mohb.shutternotes.fragments.dialogs.FlickrNoteTipAlertFragment;
import com.apps.mohb.shutternotes.notes.GearList;
import com.apps.mohb.shutternotes.views.Toasts;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;


public class FlickrNoteActivity extends AppCompatActivity
        implements OnMapReadyCallback, FlickrNoteTipAlertFragment.FlickrNoteTipDialogListener {

    private EditText editTextTitle;
    private EditText editTextDescription;
    private TextView textTags;
    private GearList gearList;

    private double lastLatitude;
    private double lastLongitude;

    private SharedPreferences settings;
    private SharedPreferences warningFirstShow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flickr_note);

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        editTextTitle = findViewById(R.id.editTextFlickrNoteTitle);
        editTextDescription = findViewById(R.id.editTextFlickrNoteDescription);
        textTags = findViewById(R.id.tagsView);
        FloatingActionButton fabAddTags = findViewById(R.id.fabAddGearFlickr);

        Button buttonCancel = findViewById(R.id.buttonFlickrNoteCancel);
        Button buttonClear = findViewById(R.id.buttonFlickrNoteClear);
        Button buttonOK = findViewById(R.id.buttonFlickrNoteOk);

        // create map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragmentFlickrNote);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fabAddTags.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), GearNoteActivity.class);
            intent.putExtra(Constants.KEY_CALLER_ACTIVITY, Constants.ACTIVITY_FLICKR_NOTE);
            startActivity(intent);
        });

        buttonCancel.setOnClickListener(view -> onBackPressed());

        buttonClear.setOnClickListener(view -> {
            editTextTitle.setText(Constants.EMPTY);
            editTextDescription.setText(Constants.EMPTY);
            textTags.setText(Constants.EMPTY);
            try {
                gearList.loadState(getApplicationContext(), Constants.GEAR_LIST_SAVED_STATE);
                gearList.saveState(getApplicationContext(), Constants.GEAR_LIST_SELECTED_STATE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        buttonOK.setOnClickListener(view -> {

            String textTitle = editTextTitle.getText().toString();
            String textDescription = editTextDescription.getText().toString();
            if (textDescription.isEmpty()) {
                textDescription = Constants.SPACE;
            }

            try {
                gearList.loadState(getApplicationContext(), Constants.GEAR_LIST_SELECTED_STATE);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ArrayList<String> tags = gearList.getFlickrTags();
            String textGearList = gearList.getGearListText();

            if (textTitle.equals(Constants.EMPTY)) {
                Toasts.showMustType(getApplicationContext(), true);
            } else {
                Intent intent = new Intent(getApplicationContext(), FullscreenNoteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.FLICKR_TITLE, textTitle);
                bundle.putString(Constants.FLICKR_DESCRIPTION, textDescription);
                bundle.putStringArrayList(Constants.FLICKR_TAGS, tags);
                bundle.putDouble(Constants.LATITUDE, lastLatitude);
                bundle.putDouble(Constants.LONGITUDE, lastLongitude);

                String textToShow = textTitle;
                String prefKey = settings.getString(Constants.PREF_KEY_WHAT_SHOW, Constants.PREF_SHOW_TITLE);

                switch (Objects.requireNonNull(prefKey)) {

                    case Constants.PREF_SHOW_DESCRIPTION:
                        textToShow = textDescription;
                        break;

                    case Constants.PREF_SHOW_TAGS:
                        textToShow = textGearList;
                        break;
                }

                if (textToShow.equals(Constants.EMPTY) || textToShow.equals(Constants.SPACE)) {
                    textToShow = textTitle;
                }

                bundle.putString(Constants.KEY_FULL_SCREEN_TEXT, textToShow);
                bundle.putInt(Constants.KEY_CALLER_ACTIVITY, Constants.ACTIVITY_FLICKR_NOTE);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        warningFirstShow = this.getSharedPreferences(Constants.FLICKR_NOTE_WARNING, Constants.PRIVATE_MODE);

        if (warningFirstShow.getBoolean(Constants.KEY_FIRST_SHOW, true)) {
            FlickrNoteTipAlertFragment dialogWarning = new FlickrNoteTipAlertFragment();
            dialogWarning.show(getSupportFragmentManager(), "FlickrNoteTipAlertFragment");
        }

    }

    // OPTIONS MENU

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_flickr_note, menu);
        MenuItem menuSyncClock = menu.findItem(R.id.action_sync_clock);
        menuSyncClock.setEnabled(true);
        MenuItem menuHelp = menu.findItem(R.id.action_help);
        menuHelp.setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            // Synchronize clock
            case R.id.action_sync_clock: {
                Intent intent = new Intent(this, ClockActivity.class);
                startActivity(intent);
                break;
            }

            // Help
            case R.id.action_help: {
                Intent intent = new Intent(this, HelpActivity.class);
                intent.putExtra(Constants.KEY_URL, getString(R.string.url_help_flickr_note));
                startActivity(intent);
                break;
            }

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();

        gearList = new GearList();
        try {
            gearList.loadState(getApplicationContext(), Constants.GEAR_LIST_SELECTED_STATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (gearList.getList().isEmpty()) {
            try {
                gearList.loadState(getApplicationContext(), Constants.GEAR_LIST_SAVED_STATE);
                gearList.saveState(getApplicationContext(), Constants.GEAR_LIST_SELECTED_STATE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String tags = gearList.getGearListText().replace(Constants.NEW_LINE,
                Constants.SPACE + Constants.SPACE + Constants.SPACE);

        if (!tags.equals(Constants.EMPTY)) {
            textTags.setText(tags);
        } else {
            textTags.setText(Constants.EMPTY);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        String prefKey = settings.getString(Constants.PREF_KEY_MAP_ZOOM_LEVEL, Constants.PREF_MID);

        int zoomLevel = Constants.MAP_NONE_ZOOM_LEVEL;
        double markerOffset = Constants.MARKER_NZ_Y_OFFSET;

        switch (Objects.requireNonNull(prefKey)) {

            case Constants.PREF_HIGH:
                zoomLevel = Constants.MAP_HIGH_ZOOM_LEVEL;
                markerOffset = Constants.MARKER_HZ_Y_OFFSET;
                break;

            case Constants.PREF_MID:
                zoomLevel = Constants.MAP_MID_ZOOM_LEVEL;
                markerOffset = Constants.MARKER_MZ_Y_OFFSET;
                break;

            case Constants.PREF_LOW:
                zoomLevel = Constants.MAP_LOW_ZOOM_LEVEL;
                markerOffset = Constants.MARKER_LZ_Y_OFFSET;
                break;
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            lastLatitude = bundle.getDouble(Constants.LATITUDE);
            lastLongitude = bundle.getDouble(Constants.LONGITUDE);
        } else {
            lastLatitude = 0.0;
            lastLongitude = 0.0;
        }

        LatLng currentLocation = new LatLng(lastLatitude, lastLongitude);
        LatLng markerLocation = new LatLng(lastLatitude + markerOffset, lastLongitude);
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoomLevel));
        Marker marker = googleMap.addMarker(new MarkerOptions().position(markerLocation));
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_camera_red_36dp));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GearList gearList = new GearList();
        try {
            gearList.saveState(getApplicationContext(), Constants.GEAR_LIST_SELECTED_STATE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toasts.cancelMustType();
    }

    @Override
    public void onFlickrNoteTipDialogPositiveClick(DialogFragment dialog) {
        warningFirstShow.edit().putBoolean(Constants.KEY_FIRST_SHOW, false).apply();
    }

}
