/*
 *  Copyright (c) 2019 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : FlickrUploadToPhotosActivity.java
 *  Last modified : 9/3/19 11:55 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

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
import java.util.Iterator;


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
		selectedSetId = bundle.getString(Constants.PHOTOSET_ID);
		selectedSetSize = bundle.getInt(Constants.PHOTOSET_SIZE);
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

		showProgressDialog();

		new checkToken().execute();

	}

	private class checkToken extends FlickrApi.checkToken {

		@Override
		protected void onPostExecute(Object o) {
			super.onPostExecute(o);
			auth = flickrApi.getAuth();
			if (auth == null) {
				progressDialog.cancel();
				Intent intent = new Intent(getApplicationContext(), FlickrAccountActivity.class);
				startActivity(intent);
			} else {
				new uploadData().execute();
			}

		}
	}

	private class uploadData extends AsyncTask {

		protected Object doInBackground(Object[] objects) {

			try {
				Flickr flickr = FlickrApi.getFlickrInterface();
				String user = auth.getUser().getId();
				RequestContext.getRequestContext().setAuth(auth);
				Collection<Photoset> photosets = flickr.getPhotosetsInterface().getList(user).getPhotosets();
				Iterator<Photoset> photosetIterator = photosets.iterator();
				while (photosetIterator.hasNext()) {
					Photoset photoset = photosetIterator.next();
					if (photoset.getId().equals(selectedSetId)) {
						int progress = 0;
						int pages = FlickrApi.getNumOfPages(selectedSetSize, Constants.PHOTOSET_PER_PAGE);
						for (int page = 1; page <= pages; page++) {
							Collection<Photo> photos = flickr.getPhotosetsInterface()
									.getPhotos(selectedSetId, Constants.PHOTOSET_PER_PAGE, page);
							Iterator<Photo> photoIterator = photos.iterator();
							while (photoIterator.hasNext()) {
								Photo photo = photoIterator.next();
								String photoId = photo.getId();
								for (int i = 0; i < selectedNotes.size(); i++) {
									FlickrNote note = selectedNotes.get(i);
									String date = FlickrApi.getDateTaken(photoId);
									if (note.isInTimeInterval(date)) {
										uploadDataToPhoto(photoId, note);
										updatedPhotos.add(FlickrApi.getFlickrInterface()
												.getPhotosInterface().getPhoto(photoId));
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
				Toasts.setContext(getApplicationContext());
				Toasts.createNoPhotosUpdated();
				Toasts.showNoPhotosUpdated();
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

	private void uploadDataToPhoto(String photoId, FlickrNote note) throws FlickrException {

		Boolean overwriteData = settings.getBoolean(Constants.PREF_KEY_OVERWRITE_DATA, false);
		String overwriteTags = settings.getString(Constants.PREF_KEY_OVERWRITE_TAGS, Constants.PREF_REPLACE_ALL);
		RequestContext.getRequestContext().setAuth(auth);
		PhotosInterface photosInterface = FlickrApi.getFlickrInterface().getPhotosInterface();

		if (overwriteData || photosInterface.getPhoto(photoId).getTitle().isEmpty()) {
			photosInterface.setMeta(photoId, note.getTitle(), note.getDescription());
		}

		if (settings.getBoolean(Constants.PREF_KEY_UPLOAD_TAGS, true)) {

			String[] tagsToAdd = note.getTagsArray();

			if (photosInterface.getPhoto(photoId).getTags().isEmpty()) {
				photosInterface.setTags(photoId, tagsToAdd);

			} else if (overwriteData) {

				String[] tagsOnPhoto = FlickrApi.getTagsStringArray(photosInterface.getPhoto(photoId).getTags().toArray());

				switch (overwriteTags) {

					case Constants.PREF_REPLACE_ALL:
						photosInterface.setTags(photoId, tagsToAdd);
						break;

					case Constants.PREF_INSERT_BEGIN:
						photosInterface.setTags(photoId, FlickrApi.getNewTagsArray(tagsToAdd, tagsOnPhoto));
						break;

					case Constants.PREF_INSERT_END:
						photosInterface.setTags(photoId, FlickrApi.getNewTagsArray(tagsOnPhoto, tagsToAdd));
						break;

				}
			}
		}

		if (settings.getBoolean(Constants.PREF_KEY_UPLOAD_LOCATION, true)
				&& (overwriteData || photosInterface.getPhoto(photoId).getGeoData() == null)) {
			photosInterface.getGeoInterface().setLocation(photoId, note.getGeoData());
		}
	}

	private void showProgressDialog() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(getApplicationContext()
				.getResources().getString(R.string.dialog_progress_upload_data));
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setIndeterminate(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(false);
		progressDialog.setMax(selectedSetSize);
		progressDialog.show();
	}

}

