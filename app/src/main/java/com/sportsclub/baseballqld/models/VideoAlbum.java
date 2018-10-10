package com.sportsclub.baseballqld.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class VideoAlbum extends Video {

    public int mediaAlbumId;
    public String name;
    public String createdBy;
    public String albumDescription;
    public List<Video> mediaModels = new ArrayList<>();
    public String createdByAvatar;
    public String coverImage;


    public void sortVideoAlbumsByDate()
    {
        Collections.sort(this.mediaModels, new Comparator<Video>(){
            public int compare(Video emp1, Video emp2) {

                //descending ids = descending date, better to fix in api but oh well
                return Integer.valueOf(emp2.videoId).compareTo(emp1.videoId); // To compare integer values


            }
        });


    }


}
