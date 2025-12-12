package com.mustafa.xogame.firebase;

import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mustafa.xogame.models.GameRoom;
import java.util.HashMap;
import java.util.Map;

public class GameRoomManager {
    private DatabaseReference roomsRef;
    private FirebaseHelper firebaseHelper;

    public GameRoomManager() {
        firebaseHelper = FirebaseHelper.getInstance();
        roomsRef = firebaseHelper.getDatabase().child("rooms");
    }

    public interface RoomCallback {
        void onSuccess(GameRoom room);
        void onFailure(String error);
    }

    public interface RoomExistsCallback {
        void onResult(boolean exists, GameRoom room);
    }

    public void createRoom(String roomName, String playerName, RoomCallback callback) {
        String userId = firebaseHelper.getCurrentUserId();
        if (userId == null) {
            callback.onFailure("User not authenticated");
            return;
        }

        // Check if room already exists
        checkRoomExists(roomName, (exists, existingRoom) -> {
            if (exists) {
                callback.onFailure("Room already exists");
            } else {
                String roomId = roomsRef.push().getKey();
                GameRoom room = new GameRoom(roomId, roomName, userId, playerName);
                
                roomsRef.child(roomName).setValue(room)
                    .addOnSuccessListener(aVoid -> callback.onSuccess(room))
                    .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
            }
        });
    }

    public void joinRoom(String roomName, String playerName, RoomCallback callback) {
        String userId = firebaseHelper.getCurrentUserId();
        if (userId == null) {
            callback.onFailure("User not authenticated");
            return;
        }

        roomsRef.child(roomName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callback.onFailure("Room not found");
                    return;
                }

                GameRoom room = snapshot.getValue(GameRoom.class);
                if (room == null) {
                    callback.onFailure("Invalid room data");
                    return;
                }

                if (room.getPlayerOId() != null) {
                    callback.onFailure("Room is full");
                    return;
                }

                // Join as player O
                Map<String, Object> updates = new HashMap<>();
                updates.put("playerOId", userId);
                updates.put("playerOName", playerName);
                updates.put("status", "playing");

                roomsRef.child(roomName).updateChildren(updates)
                    .addOnSuccessListener(aVoid -> {
                        room.setPlayerOId(userId);
                        room.setPlayerOName(playerName);
                        room.setStatus("playing");
                        callback.onSuccess(room);
                    })
                    .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    public void checkRoomExists(String roomName, RoomExistsCallback callback) {
        roomsRef.child(roomName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    GameRoom room = snapshot.getValue(GameRoom.class);
                    callback.onResult(true, room);
                } else {
                    callback.onResult(false, null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onResult(false, null);
            }
        });
    }

    public void updateGameState(String roomName, String[][] board, String currentPlayer, 
                                boolean gameOver, String winner, UpdateCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("board", board);
        updates.put("currentPlayer", currentPlayer);
        updates.put("gameOver", gameOver);
        if (winner != null) {
            updates.put("winner", winner);
        }

        roomsRef.child(roomName).updateChildren(updates)
            .addOnSuccessListener(aVoid -> callback.onSuccess())
            .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void listenToRoom(String roomName, ValueEventListener listener) {
        roomsRef.child(roomName).addValueEventListener(listener);
    }

    public void removeRoomListener(String roomName, ValueEventListener listener) {
        roomsRef.child(roomName).removeEventListener(listener);
    }

    public void deleteRoom(String roomName) {
        roomsRef.child(roomName).removeValue();
    }

    public interface UpdateCallback {
        void onSuccess();
        void onFailure(String error);
    }
}
