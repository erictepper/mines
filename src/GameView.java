import javax.swing.*;
import java.awt.*;

class GameView extends JPanel {
    private int BOARD_START_X;
    private static int BOARD_START_Y = 200;
    private static int SQUARE_SIZE = 30;
    private GameGrid GAME_GRID;
    private boolean GAME_STARTED;  // true if the game has been started, false if not.
    private boolean GAME_LOST;  // true if lost, false if not lost.
    private boolean GAME_WON; // true if won, false if not won.
    private int NUMBER_OF_FLAGS;
    private int NUMBER_OF_REVEALED_NUMBERS;

    GameView() {
        BOARD_START_X = 50;
        GAME_GRID = new GameGrid("expert", BOARD_START_X, BOARD_START_Y, SQUARE_SIZE);
        GAME_STARTED = false;
        GAME_LOST = false;
        GAME_WON = false;
        NUMBER_OF_FLAGS = 0;
        NUMBER_OF_REVEALED_NUMBERS = 0;
    }

    private void reset(String difficulty) {
        switch (difficulty) {
            case "beginner":
                BOARD_START_X = 365;
                GAME_GRID = new GameGrid(difficulty, BOARD_START_X, BOARD_START_Y, SQUARE_SIZE);
                break;

            case "intermediate":
                BOARD_START_X = 260;
                GAME_GRID = new GameGrid(difficulty, BOARD_START_X, BOARD_START_Y, SQUARE_SIZE);
                break;

            case "expert":
                BOARD_START_X = 50;
                GAME_GRID = new GameGrid(difficulty, BOARD_START_X, BOARD_START_Y, SQUARE_SIZE);
                break;
        }
        GAME_STARTED = false;
        GAME_LOST = false;
        GAME_WON = false;
        NUMBER_OF_FLAGS = 0;
        NUMBER_OF_REVEALED_NUMBERS = 0;
    }

    void mousePressed(int xPosition, int yPosition, int button) {
        if (xPosition <= BOARD_START_X || xPosition >= BOARD_START_X + (30*SQUARE_SIZE)) { return; }
        if (yPosition <= BOARD_START_Y || yPosition >= BOARD_START_Y + (16*SQUARE_SIZE)) { return; }
        int gridIndexXPosition = (xPosition - BOARD_START_X) / SQUARE_SIZE;
        int gridIndexYPosition = (yPosition - BOARD_START_Y) / SQUARE_SIZE;

        if (!GAME_STARTED && button == 1) {
            GAME_GRID.moveMines(gridIndexXPosition, gridIndexYPosition);
            GAME_STARTED = true;
        }
        if (button == 3) {
            NUMBER_OF_FLAGS = NUMBER_OF_FLAGS + GAME_GRID.flag(gridIndexXPosition, gridIndexYPosition);
            repaint();
        }
        else if (button == 1) {
            int typeRevealed = GAME_GRID.reveal(gridIndexXPosition, gridIndexYPosition);
            if (typeRevealed == -1) {
                GAME_LOST = true;
                GAME_GRID.revealAllBombs();
            }
            else {
                NUMBER_OF_REVEALED_NUMBERS = NUMBER_OF_REVEALED_NUMBERS + typeRevealed;
                GAME_WON = NUMBER_OF_REVEALED_NUMBERS >= ((16*30) - 99);
            }
            repaint();
        }
    }

    boolean getGameStatus() {
        return (GAME_LOST || GAME_WON);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        GAME_GRID.paint(g);

        g.setColor(Color.BLACK);
        g.drawString("Flags remaining: " + (99 - NUMBER_OF_FLAGS), 400, 150);

        if (GAME_LOST) {
            g.setColor(Color.RED);
            g.setFont(new Font("Courier New", Font.BOLD, 60));
            g.drawString("GAME OVER", 350, 100);
        }

        else if (GAME_WON) {
            g.setColor(Color.GREEN);
            g.setFont(new Font("Courier New", Font.BOLD, 60));
            g.drawString("GAME WON!", 350, 100);
        }
    }

}
