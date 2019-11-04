package com.example.stenoscribe.ui.recordings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.stenoscribe.MeetingDetails;
import com.example.stenoscribe.R;
import com.example.stenoscribe.db.AppDatabase;
import com.example.stenoscribe.db.File;
import com.example.stenoscribe.db.FileAccessor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class RecordingsFragment extends Fragment {
    private AppDatabase db;
    private FileAccessor accessor;
    private FloatingActionButton fab;
    private RecordingAdapter adapter;
    private List<File> recordings;
    private ListView listView;
//    private RecordingsViewModel recordingsViewModel;

    public class RecordingAdapter extends ArrayAdapter<File> {
        private List<File> items;

        private RecordingAdapter(Context context, int rId, List<File> items) {
            super(context, rId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            final File item;
            final TextView title;
            final TextView date;

            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.meetings_list_elem, null);
            }
            item = items.get(position);
            if (item != null) {
                title = v.findViewById(R.id.viewMeetingsListElemTitle);
                date = v.findViewById(R.id.viewMeetingsListElemDate);
                String titleString = "Recording " + (position + 1);
                title.setText(titleString);
                date.setText(item.datetime);
            }
            return v;
        }
    }

    public void configureFab(View root) {
        this.fab = root.findViewById(R.id.fab_recordings);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "This creates a new recording", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void configureListView() {
        this.listView.setAdapter(this.adapter);
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?>adapter, View v, int position, long id){
                Snackbar.make(v, "This opens a new activity showing the transcription", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //final Intent intent;
                // add extras
                //startActivity(intent);
            }
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        this.recordingsViewModel = ViewModelProviders.of(this).get(RecordingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_recordings, container, false);

        this.db = AppDatabase.getDatabase(root.getContext());
        this.accessor = new FileAccessor(this.db, ((MeetingDetails)getActivity()).getUid(), "recording");
        this.fab = root.findViewById(R.id.fab);
        this.configureFab(root);

        this.recordings = accessor.listFiles();

        File f1 = new File(((MeetingDetails)getActivity()).getUid(), "/test/path/1", "recording");
        File f2 = new File(((MeetingDetails)getActivity()).getUid(), "/test/path/2", "recording");
        this.recordings = new ArrayList<>();
        this.recordings.add(f1);
        this.recordings.add(f2);

        this.adapter = new RecordingAdapter(root.getContext(), R.layout.meetings_list_elem, recordings);
        this.listView = root.findViewById(R.id.recordings_list);
        this.configureListView();
        return root;
    }



}