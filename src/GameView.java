import javax.swing.*;
import java.awt.*;

class GameView extends JPanel {
    private int BOARD_START_X;
    private int BOARD_START_Y;
    private static int SQUARE_SIZE = 30;
    private GameGrid GAME_GRID;
    private boolean GAME_STARTED;  // true if the game has been started, false if not.
    private boolean GAME_LOST;  // true if lost, false if not lost.
    private boolean GAME_WON; // true if won, false if not won.
    private int FLAGS_LAID;
    private int NUMBER_OF_REVEALED_NUMBERS;

    GameView() {
        newGame("expert");
    }

    private void newGame(String difficulty) {
        switch (difficulty) {
            case "beginner":
                BOARD_START_X = 365;
                BOARD_START_Y = 300;
                GAME_GRID = new GameGrid(difficulty, BOARD_START_X, BOARD_START_Y, SQUARE_SIZE);
                break;

            case "intermediate":
                BOARD_START_X = 260;
                BOARD_START_Y = 200;
                GAME_GRID = new GameGrid(difficulty, BOARD_START_X, BOARD_START_Y, SQUARE_SIZE);
                break;

            case "expert":
                BOARD_START_X = 50;
                BOARD_START_Y = 200;
                GAME_GRID = new GameGrid(difficulty, BOARD_START_X, BOARD_START_Y, SQUARE_SIZE);
                break;
        }
        GAME_STARTED = false;
        GAME_LOST = false;
        GAME_WON = false;
        FLAGS_LAID = 0;
        NUMBER_OF_REVEALED_NUMBERS = 0;
        repaint();
    }

    void reset() {
        GAME_STARTED = false;
        GAME_LOST = false;
        GAME_WON = false;
        FLAGS_LAID = 0;
        NUMBER_OF_REVEALED_NUMBERS = 0;
        GAME_GRID.reset();
        repaint();
    }

    void mousePressed(int xPosition, int yPosition, int button) {
        if (xPosition <= BOARD_START_X || xPosition >= BOARD_START_X + (GAME_GRID.getBoardWidth()*SQUARE_SIZE)) {
            return;
        }
        if (yPosition <= BOARD_START_Y || yPosition >= BOARD_START_Y + (GAME_GRID.getBoardHeight()*SQUARE_SIZE)) {
            return;
        }
        int gridIndexXPosition = (xPosition - BOARD_START_X) / SQUARE_SIZE;
        int gridIndexYPosition = (yPosition - BOARD_START_Y) / SQUARE_SIZE;

        if (!GAME_STARTED && button == 1) {
            GAME_GRID.moveMines(gridIndexXPosition, gridIndexYPosition);
            GAME_STARTED = true;
        }
        if (button == 3) {
            FLAGS_LAID = FLAGS_LAID + GAME_GRID.flag(gridIndexXPosition, gridIndexYPosition);
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
                GAME_WON = NUMBER_OF_REVEALED_NUMBERS >= GAME_GRID.getBoardHeight() * GAME_GRID.getBoardWidth() -
                        GAME_GRID.getTotalMines();
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
        g.drawString("Flags remaining: " + (GAME_GRID.getTotalMines() - FLAGS_LAID), 400, 150);

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
