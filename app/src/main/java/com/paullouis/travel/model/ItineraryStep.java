package com.paullouis.travel.model;

import java.util.List;

public class ItineraryStep {
    private String time;
    private String title;
    private String description;
    private String hours;
    private String status;
    private String duration;
    private String price;
    private String period;
    private String mediaInfo;
    private int typeIconRes;
    private int imageRes;
    private List<StepPhoto> photos;
    private double latitude;
    private double longitude;

    public ItineraryStep(String time, String title, String description, String hours, String status, String duration, String price, String period, String mediaInfo, int typeIconRes, int imageRes) {
        this.time = time;
        this.title = title;
        this.description = description;
        this.hours = hours;
        this.status = status;
        this.duration = duration;
        this.price = price;
        this.period = period;
        this.mediaInfo = mediaInfo;
        this.typeIconRes = typeIconRes;
        this.imageRes = imageRes;
    }

    public ItineraryStep(String time, String title, String description, String hours, String status, String duration, String price, String period, String mediaInfo, int typeIconRes, int imageRes, double latitude, double longitude) {
        this(time, title, description, hours, status, duration, price, period, mediaInfo, typeIconRes, imageRes);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getTime() { return time; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getHours() { return hours; }
    public String getStatus() { return status; }
    public String getDuration() { return duration; }
    public String getPrice() { return price; }
    public String getPeriod() { return period; }
    public String getMediaInfo() { return mediaInfo; }
    public int getTypeIconRes() { return typeIconRes; }
    public int getImageRes() { return imageRes; }
    public List<StepPhoto> getPhotos() { return photos; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    public void setPhotos(List<StepPhoto> photos) { this.photos = photos; }
}
