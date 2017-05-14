package com.example.volcano.dewdrop.utils;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.example.volcano.dewdrop.auth.Video;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by volcano on 10/04/17.
 */

public class DatabaseOpenHelper {

    private static DatabaseOpenHelper instance = null;
    private static List<String> mainFields = new ArrayList<>();
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private StorageHelper storageHelper;
    private DatabaseReference root;

    private DatabaseOpenHelper() {
        database = FirebaseDatabase.getInstance();
    }

    public static DatabaseOpenHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseOpenHelper();
            instance.onCreate(mainFields);
        }
        return instance;
    }

    private void initDefaultMainFields() {
        mainFields.add("Users");
        mainFields.add("Videos");
    }

    private void onCreate(List<String> mainFields) {
        database.setPersistenceEnabled(true);
        database.goOnline();

        storageHelper = StorageHelper.getInstance();
        reference = database.getReference();
        setListeners();
        initDefaultMainFields();

        root = reference.getRoot();
/*
        for (String s:mainFields){
            if(root.child(s)==null){
                DatabaseReference dr=root.child(s);
                dr.setValue("xxx");
                dr.push();
            }
        }

        HashMap<String,Object>hashMap=new HashMap<>();
        if(!mainFields.isEmpty()) {
            for (String s:mainFields){
                hashMap.put(s,"xxx");
            }
        }

        root.updateChildren(hashMap);*/
    }


    public DatabaseReference select(String container, String primaryKey) {
        System.out.println(container + " " + primaryKey);

        DatabaseReference reference = root.child(container).child(primaryKey);

        return reference;
    }

    public void fetchAllVideos(final Activity target, final ProgressDialogFragment progressDialogFragment, final AsyncTask continuation, final ArrayList<VideoChoice> videoChoices) {
        final DatabaseReference reference = root.child("Videos");
        final ImageView dump = new ImageView(target);

        final AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                Iterator<DataSnapshot> it = ((DataSnapshot) params[0]).getChildren().iterator();
                while (it.hasNext()) {
                    DataSnapshot currentSnapshot = it.next();
                    Iterator<DataSnapshot> ds = currentSnapshot.getChildren().iterator();
                    while (ds.hasNext()) {
                        DataSnapshot leafSnapshot = ds.next();
                        final HashMap<String, Object> hashMap = (HashMap<String, Object>) leafSnapshot.getValue();

                        DownloadImageTask downloadImageTask = new DownloadImageTask(dump);
                        System.out.println("KEY: " + currentSnapshot.getKey());
                        Log.d("TAG", dump.toString());
                        Continuation c = new Continuation() {
                            @Override
                            public Object then(@NonNull Task task) throws Exception {
                                VideoChoice videoChoice = VideoChoice.getInstance(dump, (String) hashMap.get("Title"), (String) hashMap.get("Description"), (long) hashMap.get("Duration"));
                                Log.d("Videochoice", videoChoice.toString());
                                videoChoices.add(videoChoice);
                                Log.d("HELP", videoChoices.toString());
                                return null;
                            }
                        };
                        Uri uri = null;
                        try {
                            uri = StorageHelper.getInstance().getImageUrl(currentSnapshot.getKey(), c);
                        } catch (Exception e) {
                            System.out.println(e.getLocalizedMessage());
                        }

                        if (uri != null) {
                            downloadImageTask.execute(uri.toString());
                        }


                    }
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialogFragment.show(target.getFragmentManager(), "TAG");
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                Log.d("Continuation", videoChoices.toString());
                continuation.execute(progressDialogFragment);
            }

        };

        reference.addListenerForSingleValueEvent(new ValueEventListener() { //problema  qui: non aspetta di finire sostituire con handler
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                asyncTask.execute(dataSnapshot);
                Handler handler = new Handler();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public boolean addChild(String mainField, DatabaseValue child) {
        boolean condition = mainFields.contains(mainField);
        try {
            DatabaseReference temp = root.child(mainField);
            System.out.println(temp);
            //add primary key
            temp = temp.child(child.getPrimaryKey()).push();
            //add fields
            System.out.println("############################################" + child);
            if (child.hasImages()) {
                File blobs[] = child.getImages();
                for (File f : blobs) {
                    storageHelper.uploadImage(f, child.getPrimaryKey());

                }
            }
            temp.updateChildren(child.toMap());
        } catch (
                DatabaseException e) {
            System.out.println(e.getMessage());
        }
        return !condition;
    }


    public boolean addChild(String mainField, Video child) {
        boolean condition = mainFields.contains(mainField);
        try {
            DatabaseReference temp = root.child(mainField);
            System.out.println(temp);
            //add primary key
            temp = temp.child(child.getPrimaryKey()).push();
            //add fields
            System.out.println("############################################" + child);
            if (child.hasImages()) {

                storageHelper.uploadImageForVideo(child.getImageInputstream(), child.getPrimaryKey());

            }
            temp.updateChildren(child.toMap());
        } catch (
                DatabaseException e) {
            System.out.println(e.getMessage());
        }
        return !condition;
    }

    public void onUpgrade() {
    }

    public void onDestroy() {
        database.purgeOutstandingWrites();
        database.goOffline();
    }


 /*   public void writeUser(User user) {
        //to be done in transaction
        DatabaseReference root = reference.getRoot();
        if (root.child("users") == null) {
            root.child("users").push();
        }
        DatabaseReference users = root.child("users");

        users = users.child(user.UID).push();
        users.updateChildren(user.toMap());
        StorageHelper.getInstance().uploadImage(new File(user.PHOTO_URL.toString()), user.UID);
        //ADD PHOTO TO STORAGE.

    }*/

    private void setListeners() {
        // Read from the database
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                // String value = dataSnapshot.getValue(String.class);
                // Log.d("TAG", "Value is: " + value);
            }


            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }

}
