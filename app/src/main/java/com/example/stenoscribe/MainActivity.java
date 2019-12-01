package com.example.stenoscribe;

import android.content.Context;
import android.os.Bundle;
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
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private AppDatabase db;
    private MeetingAccessor accessor;
    private FirebaseAccessor firebaseAccessor;
    private List<Meeting> meetings;
    private FloatingActionButton fab;
    private MeetingAdapter adapter;
    private ListView listView;
    FirebaseUser user;

    // Meeting list adapter for displaying custom meeting elements.
    public class MeetingAdapter extends ArrayAdapter<Meeting> {
        private List<Meeting> items;

        private MeetingAdapter(Context context, int rId, List<Meeting> items) {
            super(context, rId, items);
            this.items = items;
        }

        // Displays the meeting title and date
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

    // sets the action bar title
    public void setToolbarTitle() {
        final Toolbar toolbar;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.saved_meetings);
    }

    // configures the floating action button.
    // on click, creates a meeting, inserts it into the db,
    // then starts that meeting' s meeting details activity
    public void configureFab() {
        this.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Meeting meeting;
                final Intent intent;

                String uid = UUID.randomUUID().toString();
                meeting = new Meeting(uid);
                intent = new Intent(view.getContext(), MeetingDetails.class);
                intent.putExtra("uid", meeting.uid);
                accessor.insertMeetingAsync(meeting);
                view.getContext().startActivity(intent);
            }
        });
    }

    // calls firebaseAccessor method to sync
    public void syncFirebaseToLocal() {
        this.firebaseAccessor.updateDB();
    }

    // calls firebaseAccessor method to sync
    public void syncLocalToFirebase() {
        this.firebaseAccessor.updateFB();
    }

    // configures the pull to refresh widget. refreshes meetings from db to listview
    public void configurePullToRefresh() {
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pull_to_refresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                meetings = accessor.listMeetings();
                adapter.clear();
                adapter.addAll(meetings);
                adapter.notifyDataSetChanged();
                pullToRefresh.setRefreshing(false);
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

    // gets db instance, creates db accessor (for easy db operations),
    // configures fab, pull to refresh, and listview, and gets the current firebase user
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setToolbarTitle();

        // delete database on restart
        getApplicationContext().deleteDatabase("stenoscribe");

        this.db = AppDatabase.getDatabase(getApplicationContext());
        this.accessor = new MeetingAccessor(this.db);
        this.firebaseAccessor = FirebaseAccessor.getInstance(getApplicationContext(), this.accessor, new FileAccessor(this.db));
        this.fab = findViewById(R.id.fab);
        this.configureFab();
        this.configurePullToRefresh();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    // sets the listview to have onclick listeners for clicking on meetings
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

    // reinstantiates the db and acccessor if need be, and refreshes the listview
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
        this.adapter = new MeetingAdapter(MainActivity.this, R.layout.meetings_list_elem, meetings);
        this.listView = findViewById(R.id.meetings_list);
        this.configureListView();
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

        if (id == R.id.sync_local_firebase) {
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
