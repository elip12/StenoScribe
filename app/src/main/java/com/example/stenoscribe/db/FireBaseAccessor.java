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

public class FireBaseAccessor {
    private FirebaseFirestore db;
    private final String TAG = "FIREBASEACCESSOR";
    private Context context;

    public void configureListView(ListView listView, MainActivity.MeetingAdapter adapter) {
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?>adapter, View v, int position, long id){
                final Meeting item;
                final Intent intent;

                item = (Meeting) adapter.getItemAtPosition(position);
                intent = new Intent(context, MeetingDetails.class);
                intent.putExtra("uid", item.uid);
                context.startActivity(intent);
            }
        });
    }

    public FireBaseAccessor(Context context) {
        this.db = FirebaseFirestore.getInstance();
        this.context = context;
    }

    public String getDate() {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MM/dd/yyyy hh:mma");
        return date.format(formatter);
    }

    public Map<String, Object> createMeeting(long uid) {
        final Map<String, Object> meeting;
        meeting = new HashMap<>();
        meeting.put("uid", uid);
        meeting.put("title", "New Meeting");
        meeting.put("datetime", getDate());
        meeting.put("recordings", new HashMap<String, Object>());
        meeting.put("photos", new HashMap<String, Object>());
        meeting.put("documents", new HashMap<String, Object>());
        return meeting;
    }

    public void upsertMeeting(Map<String, Object> meeting) {
        db.collection("meetings")
                .add(meeting)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public class MeetingComparator implements Comparator<Map<String, Object>> {

        public int compare(Map<String, Object> m1, Map<String, Object> m2) {
            long m1id = (long)m1.get("uid");
            long m2id = (long)m2.get("uid");
            if (m1id > m2id)
                return -1;
            else if (m2id == m1id)
                return 0;
            else
                return 1;
        }
    }

    // reads all meetings from firebase, and updates the listview to display those meetings
    public void listMeetings(final List<Map<String, Object>> meetings,
                             final ListView listView, final MainActivity.MeetingAdapter adapter) {
        db.collection("meetings")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                meetings.add(document.getData());
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            meetings.sort(new MeetingComparator());
                            listView.setAdapter(adapter);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

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
