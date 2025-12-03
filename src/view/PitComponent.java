/*
 * Assignment: CS151 Finals Project - Mancala Game
 * Author: Umar Lin
 * Date: 2025-11-30
 */

package view;

import model.MancalaGame;

import javax.swing.JButton;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Custom component representing a Mancala pit or store. It renders stones as
 * circles and animates changes in the stone count.
 */
public class PitComponent extends JButton implements PitView {
    private final int pitIndex;
    private final boolean store;
    private StyleStrategy style;
    private Color baseColor;
    private final String slotLabel;

    private int targetStoneCount = 0;
    private int displayedStoneCount = 0;
    private Timer animationTimer;
    private Timer flashTimer;

    private boolean hoverActive = false;
    private boolean pressedActive = false;
    private boolean flashActive = false;

    /**
     * Creates a visual pit/store representation.
     *
     * @param pitIndex index in Mancala board array
     * @param isStore  true if this represents a Mancala store
     * @param style    board styling to use
     */
    public PitComponent(int pitIndex, boolean isStore, StyleStrategy style) {
        this.pitIndex = pitIndex;
        this.store = isStore;
        this.slotLabel = computeSlotLabel(pitIndex, isStore);
        setPreferredSize(new Dimension(isStore ? 90 : 80, isStore ? 220 : 80));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setMargin(new Insets(0, 0, 0, 0));
        setOpaque(false);
        setEnabled(!isStore);
        setStyle(style);
    }

    /**
     * @return pit index represented by the component.
     */
    public int getPitIndex() {
        return pitIndex;
    }

    /**
     * @return true if this component represents a store.
     */
    public boolean isStore() {
        return store;
    }

    /**
     * Applies the given style to the component.
     *
     * @param style style to use for painting
     */
    public void setStyle(StyleStrategy style) {
        this.style = style;
        this.baseColor = store ? style.getStoreColor() : style.getPitColor();
        repaint();
    }

    /**
     * Updates the target stone count and triggers an animation toward it.
     *
     * @param count number of stones currently in this pit
     */
    public void setStoneCount(int count) {
        targetStoneCount = Math.max(0, count);
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
        if (displayedStoneCount == targetStoneCount) {
            return;
        }
        animationTimer = new Timer(60, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (displayedStoneCount == targetStoneCount) {
                    animationTimer.stop();
                    return;
                }
                if (displayedStoneCount < targetStoneCount) {
                    displayedStoneCount++;
                } else {
                    displayedStoneCount--;
                }
                repaint();
            }
        });
        animationTimer.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showInvalidClickFeedback() {
        flashActive = true;
        repaint();
        if (flashTimer != null && flashTimer.isRunning()) {
            flashTimer.stop();
        }
        flashTimer = new Timer(250, e -> {
            flashActive = false;
            repaint();
        });
        flashTimer.setRepeats(false);
        flashTimer.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showHoverEffect(boolean active) {
        hoverActive = active;
        repaint();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showPressedEffect(boolean active) {
        pressedActive = active;
        repaint();
    }

    /**
     * Paints the pit background, outline, stones, and labels.
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int arc = store ? 60 : 40;

        Color fill = baseColor;
        if (flashActive) {
            fill = style.getAccentColor();
        } else if (pressedActive) {
            fill = baseColor.darker();
        } else if (hoverActive && isEnabled()) {
            fill = baseColor.brighter();
        }

        g2.setColor(fill);
        g2.fillRoundRect(0, 0, width, height, arc, arc);

        g2.setColor(style.getAccentColor().darker());
        g2.setStroke(new BasicStroke(store ? 4f : 2f));
        g2.drawRoundRect(1, 1, width - 2, height - 2, arc, arc);

        if (store) {
            drawStoreUnderlay(g2);
        }

        drawStones(g2);
        drawLabels(g2);

        g2.dispose();
    }

    /**
     * Draws the stones given the current animation state.
     */
    private void drawStones(Graphics2D g2) {
        int stones = displayedStoneCount;
        if (stones <= 0) {
            return;
        }

        int padding = store ? 18 : 12;
        int width = getWidth() - padding * 2;
        int height = getHeight() - padding * 2;

        int columns = store ? 3 : 3;
        int rows = Math.max(1, (int) Math.ceil(stones / (double) columns));

        int cellWidth = Math.max(10, width / columns);
        int cellHeight = Math.max(10, height / rows);
        int radius = Math.max(5, Math.min(cellWidth, cellHeight) / 2 - 4);
        int diameter = radius * 2;

        Color stoneColor = style.getTextColor();
        Color outline = style.getAccentColor().darker();

        for (int i = 0; i < stones; i++) {
            int row = i / columns;
            int col = i % columns;
            int cx = padding + col * cellWidth + cellWidth / 2;
            int cy = padding + row * cellHeight + cellHeight / 2;

            g2.setColor(stoneColor);
            g2.fillOval(cx - radius, cy - radius, diameter, diameter);
            g2.setColor(outline);
            g2.drawOval(cx - radius, cy - radius, diameter, diameter);
        }
    }

    /**
     * Draws the counter and label text.
     */
    private void drawLabels(Graphics2D g2) {
        g2.setColor(style.getTextColor());

        Font labelFont = store
                ? style.getLabelFont().deriveFont((float) style.getLabelFont().getSize())
                : style.getPitFont();
        g2.setFont(labelFont);
        FontMetrics labelMetrics = g2.getFontMetrics();

        if (slotLabel != null && !slotLabel.isEmpty()) {
            int labelY = labelMetrics.getAscent() + (store ? 12 : 8);
            int labelX = (getWidth() - labelMetrics.stringWidth(slotLabel)) / 2;
            g2.drawString(slotLabel, labelX, labelY);
        }

        g2.setFont(style.getPitFont());
        FontMetrics fm = g2.getFontMetrics();
        String countText = String.valueOf(targetStoneCount);
        int y = getHeight() - (store ? 14 : 10);
        int x = (getWidth() - fm.stringWidth(countText)) / 2;
        g2.drawString(countText, x, y);
    }

    /**
     * Draws the "Mancala" label beneath the stones so that marbles render on top.
     */
    private void drawStoreUnderlay(Graphics2D g2) {
        g2.setFont(style.getPitFont());
        g2.setColor(style.getTextColor().darker());
        FontMetrics metrics = g2.getFontMetrics();
        String label = "Mancala";
        int y = getHeight() / 2;
        int x = (getWidth() - metrics.stringWidth(label)) / 2;
        g2.drawString(label, x, y);
    }

    /**
     * Generates the textual label for pits and Mancalas per assignment spec.
     */
    private static String computeSlotLabel(int index, boolean isStore) {
        if (isStore) {
            return index == MancalaGame.STORE_A ? "A" : "B";
        }

        if (index >= 0 && index < MancalaGame.PITS_PER_SIDE) {
            return "A" + (index + 1);
        }

        if (index > MancalaGame.STORE_A && index < MancalaGame.STORE_B) {
            int labelNumber = index - MancalaGame.STORE_A;
            return "B" + labelNumber;
        }

        return "";
    }
}
