package com.storyspot.data;

/**
 * Created by rickylaishram on 11/15/13.
 */
public class NearbyData {
    public String title;
    public String description;
    public String distance;
    public String image;

    public void setData(String title, String description, String distance, String image) {
        this.title = title;
        this.description = description;
        this.distance = distance;
        this.image = image;
    }
}
