package com.example.nikolay.news.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class News {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("viewed")
    @Expose
    private int viewed;
    @SerializedName("resource")
    @Expose
    private int resource;

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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getViewed() {
        return viewed;
    }

    public void setViewed(int viewed) {
        this.viewed = viewed;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }
}
