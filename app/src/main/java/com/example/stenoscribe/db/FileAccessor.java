package com.example.stenoscribe.db;

import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.stenoscribe.MainActivity;
import com.example.stenoscribe.MeetingDetails;
import com.example.stenoscribe.ui.recordings.RecordingsFragment;

import java.util.List;

/*
Add return values if insert, read, or update fails
 */

public class FileAccessor {
    private final AppDatabase db;
    private final String tag = "FILEACCESSOR";

    public FileAccessor(AppDatabase db){
        this.db = db;
    }

    private class ListerRunnable implements Runnable {
        private AppDatabase db;
        private int uid;
        private String[] types;
        private List<File> files;

        public ListerRunnable(AppDatabase db, int uid, String[] types) {
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
        private int meetingId;

        public GetterRunnable(AppDatabase db, int uid, int meetingId) {
            this.db = db;
            this.uid = uid;
            this.meetingId = meetingId;
        }

        @Override
        public void run() {
            this.file = this.db.meetingDao().getFile(this.uid, this.meetingId);
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

    public List<File> listFiles(int uid, String[] types) {
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

    public List<File> listFiles(int uid, String type) {
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

    public String getFilePath(int uid, int meeting_id) {
        GetterRunnable runnable = new GetterRunnable(this.db, uid, meeting_id);
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
