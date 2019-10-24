package com.example.stenoscribe.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Meeting.class, File.class, Type.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MeetingDao meetingDao();

}

