package com.paullouis.travel;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.paullouis.travel.adapter.SimilarPhotoAdapter;
import com.paullouis.travel.data.MockDataProvider;
import com.paullouis.travel.model.Comment;
import com.paullouis.travel.model.Photo;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class PhotoDetailActivity extends AppCompatActivity {

    private Photo photo;
    private boolean isLiked;
    private boolean isBookmarked;
    private int likesCount;
    private int commentsCount;
    
    private List<Comment> comments;
    private CommentAdapter commentAdapter;
    private TextView tvCommentsCount;
    private TextView tvCommentsHeader;

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
        setupSimilarPhotos();
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
        tvCTASubtitle.setText("Générez un parcours de visite autour de " + photo.getLocationName());

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
            if (MockDataProvider.isUserLoggedIn()) {
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

        // Action Bar
        ImageView ivLike = findViewById(R.id.ivLikeDetail);
        TextView tvLikes = findViewById(R.id.tvLikesCountDetail);
        tvCommentsCount = findViewById(R.id.tvCommentsCountDetail);
        
        tvLikes.setText(String.valueOf(likesCount));
        tvCommentsCount.setText(String.valueOf(commentsCount));
        updateLikeUI(ivLike, tvLikes);

        ivLike.setOnClickListener(v -> {
            isLiked = !isLiked;
            if (isLiked) likesCount++; else likesCount--;
            tvLikes.setText(String.valueOf(likesCount));
            updateLikeUI(ivLike, tvLikes);
        });

        findViewById(R.id.ivShareDetail).setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Regardez cette superbe photo : " + photo.getTitle() + " à " + photo.getLocationName());
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Partager via"));
        });

        findViewById(R.id.ivReportDetail).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Signaler la photo")
                .setMessage("Voulez-vous signaler cette photo pour contenu inapproprié ?")
                .setPositiveButton("Signaler", (dialog, which) -> {
                    Toast.makeText(this, "Photo signalée aux administrateurs", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Annuler", null)
                .show();
        });
    }

    private void setupComments() {
        tvCommentsHeader = findViewById(R.id.tvCommentsHeader);
        RecyclerView rvComments = findViewById(R.id.rvComments);
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        
        comments = new ArrayList<>(MockDataProvider.getMockComments());
        commentAdapter = new CommentAdapter(comments);
        rvComments.setAdapter(commentAdapter);
        
        updateCommentsUI();

        EditText etComment = findViewById(R.id.etComment);
        View btnSend = findViewById(R.id.btnSendComment);
        
        btnSend.setOnClickListener(v -> {
            if (!MockDataProvider.isUserLoggedIn()) {
                LoginRequiredDialogFragment.newInstance().show(getSupportFragmentManager(), "login_required");
                return;
            }
            String text = etComment.getText().toString().trim();
            if (!text.isEmpty()) {
                Comment newComment = new Comment("Sophie M.", "S", "À l'instant", text);
                comments.add(0, newComment);
                commentAdapter.notifyItemInserted(0);
                rvComments.scrollToPosition(0);
                
                commentsCount++;
                updateCommentsUI();
                
                etComment.setText("");
                Toast.makeText(this, "Commentaire ajouté", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCommentsUI() {
        tvCommentsCount.setText(String.valueOf(commentsCount));
        tvCommentsHeader.setText("Commentaires (" + commentsCount + ")");
    }

    private void setupSimilarPhotos() {
        RecyclerView rvSimilar = findViewById(R.id.rvSimilarPhotos);
        rvSimilar.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        
        List<Photo> similarPhotos = MockDataProvider.getSimilarPhotos(photo);
        SimilarPhotoAdapter similarAdapter = new SimilarPhotoAdapter(similarPhotos);
        rvSimilar.setAdapter(similarAdapter);
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
}
