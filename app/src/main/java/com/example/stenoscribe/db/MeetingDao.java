package com.example.stenoscribe.db;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

// Meetign dao for Room Persistence Library ORM model
@Dao
public interface MeetingDao {
    // Meeting methods
    @Query("SELECT * FROM meeting ORDER BY uid DESC")
    List<Meeting> listMeetings();

    @Query("SELECT * FROM meeting WHERE uid IS :uid LIMIT 1")
    Meeting getMeeting(String uid);

    //@Query("INSERT INTO meeting DEFAULT VALUES")
    @Insert(entity = Meeting.class)
    void insertMeeting(Meeting meeting);

    @Update(entity = Meeting.class)
    void updateMeeting(Meeting meeting);

    @Delete(entity = Meeting.class)
    void deleteMeeting(Meeting meeting);

    // File methods
    @Query("SELECT * from file where type IN (:types) AND " +
            "meeting_id IS :meetingId ORDER BY uid DESC")
    List<File> listFilesOfType(String[] types, String meetingId);

    @Query("SELECT * FROM file WHERE uid IS :uid AND meeting_id is :mid AND type is :type LIMIT 1")
    File getFile(int uid, String mid, String type);

    @Insert(entity = File.class)
    void insertFile(File file);

    @Update(entity = File.class)
    void updateFile(File file);

    @Delete(entity = File.class)
    void deleteFile(File file);
}
