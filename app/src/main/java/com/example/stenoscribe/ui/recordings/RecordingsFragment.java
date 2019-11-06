package com.example.stenoscribe.ui.recordings;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.content.ActivityNotFoundException;
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
import com.example.stenoscribe.ReadTranscriptionActivity;
import com.example.stenoscribe.db.AppDatabase;
import com.example.stenoscribe.db.File;
import com.example.stenoscribe.db.FileAccessor;
import com.example.stenoscribe.db.FileOperator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class RecordingsFragment extends Fragment {
    private AppDatabase db;
    private FileAccessor accessor;
    private FloatingActionButton fab;
    private RecordingAdapter adapter;
    private List<File> recordings;
    private ListView listView;
    private int lastRecordingId = 0;
//    private RecordingsViewModel recordingsViewModel;
    private FileOperator io;
    private int meetingId;
    private final String type = "recording";

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

    public void startRecording(View view) {
        int SPEECH_CODE = 3;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
        try {
            //startActivityForResult(intent, SPEECH_CODE);

            // TEST CODE FOR SPEECH RECOGNIZER NOT USING ACTIVITY
//            final SpeechRecognizer sr = SpeechRecognizer.createSpeechRecognizer(getContext());
//            sr.startListening(intent);
//            new CountDownTimer(2000, 1000) {
//
//                public void onTick(long millisUntilFinished) {
//                    //do nothing, just let it tick
//                }
//
//                public void onFinish() {
//                    sr.stopListening();
//                }
//            }.start();

            // TEST CODE FOR MOCKING FILE INSERTION
            int id = this.lastRecordingId + 1;
            File f = new File(id, this.meetingId, "tempfile" + id + ".txt", this.type);
            this.accessor.insertFile(f);
            this.io.store("tempfile" + id + ".txt", "This is an example transcription.");
        } catch (ActivityNotFoundException a) {
            Snackbar.make(view,
                    "Speech-to-text not supported on your device",
                    Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent i) {
        super.onActivityResult(requestCode, resultCode, i);
        final int SPEECH_CODE = 3;

        switch (requestCode) {
            case SPEECH_CODE: {
                if (resultCode == RESULT_OK && i != null) {
                    int uid = this.lastRecordingId + 1;
                    ArrayList<String> result = i.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
                    String transcription = result.get(0);
                    String fname = "meeting_" + this.meetingId +
                                "recording_" + uid + ".txt";
                    this.io.store(fname, transcription);
                    File file = new File(uid, this.meetingId, fname, this.type);
                    this.accessor.insertFile(file);
                    this.lastRecordingId += 1;
                }
                break;
            }
        }
    }

    public void configureFab(View root) {
        this.fab = root.findViewById(R.id.fab_recordings);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRecording(view);
                Snackbar.make(view, "Recording", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void configureListView() {
        this.listView.setAdapter(this.adapter);
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?>adapter, View v, int position, long id){
                int uid = RecordingsFragment.this.recordings.get(position).uid;
                String path = RecordingsFragment.this.accessor.getFilePath(uid);
                final Intent intent = new Intent(getContext(), ReadTranscriptionActivity.class);
                intent.putExtra("path", path);
                intent.putExtra("meetingTitle", "Recording " + uid);
                startActivity(intent);
            }
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        this.meetingId = ((MeetingDetails)getActivity()).getUid();
//        this.recordingsViewModel = ViewModelProviders.of(this).get(RecordingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_recordings, container, false);

        this.db = AppDatabase.getDatabase(root.getContext());
        this.accessor = new FileAccessor(this.db, this.meetingId, this.type);
        this.fab = root.findViewById(R.id.fab);
        this.configureFab(root);

        this.recordings = this.accessor.listFiles();
        if(this.recordings.size() > 0)
            this.lastRecordingId = this.recordings.get(0).uid;
        this.io = new FileOperator(this.getContext());
        this.adapter = new RecordingAdapter(root.getContext(), R.layout.meetings_list_elem, recordings);
        this.listView = root.findViewById(R.id.recordings_list);
        this.configureListView();
        return root;
    }
}