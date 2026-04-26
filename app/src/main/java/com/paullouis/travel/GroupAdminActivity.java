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
import com.paullouis.travel.admin.AdminModerationFragment;
import com.paullouis.travel.admin.AdminSettingsFragment;
import com.paullouis.travel.admin.AdminStatsFragment;
import com.paullouis.travel.data.MockDataProvider;
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

        initToolbar();
        initViewPager();
        WindowInsetsHelper.applyStatusBarPadding(findViewById(R.id.llToolbarContainer));
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        Group group = MockDataProvider.getGroupById(groupId);
        if (group != null) {
            ((android.widget.TextView) findViewById(R.id.tvAdminSubtitle)).setText(group.getName());
        }
    }

    private void initViewPager() {
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        viewPager.setAdapter(new AdminPagerAdapter(this, groupId));
        viewPager.setOffscreenPageLimit(3); // Keep tabs in memory for smooth transitions

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Membres");
                    tab.setIcon(R.drawable.ic_user);
                    break;
                case 1:
                    tab.setText("Modération");
                    tab.setIcon(R.drawable.ic_shield);
                    break;
                case 2:
                    tab.setText("Paramètres");
                    tab.setIcon(R.drawable.ic_settings);
                    break;
                case 3:
                    tab.setText("Stats");
                    tab.setIcon(R.drawable.ic_trending_up);
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
                case 1: return AdminModerationFragment.newInstance(groupId);
                case 2: return AdminSettingsFragment.newInstance(groupId);
                case 3: return AdminStatsFragment.newInstance(groupId);
                default: return new Fragment();
            }
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }
}
