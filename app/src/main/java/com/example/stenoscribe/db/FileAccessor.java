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

    private class InserterRunnable implements Runnable {
        private AppDatabase db;
        private File file;

        public InserterRunnable(AppDatabase db, File file) {
            this.db = db;
            this.file = file;
        }

        @Override
        public void run() {
            this.db.meetingDao().insertFile(this.file);
        }
    }

    private class GetterRunnable implements Runnable {
        private AppDatabase db;
        private File file;
        private int uid;

        public GetterRunnable(AppDatabase db, int uid) {
            this.db = db;
            this.uid = uid;
        }

        @Override
        public void run() {
            this.file = this.db.meetingDao().getFile(this.uid);
        }

        public File getFile() { return this.file; }
    }

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

    public void insertFile(File file) {
        InserterRunnable runnable = new InserterRunnable(this.db, file);
        Thread thread = new Thread(runnable);
        thread.start();
        try {
            thread.join();
        }
        catch(Exception e) {
            Log.e(this.tag, e.toString());
        }
    }

    public String getFilePath(int uid) {
        GetterRunnable runnable = new GetterRunnable(this.db, uid);
        Thread thread = new Thread(runnable);
        thread.start();
        try {
            thread.join();
            return runnable.getFile().path;
        }
        catch(Exception e) {
            Log.e(this.tag, e.toString());
            return null;
        }
    }
}
