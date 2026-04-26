package com.paullouis.travel.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class OfflineManager {
    private static final String PREF_NAME = "offline_data";
    private static final String KEY_SAVED_ITINERARIES = "saved_itineraries";

    public static void saveItineraryOffline(Context context, String itineraryId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> saved = prefs.getStringSet(KEY_SAVED_ITINERARIES, new HashSet<>());
        Set<String> newSet = new HashSet<>(saved);
        newSet.add(itineraryId);
        prefs.edit().putStringSet(KEY_SAVED_ITINERARIES, newSet).apply();
    }

    public static boolean isItinerarySaved(Context context, String itineraryId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> saved = prefs.getStringSet(KEY_SAVED_ITINERARIES, new HashSet<>());
        return saved.contains(itineraryId);
    }

    public static void removeItineraryOffline(Context context, String itineraryId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> saved = prefs.getStringSet(KEY_SAVED_ITINERARIES, new HashSet<>());
        Set<String> newSet = new HashSet<>(saved);
        newSet.remove(itineraryId);
        prefs.edit().putStringSet(KEY_SAVED_ITINERARIES, newSet).apply();
    }
}
