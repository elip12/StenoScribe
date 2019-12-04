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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.stenoscribe.AddPhotosActivity;
import com.example.stenoscribe.FirebaseAccessor2;
import com.example.stenoscribe.MeetingDetails;
import com.example.stenoscribe.R;
import com.example.stenoscribe.ReadTranscriptionActivity;
import com.example.stenoscribe.ViewPhotoActivity;
import com.example.stenoscribe.db.File;
import com.example.stenoscribe.ui.recordings.RecordingsFragment;
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
    private String type = "photo";
    private GridView gridView;

    public class PhotoAdapter extends ArrayAdapter<File> {
        private List<File> items;

        private PhotoAdapter(Context context, int rId, List<File> items) {
            super(context, rId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            final File item;
            final ImageView image;

            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.photo_list_elem, null);
            }
            item = items.get(position);
            //Bitmap bitmap = StringToBitMap(item.path);
            if (item != null) {
                image = v.findViewById(R.id.imageView);
                //images.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 120, 120, false));

//                image.setRotation(90);
                accessor.viewImage(item.path, image);
//                Bitmap bitmap = StringToBitMap(item.path);
//                Bitmap bMapScaled = Bitmap.createScaledBitmap(bitmap, 120, 120, true);
//                image.setImageBitmap(bMapScaled);
//                image.setAdjustViewBounds(true);
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
        gridView.setAdapter(this.adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?>adapter, View v, int position, long id){
                String path = PhotosFragment.this.adapter.items.get(position).path;
                final Intent intent = new Intent(getContext(), ViewPhotoActivity.class);
                intent.putExtra("path", path);
                startActivity(intent);
            }
        });
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
        gridView = root.findViewById(R.id.photos_list);
        configureListView();

        accessor.listFiles(meetingId, type, adapter);

        return root;
    }
}