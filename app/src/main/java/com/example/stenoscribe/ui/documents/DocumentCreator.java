package com.example.stenoscribe.ui.documents;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.Toast;

import com.example.stenoscribe.MeetingDetails;
import com.example.stenoscribe.R;
import com.example.stenoscribe.db.AppDatabase;
import com.example.stenoscribe.db.File;
import com.example.stenoscribe.db.FileAccessor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class DocumentCreator extends AppCompatActivity {
    private AppDatabase database;
    private FileAccessor access;
    private String meetingId;
    private int uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_creator);
        final EditText urltext = findViewById(R.id.url_input);
        FloatingActionButton fab = findViewById(R.id.fab_document_creator);
        this.meetingId = this.getmeetingid();
        this.database = AppDatabase.getDatabase(getApplicationContext());
        this.access = new FileAccessor(this.database);
        List<File> document_library = this.access.listFiles(this.meetingId,"document");
        this.uid = document_library.size();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = urltext.getText().toString();
                // internet checker
                if (true == true) {
                    // url checker
                    if ( URLUtil.isValidUrl(url) == true) {
                        uid++;
                        File new_file = new File(uid,meetingId,url,"document");
                        access.insertFileAsync(new_file);
                    } else {

                    }
                }
            }
        });
    }
    private String getmeetingid() {
        Intent intent = getIntent();
        return intent.getStringExtra("id");
    }
}
