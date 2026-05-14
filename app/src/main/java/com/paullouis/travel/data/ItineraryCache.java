package com.paullouis.travel.data;

import com.paullouis.travel.model.GeneratedItinerary;

import java.util.ArrayList;
import java.util.List;

public class ItineraryCache {
    private static List<GeneratedItinerary> itineraries = new ArrayList<>();
    private static int selectedIndex = 0;
    private static GeneratedItinerary fallbackSelected = null;

    public static void set(List<GeneratedItinerary> list) {
        itineraries = list;
        fallbackSelected = null;
    }

    public static List<GeneratedItinerary> get() {
        return itineraries;
    }

    public static void setSelectedIndex(int index) {
        selectedIndex = index;
        fallbackSelected = null;
    }

    public static void setSelectedFallback(GeneratedItinerary fallback) {
        fallbackSelected = fallback;
    }

    public static GeneratedItinerary getSelected() {
        if (fallbackSelected != null) {
            return fallbackSelected;
        }
        if (itineraries == null || selectedIndex < 0 || selectedIndex >= itineraries.size()) {
            return null;
        }
        return itineraries.get(selectedIndex);
    }

    public static void clear() {
        itineraries.clear();
        selectedIndex = 0;
        fallbackSelected = null;
    }
}
