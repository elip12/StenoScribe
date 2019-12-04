package com.example.stenoscribe.ui.documents;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.stenoscribe.FirebaseAccessor2;
import com.example.stenoscribe.MeetingDetails;
import com.example.stenoscribe.R;
import com.example.stenoscribe.db.File;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class DocumentsFragment extends Fragment {
    private String meetingId;
    DocumentAdapter adapter;
    private FirebaseAccessor2 accessor;
    private final String type = "document";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_documents, container, false);
        meetingId = ((MeetingDetails)getActivity()).getUid();
        final ListView document_viewer = root.findViewById(R.id.document_view);
        accessor = FirebaseAccessor2.getInstance(getContext());

        adapter = new DocumentAdapter(getContext(), R.layout.document_layout, new ArrayList<File>());

        document_viewer.setAdapter(adapter);
        accessor.listFiles(meetingId, type, adapter);

        FloatingActionButton fab = root.findViewById(R.id.fab_documents);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),DocumentCreator.class);
                intent.putExtra("id",meetingId);
                startActivity(intent);
            }
        });
        document_viewer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File current = (File) document_viewer.getItemAtPosition(position);
                String urlandname = current.path;
                String[] parsed = urlandname.split(" ////// ");
                String url = parsed[0];
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });
        return root;
    }
}