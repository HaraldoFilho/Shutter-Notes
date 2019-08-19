/*
 *  Copyright (c) 2019 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : FlickrAccountActivity.java
 *  Last modified : 8/17/19 11:12 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.apps.mohb.shutternotes.fragments.dialogs.FlickrAccountTipAlertFragment;
import com.apps.mohb.shutternotes.views.Toasts;
import com.flickr4java.flickr.auth.Auth;


public class FlickrAccountActivity extends AppCompatActivity
		implements FlickrAccountTipAlertFragment.FlickrAccountTipDialogListener {

	private WebView flickrWebView;
	private TextView codeTextView;
	private Button connectButton;

	private FlickrApi flickrApi;
	private String tokenKey;

	private SharedPreferences warningFirstShow;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_flickr_account_authorize);
		getSupportActionBar().hide();

		flickrApi = new FlickrApi(getApplicationContext());

		flickrWebView = findViewById(R.id.webViewFlickrAuth);
		configureWebView(flickrWebView);

		codeTextView = findViewById(R.id.inputTextFlickrAuth);
		codeTextView.setOnFocusChangeListener((view, b) -> {
			codeTextView.setHint(Constants.EMPTY);
			connectButton.setClickable(true);
			connectButton.setText(R.string.button_connect);
			connectButton.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorGreen));
		});

		connectButton = findViewById(R.id.authButtonFlickrAuth);
		connectButton.setClickable(false);
		connectButton.setOnClickListener(view -> {
			int textSize = codeTextView.getText().length();
			if (textSize >= 9 && textSize <= 11) {
				tokenKey = codeTextView.getText().toString();
				flickrApi.setTokenKey(tokenKey);
				new getAccessToken().execute();
			} else {
				Toasts.setContext(getApplicationContext());
				Toasts.createTypeCode();
				Toasts.showTypeCode();
			}
		});

		warningFirstShow = this.getSharedPreferences(Constants.FLICKR_ACCOUNT_WARNING, Constants.PRIVATE_MODE);

		if(warningFirstShow.getBoolean(Constants.KEY_FIRST_SHOW, true)) {
			FlickrAccountTipAlertFragment dialogWarning = new FlickrAccountTipAlertFragment();
			dialogWarning.show(getSupportFragmentManager(), "FlickrAccountTipAlertFragment");
		}

		if (!flickrApi.getToken().isEmpty() && !flickrApi.getTokenSecret().isEmpty()) {
			new checkToken().execute();
		} else {
			new getRequestToken().execute();
		}


	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Toasts.cancelAccountConnected();
		Toasts.cancelTypeCode();
		Toasts.cancelWrongCode();
	}

	private class WebBrowser extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

	private class getRequestToken extends FlickrApi.getRequestToken {

		@Override
		protected void onPostExecute(Object o) {
			super.onPostExecute(o);
			flickrWebView.loadUrl(FlickrApi.getAuthorizationUrl());
			connectButton.setClickable(true);
			connectButton.setText(R.string.button_connect);
		}

	}

	private class getAccessToken extends FlickrApi.getAccessToken {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			connectButton.setClickable(false);
			connectButton.setText(R.string.button_connecting);
			connectButton.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorYellow));
		}

		@Override
		protected void onPostExecute(Object o) {
			super.onPostExecute(o);
			if (FlickrApi.accessTokenFailed()) {
				Toasts.setContext(getApplicationContext());
				Toasts.createWrongCode();
				Toasts.showWrongCode();
				connectButton.setClickable(true);
				connectButton.setText(R.string.button_connect);
				connectButton.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorGreen));
			} else {
				new checkToken().execute();
			}
		}

	}

	private class checkToken extends FlickrApi.checkToken {

		@Override
		protected void onPostExecute(Object o) {
			super.onPostExecute(o);
			Auth auth = flickrApi.getAuth();
			Toasts.setContext(getApplicationContext());
			if (auth == null) {
				if (connectButton.getText()
						.equals(getApplicationContext().getResources().getString(R.string.button_connecting))) {
					Toasts.setContext(getApplicationContext());
					Toasts.createWrongCode();
					Toasts.showWrongCode();
					connectButton.setClickable(true);
					connectButton.setText(R.string.button_connect);
					connectButton.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorGreen));
				}
				FlickrApi.clearTokens();
				new getRequestToken().execute();
			} else {
				String user = auth.getUser().getId();
				Log.d(Constants.LOG_DEBUG_TAG, user);
				Toasts.createAccountConnected();
				Toasts.showAccountConnected();
				setContentView(R.layout.activity_flickr_account_connected);
				flickrWebView = findViewById(R.id.webViewFlickrProfile);
				configureWebView(flickrWebView);
				flickrWebView.loadUrl(Constants.FLICKR_URL + user);
			}
		}

	}

	@Override
	public void onFlickrAccountTipDialogPositiveClick(DialogFragment dialog) {
		warningFirstShow.edit().putBoolean(Constants.KEY_FIRST_SHOW, false).commit();
	}

	private void configureWebView(WebView webView) {
		webView.setWebViewClient(new WebBrowser());
		webView.getSettings().setLoadsImagesAutomatically(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
	}

}

