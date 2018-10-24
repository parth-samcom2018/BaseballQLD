package com.sportsclub.baseballqld.models;


import com.sportsclub.baseballqld.models.VideoAlbum;

import java.util.List;

public class VideoAlbumResponse  {

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

