package com.example.stenoscribe.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class File {

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
}
