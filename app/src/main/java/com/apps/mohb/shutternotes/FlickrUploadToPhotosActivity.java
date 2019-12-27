/*
 *  Copyright (c) 2019 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : FlickrUploadToPhotosActivity.java
 *  Last modified : 12/26/19 12:52 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.apps.mohb.shutternotes.adapters.FlickrPhotosListAdapter;
import com.apps.mohb.shutternotes.notes.Archive;
import com.apps.mohb.shutternotes.notes.FlickrNote;
import com.apps.mohb.shutternotes.notes.Notebook;
import com.apps.mohb.shutternotes.views.Toasts;
import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.photosets.Photoset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;


@SuppressWarnings("unchecked")
public class FlickrUploadToPhotosActivity extends AppCompatActivity {

    private Collection<Photo> updatedPhotos;
    private ListView photosListView;
    private FlickrPhotosListAdapter adapter;

    private String selectedSetId;
    private int selectedSetSize;

    private Notebook notebook;
    private Archive archive;
    private ArrayList<FlickrNote> flickrNotes;
    private ArrayList<FlickrNote> selectedNotes;

    private FlickrApi flickrApi;
    private Auth auth;

    private SharedPreferences settings;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flickr_upload_to_photos);

        flickrApi = new FlickrApi(getApplicationContext());

        photosListView = findViewById(R.id.photosList);

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            selectedSetId = bundle.getString(Constants.PHOTOSET_ID);
            selectedSetSize = bundle.getInt(Constants.PHOTOSET_SIZE);
        }
        updatedPhotos = new ArrayList<>();

        if (notebook == null) {
            notebook = new Notebook();
        }

        if (archive == null) {
            archive = new Archive();
        }

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

        flickrNotes = notebook.getFlickrNotes();
        selectedNotes = new ArrayList<>();

        for (int i = 0; i < flickrNotes.size(); i++) {
            FlickrNote note = flickrNotes.get(i);
            if (note.isSelected()) {
                selectedNotes.add(note);
            }
        }

        photosListView.setOnItemClickListener((adapterView, view, i, l) -> {
            Photo photo = (Photo) adapterView.getAdapter().getItem(i);
            String url = photo.getUrl();
            Intent intent = new Intent(getApplicationContext(), FlickrPhotoActivity.class);
            intent.putExtra(Constants.KEY_URL, url);
            startActivity(intent);
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getApplicationContext().getResources().getString(R.string.dialog_progress_upload_data));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setMax(selectedSetSize);

        new CheckToken().execute();

    }

    @SuppressLint("StaticFieldLeak")
    private class CheckToken extends FlickrApi.CheckToken {

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            auth = flickrApi.getAuth();
            if (auth == null) {
                Intent intent = new Intent(getApplicationContext(), FlickrAccountActivity.class);
                startActivity(intent);
            } else {
                new UploadData().execute();
            }

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class UploadData extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        protected Object doInBackground(Object[] objects) {

            try {
                Flickr flickr = FlickrApi.getFlickrInterface();
                String user = auth.getUser().getId();
                RequestContext.getRequestContext().setAuth(auth);
                Collection<Photoset> photosets = flickr.getPhotosetsInterface().getList(user).getPhotosets();
                for (Photoset photoset : photosets) {
                    if (photoset.getId().equals(selectedSetId)) {
                        int progress = 0;
                        int pages = FlickrApi.getNumOfPages(selectedSetSize, Constants.PHOTOSET_PER_PAGE);
                        for (int page = 1; page <= pages; page++) {
                            Collection<Photo> photos = flickr.getPhotosetsInterface()
                                    .getPhotos(selectedSetId, Constants.PHOTOSET_PER_PAGE, page);
                            for (Photo photo : photos) {
                                String photoId = photo.getId();
                                for (int i = 0; i < selectedNotes.size(); i++) {
                                    FlickrNote note = selectedNotes.get(i);
                                    String date = FlickrApi.getDateTaken(photoId);
                                    if (note.isInTimeInterval(date)) {
                                        if (uploadDataToPhoto(photoId, note)) {
                                            // If photo was updated, added it to list
                                            updatedPhotos.add(FlickrApi.getFlickrInterface()
                                                    .getPhotosInterface().getPhoto(photoId));
                                        }
                                    }
                                }
                                progress++;
                                progressDialog.setProgress(progress);
                            }
                        }
                    }
                }

            } catch (FlickrException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            progressDialog.cancel();

            if (adapter == null) {
                adapter = new FlickrPhotosListAdapter(getApplicationContext(), updatedPhotos);
            }
            photosListView.setAdapter(adapter);

            if (updatedPhotos.isEmpty()) {
                Toasts.showNoPhotosUpdated(getApplicationContext());
                onBackPressed();
            } else if (settings.getBoolean(Constants.PREF_KEY_ARCHIVE_NOTES, false)) {
                int i, listSize = flickrNotes.size();
                for (i = listSize - 1; i >= 0; i--) {
                    FlickrNote note = flickrNotes.get(i);
                    if (note.isSelected()) {
                        archive.addNote(note);
                        notebook.removeFlickrNote(i);
                    }
                }
            }

            try {
                notebook.saveState(getApplicationContext());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                archive.saveState(getApplicationContext());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private boolean uploadDataToPhoto(String photoId, FlickrNote note) throws FlickrException {

        boolean overwriteData = settings.getBoolean(Constants.PREF_KEY_OVERWRITE_DATA, false);
        String overwriteTags = settings.getString(Constants.PREF_KEY_OVERWRITE_TAGS, Constants.PREF_REPLACE_ALL);

        RequestContext.getRequestContext().setAuth(auth);
        PhotosInterface photosInterface = FlickrApi.getFlickrInterface().getPhotosInterface();

        boolean wasUpdated = false;

        // If overwrite data setting is on or there is no title in the photo
        // upload title and description to photo
        if (overwriteData || photosInterface.getPhoto(photoId).getTitle().isEmpty()) {
            photosInterface.setMeta(photoId, note.getTitle(), note.getDescription());
            wasUpdated = true;
        }

        // If upload tags setting is on and there are tags in the notes upload tags to photo
        if ((settings.getBoolean(Constants.PREF_KEY_UPLOAD_TAGS, true)) && (note.getTagsArray().length != 0)) {

            String[] tagsToAdd = note.getTagsArray();
            String[] tagsOnPhoto = FlickrApi.getPhotoTagsArray(photosInterface.getPhoto(photoId).getTags().toArray());

            // Add note's tags if there are no tags on the photo
            // or overwrite data setting is on and overwrite tags setting is replace all
            if (tagsOnPhoto.length == 0) {
                photosInterface.setTags(photoId, FlickrApi.getNewPhotoTagsArray(tagsToAdd));

            } else if (overwriteData) {

                // If there are tags on photo and overwrite data is on
                // add tags according to overwrite tags setting
                switch (Objects.requireNonNull(overwriteTags)) {

                    case Constants.PREF_REPLACE_ALL:
                        FlickrApi.getNewPhotoTagsArray(tagsToAdd);
                        photosInterface.setTags(photoId, FlickrApi.getNewPhotoTagsArray(tagsToAdd));
                        break;

                    case Constants.PREF_INSERT_BEGIN:
                        FlickrApi.getNewPhotoTagsArray(tagsToAdd, tagsOnPhoto);
                        photosInterface.setTags(photoId, FlickrApi.getNewPhotoTagsArray(tagsToAdd, tagsOnPhoto));
                        break;

                    case Constants.PREF_INSERT_END:
                        FlickrApi.getNewPhotoTagsArray(tagsOnPhoto, tagsToAdd);
                        photosInterface.setTags(photoId, FlickrApi.getNewPhotoTagsArray(tagsOnPhoto, tagsToAdd));
                        break;

                }
            }

            wasUpdated = true;

        }

        // Add location info to the photo if upload location setting is on
        // and overwrite data is on or there is no location info on the photo
        if (settings.getBoolean(Constants.PREF_KEY_UPLOAD_LOCATION, true)
                && (overwriteData || photosInterface.getPhoto(photoId).getGeoData() == null)) {
            photosInterface.getGeoInterface().setLocation(photoId, note.getGeoData());
            wasUpdated = true;
        }

        return wasUpdated;

    }

}

