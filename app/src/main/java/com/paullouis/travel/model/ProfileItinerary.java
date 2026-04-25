package com.paullouis.travel.model;

public class ProfileItinerary {
    private String title;
    private String duration;
    private String location;
    private int imageRes;

    public ProfileItinerary(String title, String duration, String location, int imageRes) {
        this.title = title;
        this.duration = duration;
        this.location = location;
        this.imageRes = imageRes;
    }

    public String getTitle() { return title; }
    public String getDuration() { return duration; }
    public String getLocation() { return location; }
    public int getImageRes() { return imageRes; }
}
