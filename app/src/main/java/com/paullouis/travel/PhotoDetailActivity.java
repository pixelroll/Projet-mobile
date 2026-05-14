package com.paullouis.travel;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.format.DateUtils;
import android.widget.HorizontalScrollView;
import android.os.Bundle;
import android.util.TypedValue;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.paullouis.travel.adapter.CommentAdapter;
import com.paullouis.travel.data.DataCallback;
import com.paullouis.travel.data.FirebaseRepository;
import com.paullouis.travel.model.Comment;
import com.paullouis.travel.data.EventBus;
import com.paullouis.travel.model.Photo;
import com.paullouis.travel.ItineraryDetailActivity;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class PhotoDetailActivity extends AppCompatActivity implements EventBus.CommentListener, EventBus.PhotoLikeListener {

    private Photo photo;
    private boolean isLiked;
    private int likesCount;
    private int commentsCount;

    private List<Comment> comments;
    private CommentAdapter commentAdapter;
    private TextView tvCommentsHeader;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        View rootLayout = findViewById(R.id.detailRootLayout);
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        photo = getIntent().getParcelableExtra("photo");
        if (photo == null) {
            finish();
            return;
        }

        isLiked = photo.isLiked();
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
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Photo
        ImageView ivPhoto = findViewById(R.id.ivPhotoDetail);
        if (photo.getImageResId() != 0) {
            Glide.with(this).load(photo.getImageResId()).centerCrop().into(ivPhoto);
        } else {
            Glide.with(this).load(photo.getImageUrl()).centerCrop().into(ivPhoto);
        }

        // Author section
        TextView tvAuthorInitial = findViewById(R.id.tvAuthorInitialDetail);
        TextView tvAuthorName = findViewById(R.id.tvAuthorNameDetail);
        TextView tvDate = findViewById(R.id.tvDateDetail);

        tvAuthorName.setText(photo.getAuthorName());
        tvAuthorInitial.setText(photo.getAuthorInitial());
        CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(
                photo.getTimestamp(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
        tvDate.setText(relativeTime);

        ImageView ivAuthorAvatar = findViewById(R.id.ivAuthorAvatarDetail);
        if (photo.getAuthorAvatarUrl() != null && !photo.getAuthorAvatarUrl().isEmpty()) {
            ivAuthorAvatar.setVisibility(View.VISIBLE);
            Glide.with(this).load(photo.getAuthorAvatarUrl()).circleCrop().into(ivAuthorAvatar);
        } else {
            ivAuthorAvatar.setVisibility(View.GONE);
        }

        findViewById(R.id.tvGroupBadgeDetail).setVisibility(View.GONE);

        // Location + date row
        TextView tvCTASubtitle = findViewById(R.id.tvCTASubtitle);
        LinearLayout locationSection = findViewById(R.id.locationSection);
        TextView tvLocation = findViewById(R.id.tvLocationNameDetail);
        View ivLocationPin = findViewById(R.id.ivLocationPin);
        View ivMapsLink = findViewById(R.id.ivMapsLink);
        View tvSeparator = findViewById(R.id.tvLocationDateSeparator);

        boolean hasLocation = photo.getLocationName() != null && !photo.getLocationName().isEmpty();
        if (hasLocation) {
            tvLocation.setVisibility(View.VISIBLE);
            ivLocationPin.setVisibility(View.VISIBLE);
            ivMapsLink.setVisibility(View.VISIBLE);
            tvSeparator.setVisibility(View.VISIBLE);
            tvLocation.setText(photo.getLocationName());
            tvCTASubtitle.setText("Générez un parcours de visite autour de " + photo.getLocationName());

            boolean hasCoordsAndLocation = photo.getLat() != 0 && photo.getLng() != 0;
            locationSection.setOnClickListener(v -> {
                String query = hasCoordsAndLocation
                    ? "geo:" + photo.getLat() + "," + photo.getLng() + "?q=" + Uri.encode(photo.getLocationName())
                    : "geo:0,0?q=" + Uri.encode(photo.getLocationName());
                Uri gmmIntentUri = Uri.parse(query);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    startActivity(new Intent(Intent.ACTION_VIEW, gmmIntentUri));
                }
            });
        } else {
            tvLocation.setVisibility(View.GONE);
            ivLocationPin.setVisibility(View.GONE);
            ivMapsLink.setVisibility(View.GONE);
            tvSeparator.setVisibility(View.GONE);
            locationSection.setClickable(false);
            tvCTASubtitle.setText("Générez un parcours de visite autour de ce lieu");
        }

        // Report button
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

        // Tags — single horizontal scrollable line
        LinearLayout llTags = findViewById(R.id.llTagsContainer);
        HorizontalScrollView tagsScrollView = findViewById(R.id.tagsScrollView);
        if (photo.getTags() != null && !photo.getTags().isEmpty()) {
            tagsScrollView.setVisibility(View.VISIBLE);
            for (String tag : photo.getTags()) {
                Chip chip = new Chip(this);
                chip.setText(tag);
                chip.setChipBackgroundColorResource(R.color.surface);
                chip.setChipStrokeWidth(0f);
                chip.setTextColor(ContextCompat.getColor(this, R.color.muted_foreground));
                chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f);
                chip.setEnsureMinTouchTargetSize(false);
                chip.setClickable(false);
                chip.setFocusable(false);
                llTags.addView(chip);
            }
        } else {
            tagsScrollView.setVisibility(View.GONE);
        }

        // Travel Info Section
        TextView tvTravelInfo = findViewById(R.id.tvTravelInfoDetail);
        LinearLayout travelInfoSection = findViewById(R.id.travelInfoSection);
        if (photo.getTravelInfo() != null && !photo.getTravelInfo().isEmpty()) {
            travelInfoSection.setVisibility(View.VISIBLE);
            tvTravelInfo.setText(photo.getTravelInfo());
        } else {
            travelInfoSection.setVisibility(View.GONE);
        }

        // Parcours section (compact info only, no button inside)
        LinearLayout parcoursSection = findViewById(R.id.parcoursSection);
        MaterialButton btnVoirParcours = findViewById(R.id.btnVoirParcours);
        if (photo.getItineraryId() != null && !photo.getItineraryId().isEmpty()) {
            parcoursSection.setVisibility(View.VISIBLE);
            TextView tvParcoursInfo = findViewById(R.id.tvParcoursInfo);
            StringBuilder sb = new StringBuilder("Prise dans le parcours \"");
            sb.append(photo.getItineraryTitle() != null ? photo.getItineraryTitle() : "");
            sb.append("\"");
            if (photo.getStepName() != null && !photo.getStepName().isEmpty()) {
                sb.append(", étape : ").append(photo.getStepName());
            }
            tvParcoursInfo.setText(sb.toString());
            btnVoirParcours.setVisibility(View.VISIBLE);
            btnVoirParcours.setOnClickListener(v -> {
                Intent intent = new Intent(this, ItineraryDetailActivity.class);
                intent.putExtra(ItineraryDetailActivity.EXTRA_SAVED_ITINERARY_ID, photo.getItineraryId());
                startActivity(intent);
            });
        } else {
            parcoursSection.setVisibility(View.GONE);
            btnVoirParcours.setVisibility(View.GONE);
        }

        // Audio Note Section
        LinearLayout audioSection = findViewById(R.id.audioNoteSection);
        if (photo.getAudioUrl() != null && !photo.getAudioUrl().isEmpty()) {
            audioSection.setVisibility(View.VISIBLE);
            audioSection.setOnClickListener(v -> playAudioNote(photo.getAudioUrl()));
        } else {
            audioSection.setVisibility(View.GONE);
        }

        // CTA button
        findViewById(R.id.btnCreatePathDetail).setOnClickListener(v -> {
            if (FirebaseRepository.getInstance().isUserLoggedIn()) {
                startActivity(new Intent(this, LikedPhotosSelectionActivity.class));
            } else {
                LoginRequiredDialogFragment.newInstance().show(getSupportFragmentManager(), "login_required");
            }
        });

        // Like button
        ImageView ivLike = findViewById(R.id.ivLikeDetail);
        TextView tvLikes = findViewById(R.id.tvLikesCountDetail);

        tvLikes.setText(String.valueOf(likesCount));
        updateLikeUI(ivLike, tvLikes);

        ivLike.setOnClickListener(v -> {
            if (!FirebaseRepository.getInstance().isUserLoggedIn()) {
                LoginRequiredDialogFragment.newInstance().show(getSupportFragmentManager(), "login_required");
                return;
            }
            boolean wasLiked = isLiked;
            int prevLikes = likesCount;

            isLiked = !isLiked;
            if (isLiked) likesCount++; else likesCount--;
            tvLikes.setText(String.valueOf(likesCount));
            updateLikeUI(ivLike, tvLikes);

            photo.setLiked(isLiked);
            photo.setLikes(likesCount);

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

            FirebaseRepository.getInstance().toggleLike(photo.getId(), isLiked, new DataCallback<Void>() {
                @Override
                public void onSuccess(Void result) {}

                @Override
                public void onError(Exception e) {
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
            mediaPlayer.setOnCompletionListener(mp ->
                Toast.makeText(this, "Note audio terminée", Toast.LENGTH_SHORT).show());
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
        if (tvCommentsHeader != null) {
            tvCommentsHeader.setText("Commentaires (" + commentsCount + ")");
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
                btnSend.setEnabled(false);

                FirebaseRepository.getInstance().getCurrentUser(new DataCallback<com.paullouis.travel.model.User>() {
                    @Override
                    public void onSuccess(com.paullouis.travel.model.User user) {
                        String name = (user.getName() != null && !user.getName().isEmpty()) ? user.getName() : "Utilisateur";
                        String initial = name.substring(0, 1).toUpperCase();
                        Comment newComment = new Comment(name, initial, "", text);
                        newComment.setTimestamp(System.currentTimeMillis());
                        newComment.setUserAvatarUrl(user.getAvatarUrl());
                        newComment.setLoading(true);

                        etComment.setText("");
                        btnSend.setEnabled(true);
                        comments.add(newComment);
                        commentAdapter.notifyItemInserted(comments.size() - 1);
                        RecyclerView rvComments = findViewById(R.id.rvComments);
                        rvComments.scrollToPosition(comments.size() - 1);

                        commentsCount++;
                        updateCommentsUI();

                        photo.setComments(commentsCount);
                        EventBus.notifyPhotoUpdated(photo);

                        FirebaseRepository.getInstance().addComment(photo.getId(), newComment, new DataCallback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                newComment.setLoading(false);
                                commentAdapter.updateComment(newComment);
                            }

                            @Override
                            public void onError(Exception e) {
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
        View cardCTA = findViewById(R.id.cardCTAVisit);
        if (isLiked) {
            iv.setImageResource(R.drawable.ic_heart_filled);
            iv.setColorFilter(ContextCompat.getColor(this, R.color.error));
            tv.setTextColor(ContextCompat.getColor(this, R.color.error));
            if (cardCTA != null) cardCTA.setVisibility(View.VISIBLE);
        } else {
            iv.setImageResource(R.drawable.ic_favorite_border);
            iv.setColorFilter(ContextCompat.getColor(this, R.color.on_background));
            tv.setTextColor(ContextCompat.getColor(this, R.color.on_background));
            if (cardCTA != null) cardCTA.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCommentAdded(String photoId, Comment comment) {}

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

    @Override
    public void onPhotoLiked(String photoId, boolean liked) {
        if (photoId.equals(photo.getId())) {
            isLiked = liked;
            photo.setLiked(liked);
            ImageView ivLike = findViewById(R.id.ivLikeDetail);
            TextView tvLikes = findViewById(R.id.tvLikesCountDetail);
            if (ivLike != null && tvLikes != null) {
                updateLikeUI(ivLike, tvLikes);
            }
        }
    }
}
