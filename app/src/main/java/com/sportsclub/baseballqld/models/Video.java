package com.sportsclub.baseballqld.models;

import android.net.Uri;

import java.util.Date;
import java.util.List;

public class Video {

    public String url;
    public Uri getUrl;
    public String thumbnail;
    public String caption;
    public Date dateAdded;
    public List<VideoComment> comments;
    public int mediaId;

}
