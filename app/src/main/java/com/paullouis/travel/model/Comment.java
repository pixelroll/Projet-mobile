package com.paullouis.travel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Comment implements Parcelable {
    private String userName;
    private String userInitial;
    private String date;
    private String text;

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
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(userInitial);
        dest.writeString(date);
        dest.writeString(text);
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
    public String getUserInitial() { return userInitial; }
    public String getDate() { return date; }
    public String getText() { return text; }
}
