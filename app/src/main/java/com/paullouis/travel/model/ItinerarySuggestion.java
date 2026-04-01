package com.paullouis.travel.model;

import java.util.List;

public class ItinerarySuggestion {
    private String name;
    private List<String> places;
    private String duration;
    private String budget;

    public ItinerarySuggestion(String name, List<String> places, String duration, String budget) {
        this.name = name;
        this.places = places;
        this.duration = duration;
        this.budget = budget;
    }

    public String getName() { return name; }
    public List<String> getPlaces() { return places; }
    public String getDuration() { return duration; }
    public String getBudget() { return budget; }
}
