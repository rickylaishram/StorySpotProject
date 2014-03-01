package com.storyspot.data;

/**
 * Created by rickylaishram on 11/15/13.
 */
public class NearbyData {
    public String title;
    public String description;
    public String distance;
    public String image;
    public String creator;
    public String date;
    public String story_id;

    public void setData(String title, String description, String distance, String image, String creator, String date, String story_id) {
        this.title = title;
        this.description = description;
        this.distance = distance;
        this.image = image;
        this.creator = creator;
        this.date = date;
        this.story_id = story_id;
    }
}
