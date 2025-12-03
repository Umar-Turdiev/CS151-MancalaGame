/*
 * Assignment: CS151 Finals Project - Mancala Game
 * Author: Umar Lin
 * Date: 2025-11-25
 */

package view;

import java.awt.Color;

/**
 * Brighter palette for a modern look.
 */
public class ModernBoardStyle implements StyleStrategy {
    private static final Color BACKGROUND = new Color(0xE8F0FE);
    private static final Color PIT = new Color(0x4A90E2);
    private static final Color STORE = new Color(0x1665C1);
    private static final Color TEXT = Color.WHITE;
    private static final Color ACCENT = new Color(0x1B4F72);

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
        return "Modern";
    }
}
