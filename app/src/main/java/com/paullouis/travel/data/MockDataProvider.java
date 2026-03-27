package com.paullouis.travel.data;

import com.paullouis.travel.model.Itinerary;
import com.paullouis.travel.model.Photo;
import com.paullouis.travel.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MockDataProvider {

    public static List<Photo> getMockPhotos() {
        List<Photo> photos = new ArrayList<>();
        
        Photo p1 = new Photo();
        p1.setId("p1");
        p1.setUserId("u1");
        p1.setTitle("Coucher de soleil à Santorin");
        p1.setDescription("Une vue magnifique depuis notre hôtel. Les couleurs étaient incroyables.");
        p1.setLocationName("Santorin, Grèce");
        // Placeholder image URL
        p1.setImageUrl("https://images.unsplash.com/photo-1613395877344-13d4a8e0d49e?auto=format&fit=crop&q=80&w=800");
        p1.setLikesCount(142);
        p1.setTags(Arrays.asList("nature", "voyage", "grece"));
        p1.setVisibility("PUBLIC");
        
        Photo p2 = new Photo();
        p2.setId("p2");
        p2.setUserId("u2");
        p2.setTitle("Rues de Kyoto");
        p2.setDescription("Exploration nocturne des petites ruelles pleines de charme.");
        p2.setLocationName("Kyoto, Japon");
        p2.setImageUrl("https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?auto=format&fit=crop&q=80&w=800");
        p2.setLikesCount(89);
        p2.setTags(Arrays.asList("ville", "japon", "nuit"));
        p2.setVisibility("PUBLIC");

        photos.add(p1);
        photos.add(p2);
        
        return photos;
    }

    public static List<Itinerary> getMockItineraries() {
        List<Itinerary> itineraries = new ArrayList<>();
        
        Itinerary i1 = new Itinerary();
        i1.setId("i1");
        i1.setTitle("Parcours Économique");
        i1.setMatchType("ECON");
        i1.setTotalBudget(45);
        i1.setTotalDurationMinutes(360);
        i1.setEffortLevel("Élevé (Marche)");
        
        Itinerary i2 = new Itinerary();
        i2.setId("i2");
        i2.setTitle("Parcours Équilibré");
        i2.setMatchType("BALANCED");
        i2.setTotalBudget(120);
        i2.setTotalDurationMinutes(420);
        i2.setEffortLevel("Modéré (Transports)");

        Itinerary i3 = new Itinerary();
        i3.setId("i3");
        i3.setTitle("Parcours Confort");
        i3.setMatchType("COMFORT");
        i3.setTotalBudget(300);
        i3.setTotalDurationMinutes(300);
        i3.setEffortLevel("Faible (VTC)");

        itineraries.add(i1);
        itineraries.add(i2);
        itineraries.add(i3);
        
        return itineraries;
    }
}
