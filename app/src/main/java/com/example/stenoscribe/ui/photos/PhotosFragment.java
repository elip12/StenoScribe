package com.example.stenoscribe.ui.photos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.stenoscribe.AddPhotosActivity;
import com.example.stenoscribe.FirebaseAccessor2;
import com.example.stenoscribe.MeetingDetails;
import com.example.stenoscribe.R;
import com.example.stenoscribe.db.File;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.content.Intent;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class PhotosFragment extends Fragment {
    //private ImageView image;
    private PhotoAdapter adapter;
    private FirebaseAccessor2 accessor;
    private String meetingId;
    private int lastPhotoId = 0;
    private String type = "photo";
    private ListView listView;

    public class PhotoAdapter extends ArrayAdapter<File> {
        private List<File> items;

        private PhotoAdapter(Context context, int rId, List<File> items) {
            super(context, rId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            final File item;
            final ImageView images;

            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.photo_list_elem, null);
            }
            item = items.get(position);
            //Bitmap bitmap = StringToBitMap(item.path);
            if (item != null) {
                images = v.findViewById(R.id.imageView);
                //images.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 120, 120, false));

                images.setRotation(90);
                Bitmap bitmap = StringToBitMap(item.path);
                Bitmap bMapScaled = Bitmap.createScaledBitmap(bitmap, 120, 120, true);
                images.setImageBitmap(bMapScaled);
                images.setAdjustViewBounds(true);
            }
            return v;
        }
    }


    public void configureFab(View root) {
        FloatingActionButton fab = root.findViewById(R.id.fab_photos);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddPhotosActivity.class);
                String meetingId = ((MeetingDetails)getActivity()).getUid();
                intent.putExtra("meetingId", meetingId);
                view.getContext().startActivity(intent);
            }
        });
    }

    public void configureListView() {
        this.listView.setAdapter(this.adapter);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        this.meetingId = ((MeetingDetails)getActivity()).getUid();
        View root = inflater.inflate(R.layout.fragment_photos, container, false);
        //image = root.findViewById(R.id.cameraIV);
        configureFab(root);

        accessor = FirebaseAccessor2.getInstance(getContext());

        adapter = new PhotosFragment.PhotoAdapter(root.getContext(),
                R.layout.meetings_list_elem, new ArrayList<File>());
        listView = root.findViewById(R.id.photos_list);
        configureListView();

        accessor.listFiles(meetingId, type, adapter);

        return root;
    }

    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte = Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }


}