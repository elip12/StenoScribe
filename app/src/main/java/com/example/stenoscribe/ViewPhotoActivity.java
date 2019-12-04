package com.example.stenoscribe;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewPhotoActivity extends AppCompatActivity {

    // actionbar shows meeting title but is not editable, and show back button
    public void configureActionBar(String title) {
        TextView actionBarText;
        final ViewGroup actionBarLayout;
        final ActionBar actionBar;

        actionBarLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.action_bar_noneditable, null);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_24dp);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarLayout);

        actionBarText = findViewById(R.id.action_bar_textview);
        actionBarText.setText(title);
    }

    // to make back button have correct behavior
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);
        configureActionBar("View Image");
        Intent i = getIntent();
        String path = i.getStringExtra("path");
        ImageView view = findViewById(R.id.view_image);
        FirebaseAccessor2 accessor = FirebaseAccessor2.getInstance(getApplicationContext());
        accessor.viewImage(path, view);
    }
}
