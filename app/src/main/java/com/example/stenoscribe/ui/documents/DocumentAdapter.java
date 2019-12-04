package com.example.stenoscribe.ui.documents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.stenoscribe.R;
import com.example.stenoscribe.db.File;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

class DocumentAdapter extends ArrayAdapter<File> {
    private Context current_context;
    private List<File> files;

    public DocumentAdapter(Context context, int rId, List<File> file_list){
        super(context, rId, file_list);
        current_context = context;
        files = file_list;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View current_view = convertView;
        if(current_view == null) {
            LayoutInflater vi = (LayoutInflater)current_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            current_view = vi.inflate(R.layout.meetings_list_elem, null);
        }
        File current_file = files.get(position);
        TextView file_date = current_view.findViewById(R.id.viewMeetingsListElemDate);
        TextView file_title = current_view.findViewById(R.id.viewMeetingsListElemTitle);

        String urlandname = current_file.path;
        String[] parsed = urlandname.split(" ////// ",2);
        String name = parsed[1];

        file_title.setText(name);
        file_date.setText(current_file.datetime);

        return current_view;
    }
}