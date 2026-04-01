package com.paullouis.travel;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.paullouis.travel.data.MockDataProvider;
import com.paullouis.travel.model.NotificationSettingItem;
import java.util.ArrayList;
import java.util.List;

public class NotificationSettingsActivity extends AppCompatActivity {

    private LinearLayout containerPerson, containerGroup, containerPlace, containerTag;
    private List<NotificationSettingItem> settings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        // Safe Area
        View headerLayout = findViewById(R.id.headerLayout);
        ViewCompat.setOnApplyWindowInsetsListener(headerLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        // Init views
        containerPerson = findViewById(R.id.containerPerson);
        containerGroup = findViewById(R.id.containerGroup);
        containerPlace = findViewById(R.id.containerPlace);
        containerTag = findViewById(R.id.containerTag);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Load data
        settings = new ArrayList<>(MockDataProvider.getNotificationSettings());
        refreshUi();

        // Setup Section Headers
        setupSection(R.id.sectionPerson, R.drawable.ic_person, "Publications d'une personne");
        setupSection(R.id.sectionGroup, R.drawable.ic_group, "Publications d'un groupe");
        setupSection(R.id.sectionPlace, R.drawable.ic_location_on, "Photos d'un lieu");
        setupSection(R.id.sectionTag, R.drawable.ic_tag, "Photos d'un thème/tag");
    }

    private void setupSection(int sectionId, int iconRes, String title) {
        View section = findViewById(sectionId);
        ((ImageView) section.findViewById(R.id.ivSectionIcon)).setImageResource(iconRes);
        ((TextView) section.findViewById(R.id.tvSectionTitle)).setText(title);
        section.findViewById(R.id.btnAdd).setOnClickListener(v -> {
            Toast.makeText(this, "Ajouter " + title + " (à implémenter)", Toast.LENGTH_SHORT).show();
        });
    }

    private void refreshUi() {
        containerPerson.removeAllViews();
        containerGroup.removeAllViews();
        containerPlace.removeAllViews();
        containerTag.removeAllViews();

        for (NotificationSettingItem item : settings) {
            LinearLayout container = null;
            int iconRes = R.drawable.ic_person;
            
            switch (item.getType()) {
                case PERSON: container = containerPerson; iconRes = R.drawable.ic_person; break;
                case GROUP: container = containerGroup; iconRes = R.drawable.ic_group; break;
                case PLACE: container = containerPlace; iconRes = R.drawable.ic_location_on; break;
                case TAG: container = containerTag; iconRes = R.drawable.ic_tag; break;
            }

            if (container != null) {
                container.addView(createItemView(item, iconRes));
            }
        }
    }

    private View createItemView(NotificationSettingItem item, int iconRes) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_notification_setting, null);
        
        TextView tvName = view.findViewById(R.id.tvItemName);
        ImageView ivIcon = view.findViewById(R.id.ivItemIcon);
        SwitchCompat sw = view.findViewById(R.id.switchItem);
        ImageView btnDelete = view.findViewById(R.id.btnDelete);

        tvName.setText(item.getName());
        ivIcon.setImageResource(iconRes);
        sw.setChecked(item.isEnabled());

        // Special styling for Tag (Chip look)
        if (item.getType() == NotificationSettingItem.Type.TAG) {
            tvName.setBackgroundResource(R.drawable.bg_gray_rounded);
            tvName.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.primary)));
            tvName.setTextColor(getColor(android.R.color.white));
            tvName.setPadding(24, 8, 24, 8);
            // Hide the icon for tags as per Figma
            view.findViewById(R.id.vItemIconBg).setVisibility(View.GONE);
            view.findViewById(R.id.ivItemIcon).setVisibility(View.GONE);
        }

        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setEnabled(isChecked);
        });

        btnDelete.setOnClickListener(v -> {
            settings.remove(item);
            refreshUi();
        });

        return view;
    }
}
