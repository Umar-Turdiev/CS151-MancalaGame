/*
 * Assignment: CS151 Finals Project - Mancala Game
 * Author: Umar Lin
 * Date: 2025-12-01
 */

package view;

import model.MancalaGame;
import model.Player;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

/**
 * Swing-based view for the Mancala game.
 */
public class MancalaView extends JFrame {
    private static final String CARD_SELECTION = "selection";
    private static final String CARD_GAME = "game";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel rootPanel = new JPanel(cardLayout);

    private final JPanel selectionPanel = new JPanel(new BorderLayout(10, 10));
    private final JPanel gamePanel = new JPanel(new BorderLayout(10, 10));

    private final JLabel currentPlayerLabel = new JLabel("Current Player: A");
    private final JLabel styleLabel = new JLabel("Style: Classic");
    private final JLabel statusLabel = new JLabel("Welcome to Mancala!");
    private final JButton undoButton = new JButton("Undo");

    private final JSpinner stonesSpinner = new JSpinner(new SpinnerNumberModel(3, 3, 4, 1));
    private final JButton startButton = new JButton("Start Game");
    private final JButton classicStyleButton = new JButton("Classic Style");
    private final JButton modernStyleButton = new JButton("Modern Style");

    private BoardPanel boardPanel;
    private StyleStrategy currentStyle = new ClassicBoardStyle();

    private ActionListener undoButtonListener;
    private ActionListener styleSelectionListener;
    private ActionListener initialStonesListener;

    private int[] boardSnapshot = new int[MancalaGame.TOTAL_POCKETS];
    private Player currentPlayer = Player.PLAYER_A;
    private boolean undoEnabled;
    private int initialStonesInput = 3;

    /**
     * Builds the full Mancala view.
     */
    public MancalaView() {
        super("Mancala");
        initializeFrame();
        buildSelectionPanel();
        buildGamePanel();
        rootPanel.add(selectionPanel, CARD_SELECTION);
        rootPanel.add(gamePanel, CARD_GAME);
        setContentPane(rootPanel);
    }

    /**
     * Configures frame defaults such as size and close operation.
     */
    private void initializeFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(900, 600));
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Creates the style/initial setup card.
     */
    private void buildSelectionPanel() {
        selectionPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextArea description = new JTextArea(
                "Welcome! Choose a board style, set the initial stones per pit (3 or 4),\n" +
                        "and press Start Game to begin.");
        description.setEditable(false);
        description.setFocusable(false);
        description.setOpaque(false);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);

        JPanel styleButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        classicStyleButton.setActionCommand("CLASSIC");
        modernStyleButton.setActionCommand("MODERN");
        styleButtonPanel.add(classicStyleButton);
        styleButtonPanel.add(modernStyleButton);

        JPanel setupPanel = new JPanel();
        setupPanel.setLayout(new BoxLayout(setupPanel, BoxLayout.Y_AXIS));
        setupPanel.setBorder(BorderFactory.createTitledBorder("Initial Setup"));
        JLabel stonesLabel = new JLabel("Stones per pit (3 or 4):");
        stonesLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        stonesSpinner.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        startButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        setupPanel.add(stonesLabel);
        setupPanel.add(Box.createVerticalStrut(8));
        setupPanel.add(stonesSpinner);
        setupPanel.add(Box.createVerticalStrut(12));
        setupPanel.add(startButton);

        selectionPanel.add(description, BorderLayout.NORTH);
        selectionPanel.add(styleButtonPanel, BorderLayout.CENTER);
        selectionPanel.add(setupPanel, BorderLayout.SOUTH);

        classicStyleButton.addActionListener(this::dispatchStyleSelection);
        modernStyleButton.addActionListener(this::dispatchStyleSelection);
        startButton.addActionListener(this::dispatchInitialStones);
    }

    /**
     * Creates the main game board card.
     */
    private void buildGamePanel() {
        boardPanel = new BoardPanel(currentStyle);
        boardPanel.updateBoard(boardSnapshot);

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        currentPlayerLabel.setFont(currentStyle.getLabelFont());
        styleLabel.setFont(currentStyle.getLabelFont());
        topBar.add(currentPlayerLabel);
        topBar.add(styleLabel);

        JPanel bottomBar = new JPanel(new BorderLayout());
        statusLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        bottomBar.add(statusLabel, BorderLayout.CENTER);
        bottomBar.add(undoButton, BorderLayout.EAST);

        undoButton.addActionListener(this::dispatchUndo);

        gamePanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        gamePanel.add(topBar, BorderLayout.NORTH);
        gamePanel.add(boardPanel, BorderLayout.CENTER);
        gamePanel.add(bottomBar, BorderLayout.SOUTH);
    }

    /* Listener Registration */

    /**
     * Wires controller callback for pit clicks.
     *
     * @param listener pit action listener
     */
    public void addPitClickListener(ActionListener listener) {
        boardPanel.setBoardListener(listener);
    }

    /**
     * Registers the controller undo handler.
     *
     * @param listener undo listener
     */
    public void addUndoButtonListener(ActionListener listener) {
        this.undoButtonListener = listener;
    }

    /**
     * Registers the controller style handler.
     *
     * @param listener style listener
     */
    public void addStyleSelectionListener(ActionListener listener) {
        this.styleSelectionListener = listener;
    }

    /**
     * Registers the controller handler for starting games.
     *
     * @param listener initial stones listener
     */
    public void addInitialStonesListener(ActionListener listener) {
        this.initialStonesListener = listener;
    }

    /* Controller Callbacks */

    /**
     * Displays a status message at the bottom of the window and logs it for
     * debugging.
     *
     * @param message text to show
     */
    public void showMessage(String message) {
        statusLabel.setText(message);
        System.out.println("VIEW MESSAGE: " + message);
    }

    /**
     * Updates the board with the latest stone counts.
     *
     * @param newBoardState Mancala board array
     */
    public void updateBoard(int[] newBoardState) {
        if (newBoardState != null) {
            boardSnapshot = Arrays.copyOf(newBoardState, newBoardState.length);
        }
        if (boardPanel != null) {
            boardPanel.updateBoard(boardSnapshot);
        }
    }

    /**
     * Updates the current player label.
     *
     * @param player player currently taking a turn
     */
    public void updateCurrentPlayer(Player player) {
        if (player != null) {
            currentPlayer = player;
        }
        currentPlayerLabel.setText("Current Player: " + (currentPlayer == Player.PLAYER_A ? "A" : "B"));
    }

    /**
     * Enables or disables the undo button.
     *
     * @param enabled whether undo is currently permitted
     */
    public void updateUndoButton(boolean enabled) {
        undoEnabled = enabled;
        undoButton.setEnabled(enabled);
    }

    /**
     * Applies the selected style to all subcompnents.
     *
     * @param style style strategy
     */
    public void setBoardStyle(StyleStrategy style) {
        this.currentStyle = style;
        styleLabel.setText("Style: " + style.getName());
        boardPanel.setStyle(style);
        currentPlayerLabel.setFont(style.getLabelFont());
    }

    /**
     * Ensures the embedded board also repaints whenever the frame does.
     */
    @Override
    public void repaint() {
        super.repaint();
        if (boardPanel != null) {
            boardPanel.repaint();
        }
    }

    /**
     * @return the last user-provided stones-per-pit value.
     */
    public int getInitialStonesInput() {
        return initialStonesInput;
    }

    /**
     * Updates the spinner and cached initial stones value.
     *
     * @param stones initial stones per pit
     */
    public void setInitialStonesInput(int stones) {
        this.initialStonesInput = stones;
        stonesSpinner.setValue(stones);
    }

    /**
     * Switches to the game card and ensures the window is visible.
     */
    public void startGame() {
        cardLayout.show(rootPanel, CARD_GAME);
        if (!isVisible()) {
            setVisible(true);
        }
        SwingUtilities.invokeLater(this::repaint);
    }

    /**
     * Shows the selection/setup card.
     */
    public void showStyleSelection() {
        cardLayout.show(rootPanel, CARD_SELECTION);
        if (!isVisible()) {
            setVisible(true);
        }
    }

    /**
     * Resets UI fields and shows the selection card.
     */
    public void resetToStyleSelection() {
        showStyleSelection();
        updateBoard(new int[MancalaGame.TOTAL_POCKETS]);
        updateUndoButton(false);
        setInitialStonesInput(3);
        statusLabel.setText("Game reset. Choose style and stones to play again.");
    }

    /* Internal dispatcher Helpers */

    /**
     * Forwards style button clicks to the registered listener.
     */
    private void dispatchStyleSelection(ActionEvent event) {
        if (styleSelectionListener != null) {
            styleSelectionListener.actionPerformed(event);
        }
    }

    /**
     * Notifies the controller to start a new game with the chosen stones value.
     */
    private void dispatchInitialStones(ActionEvent event) {
        initialStonesInput = ((Number) stonesSpinner.getValue()).intValue();
        if (initialStonesListener != null) {
            initialStonesListener.actionPerformed(
                    new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "INITIALIZE"));
        }
    }

    /**
     * Forwards undo button clicks to the controller.
     */
    private void dispatchUndo(ActionEvent event) {
        if (undoButtonListener != null) {
            undoButtonListener.actionPerformed(event);
        }
    }

    /* Accessors for Tests */

    /**
     * @return currently active style.
     */
    public StyleStrategy getCurrentStyle() {
        return currentStyle;
    }

    /**
     * @return a copy of the cached board state.
     */
    public int[] getBoardStateSnapshot() {
        return Arrays.copyOf(boardSnapshot, boardSnapshot.length);
    }

    /**
     * @return cached current player value.
     */
    public Player getCurrentPlayerSnapshot() {
        return currentPlayer;
    }

    /**
     * @return whether undo UI is currently enabled.
     */
    public boolean isUndoEnabled() {
        return undoEnabled;
    }
}
