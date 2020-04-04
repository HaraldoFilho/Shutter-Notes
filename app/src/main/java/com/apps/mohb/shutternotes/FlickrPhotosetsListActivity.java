/*
 *  Copyright (c) 2020 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : FlickrPhotosetsListActivity.java
 *  Last modified : 4/4/20 5:39 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.apps.mohb.shutternotes.adapters.FlickrPhotosetsListAdapter;
import com.apps.mohb.shutternotes.fragments.dialogs.AuthenticationNeededAlertFragment;
import com.apps.mohb.shutternotes.fragments.dialogs.ConfirmUploadAlertFragment;
import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.photosets.Photoset;

import java.util.Collection;


@SuppressWarnings("unchecked")
public class FlickrPhotosetsListActivity extends AppCompatActivity implements
        ConfirmUploadAlertFragment.ConfirmUploadAlertDialogListener,
        AuthenticationNeededAlertFragment.AuthenticationNeededAlertDialogListener {

    private Collection<Photoset> photosets;
    private ListView photosetsListView;

    private String selectedSetId;
    private int selectedSetSize;

    private FlickrApi flickrApi;
    private Auth auth;

    private ProgressDialog progressDialog;

    private int callerActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flickr_photosets_list);

        flickrApi = new FlickrApi(getApplicationContext());

        View listHeader = getLayoutInflater().inflate(R.layout.list_header, photosetsListView);
        View listFooter = getLayoutInflater().inflate(R.layout.list_footer, photosetsListView);

        photosetsListView = findViewById(R.id.photosetsList);
        photosetsListView.setOnItemClickListener((adapterView, view, i, l) -> {
            ConfirmUploadAlertFragment dialogConfirm = new ConfirmUploadAlertFragment();
            dialogConfirm.show(getSupportFragmentManager(), "ConfirmUploadDialogFragment");
            Photoset photoset = (Photoset) adapterView.getAdapter().getItem(i);
            selectedSetId = photoset.getId();
            selectedSetSize = photoset.getPhotoCount();
        });

        photosetsListView.addHeaderView(listHeader);
        photosetsListView.addFooterView(listFooter);
        listHeader.setClickable(false);
        listFooter.setClickable(false);

        callerActivity = getIntent().getIntExtra(Constants.KEY_CALLER_ACTIVITY, Constants.ACTIVITY_FLICKR_NOTES);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getApplicationContext().getResources().getString(R.string.dialog_progress_photosets));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (photosetsListView.getAdapter() == null) {
            new CheckToken().execute();
        }

    }

    @Override
    public void onConfirmUploadDialogPositiveClick(DialogFragment dialog) {

        if (!selectedSetId.isEmpty()) {
            Intent intent = new Intent(getApplicationContext(), FlickrUploadToPhotosActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(Constants.PHOTOSET_ID, selectedSetId);
            bundle.putInt(Constants.PHOTOSET_SIZE, selectedSetSize);
            intent.putExtras(bundle);
            startActivity(intent);
        }

    }

    @Override
    public void onConfirmUploadDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    @Override
    public void onAuthenticationNeededDialogPositiveClick(DialogFragment dialog) {
        Intent intent = new Intent(getApplicationContext(), FlickrAccountActivity.class);
        intent.putExtra(Constants.KEY_CALLER_ACTIVITY, Constants.ACTIVITY_FLICKR_PHOTOSETS);
        startActivity(intent);
    }

    @Override
    public void onAuthenticationNeededDialogNegativeClick(DialogFragment dialog) {
        onBackPressed();
    }

    @SuppressLint("StaticFieldLeak")
    private class CheckToken extends FlickrApi.CheckToken {

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            auth = flickrApi.getAuth();
            if (auth == null) {
                if (callerActivity == Constants.ACTIVITY_FLICKR_NOTES) {
                    callerActivity = Constants.ACTIVITY_FLICKR_PHOTOSETS;
                    Intent intent = new Intent(getApplicationContext(), FlickrAccountActivity.class);
                    intent.putExtra(Constants.KEY_CALLER_ACTIVITY, Constants.ACTIVITY_FLICKR_PHOTOSETS);
                    startActivity(intent);
                } else {
                    AuthenticationNeededAlertFragment authenticationNeeded = new AuthenticationNeededAlertFragment();
                    authenticationNeeded.show(getSupportFragmentManager(), "AuthenticationNeededDialogFragment");
                }
            } else {
                new GetPhotosets().execute();
            }

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetPhotosets extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        protected Object doInBackground(Object[] objects) {

            try {
                String user = auth.getUser().getId();
                RequestContext.getRequestContext().setAuth(auth);
                photosets = FlickrApi.getFlickrInterface().getPhotosetsInterface().getList(user).getPhotosets();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            progressDialog.cancel();
            FlickrPhotosetsListAdapter adapter = new FlickrPhotosetsListAdapter(getApplicationContext(), photosets);
            photosetsListView.setAdapter(adapter);
        }
    }

}
