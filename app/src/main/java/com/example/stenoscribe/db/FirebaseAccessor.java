package com.example.stenoscribe.db;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.example.stenoscribe.MainActivity;
import com.example.stenoscribe.MeetingDetails;
import com.example.stenoscribe.db.MeetingAccessor;
import com.example.stenoscribe.db.FileAccessor;
import com.example.stenoscribe.db.Meeting;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseAccessor {
    private FirebaseFirestore db;
    private final String TAG = "FIREBASEACCESSOR";
    private Context context;
    private MeetingAccessor meetingAccessor;
    private FileAccessor fileAccessor;

    public FirebaseAccessor(Context context, MeetingAccessor meetingAccessor, FileAccessor fileAccessor) {
        this.db = FirebaseFirestore.getInstance();
        this.context = context;
        this.meetingAccessor = meetingAccessor;
        this.fileAccessor = fileAccessor;
    }

    public Meeting convertQDSToMeeting(QueryDocumentSnapshot document) {
        Map<String, Object> data;
        Meeting meeting;
        int uid;
        String title;
        String date;

        data = document.getData();
        uid = (int)data.get("uid");
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
        data = (Map<String, Object>)document.getData();
        for (Map<String, Object> f: (Map<String, Object>[])data.get("files")) {
            uid = (int)f.get("uid");
            meeting_id = (int)f.get("meeting_id");
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
        if (fileAccessor.getFilePath(file.uid) == null)
            fileAccessor.insertFileAsync(file);
        else
            fileAccessor.updateFileAsync(file);
    }

    // for now, we assume everyone gets access to all meetings
    public void listMeetings() {
        db.collection("meetings")
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

    public void updateDB(int[] uids) {
        listMeetings();
    }

    public void updateFB() {
        // if you have any info that firebase doesnt have/ for every meeting and associated file in your database
        // push that info to firebase

    }




//    public void configureListView(ListView listView, MainActivity.MeetingAdapter adapter) {
//        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?>adapter, View v, int position, long id){
//                final Meeting item;
//                final Intent intent;
//
//                item = (Meeting) adapter.getItemAtPosition(position);
//                intent = new Intent(context, MeetingDetails.class);
//                intent.putExtra("uid", item.uid);
//                context.startActivity(intent);
//            }
//        });
//    }
//
//
//
//    public String getDate() {
//        LocalDateTime date = LocalDateTime.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MM/dd/yyyy hh:mma");
//        return date.format(formatter);
//    }
//
//    public Map<String, Object> createMeeting(long uid) {
//        final Map<String, Object> meeting;
//        meeting = new HashMap<>();
//        meeting.put("uid", uid);
//        meeting.put("title", "New Meeting");
//        meeting.put("datetime", getDate());
//        meeting.put("recordings", new HashMap<String, Object>());
//        meeting.put("photos", new HashMap<String, Object>());
//        meeting.put("documents", new HashMap<String, Object>());
//        return meeting;
//    }
//
//    public void upsertMeeting(Map<String, Object> meeting) {
//        db.collection("meetings")
//                .add(meeting)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//                });
//    }
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
