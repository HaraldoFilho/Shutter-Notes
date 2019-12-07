/*
 *  Copyright (c) 2019 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : MainActivity.java
 *  Last modified : 8/17/19 12:08 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


public class MainActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	private FusedLocationProviderClient mFusedLocationClient;
	private double lastLatitude;
	private double lastLongitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = findViewById(R.id.navView);
		navigationView.setNavigationItemSelectedListener(this);

		Button buttonSimpleNote = findViewById(R.id.buttonSimpleNote);
		Button buttonGearNote = findViewById(R.id.buttonGearNote);
		Button buttonFlickrNote = findViewById(R.id.buttonFlickrNote);

		buttonSimpleNote.setOnClickListener(view -> {
			Intent intent = new Intent(view.getContext(), SimpleNoteActivity.class);
			startActivity(intent);
		});

		buttonGearNote.setOnClickListener(view -> {
			Intent intent = new Intent(view.getContext(), GearNoteActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt(Constants.KEY_CALLER_ACTIVITY, Constants.ACTIVITY_GEAR_NOTE);
			intent.putExtras(bundle);
			startActivity(intent);
		});

		buttonFlickrNote.setOnClickListener(view -> {
			Intent intent = new Intent(view.getContext(), FlickrNoteActivity.class);
			Bundle bundle = new Bundle();
			bundle.putDouble(Constants.LATITUDE, lastLatitude);
			bundle.putDouble(Constants.LONGITUDE, lastLongitude);
			intent.putExtras(bundle);
			startActivity(intent);
		});

		mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

	}

	@Override
	protected void onResume() {
		super.onResume();
		// Check if location permissions are granted
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
				== PackageManager.PERMISSION_GRANTED) {
			// If GoogleApiClient is connected get the last location
			mFusedLocationClient.getLastLocation()
					.addOnSuccessListener(this, new OnSuccessListener<Location>() {
						@Override
						public void onSuccess(Location location) {
							if (location != null) {
								lastLatitude = location.getLatitude();
								lastLongitude = location.getLongitude();
							} else {
								lastLongitude = Constants.DEFAULT_LATITUDE;
								lastLongitude = Constants.DEFAULT_LONGITUDE;
							}

						}
					});

		} else {
			// Check if user already denied permission request
			if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.ACCESS_FINE_LOCATION)) {
				// Request permissions
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
						Constants.FINE_LOCATION_PERMISSION_REQUEST);
			}
		}

	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}


	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		Intent intent = null;

		switch (id) {

			// Simple Notes
			case R.id.nav_simple:
				intent = new Intent(this, SimpleNotesListActivity.class);
				break;

			// Gear Notes
			case R.id.nav_gear:
				intent = new Intent(this, GearNotesListActivity.class);
				break;

			// Flickr Notes
			case R.id.nav_flickr:
				intent = new Intent(this, FlickrNotesListActivity.class);
				break;

			// Archived
			case R.id.nav_archive:
				intent = new Intent(this, ArchiveActivity.class);
				break;

			// Account
			case R.id.nav_account:
				intent = new Intent(this, FlickrAccountActivity.class);
				break;

			// Clock
			case R.id.nav_clock:
				intent = new Intent(this, ClockActivity.class);
				break;

			// Settings
			case R.id.nav_settings:
				intent = new Intent(this, SettingsActivity.class);
				break;

			// Help
			case R.id.nav_help:
				intent = new Intent(this, HelpActivity.class);
				intent.putExtra(Constants.KEY_URL, getString(R.string.url_help));
				break;

			// About
			case R.id.nav_about:
				intent = new Intent(this, AboutActivity.class);
				break;

		}

		if (intent != null) {
			startActivity(intent);
		}

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}
}
