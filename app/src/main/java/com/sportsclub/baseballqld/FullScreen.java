package com.sportsclub.baseballqld;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.sportsclub.baseballqld.models.Media;
import com.sportsclub.baseballqld.models.MediaAlbum;

public class FullScreen extends AppCompatActivity {

    private VideoView videoView;
    private MediaController mediaController;

    private Media selectedMedia;
    public static MediaAlbum mediaAlbum;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        videoView = findViewById(R.id.videoView);


        String fullScreen =  getIntent().getStringExtra("fullScreenInd");
        if("y".equals(fullScreen)){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().hide();
        }

        Uri videoUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/baseball-qld.appspot.com/o/Videos%2F121%2F1539337723.mp4?alt=media&token=7e1e2d26-9e44-480b-a6fd-edd963491d49");

        videoView.setVideoURI(videoUri);

        mediaController = new FullScreenMediaController(this);
        mediaController.setAnchorView(videoView);

        videoView.setMediaController(mediaController);
        videoView.start();
    }
}

