package com.example.stenoscribe.ui.recordings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.stenoscribe.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class RecordingsFragment extends Fragment {

    private RecordingsViewModel recordingsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        recordingsViewModel =
                ViewModelProviders.of(this).get(RecordingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_recordings, container, false);
//        final TextView textView = root.findViewById(R.id.text_recordings);
//        recordingsViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        FloatingActionButton fab = root.findViewById(R.id.fab_recordings);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "This creates a new recording", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        return root;


    }
}