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
import com.paullouis.travel.model.SearchNavigationOption;
import com.paullouis.travel.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MockDataProvider implements DataRepository {
    private static MockDataProvider instance;

    public static MockDataProvider getInstance() {
        if (instance == null) {
            instance = new MockDataProvider();
        }
        return instance;
    }

    private MockDataProvider() {}

    private static List<Group> myGroups;
    private static List<Group> discoverGroups;
    private static List<Photo> userPhotos;
    private static User currentUser;
    private static List<SearchNavigationOption> searchNavigationOptions;
    private static boolean userLoggedIn = false; // Par défaut non connecté pour montrer la modale

    static {
        myGroups = new ArrayList<>();
        discoverGroups = new ArrayList<>();

        // Initialize user photos
        userPhotos = generateInitialPhotos();
        
        // Associate some photos with groups
        userPhotos.get(0).setGroupId("1");
        userPhotos.get(0).setGroupName("Voyage Paris 2026");
        userPhotos.get(4).setGroupId("2");
        userPhotos.get(4).setGroupName("Famille Martin");

        // Initialize search navigation options
        searchNavigationOptions = new ArrayList<>();
        searchNavigationOptions.add(new SearchNavigationOption("random", "Découverte aléatoire", "Explorez des photos au hasard", R.drawable.ic_shuffle));
        searchNavigationOptions.add(new SearchNavigationOption("location", "Par lieu", "Parcourir par destination", R.drawable.ic_map_pin));
        searchNavigationOptions.add(new SearchNavigationOption("period", "Par période", "Explorer par date ou saison", R.drawable.ic_calendar));
        searchNavigationOptions.add(new SearchNavigationOption("author", "Par auteur", "Découvrir par photographe", R.drawable.ic_user));
        searchNavigationOptions.add(new SearchNavigationOption("similar", "Photos similaires", "Recherche par similarité (IA)", R.drawable.ic_sparkles));
    }

    @Override
    public boolean isUserLoggedIn() {
        return userLoggedIn;
    }

    public static void setUserLoggedIn(boolean loggedIn) {
        userLoggedIn = loggedIn;
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

    /**
     * @deprecated Use {@link #getUserPhotos()} directly.
     */
    @Deprecated
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

    /**
     * Returns a fixed list of mock comments.
     * Note: photoId parameter is reserved for future Firebase filtering.
     */
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
        if (groupId == null) return;
        for (Group g : discoverGroups) {
            if (g.getId().equals(groupId)) {
                boolean alreadyJoined = myGroups.stream()
                        .anyMatch(myG -> myG.getId().equals(groupId));
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
                R.drawable.ic_map_pin, R.drawable.profile_sophie,
                48.8541, 2.3331
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
                R.drawable.ic_map_pin, R.drawable.profile_sophie,
                48.8606, 2.3376
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
                R.drawable.ic_map_pin, R.drawable.profile_sophie,
                48.8559, 2.3346
        );
        step3.setPhotos(Arrays.asList(
            new StepPhoto(R.drawable.profile_sophie, "Terrasse", false),
            new StepPhoto(R.drawable.profile_sophie, "Menu du jour", false)
        ));
        steps.add(step3);

        ItineraryStep step4 = new ItineraryStep(
                "14:30", "Jardin du Luxembourg", "Balade relaxante dans les jardins",
                "07:30 – 21:30", "Ouvert", "1h", "0€", "Après-midi", "2 photos • 1 vidéo",
                R.drawable.ic_map_pin, R.drawable.profile_sophie,
                48.8462, 2.3371
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
                R.drawable.ic_map_pin, R.drawable.profile_sophie,
                48.8584, 2.2945
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
                R.drawable.ic_map_pin, R.drawable.profile_sophie,
                48.8631, 2.2872
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

    public static List<SearchNavigationOption> getSearchNavigationOptions() {
        return searchNavigationOptions;
    }

    public static List<com.paullouis.travel.model.GroupMember> getGroupMembers(String groupId) {
        List<com.paullouis.travel.model.GroupMember> members = new ArrayList<>();
        members.add(new com.paullouis.travel.model.GroupMember(getCurrentUser(), Group.UserRole.OWNER, 47, "Aujourd'hui"));
        members.add(new com.paullouis.travel.model.GroupMember(new User("u2", "Thomas Martin", null), Group.UserRole.ADMIN, 23, "Hier"));
        members.add(new com.paullouis.travel.model.GroupMember(new User("u3", "Lucas Bernard", null), Group.UserRole.MODERATOR, 15, "Il y a 2h"));
        members.add(new com.paullouis.travel.model.GroupMember(new User("u4", "Emma Petit", null), Group.UserRole.MEMBER, 8, "Il y a 3j"));
        members.add(new com.paullouis.travel.model.GroupMember(new User("u5", "Chloé Roux", null), Group.UserRole.MEMBER, 2, "La semaine dernière"));
        return members;
    }

    public static List<com.paullouis.travel.model.ReportedPhoto> getReportedPhotos(String groupId) {
        List<com.paullouis.travel.model.ReportedPhoto> reports = new ArrayList<>();
        if (userPhotos != null && userPhotos.size() > 2) {
            reports.add(new com.paullouis.travel.model.ReportedPhoto("r1", userPhotos.get(1), "Contenu inapproprié", "Jean Dupont", "24 Mars 2026"));
            reports.add(new com.paullouis.travel.model.ReportedPhoto("r2", userPhotos.get(2), "Spam", "Marie Curie", "25 Mars 2026"));
        }
        return reports;
    }

    public static java.util.Map<String, Integer> getGroupStats(String groupId) {
        java.util.Map<String, Integer> stats = new java.util.HashMap<>();
        stats.put("active_members", 45);
        stats.put("photos_shared", 342);
        stats.put("new_members_7d", 12);
        stats.put("total_interactions", 1560);
        return stats;
    }

    public static List<Photo> getPhotosByGroup(String groupId) {
        if (groupId == null) return new ArrayList<>();
        List<Photo> filtered = new ArrayList<>();
        if (userPhotos != null) {
            for (Photo p : userPhotos) {
                if (java.util.Objects.equals(groupId, p.getGroupId())) {
                    filtered.add(p);
                }
            }
        }
        return filtered;
    }

    // =========================================================================
    // DataRepository Async Implementation
    // These methods wrap the synchronous static methods to simulate a real backend API.
    // =========================================================================

    @Override
    public void getCurrentUser(DataCallback<User> callback) {
        callback.onSuccess(getCurrentUser());
    }

    @Override
    public void getUserById(String id, DataCallback<User> callback) {
        callback.onSuccess(getUserById(id));
    }

    @Override
    public void getUserPhotos(DataCallback<List<Photo>> callback) {
        callback.onSuccess(getUserPhotos());
    }

    @Override
    public void updateUser(User user, DataCallback<Void> callback) {
        currentUser = user;
        callback.onSuccess(null);
    }

    @Override
    public void addPhoto(Photo photo, DataCallback<Void> callback) {
        addPhoto(photo);
        callback.onSuccess(null);
    }

    @Override
    public void toggleLike(String photoId, boolean liked, DataCallback<Void> callback) {
        callback.onSuccess(null);
    }

    @Override
    public void uploadAudio(String photoId, android.net.Uri audioUri, DataCallback<String> callback) {
        callback.onSuccess(null);
    }

    @Override
    public void getGatewayPhotos(DataCallback<List<Photo>> callback) {
        callback.onSuccess(getGatewayPhotos());
    }

    @Override
    public void getPhotosByGroup(String groupId, DataCallback<List<Photo>> callback) {
        callback.onSuccess(getPhotosByGroup(groupId));
    }

    @Override
    public void getComments(String photoId, DataCallback<List<Comment>> callback) {
        callback.onSuccess(getMockComments(photoId));
    }

    @Override
    public void addComment(String photoId, Comment comment, DataCallback<Void> callback) {
        callback.onSuccess(null);
    }

    @Override
    public void getMyGroups(DataCallback<List<Group>> callback) {
        callback.onSuccess(getMyGroups());
    }

    @Override
    public void getDiscoverGroups(DataCallback<List<Group>> callback) {
        callback.onSuccess(getDiscoverGroups());
    }

    @Override
    public void getGroupById(String id, DataCallback<Group> callback) {
        callback.onSuccess(getGroupById(id));
    }

    @Override
    public void findGroupByCode(String code, DataCallback<Group> callback) {
        callback.onSuccess(findGroupByCode(code));
    }

    @Override
    public void joinGroup(String groupId, DataCallback<Void> callback) {
        joinGroup(groupId);
        callback.onSuccess(null);
    }

    @Override
    public void addGroup(Group group, DataCallback<Void> callback) {
        addGroup(group);
        callback.onSuccess(null);
    }

    @Override
    public void getGroupMembers(String groupId, DataCallback<List<com.paullouis.travel.model.GroupMember>> callback) {
        callback.onSuccess(getGroupMembers(groupId));
    }

    @Override
    public void getReportedPhotos(String groupId, DataCallback<List<com.paullouis.travel.model.ReportedPhoto>> callback) {
        callback.onSuccess(getReportedPhotos(groupId));
    }

    @Override
    public void getGroupStats(String groupId, DataCallback<java.util.Map<String, Integer>> callback) {
        callback.onSuccess(getGroupStats(groupId));
    }

    @Override
    public void getNotifications(DataCallback<List<Notification>> callback) {
        callback.onSuccess(getNotifications());
    }

    @Override
    public void getNotificationSettings(DataCallback<List<NotificationSettingItem>> callback) {
        callback.onSuccess(getNotificationSettings());
    }

    @Override
    public void getGeneratedItineraries(DataCallback<List<GeneratedItinerary>> callback) {
        callback.onSuccess(getGeneratedItineraries());
    }

    @Override
    public void getItinerarySteps(DataCallback<List<ItineraryStep>> callback) {
        callback.onSuccess(getItinerarySteps());
    }

    @Override
    public void getProfileItineraries(DataCallback<List<ProfileItinerary>> callback) {
        callback.onSuccess(getProfileItineraries());
    }

    @Override
    public void getSearchNavigationOptions(DataCallback<List<SearchNavigationOption>> callback) {
        callback.onSuccess(getSearchNavigationOptions());
    }

    @Override
    public void createNotification(com.paullouis.travel.model.Notification notification, DataCallback<Void> callback) {
        callback.onSuccess(null);
    }

    @Override
    public void markNotificationRead(String notificationId, DataCallback<Void> callback) {
        callback.onSuccess(null);
    }

    @Override
    public void reportPhoto(String photoId, String reason, DataCallback<Void> callback) {
        callback.onSuccess(null);
    }

    @Override
    public void searchPhotos(String query, DataCallback<List<Photo>> callback) {
        List<Photo> results = new ArrayList<>();
        String lower = query.toLowerCase();
        for (Photo p : getMockPhotos()) {
            if ((p.getTitle() != null && p.getTitle().toLowerCase().contains(lower))
                    || (p.getLocationName() != null && p.getLocationName().toLowerCase().contains(lower))
                    || (p.getAuthorName() != null && p.getAuthorName().toLowerCase().contains(lower))) {
                results.add(p);
            }
        }
        callback.onSuccess(results);
    }
}
