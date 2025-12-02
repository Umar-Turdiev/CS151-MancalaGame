/*
 * Assignment: CS151 Finals Project - Mancala Game
 * Author: Ali Halmamat
 * Date: 2025-11-29
 */

package model;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

/**
 * Core Mancala game model.
 *
 * This class:
 *  - holds the board state
 *  - enforces the game rules
 *  - handles the undo logic with the assignment's constraints
 *
 * It is completely independent of the GUI (view) and controller.
 *
 * Board indexing convention (fixed, public to make view/controller life easier):
 *
 *   Player A side (typically bottom):
 *     pits: 0..5
 *     store: 6
 *
 *   Player B side (typically top):
 *     pits: 7..12
 *     store: 13
 *
 * We have 14 pockets total (12 pits + 2 stores).
 */
public class MancalaGame {

    /** Number of pits per side (fixed by the assignment). */
    public static final int PITS_PER_SIDE = 6;

    /** Total number of pits on both sides. */
    public static final int TOTAL_PITS = PITS_PER_SIDE * 2;

    /** Total number of pockets (pits + stores). */
    public static final int TOTAL_POCKETS = TOTAL_PITS + 2; // 14

    /** Index of Player A's store (Mancala). */
    public static final int STORE_A = 6;

    /** Index of Player B's store (Mancala). */
    public static final int STORE_B = 13;

    /** Internal board array; length = TOTAL_POCKETS. */
    private final int[] board = new int[TOTAL_POCKETS];

    /** Player whose turn it currently is. */
    private Player currentPlayer = Player.PLAYER_A;

    /** True if the game is over and no more moves are allowed. */
    private boolean gameOver = false;

    /**
     * History stack of GameState snapshots used for undo.
     * We only keep snapshots relevant to the current player's turn.
     */
    private final Deque<GameState> history = new ArrayDeque<>();

    /**
     * True when the controller already saved a snapshot before calling makeMove.
     * Used to avoid storing duplicate snapshots in the history.
     */
    private boolean manualSnapshotQueued = false;

    /** Maximum undos allowed per player within a single turn. */
    private static final int MAX_UNDOS_PER_TURN = 3;

    /** How many undos the current player has used this turn (max 3). */
    private int undoCountThisTurn = 0;

    /** True if the last action performed was an undo (no double-undo allowed). */
    private boolean lastActionWasUndo = false;

    /** Optional ChangeListeners – typical MVC pattern for notifying views. */
    private final List<ChangeListener> listeners = new ArrayList<>();

    // ---------------------- Public API ----------------------

    /**
     * Initializes the board with the given number of stones per pit.
     *
     * According to the assignment, this must be either 3 or 4.
     * All 12 pits are filled with that many stones; both stores start at 0.
     *
     * @param stonesPerPit number of stones to place in each pit (3 or 4)
     * @throws IllegalArgumentException if stonesPerPit is not 3 or 4
     */
    public void initialize(int stonesPerPit) {
        if (stonesPerPit < 3 || stonesPerPit > 4) {
            throw new IllegalArgumentException("stonesPerPit must be 3 or 4");
        }

        // Clear the board completely first.
        Arrays.fill(board, 0);

        // Fill Player A pits 0..5
        for (int i = 0; i < PITS_PER_SIDE; i++) {
            board[i] = stonesPerPit;
        }

        // Fill Player B pits 7..12
        for (int i = STORE_A + 1; i < STORE_B; i++) {
            board[i] = stonesPerPit;
        }

        // Stores must be 0
        board[STORE_A] = 0;
        board[STORE_B] = 0;

        // Reset game meta state
        currentPlayer = Player.PLAYER_A; // you can change to random if you want, but not required
        gameOver = false;
        history.clear();
        undoCountThisTurn = 0;
        lastActionWasUndo = false;
        manualSnapshotQueued = false;

        fireChangeEvent();
    }

    /**
     * Backwards compatible alias for initialize used by some controllers.
     */
    public void initializeGame(int stonesPerPit) {
        initialize(stonesPerPit);
    }

    /**
     * Performs a move for the current player starting from the given pit index.
     *
     * The index must refer to one of the current player's pits (not a store),
     * and that pit must contain at least one stone.
     *
     * If the move is illegal, the game state is not changed.
     *
     * @param pitIndex index of the pit chosen by the current player
     * @return a MoveResult describing what happened
     */
    public MoveResult makeMove(int pitIndex) {
        // Cannot move if the game is already over.
        if (gameOver) {
            return new MoveResult(false, false, false, true, null,
                                  "Game is already over.");
        }

        // Validate index range.
        if (pitIndex < 0 || pitIndex >= TOTAL_POCKETS || isStore(pitIndex)) {
            return new MoveResult(false, false, false, false, currentPlayer,
                                  "Invalid pit index.");
        }

        // Validate that this pit belongs to the current player.
        if (!isOwnPit(pitIndex, currentPlayer)) {
            return new MoveResult(false, false, false, false, currentPlayer,
                                  "You must choose one of your own pits.");
        }

        // Validate that the pit is not empty.
        if (board[pitIndex] == 0) {
            return new MoveResult(false, false, false, false, currentPlayer,
                                  "Selected pit is empty.");
        }

        // BEFORE performing the move, push a snapshot for undo unless
        // the controller already saved the current state.
        if (!manualSnapshotQueued) {
            history.push(new GameState(board, currentPlayer, gameOver));
        } else {
            manualSnapshotQueued = false;
        }

        // Core sowing logic.
        int stonesInHand = board[pitIndex];
        board[pitIndex] = 0; // we pick up all stones from this pit

        int currentIndex = pitIndex;
        Player player = currentPlayer;
        int myStore = getStoreIndex(player);
        int opponentStore = getStoreIndex(player.opposite());

        while (stonesInHand > 0) {
            currentIndex = (currentIndex + 1) % TOTAL_POCKETS;

            // Skip the opponent's store.
            if (currentIndex == opponentStore) {
                continue;
            }

            board[currentIndex]++;
            stonesInHand--;
        }

        // Determine if this was a free-turn move (last stone in own store).
        boolean freeTurn = (currentIndex == myStore);
        boolean captureHappened = false;

        // Capture rule: last stone lands in an empty pit on player's own side
        // (and that pit had 0 before placing this last stone).
        if (!freeTurn && isOwnPit(currentIndex, player)) {
            if (board[currentIndex] == 1) { // it was 0, then we placed 1 stone
                int oppositeIndex = getOppositePit(currentIndex);
                if (oppositeIndex >= 0) {
                    int oppositeStones = board[oppositeIndex];
                    if (oppositeStones > 0) {
                        // Capture both the last stone and opposite stones.
                        board[oppositeIndex] = 0;
                        board[currentIndex] = 0;
                        board[myStore] += oppositeStones + 1;
                        captureHappened = true;
                    }
                }
            }
        }

        // Check for game-end condition: if all pits on one side are empty.
        if (isSideEmpty(Player.PLAYER_A) || isSideEmpty(Player.PLAYER_B)) {
            collectRemainingStones();
            gameOver = true;
        }

        // Determine next player:
        // - If game is over, there's no "next turn" logically.
        // - If freeTurn and not gameOver, currentPlayer stays the same.
        // - Otherwise, switch players.
        Player nextPlayer;
        if (gameOver) {
            nextPlayer = null;
        } else if (freeTurn) {
            // same player continues
            nextPlayer = currentPlayer;
        } else {
            // switch turns
            currentPlayer = currentPlayer.opposite();
            nextPlayer = currentPlayer;

            // When the turn switches, we reset undo tracking for the new player.
            undoCountThisTurn = 0;
        }

        // After a successful move, the last action is definitely NOT an undo.
        lastActionWasUndo = false;

        fireChangeEvent();

        return new MoveResult(true, freeTurn, captureHappened, gameOver,
                              nextPlayer,
                              buildMoveMessage(freeTurn, captureHappened));
    }

    /**
     * Explicit hook for controllers that want to capture the current state
     * before calling makeMove. If not invoked, makeMove will still save a
     * snapshot automatically.
     */
    public void saveState() {
        history.push(new GameState(board, currentPlayer, gameOver));
        manualSnapshotQueued = true;
    }

    /**
     * Attempts to undo the last move made in the current player's turn.
     *
     * Rules enforced:
     *  - Can only undo if there is a stored GameState in history.
     *  - Cannot undo twice in a row (no multiple undos in a row).
     *  - Maximum of 3 undos per turn for a player.
     *  - Undo is only for the moves of the current turn (history is cleared when turns change).
     *
     * @return true if undo succeeded and state was reverted; false otherwise.
     */
    public boolean undo() {
        // No history to undo.
        if (history.isEmpty()) {
            return false;
        }

        // Cannot undo if we already undid last time (no multiple undos in a row).
        if (lastActionWasUndo) {
            return false;
        }

        // Cannot exceed 3 undos per turn.
        if (undoCountThisTurn >= MAX_UNDOS_PER_TURN) {
            return false;
        }

        // Pop the previous game state and restore it.
        GameState previous = history.pop();
        int[] previousBoard = previous.getBoard();

        System.arraycopy(previousBoard, 0, board, 0, board.length);
        this.currentPlayer = previous.getCurrentPlayer();
        this.gameOver = previous.isGameOver();

        // Update undo tracking.
        undoCountThisTurn++;
        lastActionWasUndo = true;
        manualSnapshotQueued = false;

        fireChangeEvent();
        return true;
    }

    /**
     * @return true if there is at least one snapshot available to undo.
     */
    public boolean canUndo() {
        return !history.isEmpty();
    }

    /**
     * @return remaining undos the current player can perform this turn.
     */
    public int getUndosRemainingThisTurn() {
        return Math.max(0, MAX_UNDOS_PER_TURN - undoCountThisTurn);
    }

    // ---------------------- Query methods for view/controller ----------------------

    /**
     * @return current player whose turn it is.
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * @return true if the game is over.
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Returns the number of stones at the given pocket index.
     * This index may refer to a pit or a store.
     *
     * @param index pocket index 0..13
     * @return number of stones at that pocket
     * @throws IllegalArgumentException if index is out of range
     */
    public int getStonesAt(int index) {
        if (index < 0 || index >= TOTAL_POCKETS) {
            throw new IllegalArgumentException("Index must be between 0 and " + (TOTAL_POCKETS - 1));
        }
        return board[index];
    }

    /**
     * @param player player whose store to query
     * @return number of stones currently in the player's Mancala (store)
     */
    public int getScore(Player player) {
        return board[getStoreIndex(player)];
    }

    /**
     * @return a copy of the whole board array for debugging or custom views.
     */
    public int[] getBoardSnapshot() {
        return board.clone();
    }

    /**
     * Alias used by some controllers/views.
     *
     * @return copy of current board state.
     */
    public int[] getBoardState() {
        return getBoardSnapshot();
    }

    /**
     * Convenience: @return the store index for the given player.
     */
    public int getStoreIndex(Player player) {
        return player == Player.PLAYER_A ? STORE_A : STORE_B;
    }

    /**
     * @return the number of pits per side (constant: 6).
     */
    public int getPitsPerSide() {
        return PITS_PER_SIDE;
    }

    /**
     * @return total number of pockets (pits + stores) on the board (constant: 14).
     */
    public int getTotalPockets() {
        return TOTAL_POCKETS;
    }

    /**
     * Validates whether the provided pit index represents a legal move for
     * the current player.
     */
    public boolean isValidMove(int pitIndex) {
        if (gameOver) {
            return false;
        }
        if (pitIndex < 0 || pitIndex >= TOTAL_POCKETS) {
            return false;
        }
        if (isStore(pitIndex)) {
            return false;
        }
        if (!isOwnPit(pitIndex, currentPlayer)) {
            return false;
        }
        return board[pitIndex] > 0;
    }

    /**
     * Convenience access to both players' store scores.
     *
     * @return array with scores [playerA, playerB]
     */
    public int[] getMancalaScores() {
        return new int[] { board[STORE_A], board[STORE_B] };
    }

    /**
     * Determines the winner once the game is over.
     *
     * @return PLAYER_A if Player A wins, PLAYER_B if Player B wins,
     *         or null if it's a tie or game is not over yet.
     */
    public Player getWinner() {
        if (!gameOver) {
            return null;
        }
        int scoreA = board[STORE_A];
        int scoreB = board[STORE_B];

        if (scoreA > scoreB) {
            return Player.PLAYER_A;
        } else if (scoreB > scoreA) {
            return Player.PLAYER_B;
        } else {
            return null; // tie
        }
    }

    // ---------------------- Listener support (optional but nice for MVC) ----------------------

    /**
     * Adds a ChangeListener that will be notified whenever the model's state changes
     * (initialize, legal move, successful undo).
     *
     * @param listener the listener to add
     */
    public void addChangeListener(ChangeListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /**
     * Removes a previously added ChangeListener.
     *
     * @param listener the listener to remove
     */
    public void removeChangeListener(ChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies all registered ChangeListeners that the model has changed.
     */
    private void fireChangeEvent() {
        ChangeEvent event = new ChangeEvent(this);
        for (ChangeListener listener : listeners) {
            listener.stateChanged(event);
        }
    }

    // ---------------------- Internal helper methods ----------------------

    /**
     * Returns true if the given index is a store (Mancala) index.
     */
    private boolean isStore(int index) {
        return index == STORE_A || index == STORE_B;
    }

    /**
     * Returns true if the given pit index belongs to the given player.
     * Stores are not considered "pits" here and always return false.
     */
    private boolean isOwnPit(int pitIndex, Player player) {
        if (isStore(pitIndex)) {
            return false;
        }
        if (player == Player.PLAYER_A) {
            // Player A pits: 0..5
            return pitIndex >= 0 && pitIndex < PITS_PER_SIDE;
        } else {
            // Player B pits: 7..12
            return pitIndex > STORE_A && pitIndex < STORE_B;
        }
    }

    /**
     * Checks whether all pits on the given player's side are empty.
     *
     * @param player the player to check
     * @return true if that player's 6 pits all contain 0 stones
     */
    private boolean isSideEmpty(Player player) {
        if (player == Player.PLAYER_A) {
            for (int i = 0; i < PITS_PER_SIDE; i++) {
                if (board[i] != 0) {
                    return false;
                }
            }
        } else {
            for (int i = STORE_A + 1; i < STORE_B; i++) {
                if (board[i] != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Collects remaining stones from the non-empty side into its store when the game ends.
     *
     * Exactly one side must be empty to trigger this. We:
     *  - find which side still has stones
     *  - move all from its pits into its store
     *  - set those pits to 0
     */
    public void collectRemainingStones() {
        boolean sideAEmpty = isSideEmpty(Player.PLAYER_A);
        boolean sideBEmpty = isSideEmpty(Player.PLAYER_B);

        if (!sideAEmpty) {
            // Collect from Player A side (0..5) into STORE_A
            int sum = 0;
            for (int i = 0; i < PITS_PER_SIDE; i++) {
                sum += board[i];
                board[i] = 0;
            }
            board[STORE_A] += sum;
        }

        if (!sideBEmpty) {
            // Collect from Player B side (7..12) into STORE_B
            int sum = 0;
            for (int i = STORE_A + 1; i < STORE_B; i++) {
                sum += board[i];
                board[i] = 0;
            }
            board[STORE_B] += sum;
        }
    }

    /**
     * Given a pit index (not a store), returns the index of the opposite pit.
     *
     * Using our board layout, the opposite pit mapping is:
     *   0 <-> 12
     *   1 <-> 11
     *   2 <-> 10
     *   3 <-> 9
     *   4 <-> 8
     *   5 <-> 7
     *
     * Formula: oppositeIndex = 12 - pitIndex
     *
     * @param pitIndex a pit index (0..5 or 7..12)
     * @return the opposite pit index, or -1 if pitIndex is invalid or a store
     */
    private int getOppositePit(int pitIndex) {
        if (isStore(pitIndex)) {
            return -1;
        }
        return 12 - pitIndex;
    }

    /**
     * Builds a small message string summarizing the move outcome.
     *
     * @param freeTurn        true if the player gets another turn
     * @param captureHappened true if a capture occurred
     * @return a short human-readable message
     */
    private String buildMoveMessage(boolean freeTurn, boolean captureHappened) {
        if (gameOver) {
            return "Game over.";
        }
        if (freeTurn && captureHappened) {
            return "Capture and free turn!";
        }
        if (freeTurn) {
            return "Free turn!";
        }
        if (captureHappened) {
            return "Capture!";
        }
        return "Move completed.";
    }
}
