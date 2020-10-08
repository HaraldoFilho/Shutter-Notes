/*
 *  Copyright (c) 2020 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : Note.java
 *  Last modified : 10/8/20 1:29 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes.notes;


public class Note {

    private String text;

    protected String getText() {
        return text;
    }

    protected void setText(String text) {
        this.text = text;
    }
}
