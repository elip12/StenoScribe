package com.example.stenoscribe;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Base64;
import android.view.MenuItem;
import android.widget.TextView;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.content.Intent;
import android.provider.MediaStore;
import android.graphics.Bitmap;
import android.net.Uri;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.example.stenoscribe.db.File;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

public class AddPhotosActivity extends AppCompatActivity  {
    FloatingActionButton cam;
    FloatingActionButton gallery;
    private FirebaseAccessor2 accessor;
    private String type = "photo";
    private String meetingId;
    private File file;
    private final int REQUEST_IMAGE_CAPTURE = 100;
    private final int REQUEST_IMAGE_UPLOAD = 101;
    private final int WRITE_REQUEST_CODE = 102;
    String uid;
    String path;
    Uri photoUri;

    public void configureActionBar(String title) {
        TextView actionBarText;
        final ViewGroup actionBarLayout;
        final ActionBar actionBar;

        actionBarLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.action_bar_noneditable, null);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_24dp);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarLayout);

        actionBarText = findViewById(R.id.action_bar_textview);
        actionBarText.setText(title);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case WRITE_REQUEST_CODE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    uid = UUID.randomUUID().toString();
                    path = meetingId + "/" + uid;
                    PhotoModel pm = new PhotoModel(getApplicationContext());
                    java.io.File image = pm.createImageFile(uid);

                    if (image != null) {
                        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                                "com.example.android.fileprovider",
                                image);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        AddPhotosActivity.this.photoUri = photoURI;

                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                        }
                }
                else{
                    //Denied.
                }
                break;
                }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photos);

        configureActionBar("New Photo");

        cam = findViewById(R.id.camera);
        gallery = findViewById(R.id.gallery);
        this.meetingId = getIntent().getStringExtra("meetingId");
        accessor = FirebaseAccessor2.getInstance(getApplicationContext());


        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions, WRITE_REQUEST_CODE);

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                uid = UUID.randomUUID().toString();
                path = meetingId + "/" + uid;
                PhotoModel pm = new PhotoModel(getApplicationContext());
                java.io.File image = pm.createImageFile(uid);

                if (image != null) {
                    Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                            "com.example.android.fileprovider",
                            image);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    AddPhotosActivity.this.photoUri = photoURI;

                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_UPLOAD);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if(requestCode == REQUEST_IMAGE_UPLOAD && data != null && resultCode != RESULT_CANCELED){
            Uri imageUri = data.getData();
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), imageUri);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed to get image", Toast.LENGTH_LONG).show();
                return;
            }
            String uid = UUID.randomUUID().toString();
            String path = meetingId + "/" + uid + ".jpg";
            file = new File(uid, meetingId, path, type);
            accessor.addImage(path, bitmap);
            accessor.addFile(file);
        }

        else if(requestCode == REQUEST_IMAGE_CAPTURE && data != null && resultCode != RESULT_CANCELED){
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), photoUri);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed to get image", Toast.LENGTH_LONG).show();
                return;
            }

            path += ".jpg";
            file = new File(uid, meetingId, path, type);
            accessor.addImage(path, bitmap);
            accessor.addFile(file);
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