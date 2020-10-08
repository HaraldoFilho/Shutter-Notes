/*
 *  Copyright (c) 2020 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : GearNote.java
 *  Last modified : 10/8/20 1:29 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes.notes;


public class GearNote extends Note {

    public GearNote(String gearList) {
        super.setText(gearList);
    }

    public String getGearList() {
        return super.getText();
    }

}
