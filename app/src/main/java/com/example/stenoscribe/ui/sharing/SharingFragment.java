package com.example.stenoscribe.ui.sharing;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.stenoscribe.FirebaseAccessor2;
import com.example.stenoscribe.MeetingDetails;
import com.example.stenoscribe.R;

import java.util.ArrayList;
import java.util.List;

public class SharingFragment extends Fragment {
    private SharingAdapter adapter;
    private FirebaseAccessor2 firebaseAccessor;
    private List<String> users;
    private ListView listView;
    private String meetingId;
    private String TAG = "SHARINGFRAGMENT";

    public class SharingAdapter extends ArrayAdapter<String> {
        private List<String> items;

        private SharingAdapter(Context context, int rId, List<String> items) {
            super(context, rId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            final String item;
            final TextView email;
            final ImageButton button;

            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.sharing_list_element, null);
            }
            item = items.get(position);
            if (item != null) {
                email = v.findViewById(R.id.email);
                email.setText(item);
                button = v.findViewById(R.id.button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        firebaseAccessor.shareWith(meetingId, item, true);
                    }
                });
            }
            return v;
        }
    }

    public void configureListView() {
        this.listView.setAdapter(this.adapter);
    }

    public void configureShareText(View root) {
        final EditText newUser = root.findViewById(R.id.new_user);
        final Button addUser = root.findViewById(R.id.add_user);
        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = newUser.getText().toString();
                firebaseAccessor.shareWith(meetingId, email, false);
                newUser.setText("");
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                v.clearFocus();
            }
        });
        newUser.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // hide keyboard and clear focus
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    v.clearFocus();
                    return true;
                }
                return false;
            }
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sharing, container, false);

        meetingId = ((MeetingDetails)getActivity()).getUid();
        adapter = new SharingAdapter(root.getContext(), R.layout.meetings_list_elem, new ArrayList<String>());
        listView = root.findViewById(R.id.sharing_list);
        configureListView();
        firebaseAccessor = FirebaseAccessor2.getInstance(getContext());
        if (firebaseAccessor == null)
            Log.d(TAG, "firebase accessor is null");
        firebaseAccessor.listUsers(meetingId, adapter);
        configureShareText(root);
        return root;
    }
}