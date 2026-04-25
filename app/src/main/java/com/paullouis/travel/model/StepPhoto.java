package com.paullouis.travel.model;

public class StepPhoto {
    private int drawableRes;
    private String label;
    private boolean isVideo;

    public StepPhoto(int drawableRes, String label, boolean isVideo) {
        this.drawableRes = drawableRes;
        this.label = label;
        this.isVideo = isVideo;
    }

    public int getDrawableRes() {
        return drawableRes;
    }

    public String getLabel() {
        return label;
    }

    public boolean isVideo() {
        return isVideo;
    }
}
