package com.paullouis.travel;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class GroupsActivity extends AppCompatActivity implements CreateGroupDialogFragment.OnGroupCreatedListener {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        // Handle safe area
        View headerLayout = findViewById(R.id.headerLayout);
        View bottomNav = findViewById(R.id.bottom_navigation);
        
        ViewCompat.setOnApplyWindowInsetsListener(headerLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        ViewCompat.setOnApplyWindowInsetsListener(bottomNav, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), systemBars.bottom);
            return insets;
        });

        // Init views
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        refreshGroups();

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(position == 0 ? "Mes groupes" : "Découvrir");
        }).attach();

        // Listeners
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnInvite).setOnClickListener(v -> {
            JoinGroupDialogFragment dialog = new JoinGroupDialogFragment();
            dialog.show(getSupportFragmentManager(), "JoinGroupDialog");
        });
        findViewById(R.id.btnCreate).setOnClickListener(v -> {
            CreateGroupDialogFragment dialog = new CreateGroupDialogFragment();
            dialog.setOnGroupCreatedListener(this);
            dialog.show(getSupportFragmentManager(), "CreateGroupDialog");
        });

        // Bottom Navigation Logic
        com.google.android.material.bottomnavigation.BottomNavigationView navView = findViewById(R.id.bottom_navigation);
        navView.setSelectedItemId(R.id.homeFragment); // Opened from Home
        navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.homeFragment) {
                finish();
                return true;
            } else {
                android.content.Intent intent = new android.content.Intent(this, MainActivity.class);
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("target_fragment_id", itemId);
                startActivity(intent);
                finish();
                return true;
            }
        });
    }

    private void refreshGroups() {
        // Re-set adapter to force fragment recreation with fresh data
        viewPager.setAdapter(new GroupPagerAdapter(this));
    }

    @Override
    public void onGroupCreated() {
        refreshGroups();
        // Go to first tab to see the new group
        viewPager.setCurrentItem(0, true);
    }

    private static class GroupPagerAdapter extends FragmentStateAdapter {
        public GroupPagerAdapter(@NonNull AppCompatActivity activity) {
            super(activity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return GroupListFragment.newInstance(GroupListFragment.TYPE_MY_GROUPS);
            } else {
                return new DiscoverGroupsFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
