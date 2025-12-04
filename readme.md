# Mancala Game (CS151 Team Project)

<img width="1012" height="712" alt="Screenshot 2025-12-01 at 6 03 15 PM" src="https://github.com/user-attachments/assets/215c30b0-f9fd-4a61-90bb-c5f334bb566e" />

A Java Swing implementation of the classic two-player Mancala board game, following the **MVC** and **Strategy** design patterns.

## Features

- Two-player gameplay using one mouse  
- Undo function (up to 3 per turn)  
- Selectable board styles (via Strategy pattern)  
- Follows official Mancala rules

## How to Play

1. Each player starts with 3–4 stones per pit.  
2. Pick a pit on your side to start; distribute stones counter-clockwise.  
3. Skip your opponent’s Mancala, drop one stone per pit.  
4. If your last stone lands in your Mancala, you get another turn.  
5. If your last stone lands in an empty pit on your side, capture opposite stones.  
6. Game ends when one side is empty. Player with the most stones in their Mancala wins.

## Structure

- `Model` — game state and logic  
- `View` — GUI board and pit rendering  
- `Controller` — handles user input and updates model  
- `Strategy` — defines visual style themes for the board  
