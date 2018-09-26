package com.sportsclub.baseballqld.models;



import com.sportsclub.baseballqld.DM;

import java.util.Date;

/**
 * Created by carlchute on 16-04-18.
 */
public class ArticleComment {

    public int articleCommentId;
    public String articleCommentDescription;
    public Date timeCreated;
    public int authorId;
    public String author;
    public String authorAvatar;
    public String getTimeAgo()
    {
        return DM.getTimeAgo(timeCreated);
    }
}
