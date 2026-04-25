package com.paullouis.travel;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class PdfReadyDialogFragment extends DialogFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_CITY = "city";

    public static PdfReadyDialogFragment newInstance(String title, String city) {
        PdfReadyDialogFragment fragment = new PdfReadyDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_CITY, city);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pdf_ready, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                (int) (getResources().getDisplayMetrics().widthPixels * 0.90),
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String title = getArguments() != null ? getArguments().getString(ARG_TITLE) : "Équilibré";
        String city = getArguments() != null ? getArguments().getString(ARG_CITY) : "Paris";

        TextView tvSubtitle = view.findViewById(R.id.tvSubtitle);
        tvSubtitle.setText("Votre itinéraire \"Parcours " + title + " - " + city + "\" est prêt au téléchargement.");

        view.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());

        view.findViewById(R.id.btnDownloadPdf).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Téléchargement démarré", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        view.findViewById(R.id.btnSharePdf).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/pdf");
            // Simulate sharing a PDF by passing a mock text stream
            intent.putExtra(Intent.EXTRA_TEXT, "Voici mon itinéraire " + city + " au format PDF !");
            startActivity(Intent.createChooser(intent, "Partager le PDF"));
            dismiss();
        });
    }
}
