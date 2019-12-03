package com.example.stenoscribe.db;

import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.stenoscribe.MainActivity;
import com.example.stenoscribe.MeetingDetails;
import com.example.stenoscribe.ui.recordings.RecordingsFragment;

import java.util.List;

// Helper class for inserting files into DB easily.
// Options for synchronously (when you need to see it in a list immediately)
// and async (when you are directed to a new activity immediately)
public class FileAccessor {
    private final AppDatabase db;
    private final String tag = "FILEACCESSOR";

    public FileAccessor(AppDatabase db){
        this.db = db;
    }

    // a bunch of runnables, since each operation needs its own implementation of the run method.
    private class ListerRunnable implements Runnable {
        private AppDatabase db;
        private String uid;
        private String[] types;
        private List<File> files;

        public ListerRunnable(AppDatabase db, String uid, String[] types) {
            this.db = db;
            this.uid = uid;
            this.types = types;
        }

        @Override
        public void run() {
            this.files = this.db.meetingDao().listFilesOfType(this.types, this.uid);
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
        private String meetingId;
        private String type;

        public GetterRunnable(AppDatabase db, int uid, String meetingId, String type) {
            this.db = db;
            this.uid = uid;
            this.meetingId = meetingId;
            this.type = type;
        }

        @Override
        public void run() {
            this.file = this.db.meetingDao().getFile(this.uid, this.meetingId, this.type);
        }

        public File getFile() { return this.file; }
    }

    private class UpdaterRunnable implements Runnable {
        private AppDatabase db;
        private File file;

        public UpdaterRunnable(AppDatabase db, File file) {
            this.db = db;
            this.file = file;
        }

        @Override
        public void run() {
            this.db.meetingDao().updateFile(this.file);
        }
    }

    // a bunch of database operations on files
    public List<File> listFiles(String uid, String[] types) {
        ListerRunnable runnable = new ListerRunnable(this.db, uid, types);
        Thread thread = new Thread(runnable);
        thread.start();
        try {
            thread.join();
            return runnable.listFiles();
        }
        catch(Exception e) {
            Log.e(tag, "listFiles: " + e.toString());
            return null;
        }
    }

    public List<File> listFiles(String uid, String type) {
        String[] types = new String[1];
        types[0] = type;
        ListerRunnable runnable = new ListerRunnable(this.db, uid, types);
        Thread thread = new Thread(runnable);
        thread.start();
        try {
            thread.join();
            return runnable.listFiles();
        }
        catch(Exception e) {
            Log.w(tag, "listFiles: " + e.toString());
            return null;
        }
    }

    public void insertFile(File file, ArrayAdapter<File> adapter) {
        InserterRunnable runnable = new InserterRunnable(this.db, file);
        Thread thread = new Thread(runnable);
        thread.start();
        try {
            thread.join();
            adapter.add(file);
            adapter.notifyDataSetChanged();
        }
        catch(Exception e) {
            Log.e(this.tag, "insertFile: " +e.toString());
        }
    }

    public void insertFileAsync(File file) {
        InserterRunnable runnable = new InserterRunnable(this.db, file);
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public String getFilePath(int uid, String meetingId, String type) {
        GetterRunnable runnable = new GetterRunnable(this.db, uid, meetingId, type);
        Thread thread = new Thread(runnable);
        thread.start();
        try {
            thread.join();
            return runnable.getFile().path;
        }
        catch(Exception e) {
            Log.w(this.tag, "getFilePath: " +e.toString());
            return null;
        }
    }

    public void updateFile(File file) {
        UpdaterRunnable runnable = new UpdaterRunnable(this.db, file);
        Thread thread = new Thread(runnable);
        thread.start();
        try {
            thread.join();
        }
        catch(Exception e) {
            Log.e(this.tag, "updateFile: " + e.toString());
        }
    }

    public void updateFileAsync(File file) {
        UpdaterRunnable runnable = new UpdaterRunnable(this.db, file);
        Thread thread = new Thread(runnable);
        thread.start();
    }
}
