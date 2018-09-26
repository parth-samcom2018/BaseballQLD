package com.sportsclub.baseballqld.models;


import com.sportsclub.baseballqld.DM;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by carlchute on 16-04-18.
 */
public class Article {

    public int articleId;
    public String shortDescription;
    public String longDescription;
    public Date timeCreated;
    public int authorId;
    public String author;
    public List<ArticleComment> comments = new ArrayList<>();
    public String authorAvatar;

    public String getTimeAgo()
    {
        return DM.getTimeAgo(timeCreated);
    }

    public String getCommentsString()
    {
        if(comments.size() == 1)return "1 comment";
        else return this.comments.size()+" comments";
    }
}
