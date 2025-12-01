package controller;

/**
 * Controller dedicated to managing undo functionality.
 */
public class UndoController {
    private MancalaGame model;
    private int undoCountThisTurn;
    private boolean lastActionWasUndo;
    private char currentTurnPlayer;
    private static final int MAX_UNDOS_PER_TURN = 3;
    
    public UndoController(MancalaGame model) {
        this.model = model;
        this.undoCountThisTurn = 0;
        this.lastActionWasUndo = false;
        this.currentTurnPlayer = 'A';
    }
    
    public void newTurn(char player) {
        if (player != currentTurnPlayer) {
            undoCountThisTurn = 0;
            currentTurnPlayer = player;
        }
        lastActionWasUndo = false;
    }
    
    public void onMoveMade() {
        lastActionWasUndo = false;
    }
    
    public UndoResult performUndo() {
        if (lastActionWasUndo) {
            return new UndoResult(false, "Must make a move before undoing again");
        }
        
        if (undoCountThisTurn >= MAX_UNDOS_PER_TURN) {
            return new UndoResult(false, 
                "Maximum undos (" + MAX_UNDOS_PER_TURN + ") reached this turn");
        }
        
        if (model.undo()) {
            undoCountThisTurn++;
            lastActionWasUndo = true;
            
            String message = "Undo successful. Undos used: " + 
                           undoCountThisTurn + "/" + MAX_UNDOS_PER_TURN;
            return new UndoResult(true, message);
        } else {
            return new UndoResult(false, "No move to undo");
        }
    }
    
    public boolean isUndoAvailable() {
        return !lastActionWasUndo && 
               undoCountThisTurn < MAX_UNDOS_PER_TURN &&
               model.canUndo();
    }
    
    public int getUndosRemaining() {
        return MAX_UNDOS_PER_TURN - undoCountThisTurn;
    }
    
    public int getUndosUsed() {
        return undoCountThisTurn;
    }
    
    public void reset() {
        undoCountThisTurn = 0;
        lastActionWasUndo = false;
        currentTurnPlayer = 'A';
    }
    
    public boolean wasLastActionUndo() {
        return lastActionWasUndo;
    }
    
    public static class UndoResult {
        private boolean success;
        private String message;
        
        public UndoResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
    }
}