package com.example.stenoscribe.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

@Entity
public class Meeting {

    public Meeting(int uid) {
        this.uid = uid;
    }

    @Ignore
    public Meeting(int uid, String title, String date) {
        this.uid = uid;
        this.title = title;
        this.date = date;
    }

    public String getDate() {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MM/dd/yyyy hh:mma");
        return date.format(formatter);
    }

    @PrimaryKey()
    @NonNull
    public int uid;

    @ColumnInfo(name = "title")
    @NonNull
    public String title = "New Meeting";

    @ColumnInfo(name = "date")
    @NonNull
    public String date = this.getDate();

    @ColumnInfo(name = "description")
    public String description;
}
