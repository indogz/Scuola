package com.example.volcano.dewdrop.auth;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.net.URL;

/**
 * Created by volcano on 07/04/17.
 */

public class User {
    public final String NAME;
    public final String EMAIL;
    public final Uri PHOTO_URL;

    public User(String name,String email,Uri photo) {
        NAME=name;
        EMAIL=email;
        PHOTO_URL=photo;
    }


}