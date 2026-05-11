package com.paullouis.travel.model;

public class StepPhoto {
    private int drawableRes;
    private String imageUrl;
    private String label;
    private boolean isVideo;

    public StepPhoto(int drawableRes, String label, boolean isVideo) {
        this.drawableRes = drawableRes;
        this.label = label;
        this.isVideo = isVideo;
    }

    public StepPhoto(String imageUrl, String label) {
        this.imageUrl = imageUrl;
        this.label = label;
        this.drawableRes = 0;
        this.isVideo = false;
    }

    public int getDrawableRes() { return drawableRes; }
    public String getImageUrl() { return imageUrl; }
    public String getLabel() { return label; }
    public boolean isVideo() { return isVideo; }
}
