package com.example.stenoscribe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
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
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.example.stenoscribe.db.FileOperator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddPhotosActivity extends AppCompatActivity {
    TextView text;
    FloatingActionButton cam;
    FloatingActionButton gallery;
    ImageView cameraImage;
    ImageView galleryImage;

    private Uri mImageCaptureUri;

    private FileOperator io;

    String imageFilePath;

    private boolean isTakenFromCamera;

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

        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Use camera to add picture", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, 0);

//                takePictureIntent.putExtra( MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
//
//                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                    startActivityForResult(takePictureIntent, 0);
//
//                    File imageFile = null;
//                    try {
//                        imageFile = getPictureFile();
//                    } catch (IOException ex) {
//                        Toast.makeText(AddPhotosActivity.this, "Photo file can't be created, please try again", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    if (imageFile != null) {
//                        Uri photoURI = FileProvider.getUriForFile(AddPhotosActivity.this,
//                                "com.stenoscribe.android.fileprovider", imageFile);
//                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                        startActivityForResult(takePictureIntent, 0);
//                    }
//                }
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Import photo from gallery", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        saveInternalStorage(bitmap);
        cameraImage.setImageBitmap(bitmap);

        Uri selectedImage = data.getData();
//        FileOperator filepath = io.child("Photo").child(selectedImage.getLastPathSegment());
//        filepath.putFile(selectedImage).addSucessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot takeSnapshot) {
//                Toast.makeText(AddPhotosActivity.this, "Uploading finished", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private File getPictureFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureFile = "Stenoscribe_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(pictureFile,  ".jpg", storageDir);
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    private void addToGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imageFilePath);
        Uri picUri = Uri.fromFile(f);
        galleryIntent.setData(picUri);
        this.sendBroadcast(galleryIntent);
    }
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

    private String saveInternalStorage(Bitmap bitmapImage){
        io.getApplicationContext();
        File directory = io.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }
}