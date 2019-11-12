package com.example.stenoscribe.ui.documents;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.stenoscribe.R;
import com.example.stenoscribe.db.File;


public class document_creator extends Fragment {
    private DocumentsViewModel documentsViewModel;
    private EditText url_box;
    static int uid;
    private String TAG = "DOCUMENTSFRAGMENT";
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.activity_document_creator, container, false);
        /*documentsViewModel = ViewModelProviders.of(this).get(DocumentsViewModel.class);
        url_box = (EditText) root.findViewById(R.id.url_input);
        Button download_button = (Button) findViewById(R.id.download_button);
        download_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // NETWORK CHECKER GOES HERE.
                if (true == true) {
                    String url = url_box.getText().toString();
                    if ( (url.length() > 0) && (URLUtil.isValidUrl(url) == true) ) {
                        uid = uid++;
                        File new_file = new File(uid,documentsViewModel.meetingId,url,"document");
                        documentsViewModel.access.insertFile(new_file, documentsViewModel.docudapt);
                    } else {
                        Toast.makeText(getContext(), "Error: URL malformed", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Error: Device Offline", Toast.LENGTH_LONG).show();
                }
            }
        });*/
       return root;
    }
    public boolean onOptionsItemsSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
            //.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
