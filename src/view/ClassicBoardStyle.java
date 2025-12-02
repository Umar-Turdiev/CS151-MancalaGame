/*
 * Assignment: CS151 Finals Project - Mancala Game
 * Author: Umar Lin
 * Date: 2025-11-25
 */

package view;

import java.awt.Color;

/**
 * Earthy palette reminiscent of a wooden Mancala board.
 */
public class ClassicBoardStyle implements StyleStrategy {
    private static final Color BACKGROUND = new Color(0xEAD7C1);
    private static final Color PIT = new Color(0xB08968);
    private static final Color STORE = new Color(0x9C6644);
    private static final Color TEXT = Color.WHITE;
    private static final Color ACCENT = new Color(0x5E503F);

    /**
     * {@inheritDoc}
     */
    @Override
    public Color getBackgroundColor() {
        return BACKGROUND;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Color getPitColor() {
        return PIT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Color getStoreColor() {
        return STORE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Color getTextColor() {
        return TEXT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Color getAccentColor() {
        return ACCENT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Classic";
    }
}
