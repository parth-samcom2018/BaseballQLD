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
    public List<Media> mediaModels = new ArrayList<>();
    public String createdByAvatar;
    public String coverImage;


    public void sortMediaAlbumsByDate()
    {
        Collections.sort(this.mediaModels, new Comparator<Media>(){
            public int compare(Media emp1, Media emp2) {

                //descending ids = descending date, better to fix in api but oh well
                return Integer.valueOf(emp2.mediaId).compareTo(emp1.mediaId); // To compare integer values


            }
        });


    }
}
