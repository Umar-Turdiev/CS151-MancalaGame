package model;

/**
 * Describes the outcome of a single move.
 *
 * This is useful for the controller and view to know what happened:
 *  - whether the move was legal
 *  - whether the player gets a free turn
 *  - whether a capture occurred
 *  - whether the game is over
 *  - who the next player is
 *  - a short message useful for status labels
 */
public class MoveResult {

    private final boolean moveLegal;
    private final boolean freeTurn;
    private final boolean captureHappened;
    private final boolean gameOver;
    private final Player nextPlayer;
    private final String message;

    /**
     * Creates a MoveResult.
     *
     * @param moveLegal        whether the move was legal and actually performed
     * @param freeTurn         whether the current player gets another turn
     * @param captureHappened  whether a capture occurred
     * @param gameOver         whether the move ended the game
     * @param nextPlayer       whose turn is next (or null if game over / illegal)
     * @param message          short description, e.g. "Capture!" or error message
     */
    public MoveResult(boolean moveLegal,
                      boolean freeTurn,
                      boolean captureHappened,
                      boolean gameOver,
                      Player nextPlayer,
                      String message) {
        this.moveLegal = moveLegal;
        this.freeTurn = freeTurn;
        this.captureHappened = captureHappened;
        this.gameOver = gameOver;
        this.nextPlayer = nextPlayer;
        this.message = message;
    }

    public boolean isMoveLegal() {
        return moveLegal;
    }

    public boolean isExtraTurn() {
        return freeTurn;
    }

    public boolean isCaptureHappened() {
        return captureHappened;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Player getNextPlayer() {
        return nextPlayer;
    }

    public String getMessage() {
        return message;
    }
}
