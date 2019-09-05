/*
 *  Copyright (c) 2019 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : FlickrApi.java
 *  Last modified : 9/5/19 9:08 AM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.AuthInterface;
import com.flickr4java.flickr.auth.Permission;
import com.flickr4java.flickr.photos.Exif;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuth1Token;

import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;


public class FlickrApi {

	private static Flickr flickr;

	private static OAuth1RequestToken requestToken;
	private static OAuth1Token accessToken;
	private static AuthInterface authInterface;
	private static Auth auth;

	private static String apiKey;
	private static String apiSecret;
	private static String token;
	private static String tokenKey;
	private static String tokenSecret;
	private static String authorizationUrl;

	private static boolean tokenFailed;

	private static SharedPreferences flickrAccount;
	private static SharedPreferences.Editor flickrAccountEditor;


	public FlickrApi(Context context) {

		apiKey = context.getResources().getString(R.string.flickr_key);
		apiSecret = context.getResources().getString(R.string.flickr_secret);
		flickr = new Flickr(apiKey, apiSecret, new REST());
		authInterface = flickr.getAuthInterface();

		flickrAccount = context.getSharedPreferences(Constants.FLICKR_ACCOUNT, Constants.PRIVATE_MODE);
		flickrAccountEditor = flickrAccount.edit();
		token = flickrAccount.getString(Constants.TOKEN, Constants.EMPTY);
		tokenSecret = flickrAccount.getString(Constants.TOKEN_SECRET, Constants.EMPTY);

		Log.i(Constants.LOG_INFO_TAG, "Token: " + token);
		Log.i(Constants.LOG_INFO_TAG, "Token Secret: " + tokenSecret);
	}

	public static void clearTokens() {
		flickrAccountEditor.putString(Constants.TOKEN, Constants.EMPTY);
		flickrAccountEditor.putString(Constants.TOKEN_SECRET, Constants.EMPTY);
	}

	public static Flickr getFlickrInterface() {
		return flickr;
	}

	public Auth getAuth() {
		return auth;
	}

	public String getToken() {
		return token;
	}

	public void setTokenKey(String tokenKey) {
		this.tokenKey = tokenKey;
	}

	public String getTokenSecret() {
		return tokenSecret;
	}

	public static String getAuthorizationUrl() {
		return authorizationUrl;
	}

	public static boolean accessTokenFailed() {
		return tokenFailed;
	}

	public static class getRequestToken extends AsyncTask {

		protected Object doInBackground(Object[] objects) {

			Log.i(Constants.LOG_INFO_TAG, "Requesting token...");

			requestToken = authInterface.getRequestToken();
			authorizationUrl = authInterface.getAuthorizationUrl(requestToken, Permission.WRITE);

			Log.i(Constants.LOG_INFO_TAG, "Token acquired: " + requestToken);
			Log.i(Constants.LOG_INFO_TAG, "Authorization url: " + authorizationUrl);

			return null;

		}

	}

	public static class getAccessToken extends AsyncTask {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			tokenFailed = false;
		}

		protected Object doInBackground(Object[] objects) {

			Log.i(Constants.LOG_INFO_TAG, "Requesting access token...");
			Log.i(Constants.LOG_INFO_TAG, "Request token: " + requestToken.getToken());
			Log.i(Constants.LOG_INFO_TAG, "Token Key: " + tokenKey);

			if (!requestToken.getToken().isEmpty() && !tokenKey.isEmpty()) {
				try {
					accessToken = authInterface.getAccessToken(requestToken, tokenKey);
					token = accessToken.getToken();
					tokenSecret = accessToken.getTokenSecret();

					Log.i(Constants.LOG_INFO_TAG, "Token: " + token);
					Log.i(Constants.LOG_INFO_TAG, "Token Secret: " + tokenSecret);

					flickrAccountEditor.putString(Constants.TOKEN, token).commit();
					flickrAccountEditor.putString(Constants.TOKEN_SECRET, tokenSecret).commit();

				} catch (RuntimeException e) {
					tokenFailed = true;
				}
			}
			return null;

		}

	}

	public static class checkToken extends AsyncTask {

		protected Object doInBackground(Object[] objects) {

			Log.i(Constants.LOG_INFO_TAG, "Checking token...");

			try {
				auth = authInterface.checkToken(token, tokenSecret);
				Log.i(Constants.LOG_INFO_TAG, "User: " + auth.getUser().getUsername());
			} catch (FlickrException e) {
				auth = null;
			}
			return null;

		}

	}


	public static int getNumOfPages(int photosCount, int perPage) {

		float floatDiv = (float) photosCount / perPage;
		int intDiv = photosCount / perPage;

		if (floatDiv - intDiv > 0) {
			return intDiv + 1;
		} else {
			return intDiv;
		}

	}

	public static String getDateTaken(String photoId) throws FlickrException {
		Collection<Exif> exif = flickr.getPhotosInterface().getExif(photoId, apiSecret);
		Iterator<Exif> exifIterator = exif.iterator();
		int date = Constants.DATE_INIT;

		while (exifIterator.hasNext()) {
			Exif e = exifIterator.next();
			if (Pattern.matches(Constants.DATE_PATTERN, e.getRaw())) {
				if (date == Constants.DATE_TAKEN) {
					return e.getRaw();
				}
				date++;
			}
		}

		return Constants.EMPTY;
	}

	public static String[] getNewTagsArray(Object[] tagsOnPhoto, String[] tagsToAdd) {

		String[] newTagsArray = new String[tagsOnPhoto.length + tagsToAdd.length];

		for (int i = 0; i < newTagsArray.length; i++) {
			if (i < tagsOnPhoto.length) {
				newTagsArray[i] = tagsOnPhoto[i].toString()
						.replace("Tag [value=", Constants.EMPTY)
						.replace(", count=0]", Constants.EMPTY);
			} else {
				newTagsArray[i] = tagsToAdd[i - tagsOnPhoto.length];
			}
		}

		return newTagsArray;

	}

}
