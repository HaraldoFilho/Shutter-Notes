/*
 *  Copyright (c) 2020 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : BackgroundTaskActivity.java
 *  Last modified : 10/7/20 12:18 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import androidx.appcompat.app.AppCompatActivity;

public class BackgroundTaskActivity extends AppCompatActivity {

    /*
     * Class to replace deprecated AsyncTask
     */

    protected class BackgroundTask extends Thread {

        private void beforeMainThread() {
            runOnUiThread(this::onPreExecute);
        }

        private void mainThread() {
            doInBackground();
        }

        private void afterMainThread() {
            runOnUiThread(this::onPostExecute);
        }

        protected void onPreExecute() {
        }

        protected void doInBackground() {
        }

        protected void onPostExecute() {
        }

        @Override
        public void run() {
            beforeMainThread();
            mainThread();
            afterMainThread();
        }

    }
}