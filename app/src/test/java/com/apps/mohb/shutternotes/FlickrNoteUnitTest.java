/*
 *  Copyright (c) 2019 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : FlickrNoteUnitTest.java
 *  Last modified : 7/28/19 4:28 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import com.apps.mohb.shutternotes.notes.FlickrNote;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FlickrNoteUnitTest {

	@Test
	public void gettersAndSetters() {

		String startTime = "2019:02:17 10:00:00";
		String finishTime = "2019:02:17 11:00:00";
		FlickrNote note = new FlickrNote(
				"Test", "", "", 0.0, 0.0, "", "");

		assertEquals("Test", note.getTitle());
		assertEquals("", note.getDescription());
		assertEquals("", note.getTags());
		assertEquals(0.0, note.getLatitude(), 0);
		assertEquals(0.0, note.getLongitude(), 0);
		assertEquals("", note.getStartTime());
		assertEquals("", note.getFinishTime());
		assertEquals(false, note.isSelected());

		String tags = Constants.QUOTE + "tag1" + Constants.QUOTE + Constants.SPACE
				+ Constants.QUOTE + "tag2" + Constants.QUOTE + Constants.SPACE
				+ Constants.QUOTE + "tag3" + Constants.QUOTE + Constants.SPACE;

		note.setTitle("Testing title setter");
		note.setDescription("Testing description setter");
		note.setTags(tags);
		note.setLatitude(12345.6789);
		note.setLongitude(98765.4321);
		note.setStartTime(startTime);
		note.setFinishTime(finishTime);
		note.setSelected(true);

		assertEquals("Testing title setter", note.getTitle());
		assertEquals("Testing description setter", note.getDescription());
		assertEquals(tags, note.getTags());
		assertEquals(12345.6789, note.getLatitude(), 0);
		assertEquals(98765.4321, note.getLongitude(), 0);
		assertEquals(startTime, note.getStartTime());
		assertEquals(finishTime, note.getFinishTime());
		assertEquals(true, note.isSelected());

	}

	@Test
	public void timeIsInInterval() {

		String startTime = "2019:02:17 10:00:00";
		String finishTime = "2019:02:17 11:00:00";
		FlickrNote note = new FlickrNote(
				"Test", "", "", 0.0, 0.0, startTime, finishTime);

		String time1 = "2018:04:15 15:10:00";
		String time2 = "2019:02:17 10:30:00";
		String time3 = "2019:02:17 12:00:00";
		String time4 = "2019:02:17 10:00:00";
		String time5 = "2019:02:17 11:00:00";
		String time6 = "2019:02:17 10:01:00";
		String time7 = "2019:02:17 10:59:00";

		assertEquals(false, note.isInTimeInterval(time1));
		assertEquals(true, note.isInTimeInterval(time2));
		assertEquals(false, note.isInTimeInterval(time3));
		assertEquals(false, note.isInTimeInterval(time4));
		assertEquals(false, note.isInTimeInterval(time5));
		assertEquals(true, note.isInTimeInterval(time6));
		assertEquals(true, note.isInTimeInterval(time7));

	}

}
