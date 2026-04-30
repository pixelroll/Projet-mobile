package com.paullouis.travel.model;

public class Notification {
    public enum Type { LIKE, COMMENT, FOLLOW, ITINERARY, SUGGESTION, PHOTO_PUBLISHED }

    @com.google.firebase.firestore.Exclude
    private int id;
    @com.google.firebase.firestore.Exclude
    private Type type;

    private String notificationId;
    private String userId;       // Firestore recipient UID
    private String typeString;   // Persisted as String in Firestore (e.g. "PHOTO_PUBLISHED")
    private String userName;
    private String userAvatar;
    private String content;
    private String photoUrl;
    private String time;
    private long timestamp;
    private boolean isRead;

    public Notification() {}

    @com.google.firebase.firestore.Exclude
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @com.google.firebase.firestore.Exclude
    public Type getType() {
        if (type != null) return type;
        if (typeString != null) {
            try { return Type.valueOf(typeString); } catch (IllegalArgumentException ignored) {}
        }
        return null;
    }
    public void setType(Type type) {
        this.type = type;
        if (type != null) this.typeString = type.name();
    }

    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTypeString() { return typeString; }
    public void setTypeString(String typeString) { this.typeString = typeString; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserAvatar() { return userAvatar; }
    public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
