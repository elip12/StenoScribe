package com.example.stenoscribe.ui.documents;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.stenoscribe.FirebaseAccessor2;
import com.example.stenoscribe.R;
import com.example.stenoscribe.ReadTranscriptionActivity;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View current_view = convertView;
        if(current_view == null) {
            LayoutInflater vi = (LayoutInflater)current_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            current_view = vi.inflate(R.layout.meetings_list_elem, null);
        }
        final File current_file = files.get(position);
        final TextView file_date = current_view.findViewById(R.id.viewMeetingsListElemDate);
        final TextView file_title = current_view.findViewById(R.id.viewMeetingsListElemTitle);
        final ImageButton button;

        String urlandname = current_file.path;
        String[] parsed = urlandname.split(" ////// ",2);
        String name = parsed[1];

        file_title.setText(name);
        file_date.setText(current_file.datetime);

        button = current_view.findViewById(R.id.button);
        button.setFocusable(false);
        button.setVisibility(View.INVISIBLE);

        current_view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                button.setVisibility(View.VISIBLE);
                return true;
            }
        });
        current_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button.getVisibility() == View.VISIBLE)
                    button.setVisibility(View.INVISIBLE);
                else {
                    String urlandname = current_file.path;
                    String[] parsed = urlandname.split(" ////// ");
                    String url = parsed[0];
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    getContext().startActivity(browserIntent);
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAccessor2 accessor =  FirebaseAccessor2.getInstance(getContext());
                accessor.removeFile(current_file);
            }
        });

        return current_view;
    }
}