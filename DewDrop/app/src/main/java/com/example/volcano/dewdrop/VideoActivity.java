package com.example.volcano.dewdrop;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;

/**
 * Convertire questa classe in fragment e creare una gemella per la musica
 */
public class VideoActivity extends Activity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, MediaController.MediaPlayerControl, MediaPlayer.OnBufferingUpdateListener {
    String vidAddress = null;
    private MediaPlayer mediaPlayer;
    private SurfaceHolder vidHolder;
    private SurfaceView vidSurface;
    private MediaController controller;
    private Handler handler = new Handler();
    private float x1, x2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        vidSurface = (SurfaceView) findViewById(R.id.surfView);
        vidSurface.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Toast.makeText(VideoActivity.this, "" + isPlaying(), Toast.LENGTH_SHORT).show();
                if (controller.isShowing() && isPlaying()) {
                    pause();
                } else {
                    start();
                }
                controller.show();
                return VideoActivity.super.onTouchEvent(event);
            }
        });
        vidHolder = vidSurface.getHolder();
        vidHolder.addCallback(this);
        vidAddress = getIntent().getStringExtra("VIDEO");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mediaPlayer.release();
        finish();
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        controller.setMediaPlayer(this);
        controller.setAnchorView(findViewById(R.id.surfView));
        handler.post(new Runnable() {
            @Override
            public void run() {
                controller.setEnabled(true);
                controller.show();
            }
        });
        mediaPlayer.start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mediaPlayer = new MediaPlayer();
            System.out.println(vidAddress);
            mediaPlayer.setDisplay(vidHolder);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.setDataSource(this, Uri.parse(vidAddress));
            controller = new MediaController(this);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public void seekTo(int i) {
        mediaPlayer.seekTo(i);
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public int getBufferPercentage() {
        return 0;
    }

    public boolean canPause() {
        return true;
    }

    public boolean canSeekBackward() {
        return true;
    }

    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        ProgressDialog.show(this, "Loading", percent + "%");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;
                AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                if (x2 > x1) {
                    //slide left
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (currentVolume - (currentVolume * Math.abs(deltaX))), 0);
                } else {
                    //slide_right
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (currentVolume + (currentVolume * Math.abs(deltaX))), 0);
                }

                break;

        }

        return super.onTouchEvent(event);

    }
}
