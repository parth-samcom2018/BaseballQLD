package com.sportsclub.baseballqld.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MediaAlbum extends Media {

    public int mediaAlbumId;
    public String name;
    public String createdBy;
    public String albumDescription;
    public List<Media> mediaModels = new ArrayList<>();
    //public List<Video> videoModels = new ArrayList<>();
    public String createdByAvatar;
    public String coverImage;
    public int folderType;


    public void sortMediaAlbumsByDate()
    {
        Collections.sort(this.mediaModels, new Comparator<Media>(){
            public int compare(Media emp1, Media emp2) {

                //descending ids = descending date, better to fix in api but oh well
                return Integer.valueOf(emp2.mediaId).compareTo(emp1.mediaId); // To compare integer values


            }
        });


    }

   /* public void sortVideoAlbumByDate() {
        Collections.sort(this.videoModels, new Comparator<Video>() {
            @Override
            public int compare(Video video, Video t1) {
                return Integer.valueOf(video.mediaId).compareTo(t1.mediaId);
            }
        });
    }*/
}
