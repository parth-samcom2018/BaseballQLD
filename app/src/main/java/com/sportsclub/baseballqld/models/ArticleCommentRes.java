package com.sportsclub.baseballqld.models;

import java.util.ArrayList;
import java.util.List;

public class ArticleCommentRes {

    private boolean error;

    private String message;

    private List<ArticleComment> data = new ArrayList<>();

    public ArticleCommentRes() {
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

    public List<ArticleComment> getData() {
        return data;
    }

    public void setData(List<ArticleComment> data) {
        this.data = data;
    }
}
