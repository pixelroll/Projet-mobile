package com.paullouis.travel.model;

public class Group {
    public enum UserRole {
        ADMIN,
        MEMBER_WITH_CODE,
        MEMBER
    }

    private String id;
    private String name;
    private String description;
    private int membersCount;
    private int photosCount;
    private boolean isPrivate;
    private boolean isJoined;
    private boolean isOwner;
    private String coverImage;
    private String code;
    private UserRole role;

    public Group(String id, String name, String description, int membersCount, int photosCount, 
                 boolean isPrivate, boolean isJoined, boolean isOwner, String coverImage, String code) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.membersCount = membersCount;
        this.photosCount = photosCount;
        this.isPrivate = isPrivate;
        this.isJoined = isJoined;
        this.isOwner = isOwner;
        this.coverImage = coverImage;
        this.code = code;
        this.role = isOwner ? UserRole.ADMIN : UserRole.MEMBER;
    }

    public Group(String id, String name, String description, int membersCount, int photosCount, 
                 boolean isPrivate, boolean isJoined, boolean isOwner, String coverImage, String code, UserRole role) {
        this(id, name, description, membersCount, photosCount, isPrivate, isJoined, isOwner, coverImage, code);
        this.role = role;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getMembersCount() { return membersCount; }
    public int getPhotosCount() { return photosCount; }
    public boolean isPrivate() { return isPrivate; }
    public boolean isJoined() { return isJoined; }
    public boolean isOwner() { return isOwner; }
    public String getCoverImage() { return coverImage; }
    public String getCode() { return code; }
    public void setJoined(boolean joined) { this.isJoined = joined; }
    public void setMembersCount(int count) { this.membersCount = count; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
}
