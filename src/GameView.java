import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

class GameView extends JPanel {
    private int BOARD_START_X;
    private int BOARD_START_Y;
    private static int SQUARE_SIZE = 30;
    private GameGrid GAME_GRID;
    private boolean GAME_STARTED;  // true if the game has been started, false if not.
    private boolean GAME_LOST;  // true if lost, false if not lost.
    private boolean GAME_WON; // true if won, false if not won.
    private boolean GRID_HIDDEN;
    private Image FLAG;
    private int FLAGS_LAID;
    private int NUMBER_OF_REVEALED_NUMBERS;

    GameView() {
        newGame("expert");
        try {
            FLAG = ImageIO.read(new File("images/flag.png"));
        } catch (IOException e) {
            FLAG = null;
        }
    }

    void newGame(String difficulty) {
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
        GRID_HIDDEN = false;
        FLAGS_LAID = 0;
        NUMBER_OF_REVEALED_NUMBERS = 0;
    }

    void reset() {
        GAME_STARTED = false;  // TODO: remove
        GAME_LOST = false;
        GAME_WON = false;
        GRID_HIDDEN = false;
        FLAGS_LAID = 0;
        NUMBER_OF_REVEALED_NUMBERS = 0;
        GAME_GRID.reset();
    }

    void giveHint() {
        FLAGS_LAID = FLAGS_LAID + GAME_GRID.giveHint();
    }

    void hideGrid() {
        GRID_HIDDEN = true;
    }

    void showGrid() {
        GRID_HIDDEN = false;
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
        }
    }

    boolean getGameStatus() {
        return (GAME_LOST || GAME_WON);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setFont(new Font("Courier New", Font.PLAIN, 20));
        g.setColor(Color.BLACK);
        if (GAME_GRID.getTotalMines() - FLAGS_LAID > 9) {
            g.drawString(Integer.toString(GAME_GRID.getTotalMines() - FLAGS_LAID), 640, 147);
        } else {
            g.drawString("0" + (GAME_GRID.getTotalMines() - FLAGS_LAID), 640, 147);
        }
        if (FLAG != null) {
            g.drawImage(FLAG, 670, 125, null);
            g.drawRect(635, 125, 65, 30);
        } else {
            g.drawString("Flags", 600, 150);
        }

        if (GAME_LOST) {
            g.setColor(Color.RED);
            g.setFont(new Font("Courier New", Font.PLAIN, 60));
            g.drawString("GAME OVER", 350, 100);
        } else if (GAME_WON) {
            g.setColor(new Color(0,204,0));
            g.setFont(new Font("Courier New", Font.PLAIN, 60));
            g.drawString("YOU WIN!", 370, 100);
        } else {
            g.setFont(new Font("Courier New", Font.PLAIN, 60));
            g.drawString("Minesweeper", 310, 100);
        }

        if (GRID_HIDDEN) { return; }

        GAME_GRID.paint(g);
    }

}
