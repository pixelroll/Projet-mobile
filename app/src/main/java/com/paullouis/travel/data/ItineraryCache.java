package com.paullouis.travel.data;

import com.paullouis.travel.model.GeneratedItinerary;

import java.util.ArrayList;
import java.util.List;

public class ItineraryCache {
    private static List<GeneratedItinerary> itineraries = new ArrayList<>();
    private static int selectedIndex = 0;

    public static void set(List<GeneratedItinerary> list) {
        itineraries = list;
    }

    public static List<GeneratedItinerary> get() {
        return itineraries;
    }

    public static void setSelectedIndex(int index) {
        selectedIndex = index;
    }

    public static GeneratedItinerary getSelected() {
        if (itineraries == null || selectedIndex >= itineraries.size()) {
            return null;
        }
        return itineraries.get(selectedIndex);
    }

    public static void clear() {
        itineraries.clear();
        selectedIndex = 0;
    }
}
