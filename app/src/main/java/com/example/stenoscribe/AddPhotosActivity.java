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
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
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
import android.media.*;
import android.media.MediaPlayer;
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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

public class AddPhotosActivity extends AppCompatActivity  {
    TextView text;
    FloatingActionButton cam;
    FloatingActionButton gallery;

    private AppDatabase db;
    private FileAccessor accessor;

    private int lastPhotoId;
    private String type = "photo";
    private String meetingId;
    private File file;
    private int uid;

    ImageView images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photos);

        text = findViewById(R.id.textView);
        cam = findViewById(R.id.camera);
        gallery = findViewById(R.id.gallery);
        this.meetingId = getIntent().getExtras().getString("meetingId");
        this.lastPhotoId = getIntent().getExtras().getInt("lastPhotoId");
        images = findViewById(R.id.imageView);

        this.db = AppDatabase.getDatabase(getApplicationContext());
        this.accessor = new FileAccessor(this.db);

        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
                startActivityForResult(takePictureIntent, 0);
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //intent.setType("image/*");
                startActivityForResult(intent, 101);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if(requestCode == 101 && data != null && resultCode != RESULT_CANCELED){
            Uri imageUri = data.getData();
            Bitmap bitmap = decodeUriToBitmap(AddPhotosActivity.this, imageUri);
            String bm = BitMapToString(bitmap);

            uid = this.lastPhotoId +1;

            file = new File(uid, meetingId, bm, type);
            if(file == null){
                Toast.makeText(AddPhotosActivity.this, "File is null", Toast.LENGTH_LONG).show();
            }
            else{
                accessor.insertFileAsync(file);
            }
        }

        else if(requestCode == 0 && data != null && resultCode != RESULT_CANCELED){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            //images.setImageBitmap(bitmap);
            String bm = BitMapToString(bitmap);
            uid = this.lastPhotoId +1;
            file = new File(uid, meetingId, bm, type);

            if(file == null){
                Toast.makeText(AddPhotosActivity.this, "File is null", Toast.LENGTH_LONG).show();
            }
            else{
                accessor.insertFileAsync(file);
            }
        }

        else{
            //super.onActivityResult(requestCode, resultCode, data);
            Toast.makeText(AddPhotosActivity.this, "Can not capture image", Toast.LENGTH_LONG).show();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,50, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public static Bitmap decodeUriToBitmap(Context mContext, Uri sendUri) {
        Bitmap getBitmap = null;
        try {
            InputStream image_stream;
            try {
                image_stream = mContext.getContentResolver().openInputStream(sendUri);
                getBitmap = BitmapFactory.decodeStream(image_stream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getBitmap;
    }
}