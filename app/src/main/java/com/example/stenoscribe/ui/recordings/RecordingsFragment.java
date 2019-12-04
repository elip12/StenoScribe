package com.example.stenoscribe.ui.recordings;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.stenoscribe.MeetingDetails;
import com.example.stenoscribe.R;
import com.example.stenoscribe.ReadTranscriptionActivity;
import com.example.stenoscribe.SpeechService;
import com.example.stenoscribe.db.AppDatabase;
import com.example.stenoscribe.db.File;
import com.example.stenoscribe.db.FileAccessor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class RecordingsFragment extends Fragment {
    private AppDatabase db;
    private FileAccessor accessor;
    private FloatingActionButton fab;
    private RecordingAdapter adapter;
    private List<File> recordings;
    private ListView listView;
    private int lastRecordingId = 0;
    private String meetingId;
    private final String type = "recording";
    private String TAG = "RECORDINGSFRAGMENT";
    private static final int REQUEST_RECORD_PERMISSION = 100;
    private static final int REQUEST_INTERNET_PERMISSION = 101;
    private SpeechService speechService;
    private boolean isBound = false;
    private boolean recordingPermission = true;
    private boolean internetPermission = true;
    // connection to service for recording
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            SpeechService.MyBinder binder = (SpeechService.MyBinder) service;
            speechService = binder.getService();
            //Log.d(TAG, "Connected to service");
            isBound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
            //Log.d(TAG, "Disconnected to service");
        }
    };

    // recording adapter for displaying recordings
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
                String titleString = "Recording " + item.uid;
                title.setText(titleString);
                date.setText(item.datetime);
            }
            return v;
        }
    }

    // requests permissions needed for recording
    public void requestPermissions() {
        //Log.d(TAG, "requestPermission");
        ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_RECORD_PERMISSION);
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.INTERNET},
                REQUEST_INTERNET_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Log.d(TAG, "onRequestPermissionsResult");
        switch (requestCode) {
            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    recordingPermission = true;
                } else {
                    recordingPermission = false;
                }
            case REQUEST_INTERNET_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    internetPermission = true;
                } else {
                    internetPermission = false;
                }
        }
    }

    // configures FAB for starting and stopping recording.
    public void configureFab(View root) {
        this.fab = root.findViewById(R.id.fab_recordings);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isBound) {
                    if (!recordingPermission || !internetPermission) {
                        Toast.makeText(getContext(), "Permission denied",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Intent i = new Intent(getActivity(), SpeechService.class);
                        //Log.d(TAG, "telling service to start");
                        getActivity().startService(i);
                        getActivity().bindService(i, connection, Context.BIND_AUTO_CREATE);
                        Snackbar.make(view, "Recording", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
                else {
                    // stop listening for speech. should automatically process speech and append to returnedText
                    speechService.stopListening();
                    // get transcription from service
                    String transcription = speechService.returnedText;
                    speechService.returnedText = "";
                    if (transcription.equals("")) {
                        transcription = "No speech detected";
                    }
                    getContext().unbindService(connection);
                    Intent i = new Intent(getActivity(), SpeechService.class);
                    getActivity().stopService(i);
                    //Log.d(TAG, "unbound and stopped service");
                    isBound = false;

                    // add new recording into database;
                    int uid = lastRecordingId + 1;
                    //Log.d(TAG, "UID " + uid);
                    File file = new File(uid, meetingId, transcription, type);
                    accessor.insertFile(file, adapter);
                    recordings = accessor.listFiles(meetingId, type);
                    if(recordings.size() > 0)
                        lastRecordingId = recordings.get(0).uid;
                    adapter.clear();
                    adapter.addAll(recordings);
                    adapter.notifyDataSetChanged();
                    Snackbar.make(view, "Stopping recording", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }

    // configures list view or clickable recordings
    public void configureListView() {
        this.listView.setAdapter(this.adapter);
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?>adapter, View v, int position, long id){
                int uid = RecordingsFragment.this.recordings.get(position).uid;
                String path = RecordingsFragment.this.accessor.getFilePath(uid, meetingId, type);
                final Intent intent = new Intent(getContext(), ReadTranscriptionActivity.class);
                intent.putExtra("path", path);
                intent.putExtra("meetingTitle", "Recording " + uid);
                startActivity(intent);
            }
        });
    }

    // configures pull to refresh for manual refresh
    public void configurePullToRefresh(View root) {
        final SwipeRefreshLayout pullToRefresh = root.findViewById(R.id.pull_to_refresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recordings = accessor.listFiles(meetingId, type);
                if(recordings.size() > 0)
                    lastRecordingId = recordings.get(0).uid;
                adapter.clear();
                adapter.addAll(recordings);
                adapter.notifyDataSetChanged();
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    // instantiates everything and shows listview
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        this.meetingId = ((MeetingDetails)getActivity()).getUid();
        View root = inflater.inflate(R.layout.fragment_recordings, container, false);

        this.db = AppDatabase.getDatabase(root.getContext());
        this.accessor = new FileAccessor(this.db);
        this.fab = root.findViewById(R.id.fab);
        this.configureFab(root);

        this.recordings = this.accessor.listFiles(this.meetingId, this.type);
        if(this.recordings.size() > 0)
            this.lastRecordingId = this.recordings.get(0).uid;
        this.adapter = new RecordingAdapter(root.getContext(), R.layout.meetings_list_elem, recordings);
        this.listView = root.findViewById(R.id.recordings_list);
        this.configureListView();
        configurePullToRefresh(root);
        requestPermissions();
        Intent i = new Intent(getActivity(), SpeechService.class);
        getActivity().bindService(i, connection, 0);
        return root;
    }
}
