package com.example.stenoscribe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.TextView;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import com.example.stenoscribe.ui.photos.PhotosFragment;
import com.example.stenoscribe.MeetingDetails;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.stenoscribe.R;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.snackbar.Snackbar;
import android.widget.ImageView;
import android.content.Intent;
import android.provider.MediaStore;
import android.graphics.Bitmap;
import android.net.Uri;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.widget.Toast;
import android.net.Uri;
import com.example.stenoscribe.db.AppDatabase;
import com.example.stenoscribe.db.File;
import com.example.stenoscribe.db.FileAccessor;
import com.example.stenoscribe.db.FileOperator;

import java.io.FileOutputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

public class AddPhotosActivity extends AppCompatActivity {
    TextView text;
    FloatingActionButton cam;
    FloatingActionButton gallery;
    ImageView cameraImage;
    ImageView galleryImage;

    private FileOperator io;

    String imageFilePath;

    String ImageDecode;

    String[] FILE;

    private FileAccessor accessor;
    //private PhotoAdapter adapter;
    private List<File> photos;

    //private PhotoAdapter adapter2;

    private int lastPhotoId = 0;
    private String type = "photo";
    private String meetingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photos);

        text = findViewById(R.id.textView);
        cam = findViewById(R.id.camera);
        gallery = findViewById(R.id.gallery);
        cameraImage = findViewById(R.id.cameraIV);
        galleryImage = findViewById(R.id.galleryIV);
        this.io = new FileOperator(getApplicationContext());
        this.meetingId = getIntent().getExtras().getString("meetingId");;

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
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //intent.setType("image/*");
                startActivityForResult(intent, 101);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101 && resultCode == RESULT_OK && data != null){
            Uri URI = data.getData();

            String[] FILE = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(URI, FILE, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(FILE[0]);
            ImageDecode = cursor.getString(columnIndex);
            cursor.close();

            int uid = this.lastPhotoId+1;
            File file = new File(uid, meetingId, ImageDecode, type);
            accessor.insertFileAsync(file);
            //galleryImage.setImageBitmap(BitmapFactory.decodeFile(ImageDecode));

//            String path = getRealPathFromURI(uri);
//            String name = getFileName(uri);

//            try {
//                saveGalInternalStorage(name, path);
//            }catch (FileNotFoundException e){
//                e.printStackTrace();
//            }
        }

        else {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            //saveCamInternalStorage(bitmap);
            int uid = this.lastPhotoId+1;
            String bm = bitmap.toString();
            File file = new File(uid, meetingId, bm, type);
            // create FileAcessor obj;
            //replace :
            //cameraImage.setImageBitmap(bitmap);
            // with
            accessor.insertFileAsync(file);
        }
    }

//    private String saveCamInternalStorage(Bitmap bitmapImage){
//        io.getApplicationContext();
//        //ContextWrapper cw = new ContextWrapper(getApplicationContext());
//        File directory = io.getDir("imageDir", Context.MODE_PRIVATE);
//        File mypath=new File(directory,"profile.jpg");
//
//        FileOutputStream fos = null;
//
//        try {
//            fos = new FileOutputStream(mypath);
//            // Use the compress method on the BitMap object to write image to the OutputStream
//            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return directory.getAbsolutePath();
//    }
}