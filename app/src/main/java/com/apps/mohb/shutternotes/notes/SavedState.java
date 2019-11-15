/*
 *  Copyright (c) 2019 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : SavedState.java
 *  Last modified : 8/17/19 12:08 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes.notes;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.JsonReader;
import android.util.JsonWriter;

import com.apps.mohb.shutternotes.Constants;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;


public class SavedState {

	private SharedPreferences savedState;
	private SharedPreferences.Editor editor;
	private String dataType;

	public SavedState(Context context, String dataType) {
		this.dataType = dataType;
		savedState = context.getSharedPreferences(this.dataType, Constants.PRIVATE_MODE);
		editor = savedState.edit();
	}

	// ### SIMPLE NOTES ###

	// save simple notes list on memory through a json string
	public void setSimpleNotesState(ArrayList<SimpleNote> notes) throws IOException {
		String jsonSimpleNotes = writeSimpleNotesJsonString(notes);
		editor.putString(Constants.SIMPLE_NOTES, jsonSimpleNotes);
		editor.commit();
	}

	// get simple notes list from memory through a json string
	// if list was not saved yet creates a new array list
	public ArrayList<SimpleNote> getSimpleNotesState() throws IOException {
		String jsonSimpleNotes = savedState.getString(Constants.SIMPLE_NOTES, Constants.EMPTY);
		if (jsonSimpleNotes.equals(Constants.EMPTY)) {
			return new ArrayList<>();
		} else {
			return readSimpleNotesJsonString(jsonSimpleNotes);
		}
	}

	// get a json string of simple notes list from memory
	public String getSimpleNotesJsonState() {
		String jsonSimpleNotes = savedState.getString(Constants.SIMPLE_NOTES, Constants.EMPTY);
		return jsonSimpleNotes;
	}


	// create a json string of a list of simple note items
	public String writeSimpleNotesJsonString(ArrayList<SimpleNote> simpleNotes) throws IOException {
		StringWriter stringWriter = new StringWriter();
		JsonWriter jsonWriter = new JsonWriter(stringWriter);
		jsonWriter.setIndent(Constants.SPACE);
		writeSimpleNotesArrayList(jsonWriter, simpleNotes);
		jsonWriter.close();
		return stringWriter.toString();
	}

	// write all simple notes to json string
	public void writeSimpleNotesArrayList(JsonWriter writer, ArrayList<SimpleNote> simpleNotes) throws IOException {
		writer.beginArray();
		for (SimpleNote simpleNote : simpleNotes) {
			writeSimpleNote(writer, simpleNote);
		}
		writer.endArray();
	}

	// write a single simple note to json string
	public void writeSimpleNote(JsonWriter writer, SimpleNote simpleNote) throws IOException {
		writer.beginObject();
		writer.name(Constants.JSON_TEXT).value(simpleNote.getText());
		writer.endObject();
	}

	// read a json string containing a list of simple notes
	public ArrayList<SimpleNote> readSimpleNotesJsonString(String jsonString) throws IOException {
		JsonReader jsonReader = new JsonReader(new StringReader(jsonString));
		try {
			return readSimpleNotesArrayList(jsonReader);
		} finally {
			jsonReader.close();
		}
	}

	// read a list of simple notes from a json string
	public ArrayList<SimpleNote> readSimpleNotesArrayList(JsonReader jsonReader) throws IOException {
		ArrayList<SimpleNote> simpleNotes = new ArrayList<>();
		jsonReader.beginArray();
		while (jsonReader.hasNext()) {
			simpleNotes.add(readSimpleNote(jsonReader));
		}
		jsonReader.endArray();
		return simpleNotes;
	}

	// read a single simple note from a json string
	public SimpleNote readSimpleNote(JsonReader jsonReader) throws IOException {
		String noteText = Constants.EMPTY;

		jsonReader.beginObject();
		while (jsonReader.hasNext()) {
			String name = jsonReader.nextName();
			switch (name) {
				case Constants.JSON_TEXT:
					noteText = jsonReader.nextString();
					break;
				default:
					jsonReader.skipValue();
			}

		}
		jsonReader.endObject();
		SimpleNote simpleNote = new SimpleNote(noteText);
		return simpleNote;
	}

	// ### GEAR NOTES ###

	// save gear notes list on memory through a json string
	public void setGearNotesState(ArrayList<GearNote> notes) throws IOException {
		String jsonGearNotes = writeGearNotesJsonString(notes);
		editor.putString(Constants.GEAR_NOTES, jsonGearNotes);
		editor.commit();
	}

	// get gear notes list from memory through a json string
	// if list was not saved yet creates a new array list
	public ArrayList<GearNote> getGearNotesState() throws IOException {
		String jsonGearNotes = savedState.getString(Constants.GEAR_NOTES, Constants.EMPTY);
		if (jsonGearNotes.equals(Constants.EMPTY)) {
			return new ArrayList<>();
		} else {
			return readGearNotesJsonString(jsonGearNotes);
		}
	}

	// get a json string of gear notes list from memory
	public String getGearNotesJsonState() {
		String jsonGearNotes = savedState.getString(Constants.GEAR_NOTES, Constants.EMPTY);
		return jsonGearNotes;
	}

	// create a json string of a list of gear note items
	public String writeGearNotesJsonString(ArrayList<GearNote> gearNotes) throws IOException {
		StringWriter stringWriter = new StringWriter();
		JsonWriter jsonWriter = new JsonWriter(stringWriter);
		jsonWriter.setIndent(Constants.SPACE);
		writeGearNotesArrayList(jsonWriter, gearNotes);
		jsonWriter.close();
		return stringWriter.toString();
	}

	// write all gear notes to json string
	public void writeGearNotesArrayList(JsonWriter writer, ArrayList<GearNote> gearNotes) throws IOException {
		writer.beginArray();
		for (GearNote gearNote : gearNotes) {
			writeGearNote(writer, gearNote);
		}
		writer.endArray();
	}

	// write a gear note to json string
	public void writeGearNote(JsonWriter writer, GearNote gearNote) throws IOException {
		writer.beginObject();
		writer.name(Constants.JSON_TEXT).value(gearNote.getGearList());
		writer.endObject();
	}

	// read a json string containing a list of gear notes
	public ArrayList<GearNote> readGearNotesJsonString(String jsonString) throws IOException {
		JsonReader jsonReader = new JsonReader(new StringReader(jsonString));
		try {
			return readGearNotesArrayList(jsonReader);
		} finally {
			jsonReader.close();
		}
	}

	// read a list of gear notes from a json string
	public ArrayList<GearNote> readGearNotesArrayList(JsonReader jsonReader) throws IOException {
		ArrayList<GearNote> gearNotes = new ArrayList<>();
		jsonReader.beginArray();
		while (jsonReader.hasNext()) {
			gearNotes.add(readGearNote(jsonReader));
		}
		jsonReader.endArray();
		return gearNotes;
	}

	// read a gear note from a json string
	public GearNote readGearNote(JsonReader jsonReader) throws IOException {
		String gearList = Constants.EMPTY;

		jsonReader.beginObject();
		while (jsonReader.hasNext()) {
			String name = jsonReader.nextName();
			switch (name) {
				case Constants.JSON_TEXT:
					gearList = jsonReader.nextString();
					break;
				default:
					jsonReader.skipValue();
			}

		}
		jsonReader.endObject();
		GearNote gearNote = new GearNote(gearList);
		return gearNote;
	}

	// ### FLICKR NOTES ###

	// save flickr notes list on memory through a json string
	public void setFlickrNotesState(ArrayList<FlickrNote> notes) throws IOException {
		String jsonFlickrNotes = writeFlickrNotesJsonString(notes);
		editor.putString(Constants.FLICKR_NOTES, jsonFlickrNotes);
		editor.commit();
	}

	// get flickr notes list from memory through a json string
	// if list was not saved yet creates a new array list
	public ArrayList<FlickrNote> getFlickrNotesState() throws IOException {
		String jsonFlickrNotes = savedState.getString(Constants.FLICKR_NOTES, Constants.EMPTY);
		if (jsonFlickrNotes.equals(Constants.EMPTY)) {
			return new ArrayList<>();
		} else {
			return readFlickrNotesJsonString(jsonFlickrNotes);
		}
	}

	// get a json string of flickr notes list from memory
	public String getFlickrNotesJsonState() {
		String jsonFlickrNotes = savedState.getString(Constants.FLICKR_NOTES, Constants.EMPTY);
		return jsonFlickrNotes;
	}

	// create a json string of a list of flickr note items
	public String writeFlickrNotesJsonString(ArrayList<FlickrNote> flickrNotes) throws IOException {
		StringWriter stringWriter = new StringWriter();
		JsonWriter jsonWriter = new JsonWriter(stringWriter);
		jsonWriter.setIndent(Constants.SPACE);
		writeFlickrNotesArrayList(jsonWriter, flickrNotes);
		jsonWriter.close();
		return stringWriter.toString();
	}

	// write all flickr notes to json string
	public void writeFlickrNotesArrayList(JsonWriter writer, ArrayList<FlickrNote> flickrNotes) throws IOException {
		writer.beginArray();
		for (FlickrNote flickrNote : flickrNotes) {
			writeFlickrNote(writer, flickrNote);
		}
		writer.endArray();
	}

	// create a json string of a list of tags
	public String writeTagsJsonString(ArrayList<String> tags) throws IOException {
		StringWriter stringWriter = new StringWriter();
		JsonWriter jsonWriter = new JsonWriter(stringWriter);
		jsonWriter.setIndent(Constants.SPACE);
		writeTagsArrayList(jsonWriter, tags);
		jsonWriter.close();
		return stringWriter.toString();
	}

	// write all flickr notes to json string
	public void writeTagsArrayList(JsonWriter writer, ArrayList<String> tags) throws IOException {
		writer.beginArray();
		for (String tag : tags) {
			writer.value(tag);
		}
		writer.endArray();
	}

	// write a flickr note to json string
	public void writeFlickrNote(JsonWriter writer, FlickrNote flickrNote) throws IOException {
		writer.beginObject();
		writer.name(Constants.JSON_TITLE).value(flickrNote.getTitle());
		writer.name(Constants.JSON_DESCRIPTION).value(flickrNote.getDescription());
		writer.name(Constants.JSON_TAGS).value(writeTagsJsonString(flickrNote.getTags()));
		writer.name(Constants.JSON_LATITUDE).value(flickrNote.getLatitude());
		writer.name(Constants.JSON_LONGITUDE).value(flickrNote.getLongitude());
		writer.name(Constants.JSON_START_TIME).value(flickrNote.getStartTime());
		writer.name(Constants.JSON_FINISH_TIME).value(flickrNote.getFinishTime());
		writer.name(Constants.JSON_SELECTED).value(flickrNote.isSelected());
		writer.endObject();
	}

	// read a json string containing a list of flickr notes
	public ArrayList<FlickrNote> readFlickrNotesJsonString(String jsonString) throws IOException {
		JsonReader jsonReader = new JsonReader(new StringReader(jsonString));
		try {
			return readFlickrNotesArrayList(jsonReader);
		} finally {
			jsonReader.close();
		}
	}

	// read a list of flickr notes from a json string
	public ArrayList<FlickrNote> readFlickrNotesArrayList(JsonReader jsonReader) throws IOException {
		ArrayList<FlickrNote> flickrNotes = new ArrayList<>();
		jsonReader.beginArray();
		while (jsonReader.hasNext()) {
			flickrNotes.add(readFlickrNote(jsonReader));
		}
		jsonReader.endArray();
		return flickrNotes;
	}

	// read a json string containing a list of flickr notes
	public ArrayList<String> readTagsJsonString(String jsonString) throws IOException {
		JsonReader jsonReader = new JsonReader(new StringReader(jsonString));
		try {
			return readTagsArrayList(jsonReader);
		} finally {
			jsonReader.close();
		}
	}

	// read a list of flickr notes from a json string
	public ArrayList<String> readTagsArrayList(JsonReader jsonReader) throws IOException {
		ArrayList<String> tags = new ArrayList<>();
		jsonReader.beginArray();
		while (jsonReader.hasNext()) {
			tags.add(jsonReader.nextString());
		}
		jsonReader.endArray();
		return tags;
	}

	// read a flickr note from a json string
	public FlickrNote readFlickrNote(JsonReader jsonReader) throws IOException {
		String title = Constants.EMPTY;
		String description = Constants.EMPTY;
		ArrayList<String> tags = new ArrayList<>();
		Double latitude = Constants.DEFAULT_LATITUDE;
		Double longitude = Constants.DEFAULT_LONGITUDE;
		String noteStartTime = Constants.EMPTY;
		String noteFinishTime = Constants.EMPTY;
		boolean noteSelected = false;

		jsonReader.beginObject();
		while (jsonReader.hasNext()) {
			String name = jsonReader.nextName();
			switch (name) {
				case Constants.JSON_TITLE:
					title = jsonReader.nextString();
					break;
				case Constants.JSON_DESCRIPTION:
					description = jsonReader.nextString();
					break;
				case Constants.JSON_TAGS:
					tags = readTagsJsonString(jsonReader.nextString());
					break;
				case Constants.JSON_LATITUDE:
					latitude = jsonReader.nextDouble();
					break;
				case Constants.JSON_LONGITUDE:
					longitude = jsonReader.nextDouble();
					break;
				case Constants.JSON_START_TIME:
					noteStartTime = jsonReader.nextString();
					break;
				case Constants.JSON_FINISH_TIME:
					noteFinishTime = jsonReader.nextString();
					break;
				case Constants.JSON_SELECTED:
					noteSelected = jsonReader.nextBoolean();
					break;
				default:
					jsonReader.skipValue();
			}

		}
		jsonReader.endObject();
		FlickrNote flickrNote = new FlickrNote(title, description, tags, latitude, longitude, noteStartTime, noteFinishTime);
		flickrNote.setSelected(noteSelected);
		return flickrNote;
	}

	// ### GEAR LIST ###

	// save gear list on memory through a json string
	public void setGearListState(ArrayList<Gear> gear) throws IOException {
		String jsonGearNotes = writeGearListJsonString(gear);
		editor.putString(Constants.GEAR_LIST, jsonGearNotes);
		editor.commit();
	}

	// get gears list from memory through a json string
	// if list was not saved yet creates a new array list
	public ArrayList<Gear> getGearListState() throws IOException {
		String jsonGearList = savedState.getString(Constants.GEAR_LIST, Constants.EMPTY);
		if (jsonGearList.equals(Constants.EMPTY)) {
			return new ArrayList<>();
		} else {
			return readGearListJsonString(jsonGearList);
		}
	}

	// get a json string of gear list from memory
	public String getGearListJsonState() {
		String jsonGearNotes = savedState.getString(Constants.GEAR_LIST, Constants.EMPTY);
		return jsonGearNotes;
	}

	// create a json string of a list of gear items
	public String writeGearListJsonString(ArrayList<Gear> gearList) throws IOException {
		StringWriter stringWriter = new StringWriter();
		JsonWriter jsonWriter = new JsonWriter(stringWriter);
		jsonWriter.setIndent(Constants.SPACE);
		writeGearArrayList(jsonWriter, gearList);
		jsonWriter.close();
		return stringWriter.toString();
	}

	// write all gear to json string
	public void writeGearArrayList(JsonWriter writer, ArrayList<Gear> gearList) throws IOException {
		writer.beginArray();
		for (Gear gear : gearList) {
			writeGear(writer, gear);
		}
		writer.endArray();
	}

	// write a gear to json string
	public void writeGear(JsonWriter writer, Gear gear) throws IOException {
		writer.beginObject();
		writer.name(Constants.JSON_GEAR_ITEM).value(gear.getGearItem());
		switch (dataType) {
			case Constants.GEAR_LIST_SAVED_STATE:
				writer.name(Constants.JSON_SELECTED).value(false);
				break;
			case Constants.GEAR_LIST_SELECTED_STATE:
				writer.name(Constants.JSON_SELECTED).value(gear.isSelected());
				break;
		}
		writer.endObject();
	}

	// read a json string containing a list of gear
	public ArrayList<Gear> readGearListJsonString(String jsonString) throws IOException {
		JsonReader jsonReader = new JsonReader(new StringReader(jsonString));
		try {
			return readGearArrayList(jsonReader);
		} finally {
			jsonReader.close();
		}
	}

	// read a list of gear from a json string
	public ArrayList<Gear> readGearArrayList(JsonReader jsonReader) throws IOException {
		ArrayList<Gear> gear = new ArrayList<>();
		jsonReader.beginArray();
		while (jsonReader.hasNext()) {
			gear.add(readGearItem(jsonReader));
		}
		jsonReader.endArray();
		return gear;
	}

	// read a gear item
	public Gear readGearItem(JsonReader jsonReader) throws IOException {
		String gear = Constants.EMPTY;
		boolean selected = false;

		jsonReader.beginObject();
		while (jsonReader.hasNext()) {
			String name = jsonReader.nextName();
			switch (name) {
				case Constants.JSON_GEAR_ITEM:
					gear = jsonReader.nextString();
					break;
				case Constants.JSON_SELECTED:
					selected = jsonReader.nextBoolean();
					break;
				default:
					jsonReader.skipValue();
			}

		}
		jsonReader.endObject();
		Gear gearItem = new Gear(gear, selected);
		return gearItem;
	}


}
