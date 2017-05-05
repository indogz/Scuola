package com.example.volcano.dewdrop.auth;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Contacts;
import android.util.Log;
import android.widget.ImageView;

import com.example.volcano.dewdrop.utils.DatabaseValue;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by volcano on 07/04/17.
 */

public class User implements DatabaseValue {
    public String UID;
    public  String NAME;
    public String SURNAME;
    public String BIRTHDATE;
    public  String EMAIL;
    public  Uri PHOTO_URL;

    public User() {
    }

    public User(String name, String email, Uri photo) {
        NAME = name;
        EMAIL = email;
        PHOTO_URL = photo;
    }

    public User(String UID, String name, String surname, String birthDate, String email, Uri photo) {
        this(name, email, photo);
        this.UID = UID;
        SURNAME = surname;
        BIRTHDATE = birthDate;

    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("NAME", NAME);
        map.put("SURNAME", SURNAME);
        map.put("BIRTHDATE", BIRTHDATE);
        map.put("EMAIL", EMAIL);
        return map;

    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getNAME() {
        return NAME;
    }

    public String getSURNAME() {
        return SURNAME;
    }

    public void setSURNAME(String SURNAME) {
        this.SURNAME = SURNAME;
    }

    public String getBIRTHDATE() {
        return BIRTHDATE;
    }

    public void setBIRTHDATE(String BIRTHDATE) {
        this.BIRTHDATE = BIRTHDATE;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public void setPHOTO_URL(Uri PHOTO_URL) {
        this.PHOTO_URL = PHOTO_URL;
    }

    public Uri getPHOTO_URL() {
        return PHOTO_URL;
    }

    @Override
    public String getPrimaryKey() {
        return UID;

    }


    @Override
    public File[] getBlobs() {
        if (PHOTO_URL != null) {
            return new File[]{new File(PHOTO_URL.toString())};
        }
        return null;
    }

    @Override
    public boolean hasBlobs() {
        return getBlobs() != null;
    }
}
