package com.paullouis.travel.model;

import java.util.List;

public class User {
    private String id;
    private String name;
    private String email;
    private String bio;
    private String avatarUrl;
    private int profileImageResId; // drawable resource id for local image
    private int postsCount;
    private int followersCount;
    private int followingCount;
    private int countriesVisited;
    private List<String> groupIds;

    public User() {}

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public int getProfileImageResId() { return profileImageResId; }
    public void setProfileImageResId(int profileImageResId) { this.profileImageResId = profileImageResId; }

    public int getPostsCount() { return postsCount; }
    public void setPostsCount(int postsCount) { this.postsCount = postsCount; }

    public int getFollowersCount() { return followersCount; }
    public void setFollowersCount(int followersCount) { this.followersCount = followersCount; }

    public int getFollowingCount() { return followingCount; }
    public void setFollowingCount(int followingCount) { this.followingCount = followingCount; }

    public int getCountriesVisited() { return countriesVisited; }
    public void setCountriesVisited(int countriesVisited) { this.countriesVisited = countriesVisited; }

    public List<String> getGroupIds() { return groupIds; }
    public void setGroupIds(List<String> groupIds) { this.groupIds = groupIds; }
}
