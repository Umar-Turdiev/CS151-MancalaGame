/*
 * Assignment: CS151 Finals Project - Mancala Game
 * Author: Umar Lin
 * Date: 2025-11-30
 */

package view;

/**
 * Interface describing UI interactions for a pit component.
 */
public interface PitView {
    /**
     * Highlights the pit briefly to indicate an invalid move.
     */
    void showInvalidClickFeedback();

    /**
     * Turns hover highlighting on or off.
     *
     * @param active {@code true} to enable hover visuals
     */
    void showHoverEffect(boolean active);

    /**
     * Turns pressed highlighting on or off.
     *
     * @param active {@code true} while the mouse is pressed.
     */
    void showPressedEffect(boolean active);
}
