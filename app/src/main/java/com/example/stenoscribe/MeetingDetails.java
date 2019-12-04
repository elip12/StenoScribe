package com.example.stenoscribe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.stenoscribe.db.Meeting;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

public class MeetingDetails extends AppCompatActivity {

    private EditText actionBarText;
    private String uid;
    private String title;
    private FirebaseAccessor2 accessor;

    // method allowing fragments to get the uid of their parent meeting
    public String getUid() {
        return this.uid;
    }

    public String getMeetingTitle() {
        return this.title;
    }

    // Make actionbar title editable, and show back button. When user finishes editing,
    // save title to db
    public EditText configureActionBar() {
        final ViewGroup actionBarLayout;
        final ActionBar actionBar;
        final EditText actionBarText;

        actionBarLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.action_bar, null);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_24dp);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarLayout);

        actionBarText = findViewById(R.id.action_bar_text);
        actionBarText.clearFocus();
        actionBarText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // hide keyboard and clear focus
                    InputMethodManager imm = (InputMethodManager) MeetingDetails.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    v.clearFocus();

                    // update meeting title in firebase
                    title = v.getText().toString();
                    accessor.updateMeeting(uid, "title", title);
                    return true;
                }
                return false;
            }
        });
        return actionBarText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Intent intent;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_details);

        // create bottom navigation tab view
        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        // get data from intent
        intent = getIntent();
        this.uid = intent.getStringExtra("uid");
        this.title = intent.getStringExtra("title");
    }

    // displays meeting title
    @Override
    protected void onResume() {
        super.onResume();
        accessor = FirebaseAccessor2.getInstance(getApplicationContext());
        actionBarText = configureActionBar();
        actionBarText.setText(title);

    }

    // if someone quits the app or destroys this activity by any means, stop the speech service.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent i = new Intent(this, SpeechService.class);
        stopService(i);
    }
}
