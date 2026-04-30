package com.paullouis.travel;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.paullouis.travel.adapter.CommentAdapter;
import com.paullouis.travel.data.DataCallback;
import com.paullouis.travel.data.FirebaseRepository;
import com.paullouis.travel.model.Comment;
import com.paullouis.travel.data.EventBus;
import com.paullouis.travel.model.Photo;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class PhotoDetailActivity extends AppCompatActivity implements EventBus.CommentListener, EventBus.PhotoLikeListener {

    private Photo photo;
    private boolean isLiked;
    private boolean isBookmarked;
    private int likesCount;
    private int commentsCount;

    private List<Comment> comments;
    private CommentAdapter commentAdapter;
    private TextView tvCommentsCount;
    private TextView tvCommentsHeader;
    private TextView tvLikesCount;
    private ImageView ivLike;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        // Handle safe area for notch
        View rootLayout = findViewById(R.id.detailRootLayout);
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        // Get Parcelable Photo
        photo = getIntent().getParcelableExtra("photo");
        if (photo == null) {
            finish();
            return;
        }

        isLiked = photo.isLiked();
        isBookmarked = photo.isBookmarked();
        likesCount = photo.getLikes();
        commentsCount = photo.getComments();

        initViews();
        setupComments();
        EventBus.registerCommentListener(this);
        EventBus.registerPhotoLikeListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.unregisterCommentListener(this);
        EventBus.unregisterPhotoLikeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void initViews() {
        // Toolbar
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        ImageView btnBookmark = findViewById(R.id.btnBookmark);
        updateBookmarkUI(btnBookmark);
        btnBookmark.setOnClickListener(v -> {
            isBookmarked = !isBookmarked;
            updateBookmarkUI(btnBookmark);
            Toast.makeText(this, isBookmarked ? "Ajouté aux favoris" : "Retiré des favoris", Toast.LENGTH_SHORT).show();
        });

        // Banner Image
        ImageView ivPhoto = findViewById(R.id.ivPhotoDetail);
        if (photo.getImageResId() != 0) {
            Glide.with(this).load(photo.getImageResId()).centerCrop().into(ivPhoto);
        } else {
            Glide.with(this).load(photo.getImageUrl()).centerCrop().into(ivPhoto);
        }

        // Author Section
        TextView tvAuthorInitial = findViewById(R.id.tvAuthorInitialDetail);
        TextView tvAuthorName = findViewById(R.id.tvAuthorNameDetail);
        TextView tvDate = findViewById(R.id.tvDateDetail);
        
        tvAuthorName.setText(photo.getAuthorName());
        tvAuthorInitial.setText(photo.getAuthorInitial());
        tvDate.setText(photo.getDate());

        ImageView ivAuthorAvatar = findViewById(R.id.ivAuthorAvatarDetail);
        if (photo.getAuthorAvatarUrl() != null && !photo.getAuthorAvatarUrl().isEmpty()) {
            ivAuthorAvatar.setVisibility(View.VISIBLE);
            com.bumptech.glide.Glide.with(this)
                    .load(photo.getAuthorAvatarUrl())
                    .circleCrop()
                    .into(ivAuthorAvatar);
        } else {
            ivAuthorAvatar.setVisibility(View.GONE);
        }

        TextView tvGroupBadge = findViewById(R.id.tvGroupBadgeDetail);
        tvGroupBadge.setVisibility(View.GONE);

        // Content
        TextView tvTitle = findViewById(R.id.tvPhotoTitle);
        TextView tvLocation = findViewById(R.id.tvLocationNameDetail);
        TextView tvDescription = findViewById(R.id.tvDescriptionDetail);
        TextView tvCTASubtitle = findViewById(R.id.tvCTASubtitle);

        tvTitle.setText(photo.getTitle());
        tvLocation.setText(photo.getLocationName());
        tvDescription.setText(photo.getDescription());
        
        String locationForCTA = (photo.getLocationName() != null && !photo.getLocationName().isEmpty()) 
            ? photo.getLocationName() : "ce lieu";
        tvCTASubtitle.setText("Générez un parcours de visite autour de " + locationForCTA);

        // Travel Info Section
        TextView tvTravelInfo = findViewById(R.id.tvTravelInfoDetail);
        LinearLayout travelInfoSection = findViewById(R.id.travelInfoSection);
        if (photo.getTravelInfo() != null && !photo.getTravelInfo().isEmpty()) {
            travelInfoSection.setVisibility(View.VISIBLE);
            tvTravelInfo.setText(photo.getTravelInfo());
        } else {
            travelInfoSection.setVisibility(View.GONE);
        }

        // Audio Note Section
        LinearLayout audioSection = findViewById(R.id.audioNoteSection);
        if (photo.getAudioUrl() != null && !photo.getAudioUrl().isEmpty()) {
            audioSection.setVisibility(View.VISIBLE);
            audioSection.setOnClickListener(v -> playAudioNote(photo.getAudioUrl()));
        } else {
            audioSection.setVisibility(View.GONE);
        }

        // Buttons
        findViewById(R.id.btnViewOnMap).setOnClickListener(v -> {
            Toast.makeText(this, "Redirection vers la carte...", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnGetDirections).setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("geo:" + photo.getLat() + "," + photo.getLng() + "?q=" + Uri.encode(photo.getLocationName()));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                startActivity(new Intent(Intent.ACTION_VIEW, gmmIntentUri));
            }
        });

        findViewById(R.id.btnCreatePathDetail).setOnClickListener(v -> {
            if (FirebaseRepository.getInstance().isUserLoggedIn()) {
                Intent intent = new Intent(this, GatewayActivity.class);
                intent.putExtra("location", photo.getLocationName());
                startActivity(intent);
            } else {
                LoginRequiredDialogFragment.newInstance().show(getSupportFragmentManager(), "login_required");
            }
        });

        // Tags
        ChipGroup chipGroup = findViewById(R.id.chipGroupTags);
        if (photo.getTags() != null) {
            for (String tag : photo.getTags()) {
                Chip chip = new Chip(this);
                chip.setText(tag);
                chip.setChipBackgroundColorResource(R.color.surface);
                chip.setChipStrokeColorResource(R.color.primary);
                chip.setChipStrokeWidth(1.0f);
                chip.setTextColor(ContextCompat.getColor(this, R.color.primary));
                chipGroup.addView(chip);
            }
        }

        // Action Bar — Like with Firebase persistence
        ImageView ivLike = findViewById(R.id.ivLikeDetail);
        TextView tvLikes = findViewById(R.id.tvLikesCountDetail);
        tvCommentsCount = findViewById(R.id.tvCommentsCountDetail);
        
        tvLikes.setText(String.valueOf(likesCount));
        tvCommentsCount.setText(String.valueOf(commentsCount));
        updateLikeUI(ivLike, tvLikes);

        ivLike.setOnClickListener(v -> {
            // Optimistic update
            boolean wasLiked = isLiked;
            int prevLikes = likesCount;
            
            isLiked = !isLiked;
            if (isLiked) likesCount++; else likesCount--;
            tvLikes.setText(String.valueOf(likesCount));
            updateLikeUI(ivLike, tvLikes);
            
            // Update photo model and notify feed
            photo.setLiked(isLiked);
            photo.setLikes(likesCount);
            
            // Sync likedBy array locally for optimistic consistency
            if (photo.getLikedBy() == null) photo.setLikedBy(new ArrayList<>());
            String currentUserId = FirebaseRepository.getInstance().getCurrentUserId();
            if (currentUserId != null) {
                if (isLiked) {
                    if (!photo.getLikedBy().contains(currentUserId)) photo.getLikedBy().add(currentUserId);
                } else {
                    photo.getLikedBy().remove(currentUserId);
                }
            }
            
            EventBus.notifyPhotoUpdated(photo);

            // Persist to Firebase
            FirebaseRepository.getInstance().toggleLike(photo.getId(), isLiked, new DataCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    // Confirmed — nothing to do
                }

                @Override
                public void onError(Exception e) {
                    // Rollback
                    isLiked = wasLiked;
                    likesCount = prevLikes;
                    tvLikes.setText(String.valueOf(likesCount));
                    updateLikeUI(ivLike, tvLikes);
                    photo.setLiked(wasLiked);
                    photo.setLikes(prevLikes);
                    EventBus.notifyPhotoUpdated(photo);
                    Toast.makeText(PhotoDetailActivity.this, "Erreur de mise à jour", Toast.LENGTH_SHORT).show();
                }
            });
        });

        findViewById(R.id.ivShareDetail).setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Regardez cette superbe photo : " + photo.getTitle() + " à " + photo.getLocationName());
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Partager via"));
        });

        findViewById(R.id.ivReportDetail).setOnClickListener(v -> {
            String[] reasons = {"Contenu inapproprié", "Spam", "Fausses informations", "Autre"};
            new AlertDialog.Builder(this)
                .setTitle("Signaler la photo")
                .setItems(reasons, (dialog, which) -> {
                    String reason = reasons[which];
                    FirebaseRepository.getInstance().reportPhoto(photo.getId(), reason, new DataCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            Toast.makeText(PhotoDetailActivity.this, "Photo signalée, merci.", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(PhotoDetailActivity.this, "Erreur lors du signalement", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Annuler", null)
                .show();
        });
    }

    private void playAudioNote(String audioUrl) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                Toast.makeText(this, "Lecture de la note audio...", Toast.LENGTH_SHORT).show();
            });
            mediaPlayer.setOnCompletionListener(mp -> {
                Toast.makeText(this, "Note audio terminée", Toast.LENGTH_SHORT).show();
            });
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Toast.makeText(this, "Impossible de lire l'audio", Toast.LENGTH_SHORT).show();
                return true;
            });
        } catch (Exception e) {
            Toast.makeText(this, "Erreur de lecture audio", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupComments() {
        tvCommentsHeader = findViewById(R.id.tvCommentsHeader);
        RecyclerView rvComments = findViewById(R.id.rvComments);
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        
        comments = new ArrayList<>();
        commentAdapter = new CommentAdapter(comments);
        rvComments.setAdapter(commentAdapter);
        
        FirebaseRepository.getInstance().getComments(photo.getId(), new DataCallback<List<Comment>>() {
            @Override
            public void onSuccess(List<Comment> result) {
                comments.clear();
                comments.addAll(result);
                commentAdapter.notifyDataSetChanged();
                // Fix: sync commentsCount from the actual loaded data
                commentsCount = comments.size();
                updateCommentsUI();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(PhotoDetailActivity.this, "Impossible de charger les commentaires", Toast.LENGTH_SHORT).show();
            }
        });
        
        updateCommentsUI();
        setupCommentsInput();
    }

    private void updateCommentsUI() {
        if (tvCommentsCount != null && comments != null) {
            tvCommentsCount.setText(String.valueOf(commentsCount));
            if (tvCommentsHeader != null) {
                tvCommentsHeader.setText("Commentaires (" + commentsCount + ")");
            }
        }
    }

    private void setupCommentsInput() {
        EditText etComment = findViewById(R.id.etComment);
        View btnSend = findViewById(R.id.btnSendComment);
        
        btnSend.setOnClickListener(v -> {
            if (!FirebaseRepository.getInstance().isUserLoggedIn()) {
                LoginRequiredDialogFragment.newInstance().show(getSupportFragmentManager(), "login_required");
                return;
            }
            String text = etComment.getText().toString().trim();
            if (!text.isEmpty()) {
                // Prevent spam
                btnSend.setEnabled(false);
                
                FirebaseRepository.getInstance().getCurrentUser(new DataCallback<com.paullouis.travel.model.User>() {
                    @Override
                    public void onSuccess(com.paullouis.travel.model.User user) {
                        String name = (user.getName() != null && !user.getName().isEmpty()) ? user.getName() : "Utilisateur";
                        String initial = name.substring(0, 1).toUpperCase();
                        Comment newComment = new Comment(name, initial, "À l'instant", text);
                        newComment.setTimestamp(System.currentTimeMillis());
                        newComment.setUserAvatarUrl(user.getAvatarUrl());
                        newComment.setLoading(true);
                        
                        // Optimistic updates
                        etComment.setText("");
                        btnSend.setEnabled(true);
                        comments.add(newComment);
                        commentAdapter.notifyItemInserted(comments.size() - 1);
                        RecyclerView rvComments = findViewById(R.id.rvComments);
                        rvComments.scrollToPosition(comments.size() - 1);
                        
                        commentsCount++;
                        updateCommentsUI();
                        
                        // Emit update for feed
                        photo.setComments(commentsCount);
                        EventBus.notifyPhotoUpdated(photo);
                        
                        // FirebaseRepository now emits EventBus events for comment lifecycle
                        FirebaseRepository.getInstance().addComment(photo.getId(), newComment, new DataCallback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                // Comment confirmed by server; loading state already cleared by EventBus
                                newComment.setLoading(false);
                                commentAdapter.updateComment(newComment);
                            }

                            @Override
                            public void onError(Exception e) {
                                // Rollback: remove the optimistic comment
                                commentAdapter.removeComment(newComment.getTimestamp());
                                commentsCount--;
                                updateCommentsUI();
                                photo.setComments(commentsCount);
                                EventBus.notifyPhotoUpdated(photo);
                                Toast.makeText(PhotoDetailActivity.this, "Erreur d'ajout: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        btnSend.setEnabled(true);
                        Toast.makeText(PhotoDetailActivity.this, "Erreur: utilisateur non trouvé", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void updateLikeUI(ImageView iv, TextView tv) {
        if (isLiked) {
            iv.setImageResource(R.drawable.ic_heart_filled);
            iv.setColorFilter(ContextCompat.getColor(this, R.color.error)); // Use error red
            tv.setTextColor(ContextCompat.getColor(this, R.color.error));
        } else {
            iv.setImageResource(R.drawable.ic_favorite_border);
            iv.setColorFilter(ContextCompat.getColor(this, R.color.on_background));
            tv.setTextColor(ContextCompat.getColor(this, R.color.on_background));
        }
    }

    private void updateBookmarkUI(ImageView iv) {
        if (isBookmarked) {
            iv.setImageResource(R.drawable.ic_bookmark_filled);
            iv.setColorFilter(ContextCompat.getColor(this, R.color.primary));
        } else {
            iv.setImageResource(R.drawable.ic_bookmark_border);
            iv.setColorFilter(ContextCompat.getColor(this, R.color.on_background));
        }
    }

    // --- CommentListener Implementation ---

    @Override
    public void onCommentAdded(String photoId, Comment comment) {
        // Already handled by local optimistic update
    }

    @Override
    public void onCommentConfirmed(String photoId, Comment comment) {
        if (photo.getId().equals(photoId)) {
            commentAdapter.updateComment(comment);
        }
    }

    @Override
    public void onCommentFailed(String photoId, long commentTimestamp) {
        if (photo.getId().equals(photoId)) {
            commentAdapter.removeComment(commentTimestamp);
            commentsCount--;
            updateCommentsUI();
            photo.setComments(commentsCount);
            EventBus.notifyPhotoUpdated(photo);
        }
    }

    // --- PhotoLikeListener Implementation ---

    @Override
    public void onPhotoLiked(String photoId, boolean liked) {
        if (photoId.equals(photo.getId())) {
            isLiked = liked;
            photo.setLiked(liked);
            // Update UI - get the views again to be safe
            ImageView ivLike = findViewById(R.id.ivLikeDetail);
            TextView tvLikes = findViewById(R.id.tvLikesCountDetail);
            if (ivLike != null && tvLikes != null) {
                updateLikeUI(ivLike, tvLikes);
            }
        }
    }
}
