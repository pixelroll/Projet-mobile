package com.paullouis.travel.data;

import android.os.Handler;
import android.os.Looper;

import com.paullouis.travel.model.Comment;
import com.paullouis.travel.model.Photo;
import com.paullouis.travel.model.User;

import java.util.ArrayList;
import java.util.List;

public class EventBus {
    
    public interface PhotoListener {
        void onPhotoAdded(Photo photo);
        void onPhotoUpdated(Photo photo);
        void onPhotoRemoved(String photoId);
    }
    
    public interface UserListener {
        void onUserUpdated(User user);
    }

    public interface CommentListener {
        void onCommentAdded(String photoId, Comment comment);
        void onCommentConfirmed(String photoId, Comment comment);
        void onCommentFailed(String photoId, long commentTimestamp);
    }

    private static final List<PhotoListener> photoListeners = new ArrayList<>();
    private static final List<UserListener> userListeners = new ArrayList<>();
    private static final List<CommentListener> commentListeners = new ArrayList<>();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    // --- Photo Events ---

    public static void registerPhotoListener(PhotoListener listener) {
        if (!photoListeners.contains(listener)) {
            photoListeners.add(listener);
        }
    }

    public static void unregisterPhotoListener(PhotoListener listener) {
        photoListeners.remove(listener);
    }

    public static void notifyPhotoAdded(Photo photo) {
        mainHandler.post(() -> {
            for (PhotoListener listener : new ArrayList<>(photoListeners)) {
                listener.onPhotoAdded(photo);
            }
        });
    }

    public static void notifyPhotoUpdated(Photo photo) {
        mainHandler.post(() -> {
            for (PhotoListener listener : new ArrayList<>(photoListeners)) {
                listener.onPhotoUpdated(photo);
            }
        });
    }

    public static void notifyPhotoRemoved(String photoId) {
        mainHandler.post(() -> {
            for (PhotoListener listener : new ArrayList<>(photoListeners)) {
                listener.onPhotoRemoved(photoId);
            }
        });
    }

    // --- User Events ---

    public static void registerUserListener(UserListener listener) {
        if (!userListeners.contains(listener)) {
            userListeners.add(listener);
        }
    }

    public static void unregisterUserListener(UserListener listener) {
        userListeners.remove(listener);
    }

    public static void notifyUserUpdated(User user) {
        mainHandler.post(() -> {
            for (UserListener listener : new ArrayList<>(userListeners)) {
                listener.onUserUpdated(user);
            }
        });
    }

    // --- Comment Events ---

    public static void registerCommentListener(CommentListener listener) {
        if (!commentListeners.contains(listener)) {
            commentListeners.add(listener);
        }
    }

    public static void unregisterCommentListener(CommentListener listener) {
        commentListeners.remove(listener);
    }

    public static void notifyCommentAdded(String photoId, Comment comment) {
        mainHandler.post(() -> {
            for (CommentListener listener : new ArrayList<>(commentListeners)) {
                listener.onCommentAdded(photoId, comment);
            }
        });
    }

    public static void notifyCommentConfirmed(String photoId, Comment comment) {
        mainHandler.post(() -> {
            for (CommentListener listener : new ArrayList<>(commentListeners)) {
                listener.onCommentConfirmed(photoId, comment);
            }
        });
    }

    public static void notifyCommentFailed(String photoId, long commentTimestamp) {
        mainHandler.post(() -> {
            for (CommentListener listener : new ArrayList<>(commentListeners)) {
                listener.onCommentFailed(photoId, commentTimestamp);
            }
        });
    }

    // --- Photo Like Events ---

    public interface PhotoLikeListener {
        void onPhotoLiked(String photoId, boolean liked);
    }

    private static final List<PhotoLikeListener> likeListeners = new ArrayList<>();

    public static void registerPhotoLikeListener(PhotoLikeListener listener) {
        if (!likeListeners.contains(listener)) {
            likeListeners.add(listener);
        }
    }

    public static void unregisterPhotoLikeListener(PhotoLikeListener listener) {
        likeListeners.remove(listener);
    }

    public static void notifyPhotoLiked(String photoId, boolean liked) {
        mainHandler.post(() -> {
            for (PhotoLikeListener listener : new ArrayList<>(likeListeners)) {
                listener.onPhotoLiked(photoId, liked);
            }
        });
    }
}
