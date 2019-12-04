package com.example.stenoscribe;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;

public class PhotoModel {
    private Context context;

    public PhotoModel(Context context) {
        this.context = context;
    }

    public File createImageFile(String path) {
        File image = null;
        File storageDir;

        try {
            storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            image = java.io.File.createTempFile(
                    path,
                    ".jpg",
                    storageDir
            );

        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Can not capture image", Toast.LENGTH_LONG).show();
        }
        return image;
    }

}
