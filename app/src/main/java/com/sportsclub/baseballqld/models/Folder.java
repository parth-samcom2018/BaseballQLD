package com.sportsclub.baseballqld.models;

import java.util.List;

public class Folder {
    public int folderId;
    public String folderName;
    public int groupId;
    public Integer parentFolderId;
    public boolean isActive;
    public List<File> files;
    public List<Folder> childFolders;
}
