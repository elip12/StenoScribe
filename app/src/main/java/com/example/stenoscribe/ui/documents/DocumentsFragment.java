package com.example.stenoscribe.ui.documents;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.stenoscribe.MeetingDetails;
import com.example.stenoscribe.R;
import com.example.stenoscribe.db.AppDatabase;
import com.example.stenoscribe.db.File;
import com.example.stenoscribe.db.FileAccessor;
import com.example.stenoscribe.db.FileOperator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class DocumentsFragment extends Fragment {
    private ListView document_viewer;
    private DocumentsViewModel documentsViewModel;
    private AppDatabase database;
    private FileAccessor access;
    private FloatingActionButton fab;
    private List<File> documents;
    private ListView listView;
    private FileOperator io;
    private int meetingId;
    private final String type = "recording";
    private String TAG = "DOCUMENTSFRAGMENT";
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.meetingId = ((MeetingDetails)getActivity()).getUid();
        documentsViewModel = ViewModelProviders.of(this).get(DocumentsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_documents, container, false);
        document_viewer = (ListView) root.findViewById(R.id.document_view);
        this.database = AppDatabase.getDatabase(root.getContext());
        this.access = new FileAccessor(this.database);
        documents = access.listFiles(meetingId,"document");
        DocumentAdapter docudapt = new DocumentAdapter(this,documents);
        document_viewer.setAdapter(docudapt);
        FloatingActionButton fab = root.findViewById(R.id.fab_documents);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // lost due to almost bricking
            }
        });


        return root;
    }
}