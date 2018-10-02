package com.sportsclub.baseballqld.models;

import java.util.ArrayList;
import java.util.List;

public class ClubResponse {

    private boolean error;

    private String message;

    private List<ClubNames> data = new ArrayList<>();
    private boolean selected;


    public ClubResponse() {
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

    public List<ClubNames> getData() {
        return data;
    }

    public void setData(List<ClubNames> data) {
        this.data = data;
    }


    public void setSelected(boolean selected) {
        this.selected = selected;
    }



}
