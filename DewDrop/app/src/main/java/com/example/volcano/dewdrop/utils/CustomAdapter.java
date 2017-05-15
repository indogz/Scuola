package com.example.volcano.dewdrop.utils;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.volcano.dewdrop.R;

import java.util.ArrayList;

/**
 * Created by volcano on 27/04/17.
 */

public class CustomAdapter extends ArrayAdapter<VideoChoice> {

    private ArrayList<VideoChoice> choices;

    public CustomAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<VideoChoice> objects) {
        super(context, resource, objects);
        choices = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        VideoChoice videoChoice = choices.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.fragment_video_choice, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.videoImage);
            viewHolder.title = (TextView) convertView.findViewById(R.id.videoTitle);
            viewHolder.subtitle = (TextView) convertView.findViewById(R.id.videoSubtitle);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.imageView.setImageDrawable(videoChoice.getMiniature().getDrawable());
        viewHolder.title.setText(videoChoice.getTitle());
        viewHolder.subtitle.setText(videoChoice.getSubtitle());

        return convertView;
    }

    private static class ViewHolder {
        private ImageView imageView;
        private TextView title;
        private TextView subtitle;
    }

}
