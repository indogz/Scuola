package com.example.volcano.dewdrop.auth;

import android.app.Activity;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.volcano.dewdrop.utils.DatabaseValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by volcano on 10/05/17.
 */

public class Video implements DatabaseValue, Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public Object createFromParcel(Parcel source) {
            return new Video(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new Video[size];
        }
    };
    private String title;
    private long duration;
    private Uri miniature;
    private String description;
    private Uri videoUri;
    private InputStream inputStream;
    private InputStream imageInputstream;
    private Activity context;

    public Video(Activity context, String title, Uri miniature, String description, Uri videoUri) {
        this.title = title;
        this.miniature = miniature;
        this.description = description;
        this.videoUri = videoUri;
        this.context = context;
        inputStream = null;

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, videoUri);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        duration = Long.parseLong(time);

        try {
            inputStream = context.getContentResolver().openInputStream(videoUri);
            imageInputstream = context.getContentResolver().openInputStream(videoUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    public Video(Parcel in) {
        HashMap<String, Object> map = in.readHashMap(Object.class.getClassLoader());
        title = (String) map.get("Title");
        duration = (long) map.get("Duration");
        miniature = (Uri) map.get("Miniature");
        description = (String) map.get("Description");
        videoUri = (Uri) map.get("Videouri");
        inputStream = (InputStream) map.get("InputStream");
        context = (Activity) map.get("Context");
        imageInputstream = (InputStream) map.get("Imageinputstream");
    }

    public Activity getContext() {
        return context;
    }

    @Override
    public Map<String, Object> toMap() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Title", title);
        hashMap.put("Duration", duration);
        hashMap.put("Description", description);
        return hashMap;
    }

    @Override
    public String getPrimaryKey() {
        return "" + hashCode();
    }

    @Override
    public File[] getImages() {
        return new File[]{new File(miniature.toString())};
    }

    @Override
    public boolean hasImages() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Video)) return false;

        Video video = (Video) o;

        if (getDuration() != video.getDuration()) return false;
        if (!getTitle().equals(video.getTitle())) return false;
        return getVideoUri().equals(video.getVideoUri());

    }

    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public int hashCode() {
        int result = getTitle().hashCode();
        result = 31 * result + (int) (getDuration() ^ (getDuration() >>> 32));
        result = 31 * result + getVideoUri().hashCode();
        return result;
    }

    public InputStream getImageInputstream() {
        return imageInputstream;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Uri getMiniature() {
        return miniature;
    }

    public void setMiniature(Uri miniature) {
        this.miniature = miniature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Uri getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(Uri videoUri) {
        this.videoUri = videoUri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        HashMap<String, Object> hashMap = new HashMap<>(6);
        hashMap.put("Title", title);
        hashMap.put("Duration", duration);
        hashMap.put("Description", description);
        hashMap.put("Miniature", miniature);
        hashMap.put("Videouri", videoUri);
        hashMap.put("InputStream", inputStream);
        hashMap.put("Context", context);
        hashMap.put("Imageinputstream", imageInputstream);
        dest.writeMap(hashMap);

    }
}
