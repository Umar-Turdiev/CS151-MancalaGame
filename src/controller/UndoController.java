package controller;

import model.MancalaGame;

/**
 * Thin wrapper over the model's undo functionality.
 */
public class UndoController {
    private final MancalaGame model;

    public UndoController(MancalaGame model) {
        this.model = model;
    }

    public UndoResult performUndo() {
        if (model.undo()) {
            int remaining = model.getUndosRemainingThisTurn();
            String message = "Undo successful. Remaining undos this turn: " + remaining;
            return new UndoResult(true, message);
        }
        return new UndoResult(false, "No undo available right now.");
    }

    public boolean isUndoAvailable() {
        return model.canUndo();
    }

    public static class UndoResult {
        private final boolean success;
        private final String message;

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
