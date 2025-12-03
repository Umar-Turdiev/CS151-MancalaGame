/*
 * Assignment: CS151 Finals Project - Mancala Game
 * Author: Umar Lin
 * Date: 2025-11-25
 */

package view;

import java.awt.Color;
import java.awt.Font;

/**
 * Strategy interface for board styling. Provides palette and typography hints
 * used by the Mancala board widgets.
 */
public interface StyleStrategy {
    /**
     * @return background color used behind the board.
     */
    Color getBackgroundColor();

    /**
     * @return fill color for regular pits.
     */
    Color getPitColor();

    /**
     * @return fill color for the Mancala stores.
     */
    Color getStoreColor();

    /**
     * @return color used for text labels and stone fills.
     */
    Color getTextColor();

    /**
     * @return accent color for outlines or highlights.
     */
    Color getAccentColor();

    /**
     * @return font used when rendering pit labels.
     */
    default Font getPitFont() {
        return new Font("SansSerif", Font.BOLD, 16);
    }

    /**
     * @return font used for headings like "Player A".
     */
    default Font getLabelFont() {
        return new Font("SansSerif", Font.BOLD, 18);
    }

    /**
     * @return human-readable style name.
     */
    default String getName() {
        return getClass().getSimpleName();
    }
}
