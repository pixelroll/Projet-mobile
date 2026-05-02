package com.paullouis.travel.data;

import android.net.Uri;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.paullouis.travel.model.Comment;
import com.paullouis.travel.model.GeneratedItinerary;
import com.paullouis.travel.model.Group;
import com.paullouis.travel.model.GroupMember;
import com.paullouis.travel.model.ItineraryStep;
import com.paullouis.travel.model.Notification;
import com.paullouis.travel.model.NotificationSettingItem;
import com.paullouis.travel.model.Photo;
import com.paullouis.travel.model.ProfileItinerary;
import com.paullouis.travel.model.ReportedPhoto;
import com.paullouis.travel.model.SearchFilters;
import com.paullouis.travel.model.SearchNavigationOption;
import com.paullouis.travel.model.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Real implementation of DataRepository using Firebase.
 * For Phase 1, only Auth and User data are implemented here.
 * The rest delegates to MockDataProvider so the app doesn't break.
 */
public class FirebaseRepository implements DataRepository {

    private static FirebaseRepository instance;
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;
    private final FirebaseStorage storage;
    private final DataRepository mockDelegate;
    private static final java.util.Map<String, Object> cache = new java.util.HashMap<>();

    private FirebaseRepository() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        mockDelegate = MockDataProvider.getInstance();
    }

    public static FirebaseRepository getInstance() {
        if (instance == null) {
            instance = new FirebaseRepository();
        }
        return instance;
    }

    // =========================================================================
    // IMPLEMENTED IN FIREBASE (PHASE 1)
    // =========================================================================

    @Override
    public boolean isUserLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public String getCurrentUserId() {
        return auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
    }

    @Override
    public void getCurrentUser(DataCallback<User> callback) {
        FirebaseUser fUser = auth.getCurrentUser();
        if (fUser == null) {
            callback.onError(new Exception("User not authenticated"));
            return;
        }
        getUserById(fUser.getUid(), callback);
    }

    @Override
    public void getUserById(String id, DataCallback<User> callback) {
        db.collection("users").document(id).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        callback.onSuccess(user);
                    } else {
                        callback.onError(new Exception("User profile not found"));
                    }
                })
                .addOnFailureListener(callback::onError);
    }

    public void loginWithEmail(String email, String password, DataCallback<User> callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> getCurrentUser(callback))
                .addOnFailureListener(callback::onError);
    }

    public void registerWithEmail(String email, String password, String name, String avatarUri, DataCallback<User> callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser fUser = authResult.getUser();
                    if (fUser != null) {
                        User newUser = new User(fUser.getUid(), name, email);
                        if (avatarUri != null && !avatarUri.isEmpty()) {
                            newUser.setAvatarUrl(avatarUri);
                        }
                        // Use updateUser to handle potential avatar upload
                        updateUser(newUser, new DataCallback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                callback.onSuccess(newUser);
                            }

                            @Override
                            public void onError(Exception e) {
                                callback.onError(e);
                            }
                        });
                    }
                })
                .addOnFailureListener(callback::onError);
    }
    
    // Requires a Google ID token obtained from Google SignIn Client in the UI
    public void loginWithGoogle(String idToken, DataCallback<User> callback) {
        com.google.firebase.auth.AuthCredential credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser fUser = authResult.getUser();
                    if (fUser != null) {
                        // Check if user exists in Firestore
                        getUserById(fUser.getUid(), new DataCallback<User>() {
                            @Override
                            public void onSuccess(User user) {
                                callback.onSuccess(user); // user already exists
                            }

                            @Override
                            public void onError(Exception e) {
                                // User does not exist, create new profile
                                User newUser = new User(fUser.getUid(), fUser.getDisplayName(), fUser.getEmail());
                                newUser.setAvatarUrl(fUser.getPhotoUrl() != null ? fUser.getPhotoUrl().toString() : null);
                                createUserInDatabase(newUser, new DataCallback<Void>() {
                                    @Override
                                    public void onSuccess(Void result) {
                                        callback.onSuccess(newUser);
                                    }
                                    @Override
                                    public void onError(Exception e2) {
                                        callback.onError(e2);
                                    }
                                });
                            }
                        });
                    }
                })
                .addOnFailureListener(callback::onError);
    }
    
    public void logout() {
        auth.signOut();
        // Clear mock state if necessary
        com.paullouis.travel.data.MockDataProvider.setUserLoggedIn(false);
    }

    /**
     * Helper to save a newly registered user to Firestore.
     */
    public void createUserInDatabase(User user, DataCallback<Void> callback) {
        db.collection("users").document(user.getId()).set(user)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onError);
    }

    @Override
    public void updateUser(User user, DataCallback<Void> callback) {
        if (user.getAvatarUrl() != null && (user.getAvatarUrl().startsWith("content://") || user.getAvatarUrl().startsWith("file://"))) {
            StorageReference ref = storage.getReference().child("avatars/" + user.getId() + ".jpg");
            ref.putFile(android.net.Uri.parse(user.getAvatarUrl()))
                    .addOnSuccessListener(taskSnapshot -> {
                        ref.getDownloadUrl().addOnSuccessListener(uri -> {
                            user.setAvatarUrl(uri.toString());
                            saveUserToFirestore(user, callback);
                        }).addOnFailureListener(callback::onError);
                    })
                    .addOnFailureListener(callback::onError);
        } else {
            saveUserToFirestore(user, callback);
        }
    }

    private void saveUserToFirestore(User user, DataCallback<Void> callback) {
        db.collection("users").document(user.getId()).set(user)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onError);
    }

    // =========================================================================
    // IMPLEMENTED IN FIREBASE (PHASE 2)
    // =========================================================================

    @Override
    public void getUserPhotos(DataCallback<List<Photo>> callback) {
        FirebaseUser fUser = auth.getCurrentUser();
        if (fUser == null) {
            callback.onSuccess(new ArrayList<>());
            return;
        }

        // Check cache first
        List<Photo> cached = (List<Photo>) cache.get("photos:user");
        if (cached != null) {
            callback.onSuccess(cached);
            return;
        }

        db.collection("photos")
                .whereEqualTo("userId", fUser.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Photo> photos = queryDocumentSnapshots.toObjects(Photo.class);
                    if (photos.isEmpty()) {
                        callback.onSuccess(new ArrayList<>());
                    } else {
                        // Sync per-user like state
                        FirebaseUser currentUser = auth.getCurrentUser();
                        String currentUserId = currentUser != null ? currentUser.getUid() : null;
                        for (Photo p : photos) {
                            if (currentUserId != null && p.getLikedBy() != null) {
                                p.setLiked(p.getLikedBy().contains(currentUserId));
                            }
                        }
                        // Sort locally by timestamp descending
                        photos.sort((p1, p2) -> Long.compare(p2.getTimestamp(), p1.getTimestamp()));
                        cache.put("photos:user", photos); // Cache it
                        callback.onSuccess(photos);
                    }
                })
                .addOnFailureListener(callback::onError);
    }

    @Override
    public void getFeedPhotos(DataCallback<List<Photo>> callback) {
        List<Photo> cached = (List<Photo>) cache.get("photos:feed");
        if (cached != null) {
            callback.onSuccess(cached);
            return;
        }

        db.collection("photos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Photo> photos = queryDocumentSnapshots.toObjects(Photo.class);
                    FirebaseUser currentUser = auth.getCurrentUser();
                    String currentUserId = currentUser != null ? currentUser.getUid() : null;
                    for (Photo p : photos) {
                        if (currentUserId != null && p.getLikedBy() != null) {
                            p.setLiked(p.getLikedBy().contains(currentUserId));
                        }
                    }
                    photos.sort((p1, p2) -> Long.compare(p2.getTimestamp(), p1.getTimestamp()));
                    cache.put("photos:feed", photos);
                    callback.onSuccess(photos);
                })
                .addOnFailureListener(callback::onError);
    }

    @Override
    public void addPhoto(Photo photo, DataCallback<Void> callback) {
        if (photo.getImageUrl() != null && (photo.getImageUrl().startsWith("content://") || photo.getImageUrl().startsWith("file://"))) {
            // Upload to Firebase Storage first
            StorageReference ref = storage.getReference().child("photos/" + photo.getId() + ".jpg");
            ref.putFile(Uri.parse(photo.getImageUrl()))
                    .addOnSuccessListener(taskSnapshot -> {
                        ref.getDownloadUrl().addOnSuccessListener(uri -> {
                            photo.setImageUrl(uri.toString());
                            savePhotoToFirestore(photo, new DataCallback<Void>() {
                                @Override
                                public void onSuccess(Void result) {
                                    // Photo saved: update loading state and propagate remote URL
                                    photo.setLoading(false);
                                    EventBus.notifyPhotoUpdated(photo);
                                    callback.onSuccess(null);
                                }

                                @Override
                                public void onError(Exception e) {
                                    // Firestore write failed: rollback optimistic insert
                                    EventBus.notifyPhotoRemoved(photo.getId());
                                    callback.onError(e);
                                }
                            });
                        }).addOnFailureListener(e -> {
                            EventBus.notifyPhotoRemoved(photo.getId());
                            callback.onError(e);
                        });
                    })
                    .addOnFailureListener(e -> {
                        EventBus.notifyPhotoRemoved(photo.getId());
                        callback.onError(e);
                    });
        } else {
            // Already remote URL or no image
            savePhotoToFirestore(photo, new DataCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    photo.setLoading(false);
                    EventBus.notifyPhotoUpdated(photo);
                    callback.onSuccess(null);
                }

                @Override
                public void onError(Exception e) {
                    EventBus.notifyPhotoRemoved(photo.getId());
                    callback.onError(e);
                }
            });
        }
    }

    private void savePhotoToFirestore(Photo photo, DataCallback<Void> callback) {
        db.collection("photos").document(photo.getId()).set(photo)
                .addOnSuccessListener(aVoid -> {
                    cache.remove("photos:user");
                    cache.remove("photos:feed"); // Invalidate photos cache
                    if (photo.getGroupId() != null) {
                        cache.remove("photos:byGroup:" + photo.getGroupId()); // Invalidate group-specific photos
                    }
                    callback.onSuccess(null);
                })
                .addOnFailureListener(callback::onError);
    }

    @Override
    public void getComments(String photoId, DataCallback<List<Comment>> callback) {
        if (photoId == null) {
            mockDelegate.getComments(photoId, callback);
            return;
        }

        // Check cache first
        String cacheKey = "comments:" + photoId;
        List<Comment> cached = (List<Comment>) cache.get(cacheKey);
        if (cached != null) {
            callback.onSuccess(cached);
            return;
        }

        db.collection("photos").document(photoId).collection("comments")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Comment> comments = queryDocumentSnapshots.toObjects(Comment.class);
                    cache.put(cacheKey, comments); // Cache it
                    callback.onSuccess(comments);
                })
                .addOnFailureListener(callback::onError);
    }

    @Override
    public void addComment(String photoId, Comment comment, DataCallback<Void> callback) {
        if (photoId == null) {
            callback.onError(new Exception("Invalid photo ID"));
            return;
        }
        db.collection("photos").document(photoId).collection("comments")
                .add(comment)
                .addOnSuccessListener(documentReference -> {
                    // Update photo comment count
                    db.collection("photos").document(photoId)
                            .update("comments", com.google.firebase.firestore.FieldValue.increment(1))
                            .addOnSuccessListener(aVoid -> {
                                comment.setLoading(false);
                                cache.remove("comments:" + photoId); // Invalidate comments cache
                                cache.remove("photos:user"); // Invalidate photos cache
                                EventBus.notifyCommentConfirmed(photoId, comment);
                                callback.onSuccess(null);
                            })
                            .addOnFailureListener(e -> {
                                EventBus.notifyCommentFailed(photoId, comment.getTimestamp());
                                callback.onError(e);
                            });
                })
                .addOnFailureListener(e -> {
                    EventBus.notifyCommentFailed(photoId, comment.getTimestamp());
                    callback.onError(e);
                });
    }

    // =========================================================================
    // IMPLEMENTED IN FIREBASE (PHASE 3 - Likes & Audio)
    // =========================================================================

    @Override
    public void toggleLike(String photoId, boolean liked, DataCallback<Void> callback) {
        if (photoId == null) {
            callback.onError(new Exception("Invalid photo ID"));
            return;
        }
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onError(new Exception("User not authenticated"));
            return;
        }
        String uid = currentUser.getUid();
        // Use a batched write: increment/decrement likes AND add/remove from likedBy
        com.google.firebase.firestore.DocumentReference docRef = db.collection("photos").document(photoId);
        if (liked) {
            docRef.update(
                    "likes", com.google.firebase.firestore.FieldValue.increment(1),
                    "likedBy", com.google.firebase.firestore.FieldValue.arrayUnion(uid)
            ).addOnSuccessListener(aVoid -> {
                cache.remove("photos:user");
                cache.remove("photos:feed");
                EventBus.notifyPhotoLiked(photoId, liked);
                callback.onSuccess(null);
            })
             .addOnFailureListener(callback::onError);
        } else {
            docRef.update(
                    "likes", com.google.firebase.firestore.FieldValue.increment(-1),
                    "likedBy", com.google.firebase.firestore.FieldValue.arrayRemove(uid)
            ).addOnSuccessListener(aVoid -> {
                cache.remove("photos:user");
                cache.remove("photos:feed");
                EventBus.notifyPhotoLiked(photoId, liked);
                callback.onSuccess(null);
            })
             .addOnFailureListener(callback::onError);
        }
    }

    @Override
    public void uploadAudio(String photoId, android.net.Uri audioUri, DataCallback<String> callback) {
        if (photoId == null || audioUri == null) {
            callback.onError(new Exception("Invalid parameters"));
            return;
        }
        StorageReference ref = storage.getReference().child("audio/" + photoId + ".3gp");
        ref.putFile(audioUri)
                .addOnSuccessListener(taskSnapshot -> {
                    ref.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Update the photo document with audio URL
                        db.collection("photos").document(photoId)
                                .update("audioUrl", uri.toString())
                                .addOnSuccessListener(aVoid -> callback.onSuccess(uri.toString()))
                                .addOnFailureListener(callback::onError);
                    }).addOnFailureListener(callback::onError);
                })
                .addOnFailureListener(callback::onError);
    }

    // =========================================================================
    // DELEGATED TO MOCK DATA (TO BE IMPLEMENTED LATER)
    // =========================================================================

    @Override
    public void getGatewayPhotos(DataCallback<List<Photo>> callback) { mockDelegate.getGatewayPhotos(callback); }

    @Override
    public void getPhotosByGroup(String groupId, DataCallback<List<Photo>> callback) {
        if (groupId == null) {
            callback.onError(new Exception("Invalid group ID"));
            return;
        }

        String cacheKey = "photos:byGroup:" + groupId;
        List<Photo> cached = (List<Photo>) cache.get(cacheKey);
        if (cached != null) {
            callback.onSuccess(cached);
            return;
        }

        db.collection("photos")
            .whereEqualTo("groupId", groupId)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<Photo> photos = querySnapshot.toObjects(Photo.class);
                // Sync per-user like state
                FirebaseUser currentUser = auth.getCurrentUser();
                String currentUserId = currentUser != null ? currentUser.getUid() : null;
                for (Photo p : photos) {
                    if (currentUserId != null && p.getLikedBy() != null) {
                        p.setLiked(p.getLikedBy().contains(currentUserId));
                    }
                }
                // Sort locally by timestamp descending
                photos.sort((p1, p2) -> Long.compare(p2.getTimestamp(), p1.getTimestamp()));
                cache.put(cacheKey, photos);
                callback.onSuccess(photos);
            })
            .addOnFailureListener(callback::onError);
    }

    @Override
    public void getMyGroups(DataCallback<List<Group>> callback) {
        String cacheKey = "groups:my";
        List<Group> cached = (List<Group>) cache.get(cacheKey);
        if (cached != null) {
            callback.onSuccess(cached);
            return;
        }

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onError(new Exception("User not authenticated"));
            return;
        }

        String uid = currentUser.getUid();
        db.collection("groups")
            .whereArrayContains("memberIds", uid)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<Group> groups = querySnapshot.toObjects(Group.class);
                cache.put(cacheKey, groups);
                callback.onSuccess(groups);
            })
            .addOnFailureListener(callback::onError);
    }

    @Override
    public void getDiscoverGroups(DataCallback<List<Group>> callback) {
        String cacheKey = "groups:discover";
        List<Group> cached = (List<Group>) cache.get(cacheKey);
        if (cached != null) {
            callback.onSuccess(cached);
            return;
        }

        FirebaseUser currentUser = auth.getCurrentUser();
        String uid = currentUser != null ? currentUser.getUid() : null;

        db.collection("groups")
            .whereEqualTo("isPrivate", false)
            .limit(20)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<Group> groups = querySnapshot.toObjects(Group.class);
                // Filter out groups user is already in (client-side filtering)
                if (uid != null) {
                    final String finalUid = uid;
                    groups.removeIf(g -> g.getMemberIds() != null && g.getMemberIds().contains(finalUid));
                }
                cache.put(cacheKey, groups);
                callback.onSuccess(groups);
            })
            .addOnFailureListener(callback::onError);
    }

    @Override
    public void getGroupById(String id, DataCallback<Group> callback) {
        if (id == null) {
            callback.onError(new Exception("Invalid group ID"));
            return;
        }

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onError(new Exception("User not authenticated"));
            return;
        }

        String uid = currentUser.getUid();
        String cacheKey = "groups:" + id + ":" + uid;
        Group cached = (Group) cache.get(cacheKey);
        if (cached != null) {
            callback.onSuccess(cached);
            return;
        }

        db.collection("groups").document(id).get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Group group = documentSnapshot.toObject(Group.class);
                    // Fetch the current user's role from the members subcollection
                    db.collection("groups").document(id).collection("members").document(uid).get()
                        .addOnSuccessListener(memberSnapshot -> {
                            if (memberSnapshot.exists()) {
                                GroupMember member = memberSnapshot.toObject(GroupMember.class);
                                if (member != null) {
                                    group.setRole(member.getRole());
                                    group.setJoined(true);
                                }
                            }
                            cache.put(cacheKey, group);
                            callback.onSuccess(group);
                        })
                        .addOnFailureListener(e -> {
                            cache.put(cacheKey, group);
                            callback.onSuccess(group);
                        });
                } else {
                    callback.onError(new Exception("Group not found"));
                }
            })
            .addOnFailureListener(callback::onError);
    }

    @Override
    public void findGroupByCode(String code, DataCallback<Group> callback) {
        if (code == null || code.isEmpty()) {
            callback.onError(new Exception("Invalid code"));
            return;
        }

        db.collection("groups")
            .whereEqualTo("code", code)
            .limit(1)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                if (querySnapshot.getDocuments().size() > 0) {
                    Group group = querySnapshot.getDocuments().get(0).toObject(Group.class);
                    callback.onSuccess(group);
                } else {
                    callback.onError(new Exception("Group not found"));
                }
            })
            .addOnFailureListener(callback::onError);
    }

    @Override
    public void joinGroup(String groupId, DataCallback<Void> callback) {
        if (groupId == null) {
            callback.onError(new Exception("Invalid group ID"));
            return;
        }

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onError(new Exception("User not authenticated"));
            return;
        }

        String uid = currentUser.getUid();

        // Batch write: add member document + update memberIds + increment count
        com.google.firebase.firestore.WriteBatch batch = db.batch();
        com.google.firebase.firestore.DocumentReference groupRef = db.collection("groups").document(groupId);

        getCurrentUser(new DataCallback<User>() {
            @Override
            public void onSuccess(User user) {
                // Create member document
                com.paullouis.travel.model.GroupMember member = new com.paullouis.travel.model.GroupMember();
                member.setUser(user);
                member.setRole(Group.UserRole.MEMBER);
                member.setPhotosCount(0);
                member.setLastActivity("Aujourd'hui");

                com.google.firebase.firestore.DocumentReference memberRef = groupRef.collection("members").document(uid);
                batch.set(memberRef, member);

                // Update group: add uid to memberIds array and increment count
                batch.update(groupRef,
                    "memberIds", com.google.firebase.firestore.FieldValue.arrayUnion(uid),
                    "membersCount", com.google.firebase.firestore.FieldValue.increment(1)
                );

                batch.commit()
                    .addOnSuccessListener(aVoid -> {
                        cache.remove("groups:my");
                        cache.remove("groups:discover");
                        cache.remove("groups:" + groupId + ":" + uid);
                        callback.onSuccess(null);
                    })
                    .addOnFailureListener(callback::onError);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    @Override
    public void addGroup(Group group, DataCallback<Void> callback) {
        if (group == null || group.getId() == null) {
            callback.onError(new Exception("Invalid group"));
            return;
        }
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onError(new Exception("User not authenticated"));
            return;
        }

        // Initialize memberIds array with creator
        if (group.getMemberIds() == null) {
            group.setMemberIds(new ArrayList<>());
        }
        group.getMemberIds().add(currentUser.getUid());
        group.setMembersCount(1);
        group.setPhotosCount(0);

        // Batch write: create group document + creator member document
        com.google.firebase.firestore.WriteBatch batch = db.batch();

        com.google.firebase.firestore.DocumentReference groupRef = db.collection("groups").document(group.getId());
        batch.set(groupRef, group);

        // Add creator as OWNER in members subcollection
        com.paullouis.travel.model.GroupMember creatorMember = new com.paullouis.travel.model.GroupMember();
        creatorMember.setRole(Group.UserRole.OWNER);
        FirebaseRepository.this.getCurrentUser(new DataCallback<User>() {
            @Override
            public void onSuccess(User result) {
                creatorMember.setUser(result);
                creatorMember.setPhotosCount(0);
                creatorMember.setLastActivity("Aujourd'hui");

                com.google.firebase.firestore.DocumentReference memberRef = groupRef.collection("members").document(currentUser.getUid());
                batch.set(memberRef, creatorMember);

                batch.commit()
                    .addOnSuccessListener(aVoid -> {
                        cache.remove("groups:my");
                        cache.remove("groups:" + group.getId() + ":" + currentUser.getUid());
                        callback.onSuccess(null);
                    })
                    .addOnFailureListener(callback::onError);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    @Override
    public void getGroupMembers(String groupId, DataCallback<List<GroupMember>> callback) {
        if (groupId == null) {
            callback.onError(new Exception("Invalid group ID"));
            return;
        }

        String cacheKey = "groups:" + groupId + ":members";
        List<GroupMember> cached = (List<GroupMember>) cache.get(cacheKey);
        if (cached != null) {
            callback.onSuccess(cached);
            return;
        }

        db.collection("groups").document(groupId).collection("members")
            .orderBy("role")
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<GroupMember> members = querySnapshot.toObjects(GroupMember.class);
                cache.put(cacheKey, members);
                callback.onSuccess(members);
            })
            .addOnFailureListener(callback::onError);
    }

    @Override
    public void updateGroup(Group group, DataCallback<Void> callback) {
        if (group == null || group.getId() == null) {
            callback.onError(new Exception("Invalid group"));
            return;
        }

        String coverImage = group.getCoverImage();
        if (coverImage != null && (coverImage.startsWith("content://") || coverImage.startsWith("file://"))) {
            StorageReference ref = storage.getReference().child("group_covers/" + group.getId() + ".jpg");
            ref.putFile(android.net.Uri.parse(coverImage))
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        group.setCoverImage(uri.toString());
                        saveGroupFields(group, callback);
                    })
                    .addOnFailureListener(callback::onError))
                .addOnFailureListener(callback::onError);
        } else {
            saveGroupFields(group, callback);
        }
    }

    private void saveGroupFields(Group group, DataCallback<Void> callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", group.getName());
        updates.put("description", group.getDescription());
        updates.put("isPrivate", group.isPrivate());
        updates.put("code", group.getCode());
        if (group.getCoverImage() != null) {
            updates.put("coverImage", group.getCoverImage());
        }

        String uid = getCurrentUserId();
        db.collection("groups").document(group.getId()).update(updates)
            .addOnSuccessListener(aVoid -> {
                cache.remove("groups:" + group.getId() + ":" + uid);
                cache.remove("groups:my");
                cache.remove("groups:discover");
                callback.onSuccess(null);
            })
            .addOnFailureListener(callback::onError);
    }

    @Override
    public void getReportedPhotos(String groupId, DataCallback<List<ReportedPhoto>> callback) {
        if (groupId == null) {
            callback.onError(new Exception("Invalid group ID"));
            return;
        }

        String cacheKey = "groups:" + groupId + ":reported";
        List<ReportedPhoto> cached = (List<ReportedPhoto>) cache.get(cacheKey);
        if (cached != null) {
            callback.onSuccess(cached);
            return;
        }

        // Query photos in this group where reportCount > 0
        db.collection("photos")
            .whereEqualTo("groupId", groupId)
            .whereGreaterThan("reportCount", 0)
            .orderBy("reportCount", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<ReportedPhoto> reports = new ArrayList<>();
                for (com.google.firebase.firestore.QueryDocumentSnapshot doc : querySnapshot) {
                    Photo photo = doc.toObject(Photo.class);
                    // Create ReportedPhoto from Photo
                    ReportedPhoto report = new ReportedPhoto();
                    report.setId(photo.getId());
                    report.setPhoto(photo);
                    report.setReason("Contenu signalé");
                    report.setReporterName("Utilisateurs");
                    report.setDate("À vérifier");
                    reports.add(report);
                }
                cache.put(cacheKey, reports);
                callback.onSuccess(reports);
            })
            .addOnFailureListener(callback::onError);
    }

    @Override
    public void getGroupStats(String groupId, DataCallback<Map<String, Integer>> callback) {
        if (groupId == null) {
            callback.onError(new Exception("Invalid group ID"));
            return;
        }

        getGroupById(groupId, new DataCallback<Group>() {
            @Override
            public void onSuccess(Group group) {
                Map<String, Integer> stats = new java.util.HashMap<>();
                stats.put("active_members", group.getMembersCount());
                stats.put("photos_shared", group.getPhotosCount());

                // Count new members in last 7 days (simplified: just use members count for now)
                stats.put("new_members_7d", 0);

                // Total interactions (simplified: estimate as membersCount * photosCount / 10)
                int interactions = (group.getMembersCount() * group.getPhotosCount()) / Math.max(1, 10);
                stats.put("total_interactions", interactions);

                callback.onSuccess(stats);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    @Override
    public void getNotifications(DataCallback<List<Notification>> callback) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null || currentUser.isAnonymous()) {
            callback.onSuccess(new ArrayList<>());
            return;
        }
        db.collection("notifications")
                .whereEqualTo("userId", currentUser.getUid())
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Notification> notifications = new ArrayList<>();
                    for (com.google.firebase.firestore.QueryDocumentSnapshot doc : querySnapshot) {
                        Notification n = doc.toObject(Notification.class);
                        n.setNotificationId(doc.getId());
                        // Restore relative time from timestamp
                        if (n.getTime() == null || n.getTime().isEmpty()) {
                            long elapsed = System.currentTimeMillis() - n.getTimestamp();
                            long minutes = elapsed / 60000;
                            if (minutes < 60) n.setTime(minutes + " min");
                            else if (minutes < 1440) n.setTime((minutes / 60) + " h");
                            else n.setTime((minutes / 1440) + " j");
                        }
                        notifications.add(n);
                    }
                    callback.onSuccess(notifications);
                })
                .addOnFailureListener(callback::onError);
    }

    @Override
    public void createNotification(Notification notification, DataCallback<Void> callback) {
        db.collection("notifications")
                .add(notification)
                .addOnSuccessListener(ref -> callback.onSuccess(null))
                .addOnFailureListener(callback::onError);
    }

    @Override
    public void markNotificationRead(String notificationId, DataCallback<Void> callback) {
        if (notificationId == null || notificationId.isEmpty()) {
            callback.onSuccess(null);
            return;
        }
        db.collection("notifications").document(notificationId)
                .update("isRead", true)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onError);
    }

    @Override
    public void reportPhoto(String photoId, String reason, DataCallback<Void> callback) {
        if (photoId == null) {
            callback.onError(new Exception("Invalid photo ID"));
            return;
        }
        FirebaseUser currentUser = auth.getCurrentUser();
        String uid = currentUser != null ? currentUser.getUid() : "anonymous";
        db.collection("photos").document(photoId)
                .update(
                        "reportCount", com.google.firebase.firestore.FieldValue.increment(1),
                        "reportedBy", com.google.firebase.firestore.FieldValue.arrayUnion(uid)
                )
                .addOnSuccessListener(aVoid -> {
                    cache.remove("photos:user");
                    callback.onSuccess(null);
                })
                .addOnFailureListener(callback::onError);
    }

    @Override
    public void searchPhotos(String query, DataCallback<List<Photo>> callback) {
        if (query == null || query.trim().isEmpty()) {
            callback.onSuccess(new ArrayList<>());
            return;
        }
        String queryLower = query.trim().toLowerCase();
        FirebaseUser currentUser = auth.getCurrentUser();
        String currentUserId = currentUser != null ? currentUser.getUid() : null;

        // Run tag search and author search in parallel, then merge
        final List<Photo> merged = new ArrayList<>();
        final int[] remaining = {2};

        com.google.firebase.firestore.CollectionReference photosRef = db.collection("photos");

        // Query 1: by tag
        photosRef.whereArrayContains("tags", queryLower)
                .get()
                .addOnSuccessListener(snap -> {
                    for (com.google.firebase.firestore.QueryDocumentSnapshot doc : snap) {
                        Photo p = doc.toObject(Photo.class);
                        if (currentUserId != null && p.getLikedBy() != null) {
                            p.setLiked(p.getLikedBy().contains(currentUserId));
                        }
                        merged.add(p);
                    }
                    remaining[0]--;
                    if (remaining[0] == 0) deliverSearchResults(merged, callback);
                })
                .addOnFailureListener(e -> {
                    remaining[0]--;
                    if (remaining[0] == 0) deliverSearchResults(merged, callback);
                });

        // Query 2: by authorName (prefix range trick)
        String queryUpper = queryLower.substring(0, queryLower.length() - 1)
                + (char)(queryLower.charAt(queryLower.length() - 1) + 1);
        photosRef.whereGreaterThanOrEqualTo("authorName", query)
                .whereLessThan("authorName", queryUpper)
                .get()
                .addOnSuccessListener(snap -> {
                    for (com.google.firebase.firestore.QueryDocumentSnapshot doc : snap) {
                        Photo p = doc.toObject(Photo.class);
                        // Deduplicate by ID
                        boolean exists = false;
                        for (Photo existing : merged) {
                            if (existing.getId() != null && existing.getId().equals(p.getId())) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            if (currentUserId != null && p.getLikedBy() != null) {
                                p.setLiked(p.getLikedBy().contains(currentUserId));
                            }
                            merged.add(p);
                        }
                    }
                    remaining[0]--;
                    if (remaining[0] == 0) deliverSearchResults(merged, callback);
                })
                .addOnFailureListener(e -> {
                    remaining[0]--;
                    if (remaining[0] == 0) deliverSearchResults(merged, callback);
                });
    }

    private void deliverSearchResults(List<Photo> photos, DataCallback<List<Photo>> callback) {
        photos.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
        callback.onSuccess(photos);
    }

    @Override
    public void getNotificationSettings(DataCallback<List<NotificationSettingItem>> callback) { mockDelegate.getNotificationSettings(callback); }

    @Override
    public void getGeneratedItineraries(DataCallback<List<GeneratedItinerary>> callback) { mockDelegate.getGeneratedItineraries(callback); }

    @Override
    public void getItinerarySteps(DataCallback<List<ItineraryStep>> callback) { mockDelegate.getItinerarySteps(callback); }

    @Override
    public void getProfileItineraries(DataCallback<List<ProfileItinerary>> callback) { mockDelegate.getProfileItineraries(callback); }

    @Override
    public void getSearchNavigationOptions(DataCallback<List<SearchNavigationOption>> callback) { mockDelegate.getSearchNavigationOptions(callback); }

    @Override
    public void searchPhotosWithFilters(SearchFilters filters, DataCallback<List<Photo>> callback) {
        getFeedPhotos(new DataCallback<List<Photo>>() {
            @Override
            public void onSuccess(List<Photo> all) {
                List<Photo> result = new ArrayList<>(all);

                if (filters.getQuery() != null && !filters.getQuery().isEmpty()) {
                    String q = filters.getQuery().toLowerCase();
                    result.removeIf(p ->
                        (p.getTitle() == null || !p.getTitle().toLowerCase().contains(q)) &&
                        (p.getDescription() == null || !p.getDescription().toLowerCase().contains(q)));
                }

                if (filters.getPlaceType() != null && !filters.getPlaceType().isEmpty()) {
                    result.removeIf(p -> !filters.getPlaceType().equals(p.getPlaceType()));
                }

                if (filters.getMomentOfDay() != null && !filters.getMomentOfDay().isEmpty()) {
                    result.removeIf(p -> !filters.getMomentOfDay().equals(p.getMomentOfDay()));
                }

                if (filters.getPeriod() != null && !filters.getPeriod().isEmpty()) {
                    long cutoff = computePeriodCutoff(filters.getPeriod());
                    result.removeIf(p -> p.getTimestamp() < cutoff);
                }

                if (filters.getLocation() != null && !filters.getLocation().isEmpty()) {
                    String loc = filters.getLocation().toLowerCase();
                    result.removeIf(p -> p.getLocationName() == null ||
                        !p.getLocationName().toLowerCase().contains(loc));
                }

                if (filters.getAuthor() != null && !filters.getAuthor().isEmpty()) {
                    String auth = filters.getAuthor().toLowerCase();
                    result.removeIf(p -> p.getAuthorName() == null ||
                        !p.getAuthorName().toLowerCase().contains(auth));
                }

                if (filters.getGroupId() != null && !filters.getGroupId().isEmpty()) {
                    result.removeIf(p -> !filters.getGroupId().equals(p.getGroupId()));
                }

                if (filters.getTags() != null && !filters.getTags().isEmpty()) {
                    result.removeIf(p -> {
                        if (p.getTags() == null) return true;
                        for (String tag : filters.getTags()) {
                            if (p.getTags().contains(tag)) return false;
                        }
                        return true;
                    });
                }

                result.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
                callback.onSuccess(result);
            }

            @Override
            public void onError(Exception e) { callback.onError(e); }
        });
    }

    private long computePeriodCutoff(String period) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        switch (period) {
            case "Aujourd'hui":
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                break;
            case "Cette semaine":
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                break;
            case "Ce mois":
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                break;
            case "Cette année":
                cal.set(Calendar.DAY_OF_YEAR, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                break;
            default:
                return 0;
        }
        return cal.getTimeInMillis();
    }
}
