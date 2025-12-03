/*
 * Assignment: CS151 Finals Project - Mancala Game
 * Author: Sana Al Hamimidi, Umar Lin
 * Date: 2025-12-01
 */

import model.MancalaGame;
import view.MancalaView;
import controller.MancalaController;

import javax.swing.SwingUtilities;

/**
 * Entry point for launching the Mancala game.
 */
public class MancalaTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MancalaGame model = new MancalaGame();
            MancalaView view = new MancalaView();
            MancalaController controller = new MancalaController(model, view);
            controller.start();
        });
    }
}
