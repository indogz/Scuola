package com.example.volcano.dewdrop.utils;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.volcano.dewdrop.R;
import com.example.volcano.dewdrop.auth.Video;

import static android.app.Activity.RESULT_OK;

/**
 * Created by volcano on 10/05/17.
 */

public class VideoParametersSelector extends DialogFragment {

    private View view;
    private TextView title;
    private Button chooseImage;
    private TextView description;
    private ImageView imageShower;
    private Button okButton;
    private Button cancelButton;
    private Uri selectedImage;
    private Uri videoUri;
    private int PHOTO = 0;

    public static VideoParametersSelector newInstance(Uri videoUri) {
        VideoParametersSelector f = new VideoParametersSelector();
        Bundle args = new Bundle();
        args.putString("title", "Select video parameters");
        args.putParcelable("VideoUri", videoUri);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        videoUri = (Uri) getArguments().get("VideoUri");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.video_selector_layout, container, false);
        title = (TextView) view.findViewById(R.id.title);
        chooseImage = (Button) view.findViewById(R.id.chooseImage);
        description = (TextView) view.findViewById(R.id.description);
        imageShower = (ImageView) view.findViewById(R.id.imageShower);
        okButton = (Button) view.findViewById(R.id.okButton);
        cancelButton = (Button) view.findViewById(R.id.cancelButton);
        setListeners();
        return view;

    }


    private void setListeners() {
        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PHOTO);

            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Video video = new Video(getActivity(), title.getText().toString(), selectedImage, description.getText().toString(), videoUri);
                StorageHelper.getInstance().update(null, video);
                VideoParametersSelector.this.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoParametersSelector.this.dismiss();
            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    selectedImage = data.getData();
                    imageShower.setImageURI(selectedImage);
                }

            }
        }
    }

}
