package controller;

import javax.swing.*;

import model.MancalaGame;
import model.MoveResult;
import model.Player;
import view.ClassicBoardStyle;
import view.MancalaView;
import view.ModernBoardStyle;

import java.awt.event.*;

/**
 * Main controller for the Mancala game.
 */
public class MancalaController {
    private MancalaGame model;
    private MancalaView view;
    private UndoController undoController;
    private GameStateController stateController;
    
    public MancalaController(MancalaGame model, MancalaView view) {
        this.model = model;
        this.view = view;
        this.undoController = new UndoController(model);
        this.stateController = new GameStateController(model);
        
        initializeController();
    }
    
    private void initializeController() {
        view.addPitClickListener(new PitClickListener());
        view.addUndoButtonListener(new UndoButtonListener());
        view.addStyleSelectionListener(new StyleSelectionListener());
        view.addInitialStonesListener(new InitialStonesListener());
    }
    
    private class PitClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!stateController.canMakeMove()) {
                view.showMessage("Cannot make moves at this time.");
                return;
            }
            
            String command = e.getActionCommand();
            int pitIndex = Integer.parseInt(command);
            
            if (!model.isValidMove(pitIndex)) {
                view.showMessage("Invalid move! Select a pit on your side with stones.");
                return;
            }
            
            model.saveState();
            MoveResult result = model.makeMove(pitIndex);
            
            view.updateBoard(model.getBoardState());
            view.updateCurrentPlayer(model.getCurrentPlayer());
            view.updateUndoButton(undoController.isUndoAvailable());
            
            boolean extraTurn = (result != null && result.isExtraTurn());
            if (extraTurn) {
                view.showMessage("Last stone in your Mancala! Take another turn.");
            } else if (result != null && !model.isGameOver()) {
                Player next = result.getNextPlayer();
                if (next != null) {
                    String nextLabel = next == Player.PLAYER_A ? "A" : "B";
                    view.showMessage(result.getMessage() + " Player " + nextLabel + "'s turn.");
                } else {
                    view.showMessage(result.getMessage());
                }
            }
            
            stateController.nextTurn(extraTurn);
            
            if (model.isGameOver()) {
                handleGameOver();
            }
        }
    }
    
    private class UndoButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            UndoController.UndoResult result = undoController.performUndo();
            
            if (result.isSuccess()) {
                view.updateBoard(model.getBoardState());
                view.updateCurrentPlayer(model.getCurrentPlayer());
                view.updateUndoButton(undoController.isUndoAvailable());
                Player current = model.getCurrentPlayer();
                String turnText = " Player " + (current == Player.PLAYER_A ? "A" : "B") + "'s turn.";
                view.showMessage(result.getMessage() + turnText);
            } else {
                view.showMessage(result.getMessage());
            }
        }
    }
    
    private class StyleSelectionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String styleCommand = e.getActionCommand();
            
            if (styleCommand.equals("CLASSIC")) {
                view.setBoardStyle(new ClassicBoardStyle());
            } else if (styleCommand.equals("MODERN")) {
                view.setBoardStyle(new ModernBoardStyle());
            }
            
            view.repaint();
        }
    }
    
    private class InitialStonesListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int initialStones = view.getInitialStonesInput();
                
                if (initialStones < 3 || initialStones > 4) {
                    view.showMessage("Please enter 3 or 4 stones per pit.");
                    return;
                }
                
                model.initializeGame(initialStones);

                // Move from style selection -> initial setup -> player A turn
                stateController.transitionTo(GameStateController.GameState.INITIAL_SETUP);
                stateController.startGame();
                
                view.updateBoard(model.getBoardState());
                view.startGame();
                view.updateCurrentPlayer(model.getCurrentPlayer());
                view.updateUndoButton(false);
                view.showMessage("Player A's turn. Select a pit to begin.");
                
            } catch (NumberFormatException ex) {
                view.showMessage("Invalid input. Please enter a number (3 or 4).");
            }
        }
    }
    
    private void handleGameOver() {
        model.collectRemainingStones();
        view.updateBoard(model.getBoardState());
        
        int[] scores = model.getMancalaScores();
        String winner;
        if (scores[0] > scores[1]) {
            winner = "Player A wins with " + scores[0] + " stones!";
        } else if (scores[1] > scores[0]) {
            winner = "Player B wins with " + scores[1] + " stones!";
        } else {
            winner = "It's a tie! Both players have " + scores[0] + " stones.";
        }

        view.showMessage(winner);
        
        int response = JOptionPane.showConfirmDialog(
            null,
            winner + "\nWould you like to play again?",
            "Game Over",
            JOptionPane.YES_NO_OPTION
        );
        
        if (response == JOptionPane.YES_OPTION) {
            resetGame();
        } else {
            System.exit(0);
        }
    }
    
    private void resetGame() {
        stateController.reset();
        view.resetToStyleSelection();
        view.updateUndoButton(false);
    }
    
    public void start() {
        view.showStyleSelection();
    }
}
