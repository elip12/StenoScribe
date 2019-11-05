package com.example.stenoscribe.db;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class File {

    public File(int uid, int meeting_id, String path, String type) {
        this.uid = uid;
        this.meeting_id = meeting_id;
        this.path = path;
        this.type = type;
    }

    public String getDate() {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MM/dd/yyyy hh:mma");
        return date.format(formatter);
    }

    @PrimaryKey
    @NonNull
    public int uid;

    @ColumnInfo(name = "meeting_id")
    @NonNull
    public int meeting_id;

    @ColumnInfo(name = "path")
    @NonNull
    public String path;

    @ColumnInfo(name = "type")
    @NonNull
    public String type;

    @ColumnInfo(name = "datetime")
    @NonNull
    public String datetime = this.getDate();
}
