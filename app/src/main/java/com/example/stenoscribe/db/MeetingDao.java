package com.example.stenoscribe.db;

import androidx.room.Dao;
import androidx.room.Query;
import java.util.List;

@Dao
public interface MeetingDao {
    @Query("SELECT * FROM meeting")
    List<Meeting> listAllMeetings();

    @Query("SELECT * FROM meeting WHERE uid IS :uid LIMIT 1")
    Meeting getMeeting(int uid);

    @Query("SELECT path from file where type IS :type AND " +
            "meeting_id IS :meeting_id")
    List<String> listPathsOfType(String type, int meeting_id);
}
