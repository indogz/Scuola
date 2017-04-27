package com.example.volcano.dewdrop.utils;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import com.example.volcano.dewdrop.auth.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

/**
 * Created by volcano on 10/04/17.
 */

public class DatabaseOpenHelper {
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private StorageHelper storageHelper;

    private DatabaseOpenHelper() {
        database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
        storageHelper = StorageHelper.getInstance();
        reference = database.getReference();
        setListeners();
    }

    public static DatabaseOpenHelper getInstance() {
        return new DatabaseOpenHelper();
    }

    public void writeUser(User user) {
        //to be done in transaction
            DatabaseReference root = reference.getRoot();
            if (root.child("users") == null) {
                root.child("users").push();
            }
            DatabaseReference users = root.child("users");

            users = users.child(user.UID).push();
            users.updateChildren(user.toMap());
            StorageHelper.getInstance().uploadFile(new File(user.PHOTO_URL.toString()), user.UID);
        //ADD PHOTO TO STORAGE.

    }

    private void setListeners() {
        // Read from the database
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d("TAG", "Value is: " + value);
            }


            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }

}
