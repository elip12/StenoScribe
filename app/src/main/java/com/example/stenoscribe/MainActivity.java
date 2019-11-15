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
import android.widget.ListView;
import android.widget.TextView;

import com.example.stenoscribe.db.AppDatabase;
import com.example.stenoscribe.db.FileAccessor;
import com.example.stenoscribe.db.Meeting;
import com.example.stenoscribe.db.MeetingAccessor;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private AppDatabase db;
    private MeetingAccessor accessor;
    private FirebaseAccessor firebaseAccessor;
    private int lastMeetingUID = 0;
    private List<Meeting> meetings;
    private FloatingActionButton fab;
    private MeetingAdapter adapter;
    private ListView listView;
    FirebaseUser user;

    public class MeetingAdapter extends ArrayAdapter<Meeting> {
        private List<Meeting> items;

        private MeetingAdapter(Context context, int rId, List<Meeting> items) {
            super(context, rId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            final Meeting item;
            final TextView title;
            final TextView date;

            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.meetings_list_elem, null);
            }
            item = items.get(position);
            if (item != null) {
                title = v.findViewById(R.id.viewMeetingsListElemTitle);
                date = v.findViewById(R.id.viewMeetingsListElemDate);
                title.setText(item.title);
                date.setText(item.date);
            }
            return v;
        }
    }

    public void setToolbarTitle() {
        final Toolbar toolbar;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.saved_meetings);
    }

    public void configureFab() {
        this.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Meeting meeting;
                final Intent intent;

                meeting = new Meeting(MainActivity.this.lastMeetingUID + 1);
                intent = new Intent(view.getContext(), MeetingDetails.class);
                intent.putExtra("uid", meeting.uid);
                accessor.insertMeeting(meeting, adapter);
                view.getContext().startActivity(intent);
            }
        });
    }

    public void syncFirebaseToLocal() {
        this.firebaseAccessor.updateDB(new int[1]);
    }

    public void syncLocalToFirebase() {
        this.firebaseAccessor.updateFB();
    }

    public void configurePullToRefresh() {
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pull_to_refresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                meetings = accessor.listMeetings();
                if (meetings.size() > 0)
                    lastMeetingUID = meetings.get(0).uid;
                adapter.clear();
                adapter.addAll(meetings);
                adapter.notifyDataSetChanged();
                pullToRefresh.setRefreshing(false);
            }
        });
    }

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setToolbarTitle();

        // delete database on restart
        getApplicationContext().deleteDatabase("stenoscribe");

        this.db = AppDatabase.getDatabase(getApplicationContext());
        this.accessor = new MeetingAccessor(this.db);
        this.firebaseAccessor = new FirebaseAccessor(this.accessor, new FileAccessor(this.db));
        this.fab = findViewById(R.id.fab);
        this.configureFab();
        this.configurePullToRefresh();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void configureListView() {
        this.listView.setAdapter(this.adapter);
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?>adapter, View v, int position, long id){
                final Meeting item;
                final Intent intent;

                item = (Meeting) adapter.getItemAtPosition(position);
                intent = new Intent(getApplicationContext(), MeetingDetails.class);
                intent.putExtra("uid", item.uid);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.db == null) {
            this.db = AppDatabase.getDatabase(getApplicationContext());
        }
        if (this.accessor == null) {
            this.accessor = new MeetingAccessor(this.db);
        }
        this.meetings = accessor.listMeetings();
        if (this.meetings.size() > 0)
            this.lastMeetingUID = this.meetings.get(0).uid;
        this.adapter = new MeetingAdapter(MainActivity.this, R.layout.meetings_list_elem, meetings);
        this.listView = findViewById(R.id.meetings_list);
        this.configureListView();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.sync_local_firebase) {
            syncLocalToFirebase();
            return true;
        }
        else if (id == R.id.sync_firebase_local) {
            syncFirebaseToLocal();
            return true;
        }
        else if (id == R.id.logout) {
            logout();
        }

        return super.onOptionsItemSelected(item);
    }
}
