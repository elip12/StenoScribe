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
import java.util.UUID;

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
        String uid;
        String title;
        String date;

        data = document.getData();
        uid = (String)data.get("uid");
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
        String meeting_id;
        String path;
        String type;

        files = new ArrayList<>();
        data = document.getData();
        for (Map<String, Object> f: (ArrayList<Map<String, Object>>)data.get("files")) {
            uid = ((Long)f.get("uid")).intValue();
            meeting_id = (String)f.get("meeting_id");
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
                .whereArrayContains("users", this.auth.getCurrentUser().getEmail())
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

    public void shareWith(final String uid, final String email) {
        final ArrayList<String> users = new ArrayList<>();
        db.collection("meetings")
                .whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // this should have only one meeting in it
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Map<String, Object> data = document.getData();
                                users.addAll((ArrayList<String>)data.get("users"));
                                if (!users.contains(email))
                                    users.add(email);
                                db.collection("meetings")
                                        .document(uid)
                                        .update("users", users)
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error updating document", e);
                                            }
                                        });
                            }
                        } else {
                            Log.w(TAG, "Error getting document", task.getException());
                        }
                    }
                });
    }

    public void createMeeting(Map<String, Object> meeting) {
        final String uid = meeting.get("uid").toString();
        final ArrayList<String> users = new ArrayList<>();
        users.add(auth.getCurrentUser().getEmail());
        meeting.put("users", users);
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

    public void updateMeeting(Map<String, Object> meeting) {
        final String uid = meeting.get("uid").toString();
        db.collection("meetings")
                .document(uid)
                .update(meeting)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void upsertMeeting(final Map<String, Object> meeting) {
        final String uid = meeting.get("uid").toString();
        db.collection("meetings")
                .whereEqualTo("uid", uid)
                .whereArrayContains("users", auth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() == 0) {
                                Log.d(TAG, "Creating meeting");
                                createMeeting(meeting);
                            }
                            else {
                                Log.d(TAG, "Updating meeting");
                                updateMeeting(meeting);
                            }

                        }
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

    // returns the list of users who have access to a given meeting
    public List<String> getUsers(String uid) { // adapter, then put the dudes in the adapter or something
        final ArrayList<String> users = new ArrayList<>();
        db.collection("meetings")
                .whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // this should have only one meeting in it
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Map<String, Object> data = document.getData();
                                for (String f: (ArrayList<String>)data.get("users")) {
                                    users.add(f);
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        return users;
    }
}
