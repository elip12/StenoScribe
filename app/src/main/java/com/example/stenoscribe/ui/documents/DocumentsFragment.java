package com.example.stenoscribe.ui.documents;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DocumentsFragment extends Fragment {
    private ListView document_viewer;
    private DocumentsViewModel documentsViewModel;
    private FloatingActionButton fab;
    private ListView listView;
    private FileOperator io;
    private String TAG = "DOCUMENTSFRAGMENT";
    static int label = 1;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        documentsViewModel = ViewModelProviders.of(this).get(DocumentsViewModel.class);
        documentsViewModel.meetingId = ((MeetingDetails)getActivity()).getUid();
        View root = inflater.inflate(R.layout.fragment_documents, container, false);
        document_viewer = (ListView) root.findViewById(R.id.document_view);
        documentsViewModel.database = AppDatabase.getDatabase(root.getContext());
        documentsViewModel.access = new FileAccessor(documentsViewModel.database);
        documentsViewModel.documents = documentsViewModel.access.listFiles(documentsViewModel.meetingId,"document");
        File new_file = new File(label++,documentsViewModel.meetingId,"https://i.imgur.com/B3zuvJLb.jpg","document");
        File other_file = new File(label++,documentsViewModel.meetingId,"https://i.imgur.com/B3zuvJLb.jpg","document");
        documentsViewModel.documents.add(new_file);
        documentsViewModel.documents.add(other_file);
        documentsViewModel.docudapt = new DocumentAdapter(getContext(), R.layout.fragment_document_layout, documentsViewModel.documents);
        document_viewer.setAdapter(documentsViewModel.docudapt);
        FloatingActionButton fab = root.findViewById(R.id.fab_documents);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                document_creator next = new document_creator();
                /*FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.document_view,next);
                fragmentTransaction.commit();*/
                // NETWORK CHECKER GOES HERE.
                Log.d(TAG, "FAB clicked");
                File new_file = new File(label++,documentsViewModel.meetingId,"https://i.imgur.com/B3zuvJLb.jpg","document");
                Log.d(TAG, documentsViewModel.documents.toString());
                documentsViewModel.documents.add(new_file);
                Log.d(TAG, documentsViewModel.documents.toString());
                documentsViewModel.docudapt.clear();
                Log.d(TAG, documentsViewModel.documents.toString());
                documentsViewModel.docudapt.addAll(documentsViewModel.documents);
                Log.d(TAG, documentsViewModel.documents.toString());
                documentsViewModel.docudapt.notifyDataSetChanged();

            }
        });
        return root;
    }
}