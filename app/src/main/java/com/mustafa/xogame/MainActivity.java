package com.mustafa.xogame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.mustafa.xogame.firebase.FirebaseHelper;
import com.mustafa.xogame.models.User;

public class MainActivity extends AppCompatActivity {

    private Button offlineModeButton;
    private Button onlineModeButton;
    private Button aboutButton;
    private Button signInButton;
    private Button signOutButton;
    private TextView userNameText;

    private GoogleSignInClient googleSignInClient;
    private FirebaseHelper firebaseHelper;
    private ActivityResultLauncher<Intent> signInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseHelper = FirebaseHelper.getInstance();
        setupGoogleSignIn();
        setupSignInLauncher();
        initializeViews();
        setupClickListeners();
        updateUI();
    }

    private void setupSignInLauncher() {
        signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getData() != null) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        if (account != null) {
                            firebaseAuthWithGoogle(account);
                        }
                    } catch (ApiException e) {
                        Toast.makeText(this, "Sign in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        );
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void initializeViews() {
        offlineModeButton = findViewById(R.id.offlineModeButton);
        onlineModeButton = findViewById(R.id.onlineModeButton);
        aboutButton = findViewById(R.id.aboutButton);
        signInButton = findViewById(R.id.signInButton);
        signOutButton = findViewById(R.id.signOutButton);
        userNameText = findViewById(R.id.userNameText);
    }

    private void setupClickListeners() {
        offlineModeButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ModeSelectionActivity.class);
            startActivity(intent);
        });

        onlineModeButton.setOnClickListener(v -> {
            FirebaseUser user = firebaseHelper.getAuth().getCurrentUser();
            if (user != null) {
                checkUserProfileAndProceed();
            } else {
                Toast.makeText(this, "Please sign in to play online", Toast.LENGTH_SHORT).show();
            }
        });

        aboutButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        });

        signInButton.setOnClickListener(v -> signIn());

        signOutButton.setOnClickListener(v -> signOut());
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        signInLauncher.launch(signInIntent);
    }

    private void signOut() {
        firebaseHelper.getAuth().signOut();
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            updateUI();
            Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show();
        });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseHelper.getAuth().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseHelper.getAuth().getCurrentUser();
                        if (user != null) {
                            checkUserProfileAndProceed();
                        }
                    } else {
                        Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserProfileAndProceed() {
        String userId = firebaseHelper.getCurrentUserId();
        if (userId != null) {
            firebaseHelper.getUserProfile(userId, new FirebaseHelper.UserCallback() {
                @Override
                public void onUser(User user) {
                    updateUI();
                    Intent intent = new Intent(MainActivity.this, OnlineMenuActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onError(String error) {
                    // User profile not set up yet
                    Intent intent = new Intent(MainActivity.this, ProfileSetupActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void updateUI() {
        FirebaseUser user = firebaseHelper.getAuth().getCurrentUser();
        if (user != null) {
            signInButton.setVisibility(View.GONE);
            signOutButton.setVisibility(View.VISIBLE);
            
            String userId = user.getUid();
            firebaseHelper.getUserProfile(userId, new FirebaseHelper.UserCallback() {
                @Override
                public void onUser(User userData) {
                    userNameText.setText("Welcome, " + userData.getUsername());
                    userNameText.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError(String error) {
                    userNameText.setVisibility(View.GONE);
                }
            });
        } else {
            signInButton.setVisibility(View.VISIBLE);
            signOutButton.setVisibility(View.GONE);
            userNameText.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI();
    }
}
