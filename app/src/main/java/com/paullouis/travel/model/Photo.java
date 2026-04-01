package com.paullouis.travel.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

public class Photo implements Parcelable {
    private String id;
    private String userId;
    private String imageUrl;
    private int imageResId; // Added for local drawables
    private String title;
    private String description;
    private String audioUrl;
    private String locationName;
    private double lat;
    private double lng;
    private long timestamp;
    private int likes;
    private int comments;
    private List<String> tags;
    private String visibility; // PUBLIC, GROUP_ONLY, PRIVATE
    private String authorName;
    private String authorInitial;
    private String date;
    private boolean isBookmarked;
    private boolean isLiked;

    public Photo() {}

    protected Photo(Parcel in) {
        id = in.readString();
        userId = in.readString();
        imageUrl = in.readString();
        imageResId = in.readInt();
        title = in.readString();
        description = in.readString();
        audioUrl = in.readString();
        locationName = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        timestamp = in.readLong();
        likes = in.readInt();
        comments = in.readInt();
        tags = in.createStringArrayList();
        visibility = in.readString();
        authorName = in.readString();
        authorInitial = in.readString();
        date = in.readString();
        isBookmarked = in.readByte() != 0;
        isLiked = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(userId);
        dest.writeString(imageUrl);
        dest.writeInt(imageResId);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(audioUrl);
        dest.writeString(locationName);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeLong(timestamp);
        dest.writeInt(likes);
        dest.writeInt(comments);
        dest.writeStringList(tags);
        dest.writeString(visibility);
        dest.writeString(authorName);
        dest.writeString(authorInitial);
        dest.writeString(date);
        dest.writeByte((byte) (isBookmarked ? 1 : 0));
        dest.writeByte((byte) (isLiked ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }
    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }
    public int getComments() { return comments; }
    public void setComments(int comments) { this.comments = comments; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getAuthorInitial() { return authorInitial; }
    public void setAuthorInitial(String authorInitial) { this.authorInitial = authorInitial; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public boolean isBookmarked() { return isBookmarked; }
    public void setBookmarked(boolean bookmarked) { isBookmarked = bookmarked; }
    public boolean isLiked() { return isLiked; }
    public void setLiked(boolean liked) { isLiked = liked; }
}
