package com.sportsclub.baseballqld.models;

public class GroupFoldersRes {
    private boolean error;


    private String message;

    private GroupFolders data;

    public GroupFoldersRes() {
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

    public GroupFolders getData() {
        return data;
    }

    public void setData(GroupFolders data) {
        this.data = data;
    }

}
