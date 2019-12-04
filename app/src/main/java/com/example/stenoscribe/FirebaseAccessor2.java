package com.example.stenoscribe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.stenoscribe.db.File;
import com.example.stenoscribe.db.Meeting;
import com.example.stenoscribe.ui.sharing.SharingFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseAccessor2 {
    private FirebaseFirestore db;
    private final String TAG = "FIREBASEACCESSOR";
    private FirebaseAuth auth;
    private static FirebaseAccessor2 instance;
    private Context context;

    // Used to upload and download meetings from firebase
    private FirebaseAccessor2(Context context) {
        this.db = FirebaseFirestore.getInstance();
        this.context = context;
        this.auth = FirebaseAuth.getInstance();
    }

    // public getinstance because constructor is private
    public static FirebaseAccessor2 getInstance(Context context) {
        if (instance == null)
            instance = new FirebaseAccessor2(context);
        return instance;
    }

    // returns a list of all meetings that the user is allowed to access
    public void listMeetings(final MainActivity.MeetingAdapter adapter) {
        db.collection("meetings")
                .whereArrayContains("users", this.auth.getCurrentUser().getEmail())
                .orderBy("uTime", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        ArrayList<Meeting> meetings = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("uid") != null) {
                                meetings.add(doc.toObject(Meeting.class));
                            }
                        }
                        adapter.clear();
                        adapter.addAll(meetings);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    public void listFiles(final String meetingId, final String type, final ArrayAdapter<File> adapter) {
        db.collection("meetings")
                .document(meetingId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            Log.d(TAG, "Current data: " + snapshot.getData());
                            Meeting meeting = snapshot.toObject(Meeting.class);
                            ArrayList<File> files = new ArrayList<>();
                            for (File file: meeting.files) {
                                if (file.type.equals(type))
                                    files.add(file);
                            }
                            // sort by date
                            Collections.sort(files, new Comparator<File>() {
                                @Override
                                public int compare(File f1, File f2) {
                                    return f2.uTime.compareTo(f1.uTime);
                                }
                            });
                            adapter.clear();
                            adapter.addAll(files);
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });
    }

    // allows a user to share a meeting with another user.
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
    public void createMeeting(Meeting meeting) {
        final String uid = meeting.uid;
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

    public void updateMeeting(String meetingId, String field, Object value) {
        db.collection("meetings")
                .document(meetingId)
                .update(field, value)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    public void addFile(final File file) {
        db.collection("meetings")
                .document(file.meetingId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                ArrayList<File> files = document.toObject(Meeting.class).files;
                                for (File file: files) {
                                    Log.d(TAG, file.datetime);
                                }
                                files.add(file);
                                for (File file: files) {
                                    Log.d(TAG, file.datetime);
                                }
                                updateMeeting(file.meetingId, "files", files);
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }

    // gets a list of all users associated with a meeting and displays in an adapter
    public void listUsers(String meetingId, final SharingFragment.SharingAdapter adapter) {
        db.collection("meetings")
                .document(meetingId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            Log.d(TAG, "Current data: " + snapshot.getData());
                            Meeting meeting = snapshot.toObject(Meeting.class);
                            adapter.clear();
                            adapter.addAll(meeting.users);
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });
    }

    public void addImage(String path, Bitmap bitmap) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child(path);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.w(TAG, "Failed to upload image");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }
        });
    }

    public void viewImage(String path, final ImageView view) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child(path);
        imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                view.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });

    }
}
