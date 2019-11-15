/*
 *  Copyright (c) 2019 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : FullscreenNoteActivity.java
 *  Last modified : 9/2/19 7:32 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.apps.mohb.shutternotes.fragments.dialogs.FullscreenTipAlertFragment;
import com.apps.mohb.shutternotes.notes.FlickrNote;
import com.apps.mohb.shutternotes.notes.GearNote;
import com.apps.mohb.shutternotes.notes.Notebook;
import com.apps.mohb.shutternotes.notes.SimpleNote;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenNoteActivity extends AppCompatActivity
		implements FullscreenTipAlertFragment.FullscreenTipDialogListener {
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * Some older devices needs a small delay between UI widget updates
	 * and a change of the status and navigation bar.
	 */
	private static final int UI_ANIMATION_DELAY = 300;
	private final Handler mHideHandler = new Handler();
	private View mContentView;
	private int state;

	private Notebook notebook;
	private String text;
	private int callerActivity;
	private TextView textView;

	private SimpleDateFormat date;
	private String startTime = Constants.EMPTY;
	private String finishTime = Constants.EMPTY;

	private SharedPreferences settings;
	private SharedPreferences instructionsFirstShow;


	private final Runnable mHidePart2Runnable = new Runnable() {
		@SuppressLint("InlinedApi")
		@Override
		public void run() {
			// Delayed removal of status and navigation bar

			// Note that some of these constants are new as of API 16 (Jelly Bean)
			// and API 19 (KitKat). It is safe to use them, as they are inlined
			// at compile-time and do nothing on earlier devices.
			mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
					| View.SYSTEM_UI_FLAG_FULLSCREEN
					| View.SYSTEM_UI_FLAG_LAYOUT_STABLE
					| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		}
	};
	private View mControlsView;
	private final Runnable mShowPart2Runnable = new Runnable() {
		@Override
		public void run() {
			// Delayed display of UI elements
			ActionBar actionBar = getSupportActionBar();
			if (actionBar != null) {
				actionBar.show();
			}
		}
	};
	private boolean mVisible;
	private final Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			hide();
		}
	};
	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		settings = PreferenceManager.getDefaultSharedPreferences(this);

		setContentView(R.layout.activity_fullscreen_note);

		if (notebook == null) {
			notebook = new Notebook();
		}

		try {
			notebook.loadState(getApplicationContext());
		} catch (IOException e) {
			e.printStackTrace();
		}

		mVisible = true;
		mContentView = findViewById(R.id.textFullscreen);

		callerActivity = getIntent().getExtras().getInt(Constants.KEY_CALLER_ACTIVITY);

		textView = findViewById(R.id.textFullscreen);
		if (callerActivity == Constants.ACTIVITY_GEAR_NOTE) {
			textView.setLineSpacing(0, (float) 1.25);
		}

		text = getIntent().getExtras().getString(Constants.KEY_FULL_SCREEN_TEXT);

		textView.setText(text.trim());
		textView.setBackgroundColor(getResources().getColor(R.color.colorBackgroundGreen));

		String prefKey = settings.getString(Constants.PREF_KEY_FONT_SIZE, Constants.PREF_FONT_SIZE_MEDIUM);

		switch (prefKey) {

			case Constants.PREF_FONT_SIZE_SMALL:
				textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.FONT_SIZE_SMALL_LARGE);
				break;

			case Constants.PREF_FONT_SIZE_MEDIUM:
				textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.FONT_SIZE_MEDIUM_LARGE);
				break;

			case Constants.PREF_FONT_SIZE_LARGE:
				textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.FONT_SIZE_LARGE_LARGE);
				break;

		}

		state = Constants.STATE_COLOR_GREEN;
		date = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());
		startTime = date.format(new Date().getTime());

		// Set up the user interaction to manually show or hide the system UI.
		mContentView.setOnLongClickListener(view -> {
			toggle();
			return true;
		});

		instructionsFirstShow = this.getSharedPreferences(Constants.FULLSCREEN_INSTRUCTIONS, Constants.PRIVATE_MODE);

		if (instructionsFirstShow.getBoolean(Constants.KEY_FIRST_SHOW, true)) {
			FullscreenTipAlertFragment dialogInstructions = new FullscreenTipAlertFragment();
			dialogInstructions.show(getSupportFragmentManager(), "FullscreenTipAlertFragment");
		}


	}

	@Override
	protected void onResume() {
		super.onResume();
		hide();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}

	private void toggle() {

		if (mVisible) {
			textView.setBackgroundColor(getResources().getColor(R.color.colorBackgroundGreen));
		} else {

			switch (state) {

				case Constants.STATE_COLOR_GREEN:
					textView.setBackgroundColor(getResources().getColor(R.color.colorBackgroundRed));
					finishTime = date.format(new Date().getTime());
					state = Constants.STATE_COLOR_RED;
					break;

				case Constants.STATE_COLOR_RED:

					String lastNote = Constants.EMPTY;

					switch (callerActivity) {

						case Constants.ACTIVITY_SIMPLE_NOTE:
							SimpleNote simpleNote = new SimpleNote(text);
							if (!notebook.getSimpleNotes().isEmpty()) {
								lastNote = notebook.getSimpleNotes().get(Constants.LIST_HEAD).getText();
							}
							if (!simpleNote.getText().equals(lastNote)) {
								notebook.addNote(simpleNote);
							}
							break;

						case Constants.ACTIVITY_GEAR_NOTE:
							GearNote gearNote = new GearNote(text);
							if (!notebook.getGearNotes().isEmpty()) {
								lastNote = notebook.getGearNotes().get(Constants.LIST_HEAD).getGearList();
							}
							if (!gearNote.getGearList().equals(lastNote)) {
								notebook.addNote(gearNote);
							}
							break;

						case Constants.ACTIVITY_FLICKR_NOTE:
							Bundle bundle = getIntent().getExtras();
							String title = bundle.getString(Constants.FLICKR_TITLE);
							String description = bundle.getString(Constants.FLICKR_DESCRIPTION);
							ArrayList<String> tags = bundle.getStringArrayList(Constants.FLICKR_TAGS);
							double latitude = bundle.getDouble(Constants.LATITUDE);
							double longitude = bundle.getDouble(Constants.LONGITUDE);
							FlickrNote flickrNote = new FlickrNote(title, description, tags,
									latitude, longitude, startTime, finishTime);
							notebook.addNote(flickrNote);
							break;

					}
					// go back when red screen is touched
					super.onBackPressed();
					break;
			}
		}
	}

	@Override
	public void onBackPressed() {
		// do not go back when back button is pressed
	}

	private void hide() {
		// Hide UI first
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.hide();
		}
		mVisible = false;

		// Schedule a runnable to remove the status and navigation bar after a delay
		mHideHandler.removeCallbacks(mShowPart2Runnable);
		mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
	}

	@SuppressLint("InlinedApi")
	private void show() {
		// Show the system bar
		mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
		mVisible = true;

		// Schedule a runnable to display UI elements after a delay
		mHideHandler.removeCallbacks(mHidePart2Runnable);
		mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
	}

	/**
	 * Schedules a call to hide() in delay milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		try {
			notebook.saveState(getApplicationContext());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onFullscreenTipDialogPositiveClick(DialogFragment dialog) {
		instructionsFirstShow.edit().putBoolean(Constants.KEY_FIRST_SHOW, false).commit();
	}

}
