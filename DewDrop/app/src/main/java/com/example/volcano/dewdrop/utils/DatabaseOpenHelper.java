package com.example.volcano.dewdrop.utils;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by volcano on 10/04/17.
 */

public class DatabaseOpenHelper {
   private FirebaseDatabase database;

    public DatabaseOpenHelper(){
        database=FirebaseDatabase.getInstance();
    }

}
