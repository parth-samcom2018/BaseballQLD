package com.sportsclub.baseballqld.models;

import java.util.ArrayList;
import java.util.List;

public class LaddersResponse {

    private boolean error;
    private String message;
    private List<Ladders> data = new ArrayList<>();

    public LaddersResponse() {
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

    public List<Ladders> getData() {
        return data;
    }

    public void setData(List<Ladders> data) {
        this.data = data;
    }
}
