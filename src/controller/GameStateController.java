package controller;

import model.MancalaGame;

/**
 * Controller for managing game state transitions and validation.
 */
public class GameStateController {
    private MancalaGame model;
    private GameState currentState;
    
    public enum GameState {
        STYLE_SELECTION,
        INITIAL_SETUP,
        PLAYER_A_TURN,
        PLAYER_B_TURN,
        GAME_OVER
    }
    
    public GameStateController(MancalaGame model) {
        this.model = model;
        this.currentState = GameState.STYLE_SELECTION;
    }
    
    public boolean transitionTo(GameState newState) {
        if (!isValidTransition(currentState, newState)) {
            return false;
        }
        currentState = newState;
        return true;
    }
    
    private boolean isValidTransition(GameState from, GameState to) {
        switch (from) {
            case STYLE_SELECTION:
                return to == GameState.INITIAL_SETUP;
            case INITIAL_SETUP:
                return to == GameState.PLAYER_A_TURN;
            case PLAYER_A_TURN:
                return to == GameState.PLAYER_B_TURN || 
                       to == GameState.GAME_OVER ||
                       to == GameState.PLAYER_A_TURN;
            case PLAYER_B_TURN:
                return to == GameState.PLAYER_A_TURN || 
                       to == GameState.GAME_OVER ||
                       to == GameState.PLAYER_B_TURN;
            case GAME_OVER:
                return to == GameState.STYLE_SELECTION;
            default:
                return false;
        }
    }
    
    public GameState getCurrentState() {
        return currentState;
    }
    
    public boolean isPlayerTurn(char player) {
        if (player == 'A') {
            return currentState == GameState.PLAYER_A_TURN;
        } else if (player == 'B') {
            return currentState == GameState.PLAYER_B_TURN;
        }
        return false;
    }
    
    public void nextTurn(boolean extraTurn) {
        if (model.isGameOver()) {
            transitionTo(GameState.GAME_OVER);
            return;
        }
        
        if (!extraTurn) {
            if (currentState == GameState.PLAYER_A_TURN) {
                transitionTo(GameState.PLAYER_B_TURN);
            } else if (currentState == GameState.PLAYER_B_TURN) {
                transitionTo(GameState.PLAYER_A_TURN);
            }
        }
    }
    
    public void reset() {
        currentState = GameState.STYLE_SELECTION;
    }
    
    public boolean canMakeMove() {
        return currentState == GameState.PLAYER_A_TURN || 
               currentState == GameState.PLAYER_B_TURN;
    }
    
    public void startGame() {
        if (currentState == GameState.INITIAL_SETUP) {
            transitionTo(GameState.PLAYER_A_TURN);
        }
    }
}