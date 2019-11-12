package com.example.stenoscribe.ui.documents;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.stenoscribe.R;
import com.example.stenoscribe.db.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;

class DocumentAdapter extends ArrayAdapter {
    // context check to stop crashing
    private Context current_context;
    // requires a new array since it cant be passed in directly
    private ArrayList<File> files;
    // constructor
    public DocumentAdapter(Context context, ArrayList<File> file_list){
        super(context, 0 , file_list);
        current_context = context;
        files = file_list;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View current_view = convertView;
        // this stops it from crashing.
        if(current_view == null) {
            current_view = LayoutInflater.from(current_context).inflate(R.layout.fragment_document_layout,parent,false);
        }
        // grabs a book from the library to render
        File current_file = files.get(position);
        TextView file_title = (TextView) current_view.findViewById(R.id.document_title);
        file_title.setText("OWO");
        return current_view;
    }
}
