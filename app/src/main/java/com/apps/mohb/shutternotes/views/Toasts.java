/*
 *  Copyright (c) 2019 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : Toasts.java
 *  Last modified : 12/26/19 12:45 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes.views;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.apps.mohb.shutternotes.Constants;
import com.apps.mohb.shutternotes.R;


// This class manages all the toasts in the application

public class Toasts {

    private static Toast helpPage;
    private static Toast mustType;
    private static Toast mustPickup;
    private static Toast mustSelect;
    private static Toast reordered;
    private static Toast archived;
    private static Toast restored;
    private static Toast typeCode;
    private static Toast wrongCode;
    private static Toast noPhotosUpdated;
    private static Toast accountConnected;


    // Toast to notify that is getting a help page from the internet

    public static void showHelpPage(Context context) {
        helpPage = Toast.makeText((context), R.string.toast_get_help_page, Toast.LENGTH_SHORT);
        helpPage.setGravity(Gravity.BOTTOM, Constants.TOAST_X_OFFSET, Constants.TOAST_Y_OFFSET);
        helpPage.show();
    }

    public static void cancelHelpPage() {
        if (helpPage != null) {
            helpPage.cancel();
        }
    }

    // Toast to notify that something must be typed

    public static void showMustType(Context context) {
        mustType = Toast.makeText((context), R.string.toast_must_type, Toast.LENGTH_SHORT);
        mustType.setGravity(Gravity.BOTTOM, Constants.TOAST_X_OFFSET, Constants.TOAST_Y_OFFSET);
        mustType.show();
    }

    public static void showMustType(Context context, boolean Title) {
        mustType = Toast.makeText((context), R.string.toast_must_type_title, Toast.LENGTH_SHORT);
        mustType.setGravity(Gravity.BOTTOM, Constants.TOAST_X_OFFSET, Constants.TOAST_Y_OFFSET);
        mustType.show();
    }

    public static void cancelMustType() {
        if (mustType != null) {
            mustType.cancel();
        }
    }

    // Toast to notify that something must be picked up

    public static void showMustPickup(Context context) {
        mustPickup = Toast.makeText((context), R.string.toast_must_pickup, Toast.LENGTH_SHORT);
        mustPickup.setGravity(Gravity.BOTTOM, Constants.TOAST_X_OFFSET, Constants.TOAST_Y_OFFSET);
        mustPickup.show();
    }

    public static void cancelMustPickup() {
        if (mustPickup != null) {
            mustPickup.cancel();
        }
    }

    // Toast to notify that something must be picked up

    public static void showMustSelect(Context context) {
        mustSelect = Toast.makeText((context), R.string.toast_must_select, Toast.LENGTH_SHORT);
        mustSelect.setGravity(Gravity.BOTTOM, Constants.TOAST_X_OFFSET, Constants.TOAST_Y_OFFSET);
        mustSelect.show();
    }

    public static void cancelMustSelect() {
        if (mustSelect != null) {
            mustSelect.cancel();
        }
    }

    // Toast to notify that the list was reordered

    public static void showReorderedItems(Context context) {
        reordered = Toast.makeText((context), R.string.toast_reordered, Toast.LENGTH_SHORT);
        reordered.setGravity(Gravity.BOTTOM, Constants.TOAST_X_OFFSET, Constants.TOAST_Y_OFFSET);
        reordered.show();
    }

    public static void cancelReorderedItems() {
        if (reordered != null) {
            reordered.cancel();
        }
    }

    // Toast to notify that the all notes were archived

    public static void showAllNotesArchived(Context context) {
        archived = Toast.makeText((context), R.string.toast_all_notes_archived, Toast.LENGTH_SHORT);
        archived.setGravity(Gravity.BOTTOM, Constants.TOAST_X_OFFSET, Constants.TOAST_Y_OFFSET);
        archived.show();
    }

    public static void showAllNotesArchived(Context context, int message) {
        archived = Toast.makeText((context), message, Toast.LENGTH_SHORT);
        archived.setGravity(Gravity.BOTTOM, Constants.TOAST_X_OFFSET, Constants.TOAST_Y_OFFSET);
        archived.show();
    }

    public static void cancelAllNotesArchived() {
        if (archived != null) {
            archived.cancel();
        }
    }

    // Toast to notify that the all notes were restored

    public static void showAllNotesRestored(Context context) {
        restored = Toast.makeText((context), R.string.toast_all_notes_restored, Toast.LENGTH_SHORT);
        restored.setGravity(Gravity.BOTTOM, Constants.TOAST_X_OFFSET, Constants.TOAST_Y_OFFSET);
        restored.show();
    }

    public static void cancelAllNotesRestored() {
        if (restored != null) {
            restored.cancel();
        }
    }

    // Toast to notify that the code must be typed

    public static void showTypeCode(Context context) {
        typeCode = Toast.makeText((context), R.string.toast_type_code, Toast.LENGTH_SHORT);
        typeCode.setGravity(Gravity.BOTTOM, Constants.TOAST_X_OFFSET, Constants.TOAST_Y_OFFSET);
        typeCode.show();
    }

    public static void cancelTypeCode() {
        if (typeCode != null) {
            typeCode.cancel();
        }
    }

    // Toast to notify that the code typed is wrong

    public static void showWrongCode(Context context) {
        wrongCode = Toast.makeText((context), R.string.toast_wrong_code, Toast.LENGTH_SHORT);
        wrongCode.setGravity(Gravity.BOTTOM, Constants.TOAST_X_OFFSET, Constants.TOAST_Y_OFFSET);
        wrongCode.show();
    }

    public static void cancelWrongCode() {
        if (wrongCode != null) {
            wrongCode.cancel();
        }
    }

    // Toast to notify that no photos were updated

    public static void showNoPhotosUpdated(Context context) {
        noPhotosUpdated = Toast.makeText((context), R.string.toast_no_photos_updated, Toast.LENGTH_LONG);
        noPhotosUpdated.setGravity(Gravity.BOTTOM, Constants.TOAST_X_OFFSET, Constants.TOAST_Y_OFFSET);
        noPhotosUpdated.show();
    }

    public static void cancelNoPhotosUpdated() {
        if (noPhotosUpdated != null) {
            noPhotosUpdated.cancel();
        }
    }

    // Toast to notify that the account is connected

    public static void showAccountConnected(Context context) {
        accountConnected = Toast.makeText((context), R.string.toast_account_connected, Toast.LENGTH_SHORT);
        accountConnected.setGravity(Gravity.BOTTOM, Constants.TOAST_X_OFFSET, Constants.TOAST_Y_OFFSET);
        accountConnected.show();
    }

    public static void cancelAccountConnected() {
        if (accountConnected != null) {
            accountConnected.cancel();
        }
    }

}
