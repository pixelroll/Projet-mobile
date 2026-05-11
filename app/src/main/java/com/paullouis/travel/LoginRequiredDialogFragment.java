package com.paullouis.travel;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputEditText;
import com.paullouis.travel.data.DataCallback;
import com.paullouis.travel.data.FirebaseRepository;
import com.paullouis.travel.model.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class LoginRequiredDialogFragment extends DialogFragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedAvatarUri = null;
    private boolean isSignUpMode = false;
    private ImageView ivAvatarSignUp;

    public static LoginRequiredDialogFragment newInstance() {
        return new LoginRequiredDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_login_required, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());
        
        TextInputEditText etEmail = view.findViewById(R.id.etEmail);
        TextInputEditText etPassword = view.findViewById(R.id.etPassword);
        TextView tvError = view.findViewById(R.id.tvError);

        TextInputEditText etName = view.findViewById(R.id.etName);
        TextInputLayout tilName = view.findViewById(R.id.tilName);
        View flAvatarContainer = view.findViewById(R.id.flAvatarContainer);
        ivAvatarSignUp = view.findViewById(R.id.ivAvatarSignUp);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        MaterialButton btnLogin = view.findViewById(R.id.btnLogin);
        MaterialButton btnRegister = view.findViewById(R.id.btnRegister);

        flAvatarContainer.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

            if (isSignUpMode) {
                String name = etName.getText() != null ? etName.getText().toString().trim() : "";
                if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                    tvError.setText("Veuillez remplir tous les champs");
                    tvError.setVisibility(View.VISIBLE);
                    return;
                }
                
                String avatarUrl = selectedAvatarUri != null ? selectedAvatarUri.toString() : null;
                btnLogin.setEnabled(false); // disable during request
                
                FirebaseRepository.getInstance().registerWithEmail(email, password, name, avatarUrl, new DataCallback<User>() {
                    @Override
                    public void onSuccess(User result) {
                        if (getActivity() != null) getActivity().recreate();
                        dismiss();
                    }

                    @Override
                    public void onError(Exception e) {
                        btnLogin.setEnabled(true);
                        tvError.setText(e.getMessage());
                        tvError.setVisibility(View.VISIBLE);
                    }
                });
            } else {
                if (email.isEmpty() || password.isEmpty()) {
                    tvError.setText("Veuillez remplir tous les champs");
                    tvError.setVisibility(View.VISIBLE);
                    return;
                }
                btnLogin.setEnabled(false);
                FirebaseRepository.getInstance().loginWithEmail(email, password, new DataCallback<User>() {
                    @Override
                    public void onSuccess(User result) {
                        if (getActivity() != null) getActivity().recreate();
                        dismiss();
                    }

                    @Override
                    public void onError(Exception e) {
                        btnLogin.setEnabled(true);
                        tvError.setText(e.getMessage());
                        tvError.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        btnRegister.setOnClickListener(v -> {
            isSignUpMode = !isSignUpMode;
            if (isSignUpMode) {
                tilName.setVisibility(View.VISIBLE);
                flAvatarContainer.setVisibility(View.VISIBLE);
                tvTitle.setText("Créer un compte");
                btnLogin.setText("S'inscrire");
                btnRegister.setText("J'ai déjà un compte");
            } else {
                tilName.setVisibility(View.GONE);
                flAvatarContainer.setVisibility(View.GONE);
                tvTitle.setText("Connexion");
                btnLogin.setText("Se connecter");
                btnRegister.setText("Créer un compte");
            }
        });

        view.findViewById(R.id.btnGoogle).setOnClickListener(v -> {
            // Google Sign-In requires an activity to handle the intent result.
            // For a school project, you can integrate GoogleSignInClient here or in the parent Activity.
            tvError.setText("Connexion Google non configurée dans ce dialog.");
            tvError.setVisibility(View.VISIBLE);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == android.app.Activity.RESULT_OK && data != null) {
            selectedAvatarUri = data.getData();
            if (selectedAvatarUri != null && ivAvatarSignUp != null) {
                ivAvatarSignUp.setImageURI(selectedAvatarUri);
            }
        }
    }
}
