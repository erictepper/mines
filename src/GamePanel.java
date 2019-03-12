import javax.swing.*;
import java.awt.*;

class GamePanel extends JPanel {
    private int BOARD_START_X;
    private int BOARD_START_Y;
    private int SQUARE_SIZE;
    private GameGrid GAME_GRID;
    private boolean GAME_STARTED;  // true if the game has been started, false if not.
    private boolean GAME_LOST;  // true if lost, false if not lost.
    private boolean GAME_WON; // true if won, false if not won.
    private int NUMBER_OF_FLAGS;
    private int NUMBER_OF_REVEALED_NUMBERS;

    GamePanel(int boardXStart, int boardYStart, int boardSquareSize, GameGrid gameGrid) {
        BOARD_START_X = boardXStart;
        BOARD_START_Y = boardYStart;
        SQUARE_SIZE = boardSquareSize;
        GAME_GRID = gameGrid;
        GAME_STARTED = false;
        GAME_LOST = false;
        GAME_WON = false;
        NUMBER_OF_FLAGS = 0;
        NUMBER_OF_REVEALED_NUMBERS = 0;
    }

    void mouseClicked(int xPosition, int yPosition, int button) {
        if (xPosition < BOARD_START_X || xPosition > BOARD_START_X + (30*SQUARE_SIZE)) { return; }
        if (yPosition < BOARD_START_Y || yPosition > BOARD_START_Y + (16*SQUARE_SIZE)) { return; }
        int gridXPosition = (xPosition - BOARD_START_X) / SQUARE_SIZE;
        int gridYPosition = (yPosition - BOARD_START_Y) / SQUARE_SIZE;

        if (!GAME_STARTED && button == 1) {
            GAME_GRID.moveBombs(gridXPosition, gridYPosition);
            GAME_STARTED = true;
        }
        if (button == 3) {
            NUMBER_OF_FLAGS = NUMBER_OF_FLAGS + GAME_GRID.flag(gridXPosition, gridYPosition);
            repaint();
        }
        else if (button == 1) {
            int typeRevealed = GAME_GRID.reveal(gridXPosition, gridYPosition);
            if (typeRevealed == -1) {
                GAME_LOST = true;
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
            Font lostFont = new Font("Courier New", Font.BOLD, 60);
            g.setFont(lostFont);
            g.drawString("GAME OVER", 350, 100);
        }

        else if (GAME_WON) {
            g.setColor(Color.GREEN);
            Font lostFont = new Font("Courier New", Font.BOLD, 60);
            g.setFont(lostFont);
            g.drawString("GAME WON!", 350, 100);
        }
    }

}
