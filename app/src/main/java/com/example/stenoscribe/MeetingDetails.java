package com.example.stenoscribe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.stenoscribe.db.AppDatabase;
import com.example.stenoscribe.db.Meeting;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MeetingDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_details);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_recordings, R.id.navigation_photos, R.id.navigation_documents)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        final AppDatabase db = AppDatabase.getDatabase(getApplicationContext());

        Intent intent = getIntent();
        final int uid = intent.getIntExtra("uid", 0);

        // Inflate your custom layout
        final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(
                R.layout.action_bar,
                null);

        // Set up your ActionBar
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarLayout);
        // you can create listener over the EditText
        final EditText actionBarText = findViewById(R.id.action_bar_text);
        actionBarText.clearFocus();
        actionBarText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {}

            @Override
            public void onTextChanged(CharSequence s, int st, int c, int a) {}

            @Override
            public void afterTextChanged(Editable s) {
                String newTitle = s.toString();
                if (newTitle.endsWith("\n")) {
                    newTitle = newTitle.replace(newTitle.substring(newTitle.length() - 1), "");
                    actionBarText.setText(newTitle);
                    actionBarText.clearFocus();
                    // db store
                    Log.i("MEETINGDETAILS", newTitle);
                }
            }
        });

        // Create a background thread to load the meeting
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final Meeting meeting = db.meetingDao().getMeeting(uid);
                Log.i("MAIN_DB", "" + meeting.uid);
                Log.i("MAIN_DB", meeting.title);

                // UI should only be updated by main thread
                MeetingDetails.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        actionBarText.setText(meeting.title);
                    }
                });
            }
        });
        thread.start();



    }

}
