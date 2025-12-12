package com.mustafa.xogame.models;

public class GameBoard {
    private String[][] board;
    private String currentPlayer;
    private boolean gameOver;
    private String winner;

    public GameBoard() {
        board = new String[3][3];
        currentPlayer = "X";
        gameOver = false;
        winner = null;
        initializeBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = "";
            }
        }
    }

    public boolean makeMove(int row, int col) {
        if (row < 0 || row >= 3 || col < 0 || col >= 3) {
            return false;
        }
        
        if (!board[row][col].isEmpty() || gameOver) {
            return false;
        }

        board[row][col] = currentPlayer;
        
        if (checkWinner()) {
            gameOver = true;
            winner = currentPlayer;
        } else if (isBoardFull()) {
            gameOver = true;
            winner = "DRAW";
        } else {
            switchPlayer();
        }
        
        return true;
    }

    private void switchPlayer() {
        currentPlayer = currentPlayer.equals("X") ? "O" : "X";
    }

    private boolean checkWinner() {
        // Check rows
        for (int i = 0; i < 3; i++) {
            if (!board[i][0].isEmpty() && 
                board[i][0].equals(board[i][1]) && 
                board[i][1].equals(board[i][2])) {
                return true;
            }
        }

        // Check columns
        for (int j = 0; j < 3; j++) {
            if (!board[0][j].isEmpty() && 
                board[0][j].equals(board[1][j]) && 
                board[1][j].equals(board[2][j])) {
                return true;
            }
        }

        // Check diagonals
        if (!board[0][0].isEmpty() && 
            board[0][0].equals(board[1][1]) && 
            board[1][1].equals(board[2][2])) {
            return true;
        }

        if (!board[0][2].isEmpty() && 
            board[0][2].equals(board[1][1]) && 
            board[1][1].equals(board[2][0])) {
            return true;
        }

        return false;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    public String getCell(int row, int col) {
        if (row >= 0 && row < 3 && col >= 0 && col < 3) {
            return board[row][col];
        }
        return "";
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public String getWinner() {
        return winner;
    }

    public void reset() {
        initializeBoard();
        currentPlayer = "X";
        gameOver = false;
        winner = null;
    }

    public String[][] getBoard() {
        return board;
    }

    public void setBoard(String[][] board) {
        this.board = board;
    }

    public void setCurrentPlayer(String player) {
        this.currentPlayer = player;
    }
}
