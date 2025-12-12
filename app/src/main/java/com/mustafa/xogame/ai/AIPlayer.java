package com.mustafa.xogame.ai;

import com.mustafa.xogame.models.GameBoard;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AIPlayer {
    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    private Difficulty difficulty;
    private Random random;

    public AIPlayer(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.random = new Random();
    }

    public int[] getMove(GameBoard gameBoard) {
        switch (difficulty) {
            case EASY:
                return getEasyMove(gameBoard);
            case MEDIUM:
                return getMediumMove(gameBoard);
            case HARD:
                return getHardMove(gameBoard);
            default:
                return getEasyMove(gameBoard);
        }
    }

    // Easy: Random move
    private int[] getEasyMove(GameBoard gameBoard) {
        List<int[]> availableMoves = getAvailableMoves(gameBoard);
        if (availableMoves.isEmpty()) {
            return null;
        }
        return availableMoves.get(random.nextInt(availableMoves.size()));
    }

    // Medium: Block opponent or random
    private int[] getMediumMove(GameBoard gameBoard) {
        String opponent = gameBoard.getCurrentPlayer().equals("X") ? "O" : "X";
        
        // Try to block opponent's winning move
        int[] blockMove = findWinningMove(gameBoard, opponent);
        if (blockMove != null) {
            return blockMove;
        }

        // Otherwise, random move
        return getEasyMove(gameBoard);
    }

    // Hard: Minimax algorithm
    private int[] getHardMove(GameBoard gameBoard) {
        int[] bestMove = new int[2];
        int bestScore = Integer.MIN_VALUE;

        String aiPlayer = gameBoard.getCurrentPlayer();
        
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (gameBoard.getCell(i, j).isEmpty()) {
                    // Try this move
                    GameBoard tempBoard = copyBoard(gameBoard);
                    tempBoard.makeMove(i, j);
                    
                    int score = minimax(tempBoard, 0, false, aiPlayer);
                    
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove[0] = i;
                        bestMove[1] = j;
                    }
                }
            }
        }

        return bestMove;
    }

    private int minimax(GameBoard board, int depth, boolean isMaximizing, String aiPlayer) {
        if (board.isGameOver()) {
            String winner = board.getWinner();
            if (winner.equals(aiPlayer)) {
                return 10 - depth;
            } else if (winner.equals("DRAW")) {
                return 0;
            } else {
                return depth - 10;
            }
        }

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board.getCell(i, j).isEmpty()) {
                        GameBoard tempBoard = copyBoard(board);
                        tempBoard.makeMove(i, j);
                        int score = minimax(tempBoard, depth + 1, false, aiPlayer);
                        bestScore = Math.max(score, bestScore);
                    }
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board.getCell(i, j).isEmpty()) {
                        GameBoard tempBoard = copyBoard(board);
                        tempBoard.makeMove(i, j);
                        int score = minimax(tempBoard, depth + 1, true, aiPlayer);
                        bestScore = Math.min(score, bestScore);
                    }
                }
            }
            return bestScore;
        }
    }

    private int[] findWinningMove(GameBoard gameBoard, String player) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (gameBoard.getCell(i, j).isEmpty()) {
                    GameBoard tempBoard = copyBoard(gameBoard);
                    tempBoard.setCurrentPlayer(player);
                    tempBoard.makeMove(i, j);
                    
                    if (tempBoard.isGameOver() && player.equals(tempBoard.getWinner())) {
                        return new int[]{i, j};
                    }
                }
            }
        }
        return null;
    }

    private List<int[]> getAvailableMoves(GameBoard gameBoard) {
        List<int[]> moves = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (gameBoard.getCell(i, j).isEmpty()) {
                    moves.add(new int[]{i, j});
                }
            }
        }
        return moves;
    }

    private GameBoard copyBoard(GameBoard original) {
        GameBoard copy = new GameBoard();
        String[][] board = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = original.getCell(i, j);
            }
        }
        copy.setBoard(board);
        copy.setCurrentPlayer(original.getCurrentPlayer());
        return copy;
    }
}
