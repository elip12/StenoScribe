package com.example.stenoscribe.ui.photos;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
    private FloatingActionButton fab;
    private boolean deletePossible = false;
    private File selectedFile = null;
    private ImageView selectedView = null;

    public class PhotoAdapter extends ArrayAdapter<File> {
        private List<File> items;

        private PhotoAdapter(Context context, int rId, List<File> items) {
            super(context, rId, items);
            this.items = items;
        }

        // gridview only views thumbnails to save memory
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
                accessor.viewImage("thumb/" + item.path, image);

                image.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        deletePossible = true;
                        selectedFile = item;
                        selectedView = image;
                        fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_delete_white_24dp));
                        image.setPadding(3,3,3,3);

                        return true;
                    }
                });

                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (deletePossible) {
                            deletePossible = false;
                            selectedFile = null;
                            selectedView = null;
                            image.setPadding(0,0,0,0);
                            fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_add_black_24dp));
                        }
                        else {
                            String path = item.path;
                            final Intent intent = new Intent(getContext(), ViewPhotoActivity.class);
                            intent.putExtra("path", path);
                            startActivity(intent);
                        }
                    }
                });
            }
            return v;
        }
    }

    // FAB opens gallery and uploads thumbnail and full size image to FB storage
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
            Bitmap thumb = Bitmap.createScaledBitmap(
                    bitmap, 120, 120, false);


            String uid = UUID.randomUUID().toString();
            String path = meetingId + "/" + uid + ".jpg";
            String path2 = "thumb/" + meetingId + "/" + uid + ".jpg";
            File file = new File(uid, meetingId, path, type);
            accessor.addImage(path, bitmap);
            accessor.addImage(path2, thumb);
            accessor.addFile(file);
        }
        else {
            Toast.makeText(getContext(), "Could not retrieve image", Toast.LENGTH_LONG).show();
        }
    }


    public void configureFab(View root) {
        fab = root.findViewById(R.id.fab_photos);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deletePossible) {
                    accessor.removeFile(selectedFile);
                    accessor.deleteImage(selectedFile.path);
                    accessor.deleteImage("thumb/" + selectedFile.path);
                    deletePossible = false;
                    selectedFile = null;
                    selectedView.setPadding(0,0,0,0);
                    selectedView = null;
                    fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_add_black_24dp));
                }
                else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQUEST_IMAGE_UPLOAD);
                }
            }
        });
    }

    public void configureListView() {
        gridView.setAdapter(this.adapter);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        this.meetingId = ((MeetingDetails)getActivity()).getUid();
        View root = inflater.inflate(R.layout.fragment_photos, container, false);
        configureFab(root);

        adapter = new PhotosFragment.PhotoAdapter(root.getContext(),
                R.layout.meetings_list_elem, new ArrayList<File>());
        gridView = root.findViewById(R.id.photos_list);
        configureListView();

        accessor = FirebaseAccessor2.getInstance(getContext());
        accessor.listFiles(meetingId, type, adapter);

        return root;
    }
}