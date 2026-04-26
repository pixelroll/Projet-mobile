package com.paullouis.travel;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.paullouis.travel.data.MockDataProvider;
import com.paullouis.travel.model.User;

public class SwitchAccountDialogFragment extends DialogFragment {

    public static SwitchAccountDialogFragment newInstance() {
        return new SwitchAccountDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_switch_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        User user = MockDataProvider.getCurrentUser();
        ((TextView) view.findViewById(R.id.tvAccountName)).setText(user.getName());
        ((TextView) view.findViewById(R.id.tvEmail)).setText(user.getEmail());

        view.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());
        
        view.findViewById(R.id.btnLogout).setOnClickListener(v -> {
            // Logique de déconnexion : retour au mode anonyme
            MockDataProvider.setUserLoggedIn(false);
            
            // Redémarrer l'activité principale pour rafraîchir l'UI
            android.content.Intent intent = new android.content.Intent(getActivity(), MainActivity.class);
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            
            dismiss();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout((int) (getResources().getDisplayMetrics().widthPixels * 0.90), ViewGroup.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawableResource(android.R.color.transparent);
            }
        }
    }
}
