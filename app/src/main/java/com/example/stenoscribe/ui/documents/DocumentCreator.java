package com.example.stenoscribe.ui.documents;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.stenoscribe.MeetingDetails;
import com.example.stenoscribe.R;
import com.example.stenoscribe.db.AppDatabase;
import com.example.stenoscribe.db.File;
import com.example.stenoscribe.db.FileAccessor;

public class DocumentCreator extends AppCompatActivity {
    private static int uid;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_creator);
        final EditText url_box = (EditText) findViewById(R.id.url_input);
        Button download_button = (Button) findViewById(R.id.download_button);
        download_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // NETWORK CHECKER GOES HERE.
                if (true == true) {
                    String url = url_box.getText().toString();
                    if ( (url.length() > 0) && (URLUtil.isValidUrl(url) == true) ) {
                        uid = uid++;
                    } else {
                        Toast.makeText(DocumentCreator.this, "Error: URL malformed",Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(DocumentCreator.this, "Error: Device Offline", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public boolean onOptionsItemsSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
