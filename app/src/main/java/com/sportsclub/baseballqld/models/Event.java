package com.sportsclub.baseballqld.models;

import com.google.gson.annotations.SerializedName;
import com.sportsclub.baseballqld.DM;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event {

    public int eventId;
    public int memberId;
    public String eventName;
    public Date eventStart;
    public Date eventEnd;
    public String location;
    @SerializedName("groupId")
    public int familyId;
    public boolean AddNotificationToGroupHome;
    public boolean sendEmailNotification;
    public String notes;
    public double longitude;
    public double latitude;
    public Date dateAdded;
    public List<EventComment> comments = new ArrayList<>();
    public String memberAvatar;
    public String groupName;
    public String getDateString()
    {
        return DM.getDateOnlyString(eventStart);
    }

    public String getTimeString()
    {
        return DM.getTimeOnlyString(eventStart);
    }
}
