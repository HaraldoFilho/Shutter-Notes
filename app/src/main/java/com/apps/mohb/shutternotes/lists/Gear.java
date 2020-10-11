/*
 *  Copyright (c) 2020 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : Gear.java
 *  Last modified : 10/11/20 2:44 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes.lists;


public class Gear {

    private String gearItem;
    private boolean selected;

    public Gear(String gearItem) {
        this.gearItem = gearItem;
        this.selected = false;
    }

    public Gear(String gearItem, boolean selected) {
        this.gearItem = gearItem;
        this.selected = selected;
    }

    public String getGearItem() {
        return gearItem;
    }

    public void setGearItem(String gearItem) {
        this.gearItem = gearItem;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
