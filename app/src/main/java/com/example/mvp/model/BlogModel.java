package com.example.mvp.model;

public class BlogModel {
    //class for blog model serves as a container for blog data

    //variables
    private int id;
    private String title;
    private String content;
    private String imagePath;
    private String dateTime;

    //constructor
    public BlogModel(int id, String title, String content, String imagePath, String dateTime) {

        this.id = id;
        this.title = title;
        this.content = content;
        this.imagePath = imagePath;
        this.dateTime = dateTime;

    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
