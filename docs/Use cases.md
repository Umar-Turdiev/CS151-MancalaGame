# Use Cases

## Use Case: Launch App & Choose Style/Stones

| Step | User's Action                               | System's Response                                                                                       |
| ---- | ------------------------------------------- | ------------------------------------------------------------------------------------------------------- |
| 1    | Launches the Mancala application.           |                                                                                                         |
| 2    |                                             | Shows the style-selection screen with Classic/Modern buttons, stones-per-pit spinner, and Start button. |
| 3    | Clicks a style button (Classic or Modern).  |                                                                                                         |
| 4    |                                             | Highlights the chosen button to indicate the pending style.                                             |
| 5    | (Optional) adjusts spinner to 3 or 4.       |                                                                                                         |
| 6    |                                             | Updates the displayed spinner value.                                                                    |
| 7    | Presses **Start Game**.                     |                                                                                                         |
| 8    |                                             | Validates spinner (must be 3 or 4), initializes the Mancala model, builds board UI, switches to game view. |
| 9    |                                             | Displays board with both rows filled, status message "Player A's turn. Select a pit to begin."          |

---

## Use Case: Take a Turn (Select Pit)

| Step | User's Action                               | System's Response                                                                                                     |
| ---- | ------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------- |
| 1    | Clicks one of their pits on the board.      |                                                                                                                        |
| 2    |                                             | Checks that the selected pit belongs to the current player and contains stones; if invalid, shows an error.            |
| 3    | (If invalid) chooses another pit.           |                                                                                                                        |
| 4    |                                             | Saves the current state, redistributes stones from the chosen pit, applies capture/free-turn rules, updates board data. |
| 5    |                                             | Updates the view with new stone counts, current player label, and undo availability.                                   |
| 6    |                                             | Shows a status message such as "Free turn!" or "Move completed. Player B's turn."                                    |
| 7    |                                             | If the move ends the game, announces final scores and prompts to replay or exit.                                       |

**Variation A (Invalid Pit)**
A.1 User clicks an opponent's pit or an empty pit.  
A.2 System displays "Invalid move! Select a pit on your side with stones." and waits for another selection.

---

## Use Case: Undo Last Move

| Step | User's Action                                                 | System's Response                                                                                                                   |
| ---- | ------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------- |
| 1    | After making a move (before opponent acts), presses **Undo**. |                                                                                                                                     |
| 2    |                                                               | Checks undo history, ensures the player has remaining undos and hasn't already undone without a new move; restores the prior board state if allowed. |
| 3    |                                                               | Updates the board, current player label, and undo counter in the UI.                                                                |
| 4    |                                                               | Displays a confirmation message such as "Undo successful. Remaining undos this turn: X."                                           |

**Variation A (Undo Not Allowed)**
A.1 User presses Undo when no snapshots exist, after exceeding 3 undos, or twice in a row without a new move.  
A.2 System shows "No undo available right now." and leaves the board unchanged.

---

## Use Case: Choose Board Style Before Game

| Step | User's Action                                       | System's Response                                                                                           |
| ---- | --------------------------------------------------- | ----------------------------------------------------------------------------------------------------------- |
| 1    | On the selection screen, clicks Classic or Modern.  |                                                                                                             |
| 2    |                                                     | Updates the style indicator to reflect the latest selection.                                                |
| 3    | Presses **Start Game** after finalizing style/stones. |                                                                                                         |
| 4    |                                                     | Constructs the board using the chosen style and transitions into gameplay.                                  |

**Variation A (Style Changed Before Start)**
User may switch between styles multiple times before pressing Start; the last selection is used when the board renders.

---

## Use Case: Game Over & Replay

| Step | User's Action                         | System's Response                                                                                                             |
| ---- | ------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------- |
| 1    |                                       | Detects that all pits on one side are empty, collects remaining stones, and ends the game.                                     |
| 2    |                                       | Calculates scores, determines the winner or tie, and displays the outcome message.                                            |
| 3    |                                       | Shows a dialog asking "Would you like to play again?" with Yes/No options.                                                   |
| 4    | Clicks **Yes**.                       |                                                                                                                               |
| 5    |                                       | Resets controllers and view to the style-selection screen so players can start a new game.                                    |
| 6    | Clicks **No**.                        |                                                                                                                               |
| 7    |                                       | Closes the application immediately.                                                                                           |

**Variation A (Replay Declined)**
Handled by Steps 6–7: choosing "No" exits without resetting.
