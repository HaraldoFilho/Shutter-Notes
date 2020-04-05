/*
 *  Copyright (c) 2020 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : FlickrApi.java
 *  Last modified : 4/5/20 1:09 PM
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
import com.flickr4java.flickr.tags.TagRaw;
import com.flickr4java.flickr.tags.TagsInterface;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuth1Token;

import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;


@SuppressWarnings("WeakerAccess")
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


	public FlickrApi(Context context) throws Exception {

		apiKey = context.getResources().getString(R.string.flickr_key);
		apiSecret = context.getResources().getString(R.string.flickr_secret);

		flickr = new Flickr(apiKey, apiSecret, new REST());
		authInterface = flickr.getAuthInterface();

		flickrAccount = context.getSharedPreferences(Constants.FLICKR_ACCOUNT, Constants.PRIVATE_MODE);

		token = flickrAccount.getString(Constants.TOKEN, Constants.EMPTY);
		tokenSecret = flickrAccount.getString(Constants.TOKEN_SECRET, Constants.EMPTY);

		Log.i(Constants.LOG_INFO_TAG, "Token: " + token);
		Log.i(Constants.LOG_INFO_TAG, "Token Secret: " + tokenSecret);

	}

	public static void clearTokens() {
		flickrAccountEditor = flickrAccount.edit();
		flickrAccountEditor.putString(Constants.TOKEN, Constants.EMPTY);
		flickrAccountEditor.putString(Constants.TOKEN_SECRET, Constants.EMPTY);
		flickrAccountEditor.apply();
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

	public void setTokenKey(String key) {
		tokenKey = key;
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

	public static class GetRequestToken extends AsyncTask {

		protected Object doInBackground(Object[] objects) {

			Log.i(Constants.LOG_INFO_TAG, "Requesting token...");

			requestToken = authInterface.getRequestToken();
			authorizationUrl = authInterface.getAuthorizationUrl(requestToken, Permission.WRITE);

			Log.i(Constants.LOG_INFO_TAG, "Token acquired: " + requestToken);
			Log.i(Constants.LOG_INFO_TAG, "Authorization url: " + authorizationUrl);

			return null;

		}

	}

	public static class GetAccessToken extends AsyncTask {

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

					flickrAccountEditor = flickrAccount.edit();
					flickrAccountEditor.putString(Constants.TOKEN, token);
					flickrAccountEditor.putString(Constants.TOKEN_SECRET, tokenSecret);
					flickrAccountEditor.apply();

				} catch (RuntimeException e) {
					tokenFailed = true;
				}
			}
			return null;

		}

	}

	public static class CheckToken extends AsyncTask {

		protected Object doInBackground(Object[] objects) {

			Log.i(Constants.LOG_INFO_TAG, "Checking token...");

			try {
				auth = authInterface.checkToken(token, tokenSecret);
				Log.i(Constants.LOG_INFO_TAG, "Account connected!");
				Log.i(Constants.LOG_INFO_TAG, "User name: " + auth.getUser().getUsername());
			} catch (FlickrException e) {
				auth = null;
				requestToken = null;
				accessToken = null;
				token = Constants.EMPTY;
				tokenSecret = Constants.EMPTY;
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

	public static String[] getPhotoTagsArray(Object[] tags) throws FlickrException {

		TagsInterface tagsInterface = getFlickrInterface().getTagsInterface();

		String[] tagsStringArray = new String[tags.length];

		// Complete list of user's "raw" tags, including spaces, upper cases and non-alpha characters
		Collection<TagRaw> listUserRaw = tagsInterface.getListUserRaw();

		for (int i = 0; i < tags.length; i++) {

			// The tag retrieved from the photo, with only lower-case alpha characters
			String tag = tags[i].toString()
					.replace("Tag [value=", Constants.EMPTY)
					.replace(", count=0]", Constants.EMPTY);

			for (TagRaw tagRaw : listUserRaw) {
				// Get tag's raw string
				String tagString = String.valueOf(tagRaw.getRaw())
						.replace(Constants.BRACKET_LEFT, Constants.QUOTE)
						.replace(Constants.BRACKET_RIGHT, Constants.QUOTE);

				//  If there is more than one tag, get only the first
				if (tagString.contains(Constants.COMMA)) {
					tagString = tagString.split(Constants.COMMA)[0].concat(Constants.QUOTE);
				}

				// The string to compare with the tag
				String tagToCompare = tagString.toLowerCase().replaceAll(Constants.NON_ALPHA, Constants.EMPTY);

				if (tag.equals(tagToCompare)) {
					tagsStringArray[i] = tagString.replace(Constants.QUOTE, Constants.EMPTY);
				}
			}
		}

		return tagsStringArray;

	}

	public static String[] getNewPhotoTagsArray(String[] tagsArray1, String[] tagsArray2) {

		String[] newTagsArray = new String[tagsArray1.length + tagsArray2.length];

		for (int i = 0; i < newTagsArray.length; i++) {
			if (i < tagsArray1.length) {
				newTagsArray[i] = Constants.DOUBLE_QUOTE.concat(tagsArray1[i]).concat(Constants.DOUBLE_QUOTE);
			} else {
				newTagsArray[i] = Constants.DOUBLE_QUOTE.concat(tagsArray2[i - tagsArray1.length]).concat(Constants.DOUBLE_QUOTE);
			}
		}

		return newTagsArray;

	}

	public static String[] getNewPhotoTagsArray(String[] tagsArray) {

		for (int i = 0; i < tagsArray.length; i++) {
			tagsArray[i] = Constants.DOUBLE_QUOTE.concat(tagsArray[i]).concat(Constants.DOUBLE_QUOTE);
		}

		return tagsArray;

	}

}
