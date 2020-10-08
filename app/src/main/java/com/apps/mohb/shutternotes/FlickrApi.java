/*
 *  Copyright (c) 2020 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : FlickrApi.java
 *  Last modified : 10/8/20 1:49 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.AuthInterface;
import com.flickr4java.flickr.auth.Permission;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuth1Token;


public class FlickrApi {

    private static Flickr flickr;
    private static FlickrApi flickrApi;

    private OAuth1RequestToken requestToken;
    private OAuth1Token accessToken;
    private AuthInterface authInterface;
    private Auth auth;

    private String apiSecret;
    private String token;
    private String tokenKey;
    private String tokenSecret;
    private String authorizationUrl;

    private boolean tokenFailed;

    private SharedPreferences flickrAccount;
    private SharedPreferences.Editor flickrAccountEditor;


    private FlickrApi(Context context) {

        String apiKey = context.getResources().getString(R.string.flickr_key);
        apiSecret = context.getResources().getString(R.string.flickr_secret);

        flickr = new Flickr(apiKey, apiSecret, new REST());
        authInterface = flickr.getAuthInterface();

        flickrAccount = context.getSharedPreferences(Constants.FLICKR_ACCOUNT, Constants.PRIVATE_MODE);

        token = flickrAccount.getString(Constants.TOKEN, Constants.EMPTY);
        tokenSecret = flickrAccount.getString(Constants.TOKEN_SECRET, Constants.EMPTY);

        Log.i(Constants.LOG_INFO_TAG, "Token: " + token);
        Log.i(Constants.LOG_INFO_TAG, "Token Secret: " + tokenSecret);

    }

    public static FlickrApi getInstance(Context context) {
        if (flickrApi == null) {
            flickrApi = new FlickrApi(context);
        }
        return flickrApi;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public Flickr getFlickrInterface() {
        return flickr;
    }

    public void clearTokens() {
        flickrAccountEditor = flickrAccount.edit();
        flickrAccountEditor.putString(Constants.TOKEN, Constants.EMPTY);
        flickrAccountEditor.putString(Constants.TOKEN_SECRET, Constants.EMPTY);
        flickrAccountEditor.apply();
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

    public String getAuthorizationUrl() {
        try {
            Thread t = new GetRequestToken();
            t.start();
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return Constants.EMPTY;
        }
        return authorizationUrl;
    }

    public boolean getAccessToken() {
        try {
            Thread t = new GetAccessToken();
            t.start();
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return tokenFailed;
        }
        return !tokenFailed;

    }

    public Auth checkToken() {
        try {
            Thread t = new CheckToken();
            t.start();
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return auth;

    }



    /*
     * Inner classes to deal with Flickr authentication
     */

    private class GetRequestToken extends Thread {

        public void run() {

            Log.i(Constants.LOG_INFO_TAG, "Requesting token...");

            requestToken = authInterface.getRequestToken();
            authorizationUrl = authInterface.getAuthorizationUrl(requestToken, Permission.WRITE);

            Log.i(Constants.LOG_INFO_TAG, "Token acquired: " + requestToken);
            Log.i(Constants.LOG_INFO_TAG, "Authorization url: " + authorizationUrl);

        }

    }

    private class GetAccessToken extends Thread {

        public void run() {

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

        }

    }

    private class CheckToken extends Thread {

        public void run() {

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

        }

    }

}
