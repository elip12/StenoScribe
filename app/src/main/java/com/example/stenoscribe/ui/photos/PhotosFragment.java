package com.example.stenoscribe.ui.photos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.stenoscribe.MeetingDetails;
import com.example.stenoscribe.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import android.content.Intent;

public class PhotosFragment extends Fragment {

    private PhotosViewModel photosViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        photosViewModel =
                ViewModelProviders.of(this).get(PhotosViewModel.class);
        View root = inflater.inflate(R.layout.fragment_photos, container, false);
//        final TextView textView = root.findViewById(R.id.text_photos);
//        photosViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        FloatingActionButton fab = root.findViewById(R.id.fab_photos);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "This lets you add a photo", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                //Intent intent = new Intent(view.getContext(), AddPhoto.class);
                //view.getContext().startActivity(intent);
            }
        });

        return root;
    }
}