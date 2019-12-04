package com.example.stenoscribe.db;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

// File object for storing recordings, documents, and photos
public class File {
    public String uid;
    public String meetingId;
    public String path;
    public String type;
    public String datetime;
    public Long uTime;

    public File(){}

    public File(String uid, String meetingId, String path, String type) {
        this.uid = uid;
        this.meetingId = meetingId;
        this.path = path;
        this.type = type;
        this.datetime = getDate();
        this.uTime = getTime();
    }

    public File(String uid, String meetingId, String path, String type, String date, Long time) {
        this.uid = uid;
        this.meetingId = meetingId;
        this.path = path;
        this.type = type;
        this.datetime = date;
        this.uTime = time;
    }

    public String getDate() {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MM/dd/yyyy hh:mma");
        return date.format(formatter);
    }

    public Long getTime() {
        return System.currentTimeMillis() / 1000L;
    }


}
