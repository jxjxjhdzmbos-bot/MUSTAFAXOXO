package com.mustafa.xogame;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mustafa.xogame.ai.AIPlayer;
import com.mustafa.xogame.firebase.FirebaseHelper;
import com.mustafa.xogame.firebase.GameRoomManager;
import com.mustafa.xogame.models.GameBoard;
import com.mustafa.xogame.models.GameRoom;

public class GameActivity extends AppCompatActivity {
    private TextView statusText;
    private TextView[][] cells;
    private Button playAgainButton;
    private Button backToMenuButton;

    private GameBoard gameBoard;
    private String gameMode; // PVP, PVAI, ONLINE
    private AIPlayer aiPlayer;
    private String difficulty;

    // Online mode variables
    private GameRoomManager roomManager;
    private String roomName;
    private String myPlayerId;
    private String myPlayerSymbol;
    private ValueEventListener roomListener;
    private boolean isMyTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initializeViews();
        setupGame();
        setupClickListeners();
    }

    private void initializeViews() {
        statusText = findViewById(R.id.statusText);
        playAgainButton = findViewById(R.id.playAgainButton);
        backToMenuButton = findViewById(R.id.backToMenuButton);

        cells = new TextView[3][3];
        cells[0][0] = findViewById(R.id.cell00);
        cells[0][1] = findViewById(R.id.cell01);
        cells[0][2] = findViewById(R.id.cell02);
        cells[1][0] = findViewById(R.id.cell10);
        cells[1][1] = findViewById(R.id.cell11);
        cells[1][2] = findViewById(R.id.cell12);
        cells[2][0] = findViewById(R.id.cell20);
        cells[2][1] = findViewById(R.id.cell21);
        cells[2][2] = findViewById(R.id.cell22);
    }

    private void setupGame() {
        gameBoard = new GameBoard();
        gameMode = getIntent().getStringExtra("mode");

        if ("PVAI".equals(gameMode)) {
            difficulty = getIntent().getStringExtra("difficulty");
            AIPlayer.Difficulty diff = AIPlayer.Difficulty.EASY;
            if ("MEDIUM".equals(difficulty)) {
                diff = AIPlayer.Difficulty.MEDIUM;
            } else if ("HARD".equals(difficulty)) {
                diff = AIPlayer.Difficulty.HARD;
            }
            aiPlayer = new AIPlayer(diff);
            statusText.setText("Your turn (X)");
        } else if ("ONLINE".equals(gameMode)) {
            setupOnlineGame();
        } else {
            statusText.setText("Player X's turn");
        }
    }

    private void setupOnlineGame() {
        roomManager = new GameRoomManager();
        roomName = getIntent().getStringExtra("roomName");
        myPlayerId = FirebaseHelper.getInstance().getCurrentUserId();
        myPlayerSymbol = getIntent().getStringExtra("playerSymbol");
        
        isMyTurn = "X".equals(myPlayerSymbol);
        updateStatusForOnlineMode();

        // Listen to room updates
        roomListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GameRoom room = snapshot.getValue(GameRoom.class);
                if (room != null) {
                    updateBoardFromOnlineGame(room);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GameActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
            }
        };

        roomManager.listenToRoom(roomName, roomListener);
    }

    private void setupClickListeners() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int row = i;
                int col = j;
                cells[i][j].setOnClickListener(v -> onCellClicked(row, col));
            }
        }

        playAgainButton.setOnClickListener(v -> resetGame());
        backToMenuButton.setOnClickListener(v -> finish());
    }

    private void onCellClicked(int row, int col) {
        if ("ONLINE".equals(gameMode)) {
            if (!isMyTurn) {
                Toast.makeText(this, "Wait for your turn", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (gameBoard.makeMove(row, col)) {
            updateCell(row, col);
            
            if ("ONLINE".equals(gameMode)) {
                updateOnlineGameState();
            } else {
                checkGameStatus();
                
                if ("PVAI".equals(gameMode) && !gameBoard.isGameOver()) {
                    // AI makes a move after a short delay
                    new Handler().postDelayed(this::makeAIMove, 500);
                }
            }
        }
    }

    private void makeAIMove() {
        int[] move = aiPlayer.getMove(gameBoard);
        if (move != null) {
            gameBoard.makeMove(move[0], move[1]);
            updateCell(move[0], move[1]);
            checkGameStatus();
        }
    }

    private void updateCell(int row, int col) {
        String value = gameBoard.getCell(row, col);
        cells[row][col].setText(value);
        
        if ("X".equals(value)) {
            cells[row][col].setTextColor(getResources().getColor(R.color.player_x, null));
        } else if ("O".equals(value)) {
            cells[row][col].setTextColor(getResources().getColor(R.color.player_o, null));
        }
    }

    private void checkGameStatus() {
        if (gameBoard.isGameOver()) {
            String winner = gameBoard.getWinner();
            
            if ("DRAW".equals(winner)) {
                statusText.setText(getString(R.string.draw));
            } else if ("PVAI".equals(gameMode)) {
                if ("X".equals(winner)) {
                    statusText.setText(getString(R.string.you_won));
                } else {
                    statusText.setText(getString(R.string.you_lost));
                }
            } else {
                statusText.setText(winner + " " + getString(R.string.winner_x).substring(2));
            }
            
            playAgainButton.setVisibility(View.VISIBLE);
            disableBoard();
        } else {
            String currentPlayer = gameBoard.getCurrentPlayer();
            if ("PVAI".equals(gameMode)) {
                statusText.setText("X".equals(currentPlayer) ? "Your turn (X)" : "AI's turn (O)");
            } else {
                statusText.setText("Player " + currentPlayer + "'s turn");
            }
        }
    }

    private void updateOnlineGameState() {
        isMyTurn = false;
        roomManager.updateGameState(
            roomName,
            gameBoard.getBoard(),
            gameBoard.getCurrentPlayer(),
            gameBoard.isGameOver(),
            gameBoard.getWinner(),
            new GameRoomManager.UpdateCallback() {
                @Override
                public void onSuccess() {
                    updateStatusForOnlineMode();
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(GameActivity.this, "Update failed: " + error, 
                        Toast.LENGTH_SHORT).show();
                }
            }
        );
    }

    private void updateBoardFromOnlineGame(GameRoom room) {
        String[][] board = room.getBoard();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (!board[i][j].equals(gameBoard.getCell(i, j))) {
                    gameBoard.getBoard()[i][j] = board[i][j];
                    updateCell(i, j);
                }
            }
        }

        gameBoard.setCurrentPlayer(room.getCurrentPlayer());
        isMyTurn = myPlayerSymbol.equals(room.getCurrentPlayer());
        
        if (room.isGameOver()) {
            String winner = room.getWinner();
            if ("DRAW".equals(winner)) {
                statusText.setText(getString(R.string.draw));
            } else if (winner.equals(myPlayerSymbol)) {
                statusText.setText(getString(R.string.you_won));
            } else {
                statusText.setText(getString(R.string.you_lost));
            }
            playAgainButton.setVisibility(View.VISIBLE);
            disableBoard();
        } else {
            updateStatusForOnlineMode();
        }
    }

    private void updateStatusForOnlineMode() {
        if (isMyTurn) {
            statusText.setText(getString(R.string.your_turn));
        } else {
            statusText.setText(getString(R.string.opponent_turn));
        }
    }

    private void disableBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                cells[i][j].setEnabled(false);
            }
        }
    }

    private void resetGame() {
        gameBoard.reset();
        
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                cells[i][j].setText("");
                cells[i][j].setEnabled(true);
            }
        }

        playAgainButton.setVisibility(View.GONE);
        
        if ("PVAI".equals(gameMode)) {
            statusText.setText("Your turn (X)");
        } else if ("ONLINE".equals(gameMode)) {
            isMyTurn = "X".equals(myPlayerSymbol);
            updateStatusForOnlineMode();
            updateOnlineGameState();
        } else {
            statusText.setText("Player X's turn");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (roomListener != null && roomName != null) {
            roomManager.removeRoomListener(roomName, roomListener);
        }
    }
}
