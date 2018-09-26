package com.sportsclub.baseballqld.models;

import java.util.ArrayList;
import java.util.List;

public class NotificationResponse {

    private boolean error;
    private String message;
    private List<Notification> data = new ArrayList<>();

    public NotificationResponse() {
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

    public List<Notification> getData() {
        return data;
    }

    public void setData(List<Notification> data) {
        this.data = data;
    }
}
