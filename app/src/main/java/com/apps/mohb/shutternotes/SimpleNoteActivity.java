/*
 *  Copyright (c) 2019 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : SimpleNoteActivity.java
 *  Last modified : 8/18/19 5:36 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.apps.mohb.shutternotes.views.Toasts;


public class SimpleNoteActivity extends AppCompatActivity {

	private EditText editText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple_note);

		editText = findViewById(R.id.editTextSimpleNote);
		Button buttonCancel = findViewById(R.id.buttonSimpleNoteCancel);
		Button buttonClear = findViewById(R.id.buttonSimpleNoteClear);
		Button buttonOK = findViewById(R.id.buttonSimpleNoteOk);

		buttonCancel.setOnClickListener(view -> onBackPressed());

		buttonClear.setOnClickListener(view -> editText.setText(""));

		buttonOK.setOnClickListener(view -> {
			String textString = editText.getText().toString().trim();
			if (textString.equals(Constants.EMPTY)) {
				Toasts.setContext(getApplicationContext());
				Toasts.createMustType();
				Toasts.showMustType();
			} else {
				Intent intent = new Intent(getApplicationContext(), FullscreenNoteActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(Constants.KEY_FULL_SCREEN_TEXT, textString);
				bundle.putInt(Constants.KEY_CALLER_ACTIVITY, Constants.ACTIVITY_SIMPLE_NOTE);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Toasts.cancelMustType();
	}

}
