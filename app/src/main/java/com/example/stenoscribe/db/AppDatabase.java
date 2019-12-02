package com.example.stenoscribe.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// Room persistence Library DB w private constructor for global instance
@Database(entities = {Meeting.class, File.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MeetingDao meetingDao();
    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, AppDatabase.class,
                    "stenoscribe").build();
        }
        return INSTANCE;
    }
}