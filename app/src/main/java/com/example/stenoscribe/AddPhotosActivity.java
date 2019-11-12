package com.example.stenoscribe;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Button;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.stenoscribe.R;
import android.view.View;
import com.google.android.material.snackbar.Snackbar;
import android.widget.ImageView;
import android.content.Intent;
import android.provider.MediaStore;
import android.graphics.Bitmap;
import android.net.Uri;
import android.database.Cursor;
import android.graphics.BitmapFactory;

import com.example.stenoscribe.db.FileOperator;

public class AddPhotosActivity extends AppCompatActivity {
    TextView text;
    FloatingActionButton cam;
    FloatingActionButton gallery;
    ImageView cameraImage;
    ImageView galleryImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photos);

        text = findViewById(R.id.textView);
        cam = findViewById(R.id.camera);
        gallery = findViewById(R.id.gallery);
        cameraImage = findViewById(R.id.cameraIV);
        galleryImage = findViewById(R.id.galleryIV);

        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Use camera to add picture", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, 0);
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Import photo from gallery", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        cameraImage.setImageBitmap(bitmap);

//        Uri selectedImage = data.getData();
//        String[] filePathColumn = { MediaStore.Images.Media.DATA };
//
//        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
//        cursor.moveToFirst();
//
//        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//        String picturePath = cursor.getString(columnIndex);
//        cursor.close();
//
//        galleryImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
    }
}