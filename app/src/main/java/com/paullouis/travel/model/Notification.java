package com.paullouis.travel.model;

public class Notification {
    public enum Type { LIKE, COMMENT, FOLLOW, ITINERARY, SUGGESTION }

    private int id;
    private Type type;
    private String userName;
    private String userAvatar;
    private String content;
    private String photoUrl;
    private String time;
    private boolean isRead;

    public Notification() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

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

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
