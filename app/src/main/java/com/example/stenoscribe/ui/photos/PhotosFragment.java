package com.example.stenoscribe.ui.photos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.stenoscribe.AddPhotosActivity;
import com.example.stenoscribe.MeetingDetails;
import com.example.stenoscribe.R;
import com.example.stenoscribe.ReadTranscriptionActivity;
import com.example.stenoscribe.db.AppDatabase;
import com.example.stenoscribe.db.File;
import com.example.stenoscribe.db.FileAccessor;
import com.example.stenoscribe.db.FileOperator;
import com.example.stenoscribe.ui.recordings.RecordingsFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.content.Intent;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class PhotosFragment extends Fragment {
    //private ImageView image;
    private AppDatabase db;
    private FileAccessor accessor;
    private PhotoAdapter adapter;
    private List<File> photos;
    private String meetingId;
    private int lastPhotoId = 0;
    private String type = "photo";
    private ListView listView;
    private PhotosViewModel photosViewModel;

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
            final String image;

            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.meetings_list_elem, null);
            }
            item = items.get(position);
            if (item != null) {
                images = v.findViewById(R.id.imageView);
                images.setImageBitmap(BitmapFactory.decodeFile("items.path"));
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
//                int uid = RecordingsFragment.this.recordings.get(position).uid;
//                String path = RecordingsFragment.this.accessor.getFilePath(uid, meetingId);
                intent.putExtra("meetingId", meetingId);
                view.getContext().startActivity(intent);
            }
        });
    }

    public void configurePullToRefresh(View root) {
        final SwipeRefreshLayout pullToRefresh = root.findViewById(R.id.pull_to_refresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                photos = accessor.listFiles(meetingId, type);
                if(photos.size() > 0)
                    lastPhotoId = photos.get(0).uid;
                adapter.clear();
                adapter.addAll(photos);
                adapter.notifyDataSetChanged();
                pullToRefresh.setRefreshing(false);
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
        this.db = AppDatabase.getDatabase(root.getContext());
        this.accessor = new FileAccessor(this.db);
        configureFab(root);

        this.photos = this.accessor.listFiles(this.meetingId, this.type);
        if(this.photos.size() > 0)
            this.lastPhotoId = this.photos.get(0).uid;
        this.adapter = new PhotosFragment.PhotoAdapter(root.getContext(), R.layout.meetings_list_elem, photos);
        this.listView = root.findViewById(R.id.photos_list);
        this.configureListView();
        configurePullToRefresh(root);
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//        image.setImageBitmap(bitmap);
//
//        Uri selectedImage = data.getData();
//        FileOperator filepath = io.child("Photo").child(selectedImage.getLastPathSegment());
//        filepath.putFile(selectedImage).addSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot takeSnapshot) {
//                Toast.makeText(AddPhotosActivity.this, "Uploading finished", Toast.LENGTH_SHORT).show();
//            }
//        });

        final int PHOTO_CODE = 3;

        switch (requestCode) {
            case PHOTO_CODE: {
                if (resultCode == RESULT_OK && data != null) {
                    int uid = this.lastPhotoId + 1;
                    ArrayList<String> result = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
                    String image = result.get(0);
                    File file = new File(uid, this.meetingId, image, this.type);
                    this.accessor.insertFile(file, adapter);
                    photos = accessor.listFiles(meetingId, type);
                    if(photos.size() > 0)
                        lastPhotoId = photos.get(0).uid;
                    adapter.clear();
                    adapter.addAll(photos);
                    adapter.notifyDataSetChanged();
                }
                break;
            }
        }
    }

//    private void loadImageFromStorage(String path)
//    {
//
//        try {
//            File f=new File(path, "profile.jpg");
//            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
//            image.setImageBitmap(b);
//        }
//        catch (FileNotFoundException e)
//        {
//            e.printStackTrace();
//        }
//
//    }
}