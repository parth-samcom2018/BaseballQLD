package com.sportsclub.baseballqld.models;

import java.util.ArrayList;
import java.util.List;

public class GroupResponse {

    private boolean error;
    private String message;
    private List<Group> data = new ArrayList<>();

    public GroupResponse() {
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

    public List<Group> getData() {
        return data;
    }

    public void setData(List<Group> data) {
        this.data = data;
    }
}
