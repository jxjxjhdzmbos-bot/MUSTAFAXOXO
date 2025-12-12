package com.mustafa.xogame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.mustafa.xogame.firebase.FirebaseHelper;
import com.mustafa.xogame.firebase.GameRoomManager;
import com.mustafa.xogame.models.GameRoom;
import com.mustafa.xogame.models.User;

public class OnlineMenuActivity extends AppCompatActivity {
    private TextInputEditText roomNameInput;
    private Button createRoomButton;
    private Button joinRoomButton;
    private TextView waitingText;

    private GameRoomManager roomManager;
    private FirebaseHelper firebaseHelper;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_menu);

        firebaseHelper = FirebaseHelper.getInstance();
        roomManager = new GameRoomManager();

        initializeViews();
        loadUserProfile();
        setupClickListeners();
    }

    private void initializeViews() {
        roomNameInput = findViewById(R.id.roomNameInput);
        createRoomButton = findViewById(R.id.createRoomButton);
        joinRoomButton = findViewById(R.id.joinRoomButton);
        waitingText = findViewById(R.id.waitingText);
    }

    private void loadUserProfile() {
        String userId = firebaseHelper.getCurrentUserId();
        if (userId != null) {
            firebaseHelper.getUserProfile(userId, new FirebaseHelper.UserCallback() {
                @Override
                public void onUser(User user) {
                    currentUsername = user.getUsername();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(OnlineMenuActivity.this, 
                        "Failed to load profile", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }

    private void setupClickListeners() {
        createRoomButton.setOnClickListener(v -> createRoom());
        joinRoomButton.setOnClickListener(v -> joinRoom());
    }

    private void createRoom() {
        String roomName = roomNameInput.getText().toString().trim();
        
        if (roomName.isEmpty()) {
            Toast.makeText(this, "Please enter a room name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate room name: alphanumeric and underscores only, max 20 characters
        if (!roomName.matches("^[a-zA-Z0-9_]{1,20}$")) {
            Toast.makeText(this, "Room name must be alphanumeric (1-20 characters)", 
                Toast.LENGTH_SHORT).show();
            return;
        }

        createRoomButton.setEnabled(false);
        joinRoomButton.setEnabled(false);
        waitingText.setVisibility(View.VISIBLE);

        roomManager.createRoom(roomName, currentUsername, new GameRoomManager.RoomCallback() {
            @Override
            public void onSuccess(GameRoom room) {
                // Wait for opponent or start game
                startGameAsPlayerX(roomName);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(OnlineMenuActivity.this, error, Toast.LENGTH_SHORT).show();
                createRoomButton.setEnabled(true);
                joinRoomButton.setEnabled(true);
                waitingText.setVisibility(View.GONE);
            }
        });
    }

    private void joinRoom() {
        String roomName = roomNameInput.getText().toString().trim();
        
        if (roomName.isEmpty()) {
            Toast.makeText(this, "Please enter a room name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate room name: alphanumeric and underscores only, max 20 characters
        if (!roomName.matches("^[a-zA-Z0-9_]{1,20}$")) {
            Toast.makeText(this, "Room name must be alphanumeric (1-20 characters)", 
                Toast.LENGTH_SHORT).show();
            return;
        }

        joinRoomButton.setEnabled(false);
        createRoomButton.setEnabled(false);

        roomManager.joinRoom(roomName, currentUsername, new GameRoomManager.RoomCallback() {
            @Override
            public void onSuccess(GameRoom room) {
                startGameAsPlayerO(roomName);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(OnlineMenuActivity.this, error, Toast.LENGTH_SHORT).show();
                joinRoomButton.setEnabled(true);
                createRoomButton.setEnabled(true);
            }
        });
    }

    private void startGameAsPlayerX(String roomName) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("mode", "ONLINE");
        intent.putExtra("roomName", roomName);
        intent.putExtra("playerSymbol", "X");
        startActivity(intent);
        finish();
    }

    private void startGameAsPlayerO(String roomName) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("mode", "ONLINE");
        intent.putExtra("roomName", roomName);
        intent.putExtra("playerSymbol", "O");
        startActivity(intent);
        finish();
    }
}
