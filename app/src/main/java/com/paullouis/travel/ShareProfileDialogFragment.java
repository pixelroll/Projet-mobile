package com.paullouis.travel;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.paullouis.travel.data.MockDataProvider;
import com.paullouis.travel.model.User;

public class ShareProfileDialogFragment extends DialogFragment {

    public static ShareProfileDialogFragment newInstance() {
        return new ShareProfileDialogFragment();
    }

    public static ShareProfileDialogFragment newInstanceForItinerary(String itineraryTitle, String city) {
        ShareProfileDialogFragment f = new ShareProfileDialogFragment();
        Bundle args = new Bundle();
        args.putString("mode", "itinerary");
        args.putString("subtitle", "Parcours " + itineraryTitle + " — " + city);
        args.putString("shareText", "Découvrez mon parcours \"" + itineraryTitle + "\" à " + city + " sur Traveling : https://traveling.app/p/equilibre-paris");
        args.putString("shareUrl", "https://traveling.app/p/equilibre-paris");
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return inflater.inflate(R.layout.fragment_share_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String subtitleStr;
        String shareTextStr;
        String profileUrlStr;

        if (getArguments() != null && "itinerary".equals(getArguments().getString("mode"))) {
            subtitleStr = getArguments().getString("subtitle");
            shareTextStr = getArguments().getString("shareText");
            profileUrlStr = getArguments().getString("shareUrl");
        } else {
            User currentUser = MockDataProvider.getCurrentUser();
            String userName = currentUser != null ? currentUser.getName() : "Sophie Martin";
            profileUrlStr = "https://traveling.app/u/" + userName.toLowerCase().replace(" ", ".");
            shareTextStr = "Découvrez le profil de " + userName + " sur Traveling : " + profileUrlStr;
            subtitleStr = "Profil de " + userName;
        }

        TextView tvSubtitle = view.findViewById(R.id.tvSubtitle);
        tvSubtitle.setText(subtitleStr);

        view.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.btnBottomClose).setOnClickListener(v -> dismiss());

        // Setup Option 1: System Share
        View optSystem = view.findViewById(R.id.optionShareSystem);
        setupOption(optSystem, R.drawable.ic_share_2, "Partager via...", "Utiliser le menu de partage du système");
        optSystem.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, shareTextStr);
            startActivity(Intent.createChooser(intent, "Partager le profil"));
        });

        // Setup Option 2: Copy Link
        View optCopy = view.findViewById(R.id.optionCopyLink);
        setupOption(optCopy, R.drawable.ic_copy, "Copier le lien", profileUrlStr);
        optCopy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Lien Traveling", profileUrlStr);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(requireContext(), "Lien copié !", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        // Setup Option 3: Email
        View optEmail = view.findViewById(R.id.optionEmail);
        setupOption(optEmail, R.drawable.ic_mail, "Envoyer par email", "Partager via votre client email");
        optEmail.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Lien Traveling à découvrir");
            intent.putExtra(Intent.EXTRA_TEXT, shareTextStr);
            startActivity(Intent.createChooser(intent, "Envoyer par email"));
        });

        // Setup Option 4: SMS
        View optSms = view.findViewById(R.id.optionSms);
        setupOption(optSms, R.drawable.ic_message_circle, "Envoyer par SMS", "Partager via messages");
        optSms.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:"));
            intent.putExtra("sms_body", shareTextStr);
            startActivity(intent);
        });
    }

    private void setupOption(View optionView, int iconResId, String title, String subtitle) {
        ImageView ivIcon = optionView.findViewById(R.id.ivOptionIcon);
        TextView tvTitle = optionView.findViewById(R.id.tvOptionTitle);
        TextView tvSubtitle = optionView.findViewById(R.id.tvOptionSubtitle);

        ivIcon.setImageResource(iconResId);
        tvTitle.setText(title);
        tvSubtitle.setText(subtitle);
    }
}
