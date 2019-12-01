package com.example.stenoscribe.db;

import android.content.Context;
import android.util.Log;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import androidx.appcompat.app.AppCompatActivity;

// deprecated. Keeping for records
public class FileOperator extends AppCompatActivity{
    private Context context;
    private final String tag = "FILEOPERATOR";

    public FileOperator(Context context){
        super();
        this.context = context;
    }

    public void store(String fname, String data) {
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(fname, Context.MODE_APPEND | Context.MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();
        } catch(IOException e) {
            Log.e(tag,"Error saving book");
        }
    }

    public String load(String fname) {
        FileInputStream fis;
        InputStreamReader isr;
        BufferedReader reader;
        StringBuilder builder;
        String data;
        String line;

        try {
            builder = new StringBuilder();
            fis = context.openFileInput(fname);
            isr = new InputStreamReader(fis);
            reader = new BufferedReader(isr);
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            data = builder.toString();
            fis.close();
        } catch(Exception e) {
            data = "No content";
            Log.e(tag, "Error reading file");
        }
        return data;
    }
}
