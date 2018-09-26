package com.sportsclub.baseballqld.models;

import java.util.ArrayList;
import java.util.List;

public class ArticleResponse {

    private boolean error;

    private String message;

    private List<Article> data = new ArrayList<>();

    public ArticleResponse() {
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

    public List<Article> getData() {
        return data;
    }

    public void setData(List<Article> data) {
        this.data = data;
    }
}
