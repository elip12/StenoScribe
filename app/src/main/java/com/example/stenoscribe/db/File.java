package com.example.stenoscribe.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// File object for storing recordings, documents, and photos
@Entity(primaryKeys = {"uid", "meeting_id", "type"})
public class File {

    public File(int uid, String meeting_id, String path, String type) {
        this.uid = uid;
        this.meeting_id = meeting_id;
        this.path = path;
        this.type = type;
        this.datetime = getDate();
    }

    @Ignore
    public File(int uid, String meeting_id, String path, String type, String datetime) {
        this.uid = uid;
        this.meeting_id = meeting_id;
        this.path = path;
        this.type = type;
        this.datetime = datetime;
    }

    public String getDate() {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MM/dd/yyyy hh:mma");
        return date.format(formatter);
    }

    @NonNull
    @ColumnInfo(name = "uid")
    public int uid;

    @ColumnInfo(name = "meeting_id")
    @NonNull
    public String meeting_id;

    @ColumnInfo(name = "path")
    @NonNull
    public String path;

    @ColumnInfo(name = "type")
    @NonNull
    public String type;

    @ColumnInfo(name = "datetime")
    @NonNull
    public String datetime;
}
