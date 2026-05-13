package com.paullouis.travel;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.paullouis.travel.data.DataCallback;
import com.paullouis.travel.data.FirebaseRepository;
import com.paullouis.travel.model.NotificationSettingItem;
import java.util.ArrayList;
import java.util.List;

public class NotificationSettingsActivity extends AppCompatActivity {

    private static final String[] PLACE_TYPES = {
        "Nature", "Musée", "Rue", "Magasin", "Restaurant", "Monument"
    };

    private LinearLayout containerPlace, containerTag, containerType;
    private ProgressBar progressBar;
    private List<NotificationSettingItem> subscriptions = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        View headerLayout = findViewById(R.id.headerLayout);
        ViewCompat.setOnApplyWindowInsetsListener(headerLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        containerPlace = findViewById(R.id.containerPlace);
        containerTag = findViewById(R.id.containerTag);
        containerType = findViewById(R.id.containerType);
        progressBar = findViewById(R.id.progressBar);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        setupSectionHeader(R.id.sectionPlace, R.drawable.ic_location_on, "Photos d'un lieu",
                this::showAddPlaceDialog);
        setupSectionHeader(R.id.sectionTag, R.drawable.ic_tag, "Photos d'un tag",
                this::showAddTagDialog);
        setupSectionHeader(R.id.sectionType, R.drawable.ic_layers, "Photos d'un type de lieu",
                this::showAddTypeDialog);

        loadSubscriptions();
    }

    private void setupSectionHeader(int sectionId, int iconRes, String title, Runnable onAdd) {
        View section = findViewById(sectionId);
        ((ImageView) section.findViewById(R.id.ivSectionIcon)).setImageResource(iconRes);
        ((TextView) section.findViewById(R.id.tvSectionTitle)).setText(title);

        TextView btnAdd = section.findViewById(R.id.btnAddSection);
        btnAdd.setVisibility(View.VISIBLE);
        btnAdd.setOnClickListener(v -> onAdd.run());
    }

    private void loadSubscriptions() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseRepository.getInstance().getNotificationSettings(new DataCallback<List<NotificationSettingItem>>() {
            @Override
            public void onSuccess(List<NotificationSettingItem> items) {
                subscriptions = new ArrayList<>(items);
                progressBar.setVisibility(View.GONE);
                refreshUi();
            }

            @Override
            public void onError(Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(NotificationSettingsActivity.this,
                        "Erreur de chargement", Toast.LENGTH_SHORT).show();
                refreshUi();
            }
        });
    }

    private void refreshUi() {
        containerPlace.removeAllViews();
        containerTag.removeAllViews();
        containerType.removeAllViews();

        boolean hasPlace = false, hasTag = false, hasType = false;

        for (NotificationSettingItem item : subscriptions) {
            if (item.getType() == null) continue;
            switch (item.getType()) {
                case PLACE:
                    containerPlace.addView(createItemView(item, R.drawable.ic_location_on));
                    hasPlace = true;
                    break;
                case TAG:
                    containerTag.addView(createItemView(item, R.drawable.ic_tag));
                    hasTag = true;
                    break;
                case TYPE:
                    containerType.addView(createItemView(item, R.drawable.ic_layers));
                    hasType = true;
                    break;
                default:
                    break;
            }
        }

        if (!hasPlace) addEmptyState(containerPlace, "Aucun lieu suivi — appuyez sur + Ajouter");
        if (!hasTag) addEmptyState(containerTag, "Aucun tag suivi — appuyez sur + Ajouter");
        if (!hasType) addEmptyState(containerType, "Aucun type suivi — appuyez sur + Ajouter");
    }

    private void addEmptyState(LinearLayout container, String message) {
        TextView tv = new TextView(this);
        tv.setText(message);
        tv.setTextColor(getColor(R.color.muted_foreground));
        tv.setTextSize(13f);
        tv.setPadding(4, 8, 4, 8);
        container.addView(tv);
    }

    private View createItemView(NotificationSettingItem item, int iconRes) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_notification_setting, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 8);
        view.setLayoutParams(params);

        TextView tvName = view.findViewById(R.id.tvItemName);
        ImageView ivIcon = view.findViewById(R.id.ivItemIcon);
        SwitchCompat sw = view.findViewById(R.id.switchItem);
        ImageView btnDelete = view.findViewById(R.id.btnDelete);

        tvName.setText(item.getName());
        ivIcon.setImageResource(iconRes);
        sw.setChecked(item.isEnabled());

        if (item.getType() == NotificationSettingItem.Type.TAG) {
            tvName.setBackgroundResource(R.drawable.bg_gray_rounded);
            tvName.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.primary)));
            tvName.setTextColor(getColor(android.R.color.white));
            tvName.setPadding(24, 8, 24, 8);
            view.findViewById(R.id.vItemIconBg).setVisibility(View.GONE);
            view.findViewById(R.id.ivItemIcon).setVisibility(View.GONE);
        }

        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setEnabled(isChecked);
            if (item.getSubscriptionId() != null) {
                FirebaseRepository.getInstance().updateNotificationSubscription(
                        item.getSubscriptionId(), isChecked, new DataCallback<Void>() {
                            @Override public void onSuccess(Void r) {}
                            @Override public void onError(Exception e) {}
                        });
            }
        });

        btnDelete.setOnClickListener(v -> deleteItem(item));

        return view;
    }

    private void deleteItem(NotificationSettingItem item) {
        subscriptions.remove(item);
        refreshUi();
        if (item.getSubscriptionId() != null) {
            FirebaseRepository.getInstance().deleteNotificationSubscription(
                    item.getSubscriptionId(), new DataCallback<Void>() {
                        @Override public void onSuccess(Void r) {}
                        @Override public void onError(Exception e) {
                            subscriptions.add(item);
                            refreshUi();
                            Toast.makeText(NotificationSettingsActivity.this,
                                    "Erreur de suppression", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void showAddTagDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_subscription, null);
        EditText etValue = dialogView.findViewById(R.id.etSubscriptionValue);
        ((TextView) dialogView.findViewById(R.id.tvSubscriptionHint))
                .setText("Vous serez notifié pour chaque photo publiée avec ce tag.");
        etValue.setHint("Ex: plage, montagne, architecture...");

        new AlertDialog.Builder(this)
                .setTitle("Suivre un tag")
                .setView(dialogView)
                .setPositiveButton("Ajouter", (dialog, which) -> {
                    String value = etValue.getText().toString().trim().toLowerCase();
                    if (!value.isEmpty()) saveSubscription(NotificationSettingItem.Type.TAG, value);
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void showAddPlaceDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_subscription, null);
        EditText etValue = dialogView.findViewById(R.id.etSubscriptionValue);
        ((TextView) dialogView.findViewById(R.id.tvSubscriptionHint))
                .setText("Vous serez notifié pour toute photo dont le lieu contient ce mot-clé (ex: «Montpellier» notifie aussi «Pl. Comédie Montpellier»).");
        etValue.setHint("Ex: Montpellier, Paris, Alpes...");

        new AlertDialog.Builder(this)
                .setTitle("Suivre un lieu")
                .setView(dialogView)
                .setPositiveButton("Ajouter", (dialog, which) -> {
                    String value = etValue.getText().toString().trim();
                    if (!value.isEmpty()) saveSubscription(NotificationSettingItem.Type.PLACE, value);
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void showAddTypeDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Suivre un type de lieu")
                .setItems(PLACE_TYPES, (dialog, which) ->
                        saveSubscription(NotificationSettingItem.Type.TYPE, PLACE_TYPES[which]))
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void saveSubscription(NotificationSettingItem.Type type, String value) {
        for (NotificationSettingItem existing : subscriptions) {
            if (existing.getType() == type && value.equalsIgnoreCase(existing.getName())) {
                Toast.makeText(this, "Déjà dans votre liste", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        NotificationSettingItem item = new NotificationSettingItem(null, type, value, true);
        subscriptions.add(item);
        refreshUi();

        FirebaseRepository.getInstance().saveNotificationSubscription(item, new DataCallback<String>() {
            @Override
            public void onSuccess(String subscriptionId) {
                item.setSubscriptionId(subscriptionId);
            }

            @Override
            public void onError(Exception e) {
                subscriptions.remove(item);
                refreshUi();
                Toast.makeText(NotificationSettingsActivity.this,
                        "Erreur d'enregistrement", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
