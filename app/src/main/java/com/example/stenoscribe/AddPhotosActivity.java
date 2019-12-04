package com.example.stenoscribe;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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
    TextView text;
    FloatingActionButton cam;
    FloatingActionButton gallery;
    private FirebaseAccessor2 accessor;
    private int lastPhotoId;
    private String type = "photo";
    private String meetingId;
    private File file;
    private int uid;

    ImageView images;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photos);

        configureActionBar("New Photo");

        text = findViewById(R.id.textView);
        cam = findViewById(R.id.camera);
        gallery = findViewById(R.id.gallery);
        this.meetingId = getIntent().getExtras().getString("meetingId");
        images = findViewById(R.id.imageView);
        accessor = FirebaseAccessor2.getInstance(getApplicationContext());


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

            String uid = UUID.randomUUID().toString();
            file = new File(uid, meetingId, bm, type);
            if(file == null){
                Toast.makeText(AddPhotosActivity.this, "File is null", Toast.LENGTH_LONG).show();
            }
            else{
                accessor.addFile(file);
            }
        }

        else if(requestCode == 0 && data != null && resultCode != RESULT_CANCELED){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            //images.setImageBitmap(bitmap);
            String bm = BitMapToString(bitmap);
            String uid = UUID.randomUUID().toString();
            file = new File(uid, meetingId, bm, type);

            if(file == null){
                Toast.makeText(AddPhotosActivity.this, "File is null", Toast.LENGTH_LONG).show();
            }
            else{
                accessor.addFile(file);
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