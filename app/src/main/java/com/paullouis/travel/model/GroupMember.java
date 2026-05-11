package com.paullouis.travel.model;

public class GroupMember {
    private User user;
    private Group.UserRole role;
    private int photosCount;
    private String lastActivity;

    public GroupMember() {
    }

    public GroupMember(User user, Group.UserRole role, int photosCount, String lastActivity) {
        this.user = user;
        this.role = role;
        this.photosCount = photosCount;
        this.lastActivity = lastActivity;
    }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Group.UserRole getRole() { return role; }
    public void setRole(Group.UserRole role) { this.role = role; }
    public int getPhotosCount() { return photosCount; }
    public void setPhotosCount(int photosCount) { this.photosCount = photosCount; }
    public String getLastActivity() { return lastActivity; }
    public void setLastActivity(String lastActivity) { this.lastActivity = lastActivity; }
}
