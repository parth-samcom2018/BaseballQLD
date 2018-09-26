package com.sportsclub.baseballqld.models;

import java.util.ArrayList;
import java.util.List;

public class EventResponse {

    private boolean error;

    private String message;

    private List<Event> data = new ArrayList<>();

    public EventResponse() {
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

    public List<Event> getData() {
        return data;
    }

    public void setData(List<Event> data) {
        this.data = data;
    }
}
