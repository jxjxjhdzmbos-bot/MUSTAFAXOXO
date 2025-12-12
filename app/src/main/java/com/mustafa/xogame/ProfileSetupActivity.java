package com.mustafa.xogame;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.mustafa.xogame.firebase.FirebaseHelper;
import com.mustafa.xogame.models.User;

public class ProfileSetupActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView profileImageView;
    private Button chooseImageButton;
    private TextInputEditText usernameInput;
    private Button saveProfileButton;

    private Uri selectedImageUri;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        firebaseHelper = FirebaseHelper.getInstance();
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        profileImageView = findViewById(R.id.profileImageView);
        chooseImageButton = findViewById(R.id.chooseImageButton);
        usernameInput = findViewById(R.id.usernameInput);
        saveProfileButton = findViewById(R.id.saveProfileButton);
    }

    private void setupClickListeners() {
        chooseImageButton.setOnClickListener(v -> chooseImage());
        saveProfileButton.setOnClickListener(v -> saveProfile());
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), 
            PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK 
            && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            profileImageView.setImageURI(selectedImageUri);
        }
    }

    private void saveProfile() {
        String username = usernameInput.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select a profile image", Toast.LENGTH_SHORT).show();
            return;
        }

        saveProfileButton.setEnabled(false);

        // Check if username is available
        firebaseHelper.checkUsernameAvailability(username, isAvailable -> {
            if (isAvailable) {
                uploadImageAndSaveProfile(username);
            } else {
                Toast.makeText(this, getString(R.string.username_taken), 
                    Toast.LENGTH_SHORT).show();
                saveProfileButton.setEnabled(true);
            }
        });
    }

    private void uploadImageAndSaveProfile(String username) {
        FirebaseUser currentUser = firebaseHelper.getAuth().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String userId = currentUser.getUid();
        String email = currentUser.getEmail();

        firebaseHelper.uploadProfileImage(selectedImageUri, userId, 
            new FirebaseHelper.UploadCallback() {
                @Override
                public void onSuccess(String downloadUrl) {
                    User user = new User(userId, username, email, downloadUrl);
                    
                    firebaseHelper.saveUserProfile(user, username, 
                        new FirebaseHelper.SaveCallback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(ProfileSetupActivity.this, 
                                    "Profile saved successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void onFailure(String error) {
                                Toast.makeText(ProfileSetupActivity.this, 
                                    "Failed to save profile: " + error, 
                                    Toast.LENGTH_SHORT).show();
                                saveProfileButton.setEnabled(true);
                            }
                        });
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(ProfileSetupActivity.this, 
                        "Failed to upload image: " + error, Toast.LENGTH_SHORT).show();
                    saveProfileButton.setEnabled(true);
                }
            });
    }
}
