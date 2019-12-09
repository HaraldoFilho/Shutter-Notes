/*
 *  Copyright (c) 2019 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : FlickrPhotoActivity.java
 *  Last modified : 8/17/19 12:08 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;

import java.util.Objects;


public class FlickrPhotoActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_flickr_photo);
		Objects.requireNonNull(getSupportActionBar()).hide();

		String url = getIntent().getStringExtra(Constants.KEY_URL);

		WebView flickrWebView = findViewById(R.id.webViewFlickrPhoto);
		configureWebView(flickrWebView);
		flickrWebView.loadUrl(url);

	}

	@SuppressLint("SetJavaScriptEnabled")
	private void configureWebView(WebView webView) {
		webView.getSettings().setLoadsImagesAutomatically(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
	}

}
