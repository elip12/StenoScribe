package com.example.stenoscribe.db;

import android.util.Log;

public class MeetingAccessor {
    private final AppDatabase db;

    public MeetingAccessor(AppDatabase db){
        this.db = db;
    }

    private class ReaderRunnable implements Runnable {
        public AppDatabase db;
        public int uid;
        public Meeting meeting;

        public ReaderRunnable(AppDatabase db, int uid) {
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
        public AppDatabase db;
        public Meeting meeting;

        public UpdaterRunnable(AppDatabase db, Meeting meeting) {
            this.db = db;
            this.meeting = meeting;
        }

        @Override
        public void run() {
            this.db.meetingDao().updateMeeting(this.meeting);
        }
    }

    public Meeting readMeeting(int uid) {

        ReaderRunnable runnable = new ReaderRunnable(this.db, uid);
        Thread thread = new Thread(runnable);
        thread.start();
        try {
            thread.join();
            return runnable.readMeeting();
        }
        catch(Exception e) {
            Log.e("MEETINGDETAILS_DB", e.toString());
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
            Log.e("MEETINGDETAILS_DB", e.toString());
        }
    }
}
