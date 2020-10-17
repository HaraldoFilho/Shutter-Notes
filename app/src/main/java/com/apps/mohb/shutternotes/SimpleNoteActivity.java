/*
 *  Copyright (c) 2020 mohb apps - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : SimpleNoteActivity.java
 *  Last modified : 10/15/20 7:30 AM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class SimpleNoteActivity extends AppCompatActivity {

    private EditText editText;
    private Toast mustType;

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
                mustType = Toast.makeText((this), R.string.toast_must_type, Toast.LENGTH_SHORT);
                mustType.show();
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

    // OPTIONS MENU

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_simple_note, menu);
        MenuItem menuHelp = menu.findItem(R.id.action_help);
        menuHelp.setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // Help
        if (id == R.id.action_help) {
            Intent intent = new Intent(this, HelpActivity.class);
            intent.putExtra(Constants.KEY_URL, getString(R.string.url_help_simple_note));
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            mustType.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
