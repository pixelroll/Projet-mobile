package com.paullouis.travel.model;

import java.util.ArrayList;
import java.util.List;

public class SavedItinerary {
    private String id;
    private String userId;
    private String title;
    private String locationName;
    private String date;
    private String type;
    private String description;
    private int totalBudget;
    private float estimatedDurationHours;
    private String effort;
    private long createdAt;
    private List<TravelDestination> steps;

    public SavedItinerary() {}

    public static SavedItinerary from(GeneratedItinerary itinerary, String userId, String locationName, String date) {
        SavedItinerary saved = new SavedItinerary();
        saved.userId = userId;
        String loc = (locationName != null && !locationName.isEmpty()) ? locationName : "";
        saved.title = "Parcours " + itinerary.getTitle() + (loc.isEmpty() ? "" : " - " + loc);
        saved.locationName = locationName;
        saved.date = date;
        saved.type = itinerary.getType();
        saved.description = itinerary.getDescription();
        saved.totalBudget = itinerary.getTotalBudget();
        saved.estimatedDurationHours = itinerary.getEstimatedDurationHours();
        saved.effort = itinerary.getEffortRaw();
        saved.createdAt = System.currentTimeMillis();
        saved.steps = itinerary.getDestinations() != null
                ? new ArrayList<>(itinerary.getDestinations())
                : new ArrayList<>();
        return saved;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getTotalBudget() { return totalBudget; }
    public void setTotalBudget(int totalBudget) { this.totalBudget = totalBudget; }

    public float getEstimatedDurationHours() { return estimatedDurationHours; }
    public void setEstimatedDurationHours(float h) { this.estimatedDurationHours = h; }

    public String getEffort() { return effort; }
    public void setEffort(String effort) { this.effort = effort; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public List<TravelDestination> getSteps() { return steps; }
    public void setSteps(List<TravelDestination> steps) { this.steps = steps; }

    public String getDurationFormatted() {
        int h = (int) estimatedDurationHours;
        int m = Math.round((estimatedDurationHours - h) * 60);
        return m > 0 ? h + "h" + m : h + "h";
    }

    public String getBudgetFormatted() {
        return totalBudget + "€";
    }
}
