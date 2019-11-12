package com.example.stenoscribe.ui.documents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.stenoscribe.R;
import com.example.stenoscribe.db.File;

import java.util.ArrayList;
import java.util.List;

import static androidx.core.content.ContextCompat.getSystemService;

public class DocumentsViewModel extends ViewModel {
    private MutableLiveData<String> mText;
    public DocumentsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is documents fragment");
    }
    private Context current_context;
    private ArrayList<File> Document_list;
    public class DocumentAdapter extends ArrayAdapter<File> {
        private List<File> Documents;
        private DocumentAdapter(Context context,int rId, ArrayList<File> Documents) {
            super(context, rId, Documents);
            current_context = context;
            Document_list = Documents;
        }
        @Override
        public View getView(int position, View v, ViewGroup parent) {
            final TextView title;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)current_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.fragment_document_layout, null);
            }
            File Document = Document_list.get(position);
            if (Document != null) {
                title = v.findViewById(R.id.document_title);
                title.setText(Document.type);
            }
            return v;
        }
    }
    public LiveData<String> getText() {
        return mText;
    }
}