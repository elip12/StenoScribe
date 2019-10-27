package com.example.stenoscribe.db;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

@Dao
public interface MeetingDao {
    // Meeting methods
    @Query("SELECT * FROM meeting")
    List<Meeting> listMeetings();

    @Query("SELECT * FROM meeting WHERE uid IS :uid LIMIT 1")
    Meeting getMeeting(int uid);

    //@Query("INSERT INTO meeting DEFAULT VALUES")
    @Insert(entity = Meeting.class)
    void insertMeeting(Meeting meeting);

    @Update(entity = Meeting.class)
    void updateMeeting(Meeting meeting);

    @Delete(entity = Meeting.class)
    void deleteMeeting(Meeting meeting);

    // File methods
    @Query("SELECT path from file where type IS :type AND " +
            "meeting_id IS :meeting_id")
    List<String> listPathsOfType(String type, int meeting_id);

    @Insert(entity = File.class)
    void insertFile(File file);

    @Delete(entity = File.class)
    void deleteFile(File file);

    // Type methods
    @Query("SELECT * from type")
    List<Type> listTypes();
}
