package com.sportsclub.baseballqld.models;

import com.sportsclub.baseballqld.DM;

import java.util.Date;
import java.util.List;

public class Notification {

    public int notificationId;
    public int memberId;
    public String memberName;
    public String memberAvatar;
    public Date addedDate;
    public String text;
    public int notificationTypeId;
    public int notificationItemId; //albumID for media items (hacky from API)
    public int mediaId; //media item id
    public int familyId;
    public String familyName;
    public List<NotificationComment> comments;
    public String thumbnailUrl;


    //optional after attach
    //public Media media;


    public static final int TYPE_EVENT = 1;
    public static final int TYPE_ARTICLE = 2;
    public static  final int TYPE_MEDIA = 3;
    public static final int TYPE_NOTIFICATION = 4;

    public String getTimeAgo()
    {
        return DM.getTimeAgo(addedDate);
    }

    public String getCommentsString()
    {
        if(comments.size() == 1)return "1 comment";
        else return this.comments.size()+" comments";
    }
}
