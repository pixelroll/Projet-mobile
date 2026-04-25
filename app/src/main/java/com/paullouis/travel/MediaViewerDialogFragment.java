package com.paullouis.travel;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

public class MediaViewerDialogFragment extends DialogFragment {

    public static MediaViewerDialogFragment newInstance(int drawableRes, String label, boolean isVideo) {
        MediaViewerDialogFragment f = new MediaViewerDialogFragment();
        Bundle args = new Bundle();
        args.putInt("drawableRes", drawableRes);
        args.putString("label", label);
        args.putBoolean("isVideo", isVideo);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_media_viewer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int drawableRes = getArguments() != null ? getArguments().getInt("drawableRes") : 0;
        String label = getArguments() != null ? getArguments().getString("label") : "";
        boolean isVideo = getArguments() != null ? getArguments().getBoolean("isVideo") : false;

        ImageView ivMedia = view.findViewById(R.id.ivMedia);
        ImageView ivVideoPlay = view.findViewById(R.id.ivVideoPlay);
        TextView tvLabel = view.findViewById(R.id.tvLabel);

        ivMedia.setImageResource(drawableRes);
        tvLabel.setText(label);

        if (isVideo) {
            ivVideoPlay.setVisibility(View.VISIBLE);
            ivVideoPlay.setOnClickListener(v -> {
                // TODO: Implémenter le lecteur vidéo
            });
        }

        view.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());
    }
}
