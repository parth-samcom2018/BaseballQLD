package com.sportsclub.baseballqld;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.sportsclub.baseballqld.models.Media;
import com.sportsclub.baseballqld.models.MediaAlbum;

public class FullScreen extends AppCompatActivity {

    private VideoView videoView;
    private MediaController mediaController;
    String url;


    private Media selectedMedia;
    public static MediaAlbum mediaAlbum;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        videoView = findViewById(R.id.videoView);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            url = extras.getString("url");
            Log.d("data" , ":" + url);
            Toast.makeText(this, "" + url, Toast.LENGTH_LONG).show();
        }

        String fullScreen =  getIntent().getStringExtra("fullScreenInd");
        if("y".equals(fullScreen)){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().hide();
        }

        Uri videoUri = Uri.parse(url);

        videoView.setVideoURI(videoUri);

        mediaController = new FullScreenMediaController(this);
        mediaController.setAnchorView(videoView);

        videoView.setMediaController(mediaController);
        videoView.start();
    }
}

