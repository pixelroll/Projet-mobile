package com.paullouis.travel.model;

public class GeneratedItinerary {
    private String title;
    private String description;
    private String budget;
    private String duration;
    private String effort;
    private String stops;
    private boolean isLiked;

    public GeneratedItinerary(String title, String description, String budget, String duration, String effort, String stops, boolean isLiked) {
        this.title = title;
        this.description = description;
        this.budget = budget;
        this.duration = duration;
        this.effort = effort;
        this.stops = stops;
        this.isLiked = isLiked;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getBudget() { return budget; }
    public String getDuration() { return duration; }
    public String getEffort() { return effort; }
    public String getStops() { return stops; }
    public boolean isLiked() { return isLiked; }
    public void setLiked(boolean liked) { isLiked = liked; }
}
