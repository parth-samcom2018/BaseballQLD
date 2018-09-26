package com.sportsclub.baseballqld.models;

public class Profile {

    private boolean error;

    private String messsage;

    private Member.DataEntity data;

    public Profile() {
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMesssage() {
        return messsage;
    }

    public void setMesssage(String messsage) {
        this.messsage = messsage;
    }

    public Member.DataEntity getData() {
        return data;
    }

    public void setData(Member.DataEntity data) {
        this.data = data;
    }
}
