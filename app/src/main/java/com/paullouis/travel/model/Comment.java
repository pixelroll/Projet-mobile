package com.paullouis.travel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Comment implements Parcelable {
    private String userName;
    private String userInitial;
    private String date;
    private String text;
    private long timestamp;
    private String userAvatarUrl;
    private boolean isLoading;

    public Comment() {
        // Required for Firebase
    }

    public Comment(String userName, String userInitial, String date, String text) {
        this.userName = userName;
        this.userInitial = userInitial;
        this.date = date;
        this.text = text;
    }

    protected Comment(Parcel in) {
        userName = in.readString();
        userInitial = in.readString();
        date = in.readString();
        text = in.readString();
        timestamp = in.readLong();
        userAvatarUrl = in.readString();
        isLoading = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(userInitial);
        dest.writeString(date);
        dest.writeString(text);
        dest.writeLong(timestamp);
        dest.writeString(userAvatarUrl);
        dest.writeByte((byte) (isLoading ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserInitial() { return userInitial; }
    public void setUserInitial(String userInitial) { this.userInitial = userInitial; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getUserAvatarUrl() { return userAvatarUrl; }
    public void setUserAvatarUrl(String userAvatarUrl) { this.userAvatarUrl = userAvatarUrl; }

    @com.google.firebase.firestore.Exclude
    public boolean isLoading() { return isLoading; }
    @com.google.firebase.firestore.Exclude
    public void setLoading(boolean loading) { isLoading = loading; }
}
