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
import java.util.Arrays;
import java.util.List;

class DocumentAdapter extends ArrayAdapter<File> {
    // context check to stop crashing
    private Context current_context;
    // requires a new array since it cant be passed in directly
    private List<File> files;
    // constructor
    public DocumentAdapter(Context context, int rId, List<File> file_list){
        super(context, rId, file_list);
        current_context = context;
        files = file_list;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View current_view = convertView;
        // this stops it from crashing.
        if(current_view == null) {
            LayoutInflater vi = (LayoutInflater)current_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            current_view = vi.inflate(R.layout.meetings_list_elem, null);
        }
        File current_file = files.get(position);
        TextView file_title = (TextView) current_view.findViewById(R.id.viewMeetingsListElemTitle);
        String urlandname = current_file.path;
        System.out.println(urlandname);
        String[] parsed = urlandname.split(" ////// ",2);
        String name = parsed[1];
        System.out.println(parsed[1]);
        System.out.println(Arrays.toString(parsed));
        file_title.setText(name);
        TextView file_date = (TextView) current_view.findViewById(R.id.viewMeetingsListElemDate);
        file_date.setText(current_file.datetime);
        return current_view;
    }
}