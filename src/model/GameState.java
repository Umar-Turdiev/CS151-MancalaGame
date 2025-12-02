/*
 * Assignment: CS151 Finals Project - Mancala Game
 * Author: Ali Halmamat
 * Date: 2025-11-29
 */

package model;

/**
 * Immutable snapshot of the game state used for the undo feature.
 *
 * This stores only the parts of the state that must be exactly restored:
 * - board contents
 * - current player
 * - game over flag
 *
 * Undo counters and flags are handled separately by MancalaGame,
 * because performing an undo modifies those (e.g., undoCountThisTurn++).
 */
class GameState {

    private final int[] boardSnapshot;
    private final Player currentPlayerSnapshot;
    private final boolean gameOverSnapshot;

    /**
     * Creates a snapshot from the given game state.
     *
     * @param board        the current board array (will be cloned)
     * @param currentPlayer the player whose turn it is
     * @param gameOver     whether the game is currently over
     */
    GameState(int[] board, Player currentPlayer, boolean gameOver) {
        this.boardSnapshot = board.clone(); // deep copy
        this.currentPlayerSnapshot = currentPlayer;
        this.gameOverSnapshot = gameOver;
    }

    /**
     * @return a copy of the stored board array.
     */
    int[] getBoard() {
        return boardSnapshot.clone();
    }

    /**
     * @return the stored current player.
     */
    Player getCurrentPlayer() {
        return currentPlayerSnapshot;
    }

    /**
     * @return true if the stored snapshot was game over.
     */
    boolean isGameOver() {
        return gameOverSnapshot;
    }
}
