package com.example.stenoscribe.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class File {

    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "meeting_id")
    public int meeting_id;

    @ColumnInfo(name = "path")
    public String path;

    @ColumnInfo(name = "type")
    public String type;
}
