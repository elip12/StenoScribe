package com.example.stenoscribe.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Type {

    @PrimaryKey
    @NonNull
    public String type;
}
