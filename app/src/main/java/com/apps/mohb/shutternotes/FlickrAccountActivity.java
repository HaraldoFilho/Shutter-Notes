/*
 *  Copyright (c) 2019 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : FlickrAccountActivity.java
 *  Last modified : 12/5/19 10:44 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.apps.mohb.shutternotes.views.Toasts;
import com.flickr4java.flickr.auth.Auth;

import java.util.Objects;


@SuppressWarnings("unchecked")
public class FlickrAccountActivity extends AppCompatActivity {

	private WebView flickrWebView;
	private TextView codeTextView;
	private Button connectButton;

	private FlickrApi flickrApi;
	private String tokenKey;

	private Handler handlerForJavascriptInterface;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_flickr_account_authorize);
		Objects.requireNonNull(getSupportActionBar()).hide();

		flickrApi = new FlickrApi(getApplicationContext());

		flickrWebView = findViewById(R.id.webViewFlickrAuth);
		configureWebView(flickrWebView);

		handlerForJavascriptInterface = new Handler();

		codeTextView = findViewById(R.id.inputTextFlickrAuth);
		codeTextView.setOnFocusChangeListener((view, b) -> {
			codeTextView.setHint(Constants.EMPTY);
			connectButton.setClickable(true);
			connectButton.setText(R.string.button_connect);
			connectButton.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorGreen, null));
		});

		connectButton = findViewById(R.id.authButtonFlickrAuth);
		connectButton.setClickable(false);
		connectButton.setOnClickListener(view -> {
			int textSize = codeTextView.getText().length();
			if (textSize >= Constants.TOKEN_KEY_SIZE_MIN && textSize <= Constants.TOKEN_KEY_SIZE_MAX) {
				tokenKey = codeTextView.getText().toString();
				flickrApi.setTokenKey(tokenKey);
				new GetAccessToken().execute();
			} else {
				Toasts.setContext(getApplicationContext());
				Toasts.createTypeCode();
				Toasts.showTypeCode();
			}
		});

		if (!flickrApi.getToken().isEmpty() && !flickrApi.getTokenSecret().isEmpty()) {
			new CheckToken().execute();
		} else {
			new GetRequestToken().execute();
		}


	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Toasts.cancelAccountConnected();
		Toasts.cancelTypeCode();
		Toasts.cancelWrongCode();
	}

	@SuppressLint("StaticFieldLeak")
	private class GetRequestToken extends FlickrApi.GetRequestToken {

		@Override
		protected void onPostExecute(Object o) {
			super.onPostExecute(o);
			String authorizationUrl = FlickrApi.getAuthorizationUrl();
			Log.d(Constants.LOG_DEBUG_TAG, authorizationUrl);
			flickrWebView.loadUrl(authorizationUrl);
			connectButton.setClickable(true);
			connectButton.setText(R.string.button_connect);
		}

	}

	@SuppressLint("StaticFieldLeak")
	private class GetAccessToken extends FlickrApi.GetAccessToken {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			connectButton.setClickable(false);
			connectButton.setText(R.string.button_connecting);
			connectButton.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorYellow, null));
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
				connectButton.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorGreen, null));
			} else {
				new CheckToken().execute();
			}
		}

	}

	@SuppressLint("StaticFieldLeak")
	private class CheckToken extends FlickrApi.CheckToken {

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
					connectButton.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorGreen, null));
				}
				FlickrApi.clearTokens();
				new GetRequestToken().execute();
			} else {
				String userId = auth.getUser().getId();
				Log.i(Constants.LOG_INFO_TAG, "User id: " + userId);
				Toasts.createAccountConnected();
				Toasts.showAccountConnected();
				setContentView(R.layout.activity_flickr_account_connected);
				flickrWebView = findViewById(R.id.webViewFlickrProfile);
				configureWebView(flickrWebView);
				flickrWebView.loadUrl(Constants.FLICKR_URL + userId);
			}
		}

	}

	// Insert token key in text field and execute get access token
	private void insertTokenKey(String key) {
		codeTextView.setText(key);
		flickrApi.setTokenKey(key);
		new GetAccessToken().execute();

	}

	// Below this point it was used code from the following page:
	// http://technoranch.blogspot.com/2014/08/how-to-get-html-content-from-android-webview.html

	@SuppressLint("SetJavaScriptEnabled")
	private void configureWebView(WebView webView) {
		// Sets a customized web view client capable of extract html content from a javascript page
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				// Javascript code to extract html content from Flickr pages
				// If user is PRO, the token key is in the 7th (index 6) span element
				view.loadUrl("javascript:window.HtmlViewer.getTokenKey" +
						"(document.getElementsByTagName('span')[6].innerHTML);");
				// If user is NOT PRO, the token key is in the 8th (index 7) span element
				view.loadUrl("javascript:window.HtmlViewer.getTokenKey" +
						"(document.getElementsByTagName('span')[7].innerHTML);");
			}

		});
		webView.getSettings().setLoadsImagesAutomatically(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDomStorageEnabled(true);
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webView.addJavascriptInterface(new AuthJavaScriptInterface(), "HtmlViewer");
	}

	class AuthJavaScriptInterface {

		@JavascriptInterface
		public void getTokenKey(String code) {
			handlerForJavascriptInterface.post(() -> {
				if (code.length() == Constants.TOKEN_KEY_SIZE_MAX && code.contains(Constants.DASH)) {
					insertTokenKey(code);
				}
			});
		}
	}

}

