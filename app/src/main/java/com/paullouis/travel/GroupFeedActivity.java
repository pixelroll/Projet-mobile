package com.paullouis.travel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.paullouis.travel.adapter.PhotoAdapter;
import com.paullouis.travel.data.MockDataProvider;
import com.paullouis.travel.model.Group;
import com.paullouis.travel.model.Photo;
import com.paullouis.travel.util.GroupPermissionHelper;
import com.paullouis.travel.util.WindowInsetsHelper;
import java.util.List;

public class GroupFeedActivity extends AppCompatActivity {

    public static final String EXTRA_GROUP_ID = "group_id";
    public static final String EXTRA_GROUP_NAME = "group_name";

    private String groupId;
    private String groupName;

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
    }

    private void loadData() {
        Group group = MockDataProvider.getGroupById(groupId);

        if (group != null) {
            findViewById(R.id.btnSettings).setVisibility(
                GroupPermissionHelper.canEditSettings(group) ? View.VISIBLE : View.GONE
            );
        }

        RecyclerView rvPhotos = findViewById(R.id.rvPhotos);
        rvPhotos.setLayoutManager(new LinearLayoutManager(this));
        List<Photo> photos = MockDataProvider.getPhotosByGroup(groupId);
        rvPhotos.setAdapter(new PhotoAdapter(photos));
    }
}
