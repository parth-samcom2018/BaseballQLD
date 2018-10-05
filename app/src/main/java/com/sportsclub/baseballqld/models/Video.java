package com.sportsclub.baseballqld.models;

import java.util.Date;
import java.util.List;

public class Video {

    public String url;

    public String thumbnail;
    public String caption;
    public Date dateAdded;
    public List<VideoComment> comments;
    public int videoId;
}
