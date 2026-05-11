package com.paullouis.travel.model;

import com.google.firebase.firestore.PropertyName;

public class Group {
    public enum UserRole {
        OWNER,
        ADMIN,
        MODERATOR,
        MEMBER,
        MEMBER_WITH_CODE
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
    private boolean approvalRequired;
    private java.util.List<String> memberIds; // For efficient Firestore queries

    public Group() {}

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
        this.role = isOwner ? UserRole.OWNER : UserRole.MEMBER;
        this.approvalRequired = false;
    }

    public Group(String id, String name, String description, int membersCount, int photosCount, 
                 boolean isPrivate, boolean isJoined, boolean isOwner, String coverImage, String code, UserRole role) {
        this(id, name, description, membersCount, photosCount, isPrivate, isJoined, isOwner, coverImage, code);
        this.role = role;
    }

    public void regenerateCode() {
        this.code = generateInvitationCode();
    }

    public static String generateInvitationCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        java.util.Random rnd = new java.util.Random();
        while (sb.length() < 7) {
            int index = (int) (rnd.nextFloat() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getMembersCount() { return membersCount; }
    public int getPhotosCount() { return photosCount; }
    @PropertyName("isPrivate")
    public boolean isPrivate() { return isPrivate; }

    @PropertyName("isPrivate")
    public void setPrivate(boolean aPrivate) { isPrivate = aPrivate; }
    public boolean isJoined() { return isJoined; }
    public boolean isOwner() { return isOwner; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public void setJoined(boolean joined) { this.isJoined = joined; }
    public void setMembersCount(int count) { this.membersCount = count; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    public boolean isApprovalRequired() { return approvalRequired; }
    public void setApprovalRequired(boolean approvalRequired) { this.approvalRequired = approvalRequired; }
    public java.util.List<String> getMemberIds() { return memberIds; }
    public void setMemberIds(java.util.List<String> memberIds) { this.memberIds = memberIds; }
    public void setPhotosCount(int count) { this.photosCount = count; }
}
