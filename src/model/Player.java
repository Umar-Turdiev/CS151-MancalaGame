package model;

/**
 * Represents a player in the Mancala game.
 *
 * PLAYER_A is typically the "bottom" side (pits 0-5, store 6).
 * PLAYER_B is the "top" side (pits 7-12, store 13).
 */
public enum Player {
    PLAYER_A,
    PLAYER_B;

    /**
     * @return the other player.
     */
    public Player opposite() {
        return this == PLAYER_A ? PLAYER_B : PLAYER_A;
    }
}
