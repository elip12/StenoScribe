package com.example.stenoscribe.ui.documents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stenoscribe.R;
import com.example.stenoscribe.db.File;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

class DocumentAdapter extends ArrayAdapter {
    // context check to stop crashing
    private Context current_context;
    // requires a new array since it cant be passed in directly
    private List<File> files;
    // constructor
    public DocumentAdapter(Context context, List<File> file_list){
        super(context, 0, file_list);
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
        File current_file = files.get(position);
        TextView file_title = (TextView) current_view.findViewById(R.id.document_title);
        file_title.setText("Document");
        TextView file_date = (TextView) current_view.findViewById(R.id.document_date);
        file_date.setText(current_file.getDate());
        return current_view;
    }
}
