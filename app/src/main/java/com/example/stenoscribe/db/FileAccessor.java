package com.example.stenoscribe.db;

import android.util.Log;

import java.util.List;

/*
Add return values if insert, read, or update fails
 */

public class FileAccessor {
    private final AppDatabase db;
    private final String tag = "DB_RECORDINGACCESSOR";
    private final int uid;
    private final String type;

    public FileAccessor(AppDatabase db, int uid, String type){
        this.db = db;
        this.uid = uid;
        this.type = type;
    }

    private class ListerRunnable implements Runnable {
        private AppDatabase db;
        private int uid;
        private String type;
        private List<File> files;

        public ListerRunnable(AppDatabase db, int uid, String type) {
            this.db = db;
            this.uid = uid;
            this.type = type;
        }

        @Override
        public void run() {
            this.files = this.db.meetingDao().listFilesOfType(this.type, this.uid);
        }

        public List<File> listFiles() {
            return this.files;
        }
    }

//    private class InserterRunnable implements Runnable {
//        private AppDatabase db;
//        private File file;
//        private int uid;
//        private String type;
//
//        public InserterRunnable(AppDatabase db, File file, int uid, String type) {
//            this.db = db;
//            this.file = file;
//            this.uid = uid;
//            this.type = type;
//        }
//
//        @Override
//        public void run() {
//            this.db.meetingDao().insertMeeting(this.file);
//        }
//    }
//
//    private class ReaderRunnable implements Runnable {
//        private AppDatabase db;
//        private int uid;
//        private Meeting meeting;
//
//        public ReaderRunnable(AppDatabase db, int uid) {
//            this.db = db;
//            this.uid = uid;
//        }
//
//        @Override
//        public void run() {
//            this.meeting = this.db.meetingDao().getMeeting(this.uid);
//        }
//
//        public Meeting readMeeting() {
//            return this.meeting;
//        }
//    }
//
//    private class UpdaterRunnable implements Runnable {
//        private AppDatabase db;
//        private Meeting meeting;
//
//        public UpdaterRunnable(AppDatabase db, Meeting meeting) {
//            this.db = db;
//            this.meeting = meeting;
//        }
//
//        @Override
//        public void run() {
//            this.db.meetingDao().updateMeeting(this.meeting);
//        }
//    }

    public List<File> listFiles() {
        ListerRunnable runnable = new ListerRunnable(this.db, this.uid, this.type);
        Thread thread = new Thread(runnable);
        thread.start();
        try {
            thread.join();
            return runnable.listFiles();
        }
        catch(Exception e) {
            Log.e(tag, e.toString());
            return null;
        }
    }

//    public void insertMeeting(Meeting meeting) {
//        InserterRunnable runnable = new InserterRunnable(this.db, meeting);
//        Thread thread = new Thread(runnable);
//        thread.start();
//        try {
//            thread.join();
//        }
//        catch(Exception e) {
//            Log.e(tag, e.toString());
//        }
//    }
//
//    public Meeting readMeeting(int uid) {
//        ReaderRunnable runnable = new ReaderRunnable(this.db, uid);
//        Thread thread = new Thread(runnable);
//        thread.start();
//        try {
//            thread.join();
//            return runnable.readMeeting();
//        }
//        catch(Exception e) {
//            Log.e(tag, e.toString());
//            return null;
//        }
//    }
//
//    public void updateMeeting(Meeting meeting) {
//        UpdaterRunnable runnable = new UpdaterRunnable(this.db, meeting);
//        Thread thread = new Thread(runnable);
//        thread.start();
//        try {
//            thread.join();
//        }
//        catch(Exception e) {
//            Log.e(tag, e.toString());
//        }
//    }
}
