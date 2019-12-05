package com.example.stenoscribe;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.stenoscribe.db.Meeting;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private FirebaseAccessor2 firebaseAccessor;
    private FloatingActionButton fab;
    private MeetingAdapter adapter;
    private ListView listView;
    private FirebaseUser user;

    // Meeting list adapter for displaying custom meeting elements.
    public class MeetingAdapter extends ArrayAdapter<Meeting> {
        private List<Meeting> items;

        private MeetingAdapter(Context context, int rId, List<Meeting> items) {
            super(context, rId, items);
            this.items = items;
        }

        // Displays the meeting title and date
        @Override
        public @NonNull View getView(int position, View v, ViewGroup parent) {
            final Meeting item;
            final TextView title;
            final TextView date;
            final ImageButton button;

            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.meetings_list_elem, null);
            }
            item = items.get(position);
            if (item != null) {
                title = v.findViewById(R.id.viewMeetingsListElemTitle);
                date = v.findViewById(R.id.viewMeetingsListElemDate);
                button = v.findViewById(R.id.button);
                button.setFocusable(false);
                title.setText(item.title);
                date.setText(item.date);
                button.setVisibility(View.INVISIBLE);

                v.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                            button.setVisibility(View.VISIBLE);
                            return true;
                    }
                });
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (button.getVisibility() == View.VISIBLE)
                            button.setVisibility(View.INVISIBLE);
                        else {
                            final Intent intent;

                            intent = new Intent(getApplicationContext(), MeetingDetails.class);
                            intent.putExtra("uid", item.uid);
                            intent.putExtra("title", item.title);
                            startActivity(intent);
                        }
                    }
                });
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        firebaseAccessor.deleteMeeting(item);
                    }
                });
            }
            return v;
        }
    }

    // sets the action bar title
    public void setToolbarTitle() {
        final Toolbar toolbar;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.saved_meetings);
    }

    // configures the floating action button.
    // on click, creates a meeting, upserts it to FB,
    // then starts that meeting's meeting details activity
    public void configureFab() {
        this.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Meeting meeting;
                final Intent intent;

                String uid = UUID.randomUUID().toString();
                meeting = new Meeting(uid, user.getEmail());
                intent = new Intent(view.getContext(), MeetingDetails.class);
                intent.putExtra("uid", meeting.uid);
                intent.putExtra("title", meeting.title);
                firebaseAccessor.createMeeting(meeting);
                view.getContext().startActivity(intent);
            }
        });
    }

    // firebase UI logout
    public void logout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    }
                });
    }

    // configures fab, pull to refresh, and listview, and gets the current firebase user
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setToolbarTitle();
        this.fab = findViewById(R.id.fab);
        this.configureFab();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    // sets the listview to have onclick listeners for clicking on meetings
    public void configureListView() {
        listView.setAdapter(this.adapter);
    }

    // reinstantiates the db and acccessor if need be, and refreshes the listview
    @Override
    protected void onResume() {
        super.onResume();
        firebaseAccessor = FirebaseAccessor2.getInstance(getApplicationContext());
        adapter = new MeetingAdapter(MainActivity.this,
                R.layout.meetings_list_elem, new ArrayList<Meeting>());
        listView = findViewById(R.id.meetings_list);
        configureListView();
        firebaseAccessor.listMeetings(adapter);
        adapter.notifyDataSetChanged();
    }

    // options menu in upper right corner
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // options menu options
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.logout) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }
}
