/*
 *  Copyright (c) 2019 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : FlickrPhotosetsListAdapter.java
 *  Last modified : 8/17/19 12:08 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.mohb.shutternotes.Constants;
import com.apps.mohb.shutternotes.R;

import com.flickr4java.flickr.photosets.Photoset;

import com.squareup.picasso.Picasso;

import java.util.Collection;
import java.util.List;


public class FlickrPhotosetsListAdapter extends ArrayAdapter {


	public FlickrPhotosetsListAdapter(@NonNull Context context, Collection<Photoset> photosetsList) {
		super(context, Constants.LIST_ADAPTER_RESOURCE_ID, (List) photosetsList);
	}

	@NonNull
	@Override
	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.photoset_item, parent, false);
		}

		ImageView coverPhotoView = convertView.findViewById(R.id.imagePhotosetCover);
		TextView titleView = convertView.findViewById(R.id.textPhotosetTitle);
		TextView sizeView = convertView.findViewById(R.id.textPhotosetSize);

		Photoset photoset = (Photoset) getItem(position);
		String imgCoverPhotoUrl = photoset.getPrimaryPhoto().getSquareLargeUrl();

		String txtTitle = photoset.getTitle();

		int size = photoset.getPhotoCount();
		String txtSize = String.valueOf(size)
				.concat(getContext().getResources().getString(R.string.text_photoset_size));
		if (size == 1) {
			txtSize = txtSize.replace("s", "");
		}

		Picasso.get().load(imgCoverPhotoUrl).into(coverPhotoView);

		titleView.setText(txtTitle);
		titleView.setTypeface(Typeface.defaultFromStyle(android.graphics.Typeface.BOLD));
		sizeView.setText(txtSize);

		return convertView;

	}
}
