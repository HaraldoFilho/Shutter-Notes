/*
 *  Copyright (c) 2020 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : GearNote.java
 *  Last modified : 10/11/20 3:13 PM
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
