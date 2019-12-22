/*
 *  Copyright (c) 2019 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : ClockActivity.java
 *  Last modified : 12/22/19 10:55 AM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.SimpleTimeZone;

import static android.os.SystemClock.sleep;


public class ClockActivity extends AppCompatActivity {

    private TextView timeTextview;
    private TextView dateTextView;
    private Boolean stopClock;


    @Override
    protected void onCreate(Bundle readdInstanceState) {
        super.onCreate(readdInstanceState);
        setContentView(R.layout.activity_clock);
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Set the navigation bar to the same color of background
        getWindow().setNavigationBarColor(getResources().getColor(R.color.colorBackgroundDarkGreen, null));

        timeTextview = findViewById(R.id.textTime);
        dateTextView = findViewById(R.id.textDate);

    }

    @Override
    protected void onResume() {
        super.onResume();
        stopClock = false;
        new Clock().execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopClock = true;
    }

    @SuppressLint("StaticFieldLeak")
    private class Clock extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(Constants.LOG_INFO_TAG, "Clock started!");
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            updateClock();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Log.i(Constants.LOG_INFO_TAG, "Clock stopped!");
        }
    }

    private void updateClock() {
        Date currentTime = Calendar.getInstance().getTime();
        String timeText = SimpleDateFormat.getTimeInstance().format(currentTime);
        String date = SimpleDateFormat.getDateInstance().format(currentTime);
        String timeZone = String.valueOf(SimpleTimeZone.getDefault().getRawOffset() / Constants.MS_PER_HOUR);
        String dateText = date + Constants.UTC + timeZone;

        runOnUiThread(() -> {
            timeTextview.setText(timeText);
            dateTextView.setText(dateText);
        });

        if (!stopClock) {
            sleep(100);
            updateClock();
        }

    }

}