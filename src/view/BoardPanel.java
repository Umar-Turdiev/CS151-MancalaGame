/*
 * Assignment: CS151 Finals Project - Mancala Game
 * Author: Umar Lin
 * Date: 2025-11-29
 */

package view;

import model.MancalaGame;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.Arrays;

/**
 * Panel that lays out all pits and stores for the Mancala board.
 */
public class BoardPanel extends JPanel {
    private final PitComponent[] pits = new PitComponent[MancalaGame.TOTAL_POCKETS];
    private StyleStrategy style;
    private final JLabel playerALabel = new JLabel("Player A", SwingConstants.CENTER);
    private final JLabel playerBLabel = new JLabel("Player B", SwingConstants.CENTER);

    /**
     * Creates a board view using the given style.
     *
     * @param style visual style strategy
     */
    public BoardPanel(StyleStrategy style) {
        this.style = style;
        setLayout(new BorderLayout(10, 10));
        setOpaque(true);
        buildBoard();
        applyStyle(style);
    }

    /**
     * Builds and lays out the pit components.
     */
    private void buildBoard() {
        PitComponent storeB = new PitComponent(MancalaGame.STORE_B, true, style);
        PitComponent storeA = new PitComponent(MancalaGame.STORE_A, true, style);
        pits[MancalaGame.STORE_B] = storeB;
        pits[MancalaGame.STORE_A] = storeA;

        JPanel centerGrid = new JPanel(new GridLayout(2, MancalaGame.PITS_PER_SIDE, 10, 10));
        centerGrid.setOpaque(false);

        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel bottomPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        bottomPanel.setOpaque(false);

        JPanel topRow = new JPanel(new GridLayout(1, MancalaGame.PITS_PER_SIDE, 10, 10));
        JPanel bottomRow = new JPanel(new GridLayout(1, MancalaGame.PITS_PER_SIDE, 10, 10));
        topRow.setOpaque(false);
        bottomRow.setOpaque(false);

        // Player B pits (12 -> 7) 
        // Note: we need to think as the board is flipped, so 7 to 12 right to left instead
        for (int i = MancalaGame.STORE_B - 1; i > MancalaGame.STORE_A; i--) {
            PitComponent pit = new PitComponent(i, false, style);
            pits[i] = pit;
            topRow.add(pit);
        }

        // Player A pits (0 -> 5) left to right
        for (int i = 0; i < MancalaGame.PITS_PER_SIDE; i++) {
            PitComponent pit = new PitComponent(i, false, style);
            pits[i] = pit;
            bottomRow.add(pit);
        }

        topPanel.add(playerBLabel, BorderLayout.NORTH);
        topPanel.add(topRow, BorderLayout.CENTER);
        bottomPanel.add(bottomRow, BorderLayout.CENTER);
        bottomPanel.add(playerALabel, BorderLayout.SOUTH);

        centerGrid.add(topPanel);
        centerGrid.add(bottomPanel);

        add(storeB, BorderLayout.WEST);
        add(centerGrid, BorderLayout.CENTER);
        add(storeA, BorderLayout.EAST);
    }

    /**
     * Registers the provided action listener on all pits.
     *
     * @param listener controller callback for pit clicks
     */
    public void setBoardListener(ActionListener listener) {
        if (listener == null) {
            return;
        }
        for (PitComponent pit : pits) {
            if (pit == null || pit.isStore()) {
                continue;
            }
            for (ActionListener al : pit.getActionListeners()) {
                pit.removeActionListener(al);
            }
            pit.setActionCommand(String.valueOf(pit.getPitIndex()));
            pit.addActionListener(listener);
        }
    }

    /**
     * Applies a new style to every pit and label.
     *
     * @param style selected strategy
     */
    public void setStyle(StyleStrategy style) {
        this.style = style;
        applyStyle(style);
    }

    /**
     * Updates component colors and fonts according to the style.
     */
    private void applyStyle(StyleStrategy style) {
        setBackground(style.getBackgroundColor());
        playerALabel.setFont(style.getLabelFont());
        playerBLabel.setFont(style.getLabelFont());
        playerALabel.setForeground(style.getAccentColor());
        playerBLabel.setForeground(style.getAccentColor());

        Arrays.stream(pits)
                .filter(p -> p != null)
                .forEach(p -> p.setStyle(style));
    }

    /**
     * Updates all pit stone counts.
     *
     * @param boardState array representing the Mancala board
     */
    public void updateBoard(int[] boardState) {
        if (boardState == null || boardState.length != MancalaGame.TOTAL_POCKETS) {
            return;
        }
        for (int i = 0; i < boardState.length; i++) {
            PitComponent pit = pits[i];
            if (pit != null) {
                pit.setStoneCount(boardState[i]);
            }
        }
    }
}
