package controller;

import java.awt.event.*;

/**
 * Controller for individual pit components.
 * Handles mouse interactions with specific pits on the board.
 */
public class PitController extends MouseAdapter {
    private int pitIndex;
    private MancalaGame model;
    private PitView pitView;
    private ActionListener moveListener;
    
    public PitController(int pitIndex, MancalaGame model, PitView pitView) {
        this.pitIndex = pitIndex;
        this.model = model;
        this.pitView = pitView;
    }
    
    public void setMoveListener(ActionListener listener) {
        this.moveListener = listener;
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        if (pitIndex == 6 || pitIndex == 13) {
            return;
        }
        
        if (!model.isValidMove(pitIndex)) {
            pitView.showInvalidClickFeedback();
            return;
        }
        
        if (moveListener != null) {
            ActionEvent event = new ActionEvent(
                this, 
                ActionEvent.ACTION_PERFORMED, 
                String.valueOf(pitIndex)
            );
            moveListener.actionPerformed(event);
        }
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
        if (pitIndex != 6 && pitIndex != 13 && model.isValidMove(pitIndex)) {
            pitView.showHoverEffect(true);
        }
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
        pitView.showHoverEffect(false);
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        if (pitIndex != 6 && pitIndex != 13 && model.isValidMove(pitIndex)) {
            pitView.showPressedEffect(true);
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        pitView.showPressedEffect(false);
    }
    
    public int getPitIndex() {
        return pitIndex;
    }
}