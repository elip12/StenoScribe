package com.example.stenoscribe;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.stenoscribe.db.FileOperator;

import org.w3c.dom.Text;

public class ReadTranscriptionActivity extends AppCompatActivity {
    private FileOperator io;

    // Make actionbar title editable, and show back button
    public void configureActionBar(String title) {

        final ActionBar actionBar;

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_24dp);
        actionBar.setTitle(title);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_transcription);

        this.io = new FileOperator(getApplicationContext());
        Intent i = getIntent();
        String path = i.getStringExtra("path");
        String data = io.load(path);
        TextView view = findViewById(R.id.transcription_text);
        view.setText(data);
        String title = i.getStringExtra("meetingTitle");
        configureActionBar(title);
    }
}
