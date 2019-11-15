package com.example.stenoscribe;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.stenoscribe.db.File;
import com.example.stenoscribe.db.FileAccessor;
import com.example.stenoscribe.db.Meeting;
import com.example.stenoscribe.db.MeetingAccessor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseAccessor {
    private FirebaseFirestore db;
    private final String TAG = "FIREBASEACCESSOR";
    //private Context context;
    private MeetingAccessor meetingAccessor;
    private FileAccessor fileAccessor;
    private FirebaseAuth auth;

    public FirebaseAccessor(MeetingAccessor meetingAccessor, FileAccessor fileAccessor) {
        this.db = FirebaseFirestore.getInstance();

        //this.context = context;
        this.meetingAccessor = meetingAccessor;
        this.fileAccessor = fileAccessor;
        this.auth = FirebaseAuth.getInstance();
        Log.d(TAG, auth.getUid());
    }

    public Map<String, Object> convertMeetingToQDS(Meeting meeting) {
        Map<String, Object> data = new HashMap<>();

        data.put("uid", meeting.uid);
        data.put("title", meeting.title);
        data.put("date", meeting.date);
        data.put("files", new ArrayList<Map<String, Object>>());
        return data;
    }

    public Map<String, Object> addFilesToQDS(Map<String, Object> m, List<File> files) {
        ArrayList<Map<String, Object>> a = (ArrayList<Map<String, Object>>)m.get("files");
        for (File file: files) {
            Map<String, Object> fmap = new HashMap<>();
            fmap.put("uid", file.uid);
            fmap.put("meeting_id", file.meeting_id);
            fmap.put("path", file.path);
            fmap.put("type", file.type);
            a.add(fmap);
        }
        m.put("files", a);
        return m;
    }


    public Meeting convertQDSToMeeting(QueryDocumentSnapshot document) {
        Map<String, Object> data;
        Meeting meeting;
        int uid;
        String title;
        String date;

        data = document.getData();
        uid = ((Long)data.get("uid")).intValue();
        title = (String)data.get("title");
        date = (String)data.get("date");
        meeting = new Meeting(uid, title, date);
        return meeting;
    }

    public List<File> convertQDSToFiles(QueryDocumentSnapshot document) {
        Map<String, Object> data;
        List<File> files;
        File file;
        int uid;
        int meeting_id;
        String path;
        String type;

        files = new ArrayList<>();
        data = document.getData();
        for (Map<String, Object> f: (ArrayList<Map<String, Object>>)data.get("files")) {
            uid = ((Long)f.get("uid")).intValue();
            meeting_id = ((Long)f.get("meeting_id")).intValue();
            path = (String)f.get("path");
            type = (String)f.get("type");
            file = new File(uid, meeting_id, path, type);
            files.add(file);
        }
        return files;
    }

    public void upsertMeetingAsync(Meeting meeting) {
        if(meetingAccessor.readMeeting(meeting.uid) == null)
            meetingAccessor.insertMeetingAsync(meeting);
        else
            meetingAccessor.updateMeetingAsync(meeting);
    }

    public void upsertFileAsync(File file) {
        if (fileAccessor.getFilePath(file.uid, file.meeting_id) == null)
            fileAccessor.insertFileAsync(file);
        else
            fileAccessor.updateFileAsync(file);
    }

    // for now, we assume everyone gets access to all meetings
    public void listMeetings() {
        db.collection("meetings")
                .whereArrayContains("users", this.auth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Meeting meeting = convertQDSToMeeting(document);
                                upsertMeetingAsync(meeting);
                                List<File> files = convertQDSToFiles(document);
                                for (File file: files) {
                                    upsertFileAsync(file);
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void updateDB() {
        listMeetings();
    }

    public void upsertMeeting(Map<String, Object> meeting) {
        String uid = meeting.get("uid").toString();
        db.collection("meetings")
                .document(uid)
                .set(meeting)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void updateFB() {

        List<Meeting> meetings = meetingAccessor.listMeetings();
        for (Meeting meeting: meetings) {
            String[] types = {"recording", "document", "photo"};
            List<File> files = fileAccessor.listFiles(meeting.uid, types);
            Map<String, Object> m = convertMeetingToQDS(meeting);
            m = addFilesToQDS(m, files);
            upsertMeeting(m);
        }
    }


//
//    public class MeetingComparator implements Comparator<Map<String, Object>> {
//
//        public int compare(Map<String, Object> m1, Map<String, Object> m2) {
//            long m1id = (long)m1.get("uid");
//            long m2id = (long)m2.get("uid");
//            if (m1id > m2id)
//                return -1;
//            else if (m2id == m1id)
//                return 0;
//            else
//                return 1;
//        }
//    }
//
//

//    public Map<String, Object> getMeeting(int uid) {
//        db.collection("meetings")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                            }
//                        } else {
//                            Log.w(TAG, "Error getting documents.", task.getException());
//                        }
//                    }
//                });
//    }
}
