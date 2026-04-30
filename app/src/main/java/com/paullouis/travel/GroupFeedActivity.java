package com.paullouis.travel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.paullouis.travel.adapter.PhotoAdapter;
import com.paullouis.travel.data.DataCallback;
import com.paullouis.travel.data.EventBus;
import com.paullouis.travel.data.FirebaseRepository;
import com.paullouis.travel.model.Group;
import com.paullouis.travel.model.Photo;
import com.paullouis.travel.util.GroupPermissionHelper;
import com.paullouis.travel.util.WindowInsetsHelper;
import java.util.ArrayList;
import java.util.List;

public class GroupFeedActivity extends AppCompatActivity implements EventBus.PhotoListener {

    public static final String EXTRA_GROUP_ID = "group_id";
    public static final String EXTRA_GROUP_NAME = "group_name";

    private String groupId;
    private String groupName;
    private PhotoAdapter photoAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_feed);

        groupId = getIntent().getStringExtra(EXTRA_GROUP_ID);
        groupName = getIntent().getStringExtra(EXTRA_GROUP_NAME);

        if (groupId == null) {
            Toast.makeText(this, "Groupe introuvable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initToolbar();
        initViews();
        WindowInsetsHelper.applyStatusBarPadding(findViewById(R.id.appBar));
        loadData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.registerPhotoListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.unregisterPhotoListener(this);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void initViews() {
        TextView tvName = findViewById(R.id.tvGroupNameToolbar);
        tvName.setText(groupName);

        progressBar = findViewById(R.id.progressBar);

        findViewById(R.id.btnPublish).setOnClickListener(v -> {
            Intent intent = new Intent(this, PublishPhotoActivity.class);
            intent.putExtra(PublishPhotoActivity.EXTRA_GROUP_ID, groupId);
            intent.putExtra(PublishPhotoActivity.EXTRA_GROUP_NAME, groupName);
            startActivity(intent);
        });

        findViewById(R.id.btnSettings).setOnClickListener(v -> {
            Intent intent = new Intent(this, GroupAdminActivity.class);
            intent.putExtra(GroupAdminActivity.EXTRA_GROUP_ID, groupId);
            startActivity(intent);
        });

        RecyclerView rvPhotos = findViewById(R.id.rvPhotos);
        rvPhotos.setLayoutManager(new LinearLayoutManager(this));
        photoAdapter = new PhotoAdapter(new ArrayList<>());
        rvPhotos.setAdapter(photoAdapter);
    }

    private void loadData() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        // Load group info to check permissions
        FirebaseRepository.getInstance().getGroupById(groupId, new DataCallback<Group>() {
            @Override
            public void onSuccess(Group group) {
                boolean canEdit = GroupPermissionHelper.canEditSettings(group);
                View btnSettings = findViewById(R.id.btnSettings);
                if (btnSettings != null) {
                    btnSettings.setVisibility(canEdit ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void onError(Exception e) {
                View btnSettings = findViewById(R.id.btnSettings);
                if (btnSettings != null) btnSettings.setVisibility(View.GONE);
            }
        });

        // Load group photos from Firebase
        FirebaseRepository.getInstance().getPhotosByGroup(groupId, new DataCallback<List<Photo>>() {
            @Override
            public void onSuccess(List<Photo> photos) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (photoAdapter != null) {
                    photoAdapter = new PhotoAdapter(new ArrayList<>(photos));
                    RecyclerView rvPhotos = findViewById(R.id.rvPhotos);
                    if (rvPhotos != null) rvPhotos.setAdapter(photoAdapter);
                }
            }

            @Override
            public void onError(Exception e) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(GroupFeedActivity.this, "Impossible de charger les photos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- EventBus.PhotoListener ---

    @Override
    public void onPhotoAdded(Photo photo) {
        if (photoAdapter != null && groupId != null && groupId.equals(photo.getGroupId())) {
            photoAdapter.addPhoto(photo);
            RecyclerView rvPhotos = findViewById(R.id.rvPhotos);
            if (rvPhotos != null) rvPhotos.scrollToPosition(0);
        }
    }

    @Override
    public void onPhotoUpdated(Photo photo) {
        if (photoAdapter != null) photoAdapter.updatePhoto(photo);
    }

    @Override
    public void onPhotoRemoved(String photoId) {
        if (photoAdapter != null) photoAdapter.removePhoto(photoId);
    }
}
