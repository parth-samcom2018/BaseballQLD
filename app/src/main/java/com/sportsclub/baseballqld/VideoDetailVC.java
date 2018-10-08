package com.sportsclub.baseballqld;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sportsclub.baseballqld.models.VideoAlbum;

public class VideoDetailVC extends BaseVC {

    public static VideoAlbum mediaAlbum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_details);
    }
}
