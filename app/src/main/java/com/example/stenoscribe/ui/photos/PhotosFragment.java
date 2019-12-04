package com.example.stenoscribe.ui.photos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.example.stenoscribe.FirebaseAccessor2;
import com.example.stenoscribe.MeetingDetails;
import com.example.stenoscribe.R;
import com.example.stenoscribe.ViewPhotoActivity;
import com.example.stenoscribe.db.File;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class PhotosFragment extends Fragment {
    private PhotoAdapter adapter;
    private FirebaseAccessor2 accessor;
    private String meetingId;
    private String type = "photo";
    private GridView gridView;
    private final int REQUEST_IMAGE_UPLOAD = 101;

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
            if (item != null) {
                image = v.findViewById(R.id.imageView);
                accessor.viewImage(item.path, image);
            }
            return v;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_IMAGE_UPLOAD && data != null && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Could not retrieve image", Toast.LENGTH_LONG).show();
                return;
            }
            String uid = UUID.randomUUID().toString();
            String path = meetingId + "/" + uid + ".jpg";
            File file = new File(uid, meetingId, path, type);
            accessor.addImage(path, bitmap);
            accessor.addFile(file);
        }
        else {
            Toast.makeText(getContext(), "Could not retrieve image", Toast.LENGTH_LONG).show();
        }
    }


    public void configureFab(View root) {
        FloatingActionButton fab = root.findViewById(R.id.fab_photos);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_UPLOAD);
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