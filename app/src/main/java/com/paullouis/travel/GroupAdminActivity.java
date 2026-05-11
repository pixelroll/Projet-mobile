package com.paullouis.travel;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.paullouis.travel.admin.AdminMembersFragment;
import com.paullouis.travel.admin.AdminSettingsFragment;
import com.paullouis.travel.data.DataCallback;
import com.paullouis.travel.data.FirebaseRepository;
import com.paullouis.travel.model.Group;
import com.paullouis.travel.util.WindowInsetsHelper;

public class GroupAdminActivity extends AppCompatActivity {

    public static final String EXTRA_GROUP_ID = "group_id";
    private String groupId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_admin);

        groupId = getIntent().getStringExtra(EXTRA_GROUP_ID);

        // Check permissions before showing admin panel
        FirebaseRepository.getInstance().getGroupById(groupId, new DataCallback<Group>() {
            @Override
            public void onSuccess(Group group) {
                if (group.getRole() == Group.UserRole.ADMIN || group.getRole() == Group.UserRole.OWNER) {
                    initToolbar();
                    initViewPager();
                    WindowInsetsHelper.applyStatusBarPadding(findViewById(R.id.llToolbarContainer));
                } else {
                    android.widget.Toast.makeText(GroupAdminActivity.this, "Vous n'avez pas les permissions nécessaires", android.widget.Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onError(Exception e) {
                android.widget.Toast.makeText(GroupAdminActivity.this, "Impossible de vérifier les permissions", android.widget.Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        FirebaseRepository.getInstance().getGroupById(groupId, new DataCallback<Group>() {
            @Override
            public void onSuccess(Group group) {
                android.widget.TextView subtitle = findViewById(R.id.tvAdminSubtitle);
                if (subtitle != null && group != null) {
                    subtitle.setText(group.getName());
                }
            }

            @Override
            public void onError(Exception e) {}
        });
    }

    private void initViewPager() {
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        viewPager.setAdapter(new AdminPagerAdapter(this, groupId));
        viewPager.setOffscreenPageLimit(1);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Membres");
                    tab.setIcon(R.drawable.ic_user);
                    break;
                case 1:
                    tab.setText("Paramètres");
                    tab.setIcon(R.drawable.ic_settings);
                    break;
            }
        }).attach();
    }

    private static class AdminPagerAdapter extends FragmentStateAdapter {
        private final String groupId;

        public AdminPagerAdapter(@NonNull AppCompatActivity activity, String groupId) {
            super(activity);
            this.groupId = groupId;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return AdminMembersFragment.newInstance(groupId);
                case 1: return AdminSettingsFragment.newInstance(groupId);
                default: return new Fragment();
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
