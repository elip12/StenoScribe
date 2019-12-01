package com.example.stenoscribe;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.stenoscribe.db.File;
import com.example.stenoscribe.db.FileAccessor;
import com.example.stenoscribe.db.Meeting;
import com.example.stenoscribe.db.MeetingAccessor;
import com.example.stenoscribe.ui.sharing.SharingFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseAccessor {
    private FirebaseFirestore db;
    private final String TAG = "FIREBASEACCESSOR";
    private MeetingAccessor meetingAccessor;
    private FileAccessor fileAccessor;
    private FirebaseAuth auth;
    private static FirebaseAccessor instance;
    private Context context;

    // Used to upload and download meetings from firebase
    private FirebaseAccessor(Context context, MeetingAccessor meetingAccessor, FileAccessor fileAccessor) {
        this.db = FirebaseFirestore.getInstance();

        this.context = context;
        this.meetingAccessor = meetingAccessor;
        this.fileAccessor = fileAccessor;
        this.auth = FirebaseAuth.getInstance();
        Log.d(TAG, auth.getUid());
    }

    // public getinstance because constructor is private
    public static FirebaseAccessor getInstance() {
        if (instance != null)
            return instance;
        return null;
    }

    // when you first create it it needs metadata.
    public static FirebaseAccessor getInstance(Context context, MeetingAccessor ma, FileAccessor fa) {
        if (instance != null)
            return instance;
        instance = new FirebaseAccessor(context, ma, fa);
        return instance;
    }

    // converts Meeting object to firebase format
    public Map<String, Object> convertMeetingToQDS(Meeting meeting) {
        Map<String, Object> data = new HashMap<>();

        data.put("uid", meeting.uid);
        data.put("title", meeting.title);
        data.put("date", meeting.date);
        data.put("files", new ArrayList<Map<String, Object>>());
        return data;
    }

    // converts file objects to firebase format
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

    // convert firebase format to Meeting object
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

    // convert firebase format to files objects
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

    // upserts a single meeting to firebase
    public void upsertMeetingAsync(Meeting meeting) {
        if(meetingAccessor.readMeeting(meeting.uid) == null)
            meetingAccessor.insertMeetingAsync(meeting);
        else
            meetingAccessor.updateMeetingAsync(meeting);
    }

    // upserts a single file to firebase
    public void upsertFileAsync(File file) {
        if (fileAccessor.getFilePath(file.uid, file.meeting_id) == null)
            fileAccessor.insertFileAsync(file);
        else
            fileAccessor.updateFileAsync(file);
    }

    // returns a list of all meetings that the user is allowed to access
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

    // wrapper fn for readability
    public void updateDB() {
        listMeetings();
    }

    // allows a user to share a meeting with another user. caveat: users must have first synced to FB
    public void shareWith(final String uid, final String email, final boolean remove) {
        final ArrayList<String> users = new ArrayList<>();
        db.collection("meetings")
                .whereEqualTo("uid", uid)
                .whereArrayContains("users", auth.getCurrentUser().getEmail())
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
                                if (remove)
                                    users.remove(email);
                                else if (!users.contains(email))
                                    users.add(email);
                                db.collection("meetings")
                                        .document(uid)
                                        .update("users", users)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(context, "Shared successfully",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error updating document", e);
                                                Toast.makeText(context, "Error sharing",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        } else {
                            Log.w(TAG, "Error getting document", task.getException());
                        }
                    }
                });
    }

    // create a meeting in FB. used in upsertmeeting
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

    // update a meeting in fb. used in upsertmeeting
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

    // upserts a meeting to fb
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

    // uploads all meetings to firebase
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

    // gets a list of all users associated with a meeting and displays in an adapter
    public void listUsers(String uid, final SharingFragment.SharingAdapter adapter) {
        db.collection("meetings")
                .whereEqualTo("uid", uid)
                .whereArrayContains("users", auth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // this should have only one meeting in it
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Map<String, Object> data = document.getData();
                                adapter.clear();
                                adapter.addAll((ArrayList<String>)data.get("users"));
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}
