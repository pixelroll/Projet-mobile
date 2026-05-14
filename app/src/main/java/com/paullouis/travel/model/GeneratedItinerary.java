package com.paullouis.travel.model;

import java.util.List;

public class GeneratedItinerary {
    private String type;
    private String description;
    private int totalBudget;
    private float estimatedDurationHours;
    private String effort;
    private int numberOfSteps;
    private List<TravelDestination> destinations;
    private boolean isLiked;
    private String destinationCity;

    public GeneratedItinerary() {}

    public GeneratedItinerary(String type, String description, int totalBudget,
            float estimatedDurationHours, String effort, int numberOfSteps,
            List<TravelDestination> destinations, boolean isLiked) {
        this.type = type;
        this.description = description;
        this.totalBudget = totalBudget;
        this.estimatedDurationHours = estimatedDurationHours;
        this.effort = effort;
        this.numberOfSteps = numberOfSteps;
        this.destinations = destinations;
        this.isLiked = isLiked;
    }

    public String getTitle() {
        switch (type) {
            case "ECO":
                return "Économique";
            case "BALANCED":
                return "Équilibré";
            case "COMFORT":
                return "Confort";
            default:
                return type;
        }
    }

    public String getDescription() {
        return description;
    }

    public String getBudget() {
        return totalBudget + "€";
    }

    public String getDuration() {
        int h = (int) estimatedDurationHours;
        int m = Math.round((estimatedDurationHours - h) * 60);
        if (m > 0) {
            return h + "h" + m;
        } else {
            return h + "h";
        }
    }

    public String getEffort() {
        switch (effort) {
            case "LOW":
                return "Facile";
            case "MEDIUM":
                return "Modéré";
            case "HIGH":
                return "Difficile";
            default:
                return effort;
        }
    }

    public String getStops() {
        return numberOfSteps + " arrêts";
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    // Setters
    public void setType(String type) { this.type = type; }
    public void setDescription(String description) { this.description = description; }
    public void setTotalBudget(int totalBudget) { this.totalBudget = totalBudget; }
    public void setEstimatedDurationHours(float estimatedDurationHours) { this.estimatedDurationHours = estimatedDurationHours; }
    public void setEffort(String effort) { this.effort = effort; }
    public void setNumberOfSteps(int numberOfSteps) { this.numberOfSteps = numberOfSteps; }
    public void setDestinations(List<TravelDestination> destinations) { this.destinations = destinations; }
    public void setDestinationCity(String destinationCity) { this.destinationCity = destinationCity; }

    // Getters for raw fields
    public String getType() { return type; }
    public int getTotalBudget() { return totalBudget; }
    public float getEstimatedDurationHours() { return estimatedDurationHours; }
    public String getEffortRaw() { return effort; }
    public int getNumberOfSteps() { return numberOfSteps; }
    public List<TravelDestination> getDestinations() { return destinations; }
    public String getDestinationCity() { return destinationCity; }
}
