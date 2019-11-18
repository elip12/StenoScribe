package com.example.stenoscribe.db;

import android.util.Log;

import com.example.stenoscribe.MainActivity;

import java.util.List;

/*
Add return values if insert, read, or update fails
 */

public class MeetingAccessor {
    private final AppDatabase db;
    private final String TAG = "DB_MEETINGACCESSOR";

    public MeetingAccessor(AppDatabase db){
        this.db = db;
    }

    private class ListerRunnable implements Runnable {
        private AppDatabase db;
        private List<Meeting> meetings;

        public ListerRunnable(AppDatabase db) {
            this.db = db;
        }

        @Override
        public void run() {
            this.meetings = this.db.meetingDao().listMeetings();
        }

        public List<Meeting> listMeetings() {
            return this.meetings;
        }
    }

    private class InserterRunnable implements Runnable {
        private AppDatabase db;
        private Meeting meeting;

        public InserterRunnable(AppDatabase db, Meeting meeting) {
            this.db = db;
            this.meeting = meeting;
        }

        @Override
        public void run() {
            this.db.meetingDao().insertMeeting(this.meeting);
        }
    }

    private class ReaderRunnable implements Runnable {
        private AppDatabase db;
        private String uid;
        private Meeting meeting;

        public ReaderRunnable(AppDatabase db, String uid) {
            this.db = db;
            this.uid = uid;
        }

        @Override
        public void run() {
            this.meeting = this.db.meetingDao().getMeeting(this.uid);
        }

        public Meeting readMeeting() {
            return this.meeting;
        }
    }

    private class UpdaterRunnable implements Runnable {
        private AppDatabase db;
        private Meeting meeting;

        public UpdaterRunnable(AppDatabase db, Meeting meeting) {
            this.db = db;
            this.meeting = meeting;
        }

        @Override
        public void run() {
            this.db.meetingDao().updateMeeting(this.meeting);
        }
    }

    public List<Meeting> listMeetings() {
        ListerRunnable runnable = new ListerRunnable(this.db);
        Thread thread = new Thread(runnable);
        thread.start();
        try {
            thread.join();
            return runnable.listMeetings();
        }
        catch(Exception e) {
            Log.e(TAG, "listMeetings: " + e.toString());
            return null;
        }
    }

    public void insertMeeting(Meeting meeting, MainActivity.MeetingAdapter adapter) {
        InserterRunnable runnable = new InserterRunnable(this.db, meeting);
        Thread thread = new Thread(runnable);
        thread.start();
        try {
            thread.join();
            adapter.add(meeting);
            adapter.notifyDataSetChanged();
        }
        catch(Exception e) {
            Log.e(TAG, "insertMeeting: " + e.toString());
        }
    }

    public void insertMeetingAsync(Meeting meeting) {
        InserterRunnable runnable = new InserterRunnable(this.db, meeting);
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public Meeting readMeeting(String uid) {
        ReaderRunnable runnable = new ReaderRunnable(this.db, uid);
        Thread thread = new Thread(runnable);
        thread.start();
        try {
            thread.join();
            return runnable.readMeeting();
        }
        catch(Exception e) {
            Log.e(TAG, "readMeeting: " + e.toString());
            return null;
        }
    }

    public void updateMeeting(Meeting meeting) {
        UpdaterRunnable runnable = new UpdaterRunnable(this.db, meeting);
        Thread thread = new Thread(runnable);
        thread.start();
        try {
            thread.join();
        }
        catch(Exception e) {

            Log.e(TAG, "updateMeeting: " + e.toString());
        }
    }

    public void updateMeetingAsync(Meeting meeting) {
        UpdaterRunnable runnable = new UpdaterRunnable(this.db, meeting);
        Thread thread = new Thread(runnable);
        thread.start();
    }

}
