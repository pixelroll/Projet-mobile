package com.paullouis.travel.data;

import com.paullouis.travel.model.Comment;
import com.paullouis.travel.model.GeneratedItinerary;
import com.paullouis.travel.model.Group;
import com.paullouis.travel.model.GroupMember;
import com.paullouis.travel.model.ItineraryStep;
import com.paullouis.travel.model.Notification;
import com.paullouis.travel.model.NotificationSettingItem;
import com.paullouis.travel.model.Photo;
import com.paullouis.travel.model.ProfileItinerary;
import com.paullouis.travel.model.ReportedPhoto;
import com.paullouis.travel.model.SearchNavigationOption;
import com.paullouis.travel.model.User;

import java.util.List;
import java.util.Map;

/**
 * Interface defining all data operations for the application.
 * Real implementations (like Firebase) will perform these asynchronously.
 */
public interface DataRepository {

    // --- Authentication & Users ---
    boolean isUserLoggedIn();
    void getCurrentUser(DataCallback<User> callback);
    void getUserById(String id, DataCallback<User> callback);
    void updateUser(User user, DataCallback<Void> callback);

    // --- Photos ---
    void getUserPhotos(DataCallback<List<Photo>> callback);
    void getFeedPhotos(DataCallback<List<Photo>> callback);
    void addPhoto(Photo photo, DataCallback<Void> callback);
    void toggleLike(String photoId, boolean liked, DataCallback<Void> callback);
    void uploadAudio(String photoId, android.net.Uri audioUri, DataCallback<String> callback);
    void getGatewayPhotos(DataCallback<List<Photo>> callback);
    void getPhotosByGroup(String groupId, DataCallback<List<Photo>> callback);

    // --- Comments ---
    void getComments(String photoId, DataCallback<List<Comment>> callback);
    void addComment(String photoId, Comment comment, DataCallback<Void> callback);

    // --- Groups ---
    void getMyGroups(DataCallback<List<Group>> callback);
    void getDiscoverGroups(DataCallback<List<Group>> callback);
    void getGroupById(String id, DataCallback<Group> callback);
    void findGroupByCode(String code, DataCallback<Group> callback);
    void joinGroup(String groupId, DataCallback<Void> callback);
    void addGroup(Group group, DataCallback<Void> callback);
    void getGroupMembers(String groupId, DataCallback<List<GroupMember>> callback);
    void getReportedPhotos(String groupId, DataCallback<List<ReportedPhoto>> callback);
    void getGroupStats(String groupId, DataCallback<Map<String, Integer>> callback);

    // --- Notifications ---
    void getNotifications(DataCallback<List<Notification>> callback);
    void getNotificationSettings(DataCallback<List<NotificationSettingItem>> callback);
    void createNotification(Notification notification, DataCallback<Void> callback);
    void markNotificationRead(String notificationId, DataCallback<Void> callback);

    // --- Photos (extended) ---
    void reportPhoto(String photoId, String reason, DataCallback<Void> callback);

    // --- Search ---
    void searchPhotos(String query, DataCallback<List<Photo>> callback);

    // --- Itineraries ---
    void getGeneratedItineraries(DataCallback<List<GeneratedItinerary>> callback);
    void getItinerarySteps(DataCallback<List<ItineraryStep>> callback);
    void getProfileItineraries(DataCallback<List<ProfileItinerary>> callback);

    void getSearchNavigationOptions(DataCallback<List<SearchNavigationOption>> callback);
}
