package com.example.stenoscribe.ui.documents;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.stenoscribe.MeetingDetails;
import com.example.stenoscribe.R;
import com.example.stenoscribe.db.AppDatabase;
import com.example.stenoscribe.db.File;
import com.example.stenoscribe.db.FileAccessor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class DocumentsFragment extends Fragment {
    private AppDatabase database;
    private FileAccessor access;
    private String meetingId;
    List<File> documents;
    DocumentAdapter docudapt;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_documents, container, false);
        this.meetingId = ((MeetingDetails)getActivity()).getUid();
        ListView document_viewer = (ListView) root.findViewById(R.id.document_view);
        this.database = AppDatabase.getDatabase(root.getContext());
        this.access = new FileAccessor(this.database);
        this.documents = this.access.listFiles(this.meetingId,"document");
        this.docudapt = new DocumentAdapter(getContext(), R.layout.document_layout, documents);
        document_viewer.setAdapter(docudapt);
        FloatingActionButton fab = root.findViewById(R.id.fab_documents);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),DocumentCreator.class);
                intent.putExtra("id",meetingId);
                startActivity(intent);
                /*
                File tester = new File(uid++,meetingId,"https://i.imgur.com/27PVtZh.jpg","document");
                access.insertFileAsync(tester);
                documents = access.listFiles(meetingId,"document");
                docudapt.clear();
                docudapt.addAll(documents);
                docudapt.notifyDataSetChanged();*/
            }
        });
        return root;
    }
}