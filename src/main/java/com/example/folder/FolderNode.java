package com.example.folder;

import java.util.ArrayList;
import java.util.List;

public class FolderNode {
    // Đây là class đại diện cho mỗi Folder và các Node con của Folder
    private String name;
    private String relativePath;    //Đường dẫn
    private List<FolderNode> children = new ArrayList<>();  //Danh sách thư mục
    private int fileCount;  //Số lượng files

    // Constructors, Getters, Setters
    public FolderNode(String name, String relativePath, int fileCount) {
        this.name = name;
        this.relativePath = relativePath;
        this.fileCount = fileCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public List<FolderNode> getChildren() {
        return children;
    }

    public void setChildren(List<FolderNode> children) {
        this.children = children;
    }

    public int getFileCount() {
        return fileCount;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }
    
}
