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
import com.example.stenoscribe.db.Meeting;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public class MeetingAdapter extends ArrayAdapter<Meeting> {
        private List<Meeting> items;

        private MeetingAdapter(Context context, int rId, List<Meeting> items) {
            super(context, rId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            Meeting item;
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

    private int lastMeetingUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.saved_meetings);

        //getApplicationContext().deleteDatabase("stenoscribe");

        final AppDatabase db = AppDatabase.getDatabase(getApplicationContext());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Meeting meeting = new Meeting(lastMeetingUID + 1);
                Log.i("MAIN_DB", "" + meeting.uid);
                Log.i("MAIN_DB", meeting.title);
                Log.i("MAIN_DB", meeting.date);

                Intent intent = new Intent(view.getContext(), MeetingDetails.class);
                intent.putExtra("uid", meeting.uid);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        db.meetingDao().insertMeeting(meeting);
                    }
                });
                thread.start();
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        final AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Meeting> meetings = db.meetingDao().listMeetings();
                lastMeetingUID = meetings.size();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MeetingAdapter adapter = new MeetingAdapter(MainActivity.this, R.layout.meetings_list_elem, meetings);
                        ListView listView = findViewById(R.id.meetings_list);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                            @Override
                            public void onItemClick(AdapterView<?>adapter, View v, int position, long id){
                                Meeting item;
                                Intent intent;

                                item = (Meeting) adapter.getItemAtPosition(position);
                                intent = new Intent(getApplicationContext(), MeetingDetails.class);
                                intent.putExtra("uid", item.uid);
                                startActivity(intent);
                            }
                        });
                    }
                });
            }
        });
        thread.start();


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

        return super.onOptionsItemSelected(item);
    }
}
