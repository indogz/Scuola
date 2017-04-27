package com.example.volcano.dewdrop.auth;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Contacts;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by volcano on 07/04/17.
 */

public class User{
    public String UID;
    public final String NAME;
    public String SURNAME;
    public String BIRTHDATE;
    public final String EMAIL;
    public final Uri PHOTO_URL;

    public User(String name, String email, Uri photo) {
        NAME = name;
        EMAIL = email;
        PHOTO_URL = photo;
    }

    public User(String UID,String name, String surname, String birthDate, String email, Uri photo) {
        this(name, email, photo);
        this.UID= UID;
        SURNAME = surname;
        BIRTHDATE = birthDate;

    }

    public Map <String,Object>toMap(){
        Map <String,Object> map =new HashMap<>();
        map.put("NAME",NAME);
        map.put("SURNAME",SURNAME);
        map.put("BIRTHDATE",BIRTHDATE);
        map.put("EMAIL",EMAIL);
        map.put("PHOTO",PHOTO_URL);
        return map;

    }
}