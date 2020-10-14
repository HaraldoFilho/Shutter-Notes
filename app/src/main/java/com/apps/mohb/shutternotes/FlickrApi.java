/*
 *  Copyright (c) 2020 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : FlickrApi.java
 *  Last modified : 10/12/20 5:38 PM
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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class FlickrApi {

    private static Flickr flickr;
    private static FlickrApi flickrApi;
    private static AuthInterface authInterface;

    private static String apiSecret;
    private static String token;
    private static String tokenKey;
    private static String tokenSecret;
    private static OAuth1RequestToken requestToken;
    private static OAuth1Token accessToken;

    private static SharedPreferences flickrAccount;


    private FlickrApi(Context context) {

        String apiKey = context.getResources().getString(R.string.flickr_key);
        apiSecret = context.getResources().getString(R.string.flickr_secret);

        flickr = new Flickr(apiKey, apiSecret, new REST());
        authInterface = flickr.getAuthInterface();

        if (authInterface != null) {
            Log.i(Constants.LOG_INFO_TAG, "Successfully acquired authentication interface");
        } else {
            Log.i(Constants.LOG_INFO_TAG, "ERROR: Unable to acquire authentication interface");
        }

        flickrAccount = context.getSharedPreferences(Constants.FLICKR_ACCOUNT, Constants.PRIVATE_MODE);

        token = flickrAccount.getString(Constants.TOKEN, Constants.EMPTY);
        tokenSecret = flickrAccount.getString(Constants.TOKEN_SECRET, Constants.EMPTY);

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
        SharedPreferences.Editor flickrAccountEditor = flickrAccount.edit();
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

    public Auth checkToken() {
        try {
            new CheckToken();
            return CheckToken.auth;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getAuthorizationUrl() {
        try {
            new Authorization();
            if (!Authorization.url.isEmpty()) {
                return Authorization.url;
            } else {
                return Constants.EMPTY;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Constants.EMPTY;
        }
    }

    public boolean getAccessToken() {
        try {
            new AccessToken();
            return AccessToken.success;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /*
     * Inner classes to deal with Flickr authentication
     */

    private static class CheckToken {

        static Auth auth;

        private CheckToken() throws Exception {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<Auth> future = executor.submit(new GetAuth());

            try {
                auth = future.get(Constants.AUTH_TIMEOUT, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                future.cancel(true);
                Log.i(Constants.LOG_INFO_TAG, "ERROR: Authentication failed");
            }

            executor.shutdownNow();
        }
    }

    private static class Authorization {

        static String url;

        private Authorization() throws Exception {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<String> future = executor.submit(new GetAuthorizationUrl());

            try {
                url = future.get(Constants.AUTH_TIMEOUT, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                future.cancel(true);
                Log.i(Constants.LOG_INFO_TAG, "ERROR: Unable to get authorization url");
                url = Constants.EMPTY;
            }

            executor.shutdownNow();
        }
    }

    private static class AccessToken {

        static boolean success;

        private AccessToken() throws Exception {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<Boolean> future = executor.submit(new GetAccessToken());

            try {
                success = future.get(Constants.AUTH_TIMEOUT, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                future.cancel(true);
                Log.i(Constants.LOG_INFO_TAG, "ERROR: Unable to get token");
            }

            executor.shutdownNow();
        }
    }


    private static class GetAuth implements Callable<Auth> {

        @Override
        public Auth call() {

            Auth auth;

            if (!token.isEmpty() && !tokenSecret.isEmpty()) {
                Log.i(Constants.LOG_INFO_TAG, "Token: " + token);
                Log.i(Constants.LOG_INFO_TAG, "Token Secret: " + tokenSecret);
                Log.i(Constants.LOG_INFO_TAG, "Checking token...");
            } else {
                Log.i(Constants.LOG_INFO_TAG, "ERROR: Token not found");
            }

            try {
                auth = authInterface.checkToken(token, tokenSecret);
                Log.i(Constants.LOG_INFO_TAG, "Token is valid!");
                Log.i(Constants.LOG_INFO_TAG, "Account connected!");
                Log.i(Constants.LOG_INFO_TAG, "User name: " + auth.getUser().getUsername());
            } catch (FlickrException e) {
                if (!token.isEmpty() && !tokenSecret.isEmpty()) {
                    Log.i(Constants.LOG_INFO_TAG, "ERROR: Invalid Token");
                }
                auth = null;
                requestToken = null;
                accessToken = null;
                token = Constants.EMPTY;
                tokenSecret = Constants.EMPTY;
            }

            return auth;
        }
    }

    private static class GetAuthorizationUrl implements Callable<String> {

        @Override
        public String call() {
            Log.i(Constants.LOG_INFO_TAG, "Requesting authorization token...");

            requestToken = authInterface.getRequestToken();
            Log.i(Constants.LOG_INFO_TAG, "Token acquired: " + requestToken);

            String authorizationUrl = authInterface.getAuthorizationUrl(requestToken, Permission.WRITE);
            Log.i(Constants.LOG_INFO_TAG, "Authorization url: " + authorizationUrl);

            return authorizationUrl;
        }
    }

    private static class GetAccessToken implements Callable<Boolean> {

        public Boolean call() {

            boolean success = true;

            Log.i(Constants.LOG_INFO_TAG, "Requesting access token...");
            Log.i(Constants.LOG_INFO_TAG, "Authorization token: " + requestToken);
            Log.i(Constants.LOG_INFO_TAG, "Token Key: " + tokenKey);

            if (!requestToken.isEmpty() && !tokenKey.isEmpty()) {
                try {
                    accessToken = authInterface.getAccessToken(requestToken, tokenKey);
                    token = accessToken.getToken();
                    tokenSecret = accessToken.getTokenSecret();

                    SharedPreferences.Editor flickrAccountEditor = flickrAccount.edit();
                    flickrAccountEditor.putString(Constants.TOKEN, token);
                    flickrAccountEditor.putString(Constants.TOKEN_SECRET, tokenSecret);
                    flickrAccountEditor.apply();

                } catch (RuntimeException e) {
                    success = false;
                }
            }

            return success;
        }
    }

}
