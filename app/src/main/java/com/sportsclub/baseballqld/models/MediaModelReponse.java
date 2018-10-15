package com.sportsclub.baseballqld.models;

import java.util.ArrayList;
import java.util.List;

public class MediaModelReponse {

    private boolean error;
    private String message;
    private List<MediaAlbum> data = new ArrayList<>();

    public MediaModelReponse() {
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

    public List<MediaAlbum> getData() {
        return data;
    }

    public void setData(List<MediaAlbum> data) {
        this.data = data;
    }
}
