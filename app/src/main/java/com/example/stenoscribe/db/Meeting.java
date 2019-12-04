package com.example.stenoscribe.db;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

// Meeting object
public class Meeting {
    public String uid;
    public String title = "New Meeting";
    public String date;
    public Long uTime = getTime();
    public ArrayList<File> files;
    public ArrayList<String> users;

    public Meeting(){}

    public Meeting(String uid, String user) {
        this.uid = uid;
        this.title = "New Meeting";
        this.date = getDate();
        this.uTime = getTime();
        this.files = new ArrayList<>();
        this.users = new ArrayList<>();
        users.add(user);
    }

    public Meeting(String uid, String title, String date, Long uTime, ArrayList<String> users) {
        this.uid = uid;
        this.title = title;
        this.date = date;
        this.uTime = uTime;
        this.users = users;
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
