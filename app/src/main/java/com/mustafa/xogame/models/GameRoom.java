package com.mustafa.xogame.models;

public class GameRoom {
    private String roomId;
    private String roomName;
    private String playerXId;
    private String playerXName;
    private String playerOId;
    private String playerOName;
    private String[][] board;
    private String currentPlayer;
    private boolean gameOver;
    private String winner;
    private String status; // "waiting", "playing", "finished"

    public GameRoom() {
        // Default constructor required for Firebase
        board = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = "";
            }
        }
    }

    public GameRoom(String roomId, String roomName, String playerXId, String playerXName) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.playerXId = playerXId;
        this.playerXName = playerXName;
        this.currentPlayer = "X";
        this.gameOver = false;
        this.status = "waiting";
        this.board = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = "";
            }
        }
    }

    // Getters and setters
    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getPlayerXId() {
        return playerXId;
    }

    public void setPlayerXId(String playerXId) {
        this.playerXId = playerXId;
    }

    public String getPlayerXName() {
        return playerXName;
    }

    public void setPlayerXName(String playerXName) {
        this.playerXName = playerXName;
    }

    public String getPlayerOId() {
        return playerOId;
    }

    public void setPlayerOId(String playerOId) {
        this.playerOId = playerOId;
    }

    public String getPlayerOName() {
        return playerOName;
    }

    public void setPlayerOName(String playerOName) {
        this.playerOName = playerOName;
    }

    public String[][] getBoard() {
        return board;
    }

    public void setBoard(String[][] board) {
        this.board = board;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
