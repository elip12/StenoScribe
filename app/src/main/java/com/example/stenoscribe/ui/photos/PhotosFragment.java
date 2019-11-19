package com.example.stenoscribe.ui.photos;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.stenoscribe.AddPhotosActivity;
import com.example.stenoscribe.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.content.Intent;

public class PhotosFragment extends Fragment {
    private ImageView image;

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

        image = root.findViewById(R.id.cameraIV);

        FloatingActionButton fab = root.findViewById(R.id.fab_photos);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "This lets you add a photo", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                Intent intent = new Intent(view.getContext(), AddPhotosActivity.class);
                view.getContext().startActivity(intent);
            }
        });

        return root;
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//        image.setImageBitmap(bitmap);
//
////        Uri selectedImage = data.getData();
////        FileOperator filepath = io.child("Photo").child(selectedImage.getLastPathSegment());
////        filepath.putFile(selectedImage).addSucessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
////            @Override
////            public void onSuccess(UploadTask.TaskSnapshot takeSnapshot) {
////                Toast.makeText(AddPhotosActivity.this, "Uploading finished", Toast.LENGTH_SHORT).show();
////            }
////        });
//    }
}