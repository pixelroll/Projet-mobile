package com.paullouis.travel.data;

import com.paullouis.travel.model.Group;
import com.paullouis.travel.model.Itinerary;
import com.paullouis.travel.model.Notification;
import com.paullouis.travel.model.NotificationSettingItem;
import com.paullouis.travel.model.Photo;
import com.paullouis.travel.model.User;
import com.paullouis.travel.model.Comment;
import com.paullouis.travel.model.GeneratedItinerary;
import com.paullouis.travel.model.ItineraryStep;
import com.paullouis.travel.model.ProfileItinerary;
import com.paullouis.travel.model.StepPhoto;
import com.paullouis.travel.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MockDataProvider {
    private static List<Group> myGroups;
    private static List<Group> discoverGroups;
    private static List<Photo> userPhotos;
    private static User currentUser;

    static {
        myGroups = new ArrayList<>();
        discoverGroups = new ArrayList<>();
        
        // Initialize groups
        Group g1 = new Group("1", "Voyage Paris 2026", "Groupe pour notre voyage a Paris en mars 2026. Partagez vos photos et lieux preferes !", 8, 34, false, true, true, "https://images.unsplash.com/photo-1431274172761-fca41d930114?w=400", "PAR2026", Group.UserRole.ADMIN);
        Group g2 = new Group("2", "Famille Martin", "Photos de voyages en famille", 5, 67, true, true, false, "https://images.unsplash.com/photo-1613278435217-de4e5a91a4ee?w=400", "FAM-MTN", Group.UserRole.MEMBER_WITH_CODE);
        Group g3 = new Group("3", "Amis proches", "Nos aventures entre amis", 12, 89, true, true, false, "https://images.unsplash.com/photo-1626946548234-a65fd193db41?w=400", null, Group.UserRole.MEMBER);
        
        myGroups.add(g1);
        myGroups.add(g2);
        myGroups.add(g3);

        Group g4 = new Group("4", "Backpackers Europe", "Conseils et astuces pour voyager leger en Europe.", 150, 1200, false, false, false, "https://images.unsplash.com/photo-1512453979798-5ea266f8880c?w=400", null, Group.UserRole.MEMBER);
        Group g5 = new Group("5", "Digital Nomads", "La communaute des nomades digitaux.", 320, 2500, false, false, true, "https://images.unsplash.com/photo-1499750310107-5fef28a66643?w=400", null, Group.UserRole.MEMBER);
        Group g6 = new Group("6", "Photographes du monde", "Echangez vos plus beaux clichés.", 85, 900, true, true, false, "https://images.unsplash.com/photo-1452721226468-f9c902774949?w=400", null, Group.UserRole.MEMBER);
        
        discoverGroups.add(g4);
        discoverGroups.add(g5);
        discoverGroups.add(g6);
        
        myGroups.add(g4); // Simulate joined

        // Initialize user photos
        userPhotos = generateInitialPhotos();
    }

    public static User getCurrentUser() {
        if (currentUser == null) {
            currentUser = new User();
            currentUser.setId("u1");
            currentUser.setName("Sophie Martin");
            currentUser.setEmail("sophie.martin@email.com"); // Matched with mockup
            currentUser.setBio("🌍 Voyageur passionné | 🎞 Photographe amateur");
            currentUser.setPostsCount(47);
            currentUser.setFollowersCount(1248);
            currentUser.setFollowingCount(532);
            currentUser.setCountriesVisited(12);
        }
        return currentUser;
    }

    private static List<Photo> generateInitialPhotos() {
        List<Photo> photos = new ArrayList<>();
        String[] urls = {
            "https://images.unsplash.com/photo-1431274172761-fca41d930114?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=800",
            "https://images.unsplash.com/photo-1626946548234-a65fd193db41?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=800",
            "https://images.unsplash.com/photo-1653677903266-1d814985b3cc?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=800",
            "https://images.unsplash.com/photo-1514565131-fce0801e5785?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=800",
            "https://images.unsplash.com/photo-1552832230-c0197dd311b5?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=800",
            "https://images.unsplash.com/photo-1613278435217-de4e5a91a4ee?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=800"
        };
        String[] titles = {"Vue sur la Tour Eiffel", "Coucher de soleil oceanique", "Exploration foret tropicale", "Street food a Tokyo", "Colisee de Rome", "Paysage montagneux"};
        String[] locations = {"Paris, France", "Bali, Indonesie", "Amazonie, Bresil", "Kyoto, Japon", "Rome, Italie", "Alpes, Suisse"};

        for (int i = 0; i < urls.length; i++) {
            Photo p = new Photo();
            p.setId("up" + i);
            p.setUserId("u1");
            p.setImageUrl(urls[i]);
            p.setTitle(titles[i]);
            p.setDescription("Un moment incroyable partage lors de mon dernier voyage.");
            p.setLocationName(locations[i]);
            p.setLikes(100 + (i * 50));
            p.setComments(3 + i);
            p.setTags(Arrays.asList("Voyage", titles[i].split(" ")[0]));
            p.setAuthorName("Sophie Martin");
            p.setAuthorInitial("S");
            p.setDate("Janvier 2026");
            p.setTimestamp(System.currentTimeMillis() - (i * 3600000L * 24));
            photos.add(p);
        }
        return photos;
    }

    public static List<Photo> getUserPhotos() {
        return userPhotos;
    }

    public static void addPhoto(Photo photo) {
        if (userPhotos == null) {
            userPhotos = new ArrayList<>();
        }
        userPhotos.add(0, photo);
    }

    public static List<Photo> getMockPhotos() {
        return getUserPhotos();
    }

    public static User getUserById(String id) {
        if ("u1".equals(id)) {
            User u = new User();
            u.setId("u1");
            u.setName("Sophie Martin");
            u.setAvatarUrl("file:///C:/Users/paulb/.gemini/antigravity/brain/bbdbf13e-aafa-48a3-8941-a5c7384faad7/profile_sophie_martin_1775054776223.png");
            return u;
        } else if ("u2".equals(id)) {
            User u = new User();
            u.setId("u2");
            u.setName("Thomas Martin");
            return u;
        }
        return null;
    }

    public static List<Comment> getMockComments() {
        return getMockComments(null);
    }

    public static List<Comment> getMockComments(String photoId) {
        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment("Marie L.", "M", "Il y a 2 jours", "Magnifique photo ! J'y etais la semaine derniere."));
        comments.add(new Comment("Thomas B.", "T", "Il y a 3 jours", "Quelle vue ! Merci pour le partage"));
        comments.add(new Comment("Sophie M.", "S", "Il y a 5 jours", "C'est exactement ce que je cherchais pour mon voyage !"));
        return comments;
    }

    public static List<Photo> getSimilarPhotos(Photo photo) {
        return getSimilarPhotos(photo != null ? photo.getLocationName() : null);
    }

    public static List<Photo> getSimilarPhotos(String location) {
        List<Photo> similar = new ArrayList<>();
        Photo p1 = new Photo();
        p1.setId("s1");
        p1.setTitle("Arc de Triomphe");
        p1.setImageResId(R.drawable.arc_de_triomphe);
        similar.add(p1);

        Photo p2 = new Photo();
        p2.setId("s2");
        p2.setTitle("Notre-Dame");
        p2.setImageResId(R.drawable.notre_dame);
        similar.add(p2);

        Photo p3 = new Photo();
        p3.setId("s3");
        p3.setTitle("Louvre");
        p3.setImageResId(R.drawable.louvre);
        similar.add(p3);

        return similar;
    }

    public static List<Notification> getNotifications() {
        List<Notification> notifications = new ArrayList<>();
        
        Notification n1 = new Notification();
        n1.setId(1);
        n1.setType(Notification.Type.LIKE);
        n1.setUserName("Thomas Martin");
        n1.setContent("a aimé votre photo");
        n1.setTime("Il y a 5 min");
        n1.setRead(false);
        notifications.add(n1);

        Notification n2 = new Notification();
        n2.setId(2);
        n2.setType(Notification.Type.COMMENT);
        n2.setUserName("Marie Lefebvre");
        n2.setContent("a commenté : 'Magnifique !'");
        n2.setTime("Il y a 1h");
        n2.setRead(false);
        notifications.add(n2);

        Notification n3 = new Notification();
        n3.setId(3);
        n3.setType(Notification.Type.FOLLOW);
        n3.setUserName("Lucas Bernard");
        n3.setContent("a commencé à vous suivre");
        n3.setTime("Il y a 3h");
        n3.setRead(true);
        notifications.add(n3);

        return notifications;
    }

    public static List<NotificationSettingItem> getNotificationSettings() {
        List<NotificationSettingItem> settings = new ArrayList<>();
        settings.add(new NotificationSettingItem("s1", NotificationSettingItem.Type.PERSON, "Mentions", true));
        settings.add(new NotificationSettingItem("s2", NotificationSettingItem.Type.PERSON, "Nouveaux abonnés", true));
        settings.add(new NotificationSettingItem("s3", NotificationSettingItem.Type.GROUP, "Messages de groupe", false));
        settings.add(new NotificationSettingItem("s4", NotificationSettingItem.Type.PLACE, "Lieux populaires", true));
        return settings;
    }

    public static Group findGroupByCode(String code) {
        if (code == null) return null;
        for (Group g : myGroups) {
            if (code.equalsIgnoreCase(g.getCode())) return g;
        }
        for (Group g : discoverGroups) {
            if (code.equalsIgnoreCase(g.getCode())) return g;
        }
        return null;
    }

    public static List<Group> getMyGroups() { return myGroups; }
    public static List<Group> getDiscoverGroups() { return discoverGroups; }

    public static Group getGroupById(String id) {
        for (Group g : myGroups) {
            if (g.getId().equals(id)) return g;
        }
        for (Group g : discoverGroups) {
            if (g.getId().equals(id)) return g;
        }
        return null;
    }

    public static void joinGroup(String groupId) {
        for (Group g : discoverGroups) {
            if (g.getId().equals(groupId)) {
                boolean alreadyJoined = false;
                for (Group myG : myGroups) {
                    if (myG.getId().equals(groupId)) {
                        alreadyJoined = true;
                        break;
                    }
                }
                if (!alreadyJoined) {
                    myGroups.add(g);
                }
                break;
            }
        }
    }

    public static void addGroup(Group group) {
        if (myGroups == null) {
            myGroups = new ArrayList<>();
        }
        myGroups.add(0, group);
    }

    public static List<Photo> getGatewayPhotos() {
        List<Photo> photos = new ArrayList<>();
        
        String[] locations = {
            "Tour Eiffel", "Musée du Louvre", "Jardin du Luxembourg", 
            "Notre-Dame", "Arc de Triomphe", "Montmartre"
        };
        
        int[] resIds = {
            0, // Eiffel uses URL
            R.drawable.louvre,
            0, // Luxembourg uses URL
            R.drawable.notre_dame,
            R.drawable.arc_de_triomphe,
            0 // Montmartre uses URL
        };

        String[] urls = {
            "https://images.unsplash.com/photo-1431274172761-fca41d930114?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=800", // Eiffel
            null,
            "https://images.unsplash.com/photo-1626946548234-a65fd193db41?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=800", // Jardin Luxembourg
            null,
            null,
            "https://images.unsplash.com/photo-1653677903266-1d814985b3cc?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=800" // Montmartre
        };

        for (int i = 0; i < locations.length; i++) {
            Photo p = new Photo();
            p.setId("gw_" + i);
            p.setTitle(locations[i]);
            p.setLocationName("Paris");
            if (resIds[i] != 0) {
                p.setImageResId(resIds[i]);
            } else {
                p.setImageUrl(urls[i]);
            }
            photos.add(p);
        }
        return photos;
    }

    public static List<GeneratedItinerary> getGeneratedItineraries() {
        List<GeneratedItinerary> itineraries = new ArrayList<>();
        itineraries.add(new GeneratedItinerary(
            "Économique",
            "Découverte des essentiels avec un budget limité",
            "45€",
            "6h30",
            "Modéré",
            "8 arrêts",
            false
        ));
        itineraries.add(new GeneratedItinerary(
            "Équilibré",
            "Un mélange parfait de culture, gastronomie et détente",
            "95€",
            "8h",
            "Modéré",
            "10 arrêts",
            false
        ));
        itineraries.add(new GeneratedItinerary(
            "Confort",
            "Expérience premium avec des pauses régulières",
            "180€",
            "7h30",
            "Facile",
            "7 arrêts",
            false
        ));
        return itineraries;
    }

    public static List<ItineraryStep> getItinerarySteps() {
        List<ItineraryStep> steps = new ArrayList<>();
        
        ItineraryStep step1 = new ItineraryStep(
                "09:00", "Petit-déjeuner au Café de Flore", "Café historique du quartier Saint-Germain",
                "07:30 – 01:30", "Ouvert", "1h", "15€", "Matin", "2 photos • 1 vidéo",
                R.drawable.ic_map_pin, R.drawable.profile_sophie
        );
        step1.setPhotos(Arrays.asList(
            new StepPhoto(R.drawable.profile_sophie, "Terrasse du Café de...", false),
            new StepPhoto(R.drawable.profile_sophie, "Café et croissant", false),
            new StepPhoto(R.drawable.profile_sophie, "Ambiance matinale", true)
        ));
        steps.add(step1);

        ItineraryStep step2 = new ItineraryStep(
                "10:30", "Musée du Louvre", "Visite des collections principales",
                "09:00 – 18:00", "Ouvert", "2h30", "17€", "Matin", "3 photos • 1 vidéo",
                R.drawable.ic_map_pin, R.drawable.profile_sophie
        );
        step2.setPhotos(Arrays.asList(
            new StepPhoto(R.drawable.profile_sophie, "Pyramide du Louvre", false),
            new StepPhoto(R.drawable.profile_sophie, "Grande galerie", false),
            new StepPhoto(R.drawable.profile_sophie, "Joconde", true)
        ));
        steps.add(step2);

        ItineraryStep step3 = new ItineraryStep(
                "13:00", "Déjeuner à La Palette", "Restaurant traditionnel avec terrasse",
                "12:00 – 23:00", "Ouvert", "1h30", "28€", "Après-midi", "2 photos",
                R.drawable.ic_map_pin, R.drawable.profile_sophie
        );
        step3.setPhotos(Arrays.asList(
            new StepPhoto(R.drawable.profile_sophie, "Terrasse", false),
            new StepPhoto(R.drawable.profile_sophie, "Menu du jour", false)
        ));
        steps.add(step3);

        ItineraryStep step4 = new ItineraryStep(
                "14:30", "Jardin du Luxembourg", "Balade relaxante dans les jardins",
                "07:30 – 21:30", "Ouvert", "1h", "0€", "Après-midi", "2 photos • 1 vidéo",
                R.drawable.ic_map_pin, R.drawable.profile_sophie
        );
        step4.setPhotos(Arrays.asList(
            new StepPhoto(R.drawable.profile_sophie, "Fontaine", false),
            new StepPhoto(R.drawable.profile_sophie, "Allée principale", false),
            new StepPhoto(R.drawable.profile_sophie, "Pigeons", true)
        ));
        steps.add(step4);

        ItineraryStep step5 = new ItineraryStep(
                "16:00", "Tour Eiffel", "Montée au 2ème étage",
                "09:30 – 23:45", "Ouvert", "1h30", "26€", "Après-midi", "2 photos • 1 vidéo",
                R.drawable.ic_map_pin, R.drawable.profile_sophie
        );
        step5.setPhotos(Arrays.asList(
            new StepPhoto(R.drawable.profile_sophie, "Vue du dessus", false),
            new StepPhoto(R.drawable.profile_sophie, "Escaliers", false),
            new StepPhoto(R.drawable.profile_sophie, "Coucher de soleil", true)
        ));
        steps.add(step5);

        ItineraryStep step6 = new ItineraryStep(
                "18:00", "Pause café - Carette", "Pâtisserie renommée du Trocadéro",
                "08:00 – 23:30", "Ouvert", "45min", "9€", "Soir", "2 photos",
                R.drawable.ic_map_pin, R.drawable.profile_sophie
        );
        step6.setPhotos(Arrays.asList(
            new StepPhoto(R.drawable.profile_sophie, "Vitrine", false),
            new StepPhoto(R.drawable.profile_sophie, "Tasse et macaron", false)
        ));
        steps.add(step6);

        return steps;
    }

    public static List<ProfileItinerary> getProfileItineraries() {
        List<ProfileItinerary> itineraries = new ArrayList<>();
        itineraries.add(new ProfileItinerary(
                "Paris Romantique", "3 jours", "Paris, France", R.drawable.profile_sophie
        ));
        itineraries.add(new ProfileItinerary(
                "Aventure à Tokyo", "7 jours", "Tokyo, Japon", R.drawable.profile_sophie
        ));
        itineraries.add(new ProfileItinerary(
                "Barcelone Culturelle", "4 jours", "Barcelone, Espagne", R.drawable.profile_sophie
        ));
        return itineraries;
    }
}
