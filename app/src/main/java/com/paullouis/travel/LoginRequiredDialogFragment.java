package com.paullouis.travel;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.paullouis.travel.data.DataCallback;
import com.paullouis.travel.data.FirebaseRepository;
import com.paullouis.travel.model.User;

public class LoginRequiredDialogFragment extends DialogFragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedAvatarUri = null;
    private boolean isSignUpMode = false;
    private ImageView ivAvatarSignUp;
    private TextView tvError;

    // Google Sign-In
    private GoogleSignInClient googleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    public static LoginRequiredDialogFragment newInstance() {
        return new LoginRequiredDialogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configurer le GoogleSignInClient
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        // Enregistrer le launcher pour le résultat Google Sign-In
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleGoogleSignInResult
        );
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
        tvError = view.findViewById(R.id.tvError);

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
                    showError("Veuillez remplir tous les champs");
                    return;
                }

                String avatarUrl = selectedAvatarUri != null ? selectedAvatarUri.toString() : null;
                btnLogin.setEnabled(false);

                FirebaseRepository.getInstance().registerWithEmail(email, password, name, avatarUrl, new DataCallback<User>() {
                    @Override
                    public void onSuccess(User result) {
                        if (getActivity() != null) getActivity().recreate();
                        dismiss();
                    }

                    @Override
                    public void onError(Exception e) {
                        btnLogin.setEnabled(true);
                        showError(e.getMessage());
                    }
                });
            } else {
                if (email.isEmpty() || password.isEmpty()) {
                    showError("Veuillez remplir tous les champs");
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
                        showError(e.getMessage());
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

        // Bouton Google Sign-In — lance le vrai flow
        view.findViewById(R.id.btnGoogle).setOnClickListener(v -> {
            // Forcer une nouvelle sélection de compte
            googleSignInClient.signOut().addOnCompleteListener(task -> {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                googleSignInLauncher.launch(signInIntent);
            });
        });
    }

    private static final String TAG = "GoogleSignIn";

    /**
     * Traite le résultat du Google Sign-In et authentifie auprès de Firebase.
     */
    private void handleGoogleSignInResult(ActivityResult result) {
        Log.d(TAG, "handleGoogleSignInResult: resultCode=" + result.getResultCode());

        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String idToken = account.getIdToken();
                Log.d(TAG, "Google account: " + account.getEmail() + ", idToken null=" + (idToken == null));

                if (idToken == null) {
                    String msg = "Token Google null — fournisseur Google non activé dans Firebase Auth.";
                    Log.e(TAG, msg);
                    showError(msg);
                    return;
                }

                showError("Connexion en cours…");
                Log.d(TAG, "Calling Firebase signInWithCredential…");

                FirebaseRepository.getInstance().loginWithGoogle(idToken, new DataCallback<User>() {
                    @Override
                    public void onSuccess(User user) {
                        Log.d(TAG, "Firebase auth success: " + user.getId());
                        if (getActivity() != null) getActivity().recreate();
                        dismiss();
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "Firebase auth error: ", e);
                        showError("Erreur Firebase : " + e.getMessage());
                    }
                });

            } catch (ApiException e) {
                // Codes courants :
                // 10 = DEVELOPER_ERROR → SHA-1 non enregistré OU fournisseur Google désactivé
                // 12501 = annulé par l'utilisateur
                // 7 = pas de réseau
                String hint = "";
                if (e.getStatusCode() == 10) {
                    hint = " → Ajoute le SHA-1 dans Firebase et active le fournisseur Google";
                } else if (e.getStatusCode() == 7) {
                    hint = " → Vérifie ta connexion réseau";
                } else if (e.getStatusCode() == 12501) {
                    return; // annulé par l'utilisateur, pas d'erreur à afficher
                }
                Log.e(TAG, "ApiException code=" + e.getStatusCode() + hint, e);
                showError("Erreur Google code " + e.getStatusCode() + hint);
            }

        } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
            Log.d(TAG, "Google Sign-In annulé par l'utilisateur");
        } else {
            String msg = "Résultat inattendu : code " + result.getResultCode();
            Log.e(TAG, msg);
            showError(msg);
        }
    }

    private void showError(String message) {
        if (tvError != null) {
            tvError.setText(message);
            tvError.setVisibility(View.VISIBLE);
        }
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
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedAvatarUri = data.getData();
            if (selectedAvatarUri != null && ivAvatarSignUp != null) {
                ivAvatarSignUp.setImageURI(selectedAvatarUri);
            }
        }
    }
}
