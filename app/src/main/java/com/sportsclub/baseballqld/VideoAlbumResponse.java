package com.sportsclub.baseballqld;

import com.sportsclub.baseballqld.models.Video;
import com.sportsclub.baseballqld.models.VideoAlbum;

import java.util.List;

public class VideoAlbumResponse extends Video {

    private boolean error;
    private String message;
    private List<VideoAlbum> data;

    public VideoAlbumResponse() {
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<VideoAlbum> getData() {
        return data;
    }

    public void setData(List<VideoAlbum> data) {
        this.data = data;
    }

}

