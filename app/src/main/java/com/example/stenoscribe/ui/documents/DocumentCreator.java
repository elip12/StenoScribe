package com.example.stenoscribe.ui.documents;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

    // actionbar shows meeting title but is not editable, and show back button
    public void configureActionBar(String title) {
        TextView actionBarText;
        final ViewGroup actionBarLayout;
        final ActionBar actionBar;

        actionBarLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.action_bar_noneditable, null);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_24dp);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarLayout);

        actionBarText = findViewById(R.id.action_bar_textview);
        actionBarText.setText(title);
    }

    // to make back button have correct behavior
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_creator);

        configureActionBar("New Document");

        final EditText nametext = findViewById(R.id.name_input);
        final EditText urltext = findViewById(R.id.url_input);
        Button fab = findViewById(R.id.document_creator);
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
                if (isNetworkAvailable() == true) {
                    String name = nametext.getText().toString();
                    if ((name.length() > 0) && name != " ") {
                        // url checker
                        if ( (url.length() > 0) && URLUtil.isValidUrl(url) == true ) {
                            uid++;
                            String urlandname = url + " ////// " + name;
                            File new_file = new File(uid,meetingId,urlandname,"document");
                            access.insertFileAsync(new_file);
                            finish();
                        } else {
                            Toast.makeText(DocumentCreator.this, "invalid URL", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(DocumentCreator.this, "invalid name", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(DocumentCreator.this, "no internet connection found", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private String getmeetingid() {
        Intent intent = getIntent();
        return intent.getStringExtra("id");
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager internet = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = internet.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (network != null && network.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}
