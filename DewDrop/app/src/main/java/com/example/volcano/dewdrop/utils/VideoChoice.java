package com.example.volcano.dewdrop.utils;

import android.widget.ImageView;

/**
 * Created by volcano on 27/04/17.
 */

public class VideoChoice {
    private ImageView miniature;
    private String title;
    private String subtitle;
    private long duration;


    public VideoChoice(){}
    private VideoChoice(ImageView miniature, String title, String subtitle, long duration) {
        this.miniature = miniature;
        this.title = title;
        this.subtitle = subtitle;
        this.duration = duration;
    }


    public static VideoChoice getInstance(ImageView miniature, String title, String subtitle, long duration){
        return new VideoChoice(miniature, title, subtitle, duration);
    }

    public ImageView getMiniature() {
        return miniature;
    }

    public void setMiniature(ImageView miniature) {
        this.miniature = miniature;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "VideoChoice{" +
                "miniature=" + miniature +
                ", title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", duration=" + duration +
                '}';
    }
}
