package com.storyspot.data;

/**
 * Created by rickylaishram on 11/15/13.
 */
public class SummaryData {
    public String name;
    public String description;
    public String image;
    public int distance;
    public double latitude;
    public double longitude;

    public void setData(String name, String description, String image, int distance, double latitude, double longitude) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.distance = distance;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
