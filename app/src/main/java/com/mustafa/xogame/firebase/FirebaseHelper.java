package com.mustafa.xogame.firebase;

import android.net.Uri;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mustafa.xogame.models.User;
import java.util.HashMap;
import java.util.Map;

public class FirebaseHelper {
    private static FirebaseHelper instance;
    private FirebaseAuth auth;
    private DatabaseReference database;
    private StorageReference storage;

    private FirebaseHelper() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance().getReference();
    }

    public static synchronized FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public DatabaseReference getDatabase() {
        return database;
    }

    public StorageReference getStorage() {
        return storage;
    }

    public String getCurrentUserId() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    public interface UsernameCheckCallback {
        void onResult(boolean isAvailable);
    }

    public void checkUsernameAvailability(String username, UsernameCheckCallback callback) {
        database.child("usernames").child(username).addListenerForSingleValueEvent(
            new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    callback.onResult(!snapshot.exists());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    callback.onResult(false);
                }
            }
        );
    }

    public interface UploadCallback {
        void onSuccess(String downloadUrl);
        void onFailure(String error);
    }

    public void uploadProfileImage(Uri imageUri, String userId, UploadCallback callback) {
        StorageReference imageRef = storage.child("profile_images/" + userId + ".jpg");
        
        imageRef.putFile(imageUri)
            .addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    callback.onSuccess(uri.toString());
                }).addOnFailureListener(e -> {
                    callback.onFailure(e.getMessage());
                });
            })
            .addOnFailureListener(e -> {
                callback.onFailure(e.getMessage());
            });
    }

    public void saveUserProfile(User user, String username, SaveCallback callback) {
        String userId = user.getUserId();
        
        // Save user data
        database.child("users").child(userId).setValue(user)
            .addOnSuccessListener(aVoid -> {
                // Save username mapping
                database.child("usernames").child(username).setValue(userId)
                    .addOnSuccessListener(aVoid2 -> callback.onSuccess())
                    .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
            })
            .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public interface SaveCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public interface UserCallback {
        void onUser(User user);
        void onError(String error);
    }

    public void getUserProfile(String userId, UserCallback callback) {
        database.child("users").child(userId).addListenerForSingleValueEvent(
            new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        callback.onUser(user);
                    } else {
                        callback.onError("User not found");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    callback.onError(error.getMessage());
                }
            }
        );
    }
}
