/*
 *  Copyright (c) 2019 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : getNumOfPagesUnitTest.java
 *  Last modified : 8/10/19 10:27 AM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import org.junit.Test;

import static com.apps.mohb.shutternotes.FlickrApi.getTagsStringArray;
import static com.apps.mohb.shutternotes.FlickrApi.getNewTagsArray;
import static com.apps.mohb.shutternotes.FlickrApi.getNumOfPages;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class FlickrApiUnitTest {

	@Test
	public void getNumOfPagesTest() {

		assertEquals(4, getNumOfPages(40, 10));
		assertEquals(5, getNumOfPages(45, 10));
		assertEquals(1, getNumOfPages(23, 23));
		assertEquals(1, getNumOfPages(10, 23));
		assertEquals(2, getNumOfPages(24, 23));
		assertEquals(13, getNumOfPages(123, 10));
		assertEquals(6, getNumOfPages(584, 100));
		assertEquals(1, getNumOfPages(19, 20));
		assertEquals(0, getNumOfPages(0, 10));

	}

	@Test
	public void  getTagsStringArrayTest() {

		String[] array1 = { "Elem1", "Elem2", "Elem3" };
		String[] newArray = { "Elem1", "Elem2", "Elem3" };

		assertArrayEquals(newArray, getTagsStringArray(array1));

	}
	@Test
	public void  getNewTagsArrayTest() {

		String[] array1 = { "Elem1", "Elem2", "Elem3" };
		String[] array2 = { "Elem4", "Elem5", "Elem6", "Elem7" };
		String[] newArray = { "Elem1", "Elem2", "Elem3", "Elem4", "Elem5", "Elem6", "Elem7" };

		assertArrayEquals(newArray, getNewTagsArray(array1, array2));

	}
}