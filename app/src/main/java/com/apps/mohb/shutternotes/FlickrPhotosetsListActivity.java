/*
 *  Copyright (c) 2019 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : FlickrPhotosetsListActivity.java
 *  Last modified : 8/17/19 12:08 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.apps.mohb.shutternotes.adapters.FlickrPhotosetsListAdapter;
import com.apps.mohb.shutternotes.fragments.dialogs.ConfirmUploadAlertFragment;
import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.photosets.Photoset;

import java.util.Collection;


public class FlickrPhotosetsListActivity extends AppCompatActivity implements
		ConfirmUploadAlertFragment.ConfirmUploadAlertDialogListener {

	private Collection<Photoset> photosets;
	private ListView photosetsListView;
	private FlickrPhotosetsListAdapter adapter;

	private String selectedSetId;
	private int selectedSetSize;

	private FlickrApi flickrApi;
	private Auth auth;

	private ProgressDialog progressDialog;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flickr_photosets_list);

		flickrApi = new FlickrApi(getApplicationContext());

		photosetsListView = findViewById(R.id.photosetsList);
		photosetsListView.setOnItemClickListener((adapterView, view, i, l) -> {
			ConfirmUploadAlertFragment dialogConfirm = new ConfirmUploadAlertFragment();
			dialogConfirm.show(getSupportFragmentManager(), "ConfirmUploadDialogFragment");
			Photoset photoset = (Photoset) adapterView.getAdapter().getItem(i);
			selectedSetId = photoset.getId();
			selectedSetSize = photoset.getPhotoCount();
		});

		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(getApplicationContext().getResources().getString(R.string.dialog_progress_photosets));
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setIndeterminate(true);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(false);
		progressDialog.show();

		new checkToken().execute();

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
				new getPhotosets().execute();
			}

		}
	}

	private class getPhotosets extends AsyncTask {

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
			adapter = new FlickrPhotosetsListAdapter(getApplicationContext(), photosets);
			photosetsListView.setAdapter(adapter);
		}
	}

}
