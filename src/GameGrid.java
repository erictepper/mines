import java.util.*;
import java.awt.*;
import java.lang.Math;

class GameGrid {
    private int BOARD_HEIGHT;
    private int BOARD_WIDTH;
    private GameTile[][] GAME_GRID;
    private int BOARD_START_X;
    private int BOARD_START_Y;
    private int SQUARE_SIZE;

    GameGrid(String gameSize, int boardXStart, int boardYStart, int boardSquareSize) {
        if (gameSize.equals("large")) { BOARD_HEIGHT = 16; BOARD_WIDTH = 30; }
        BOARD_START_X = boardXStart;
        BOARD_START_Y = boardYStart;
        SQUARE_SIZE = boardSquareSize;

        // Initializes the game grid.
        GAME_GRID = new GameTile[BOARD_HEIGHT][BOARD_WIDTH];
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                GAME_GRID[i][j] = new GameTile();
            }
        }

        // Generates random grid spaces to place bombs.
        ArrayList<Integer> rowValues = new ArrayList<>();
        ArrayList<Integer> columnValues = new ArrayList<>();
        Random numberGenerator = new Random();
        for (int i = 0; i < 99; i++) {
            rowValues.add(numberGenerator.nextInt(BOARD_HEIGHT));
            columnValues.add(numberGenerator.nextInt(BOARD_WIDTH));
        }

        // Fills the grid with 99 bombs in the spaces determined above.
        for (int i = 0; i < 99; i++) {
            // If the space already has a bomb, relocate the bomb.
            if (GAME_GRID[rowValues.get(i)][columnValues.get(i)].getActualStatus() == 2) {
                relocateBomb(columnValues.get(i), rowValues.get(i));
            }
            GAME_GRID[rowValues.get(i)][columnValues.get(i)].setTileType(2);
        }

        // Updates the number of adjacent bombs for each tile.
        updateAdjacentBombs();
    }

    void moveBombs(int xPosition, int yPosition) {
        Random numberGenerator = new Random();
        for (int iterator1 = Math.max(0, yPosition-1); iterator1 < Math.min(BOARD_HEIGHT, yPosition+2); iterator1++) {
            for (int iterator2 = Math.max(0, xPosition-1); iterator2 < Math.min(BOARD_WIDTH, xPosition+2);
                 iterator2++) {
                // If the space already has a bomb, relocate the bomb.
                if (GAME_GRID[iterator1][iterator2].getActualStatus() == 2) {
                    GAME_GRID[iterator1][iterator2].setTileType(1);
                    int j = numberGenerator.nextInt(BOARD_HEIGHT);
                    int k = numberGenerator.nextInt(BOARD_WIDTH);
                    while (GAME_GRID[j][k].getActualStatus() == 2 ||
                            (j >= Math.max(0, yPosition-1) && j < Math.min(BOARD_HEIGHT, yPosition+2) &&
                            k >= Math.max(0, xPosition-1) && k < Math.min(BOARD_WIDTH, xPosition+2))) {
                        j = numberGenerator.nextInt(BOARD_HEIGHT);
                        k = numberGenerator.nextInt(BOARD_WIDTH);
                    }
                    GAME_GRID[j][k].setTileType(2);
                }
            }
        }

        // Updates the number of adjacent bombs for each tile.
        updateAdjacentBombs();
    }

    // Find a new, random, empty space and place the bomb there.
    private void relocateBomb(int xPosition, int yPosition) {
        Random numberGenerator = new Random();
        GAME_GRID[yPosition][xPosition].setTileType(1);
        int j = numberGenerator.nextInt(BOARD_HEIGHT);
        int k = numberGenerator.nextInt(BOARD_WIDTH);
        while (GAME_GRID[j][k].getActualStatus() == 2) {
            j = numberGenerator.nextInt(BOARD_HEIGHT);
            k = numberGenerator.nextInt(BOARD_WIDTH);
        }
        GAME_GRID[j][k].setTileType(2);
    }

    private void updateAdjacentBombs() {
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                int totalBombs = 0;
                for (int iterator1 = Math.max(0, i-1); iterator1 < Math.min(BOARD_HEIGHT, i+2); iterator1++) {
                    for (int iterator2 = Math.max(0, j-1); iterator2 < Math.min(BOARD_WIDTH, j+2); iterator2++) {
                        totalBombs = (GAME_GRID[iterator1][iterator2].getActualStatus() == 2) ? totalBombs + 1 :
                                totalBombs;
                    }
                }
                GAME_GRID[i][j].setNumberOfAdjacentBombs(totalBombs);
            }
        }
    }

    // Returns -1 if revealed space is a bomb, and returns the number of bombs otherwise otherwise
    int reveal(int xPosition, int yPosition) {
        if (GAME_GRID[yPosition][xPosition].getNumberOfAdjacentBombs() == 0 &&
                GAME_GRID[yPosition][xPosition].getDisplayStatus() == 0) {
            int totalRevealed = GAME_GRID[yPosition][xPosition].reveal();
            for (int i = Math.max(0, yPosition-1); i < Math.min(BOARD_HEIGHT, yPosition+2); i++) {
                for (int j = Math.max(0, xPosition-1); j < Math.min(BOARD_WIDTH, xPosition+2); j++) {
                    if ((i != yPosition) || (j != xPosition)) { totalRevealed = totalRevealed + reveal(j, i); }
                }
            }
            return totalRevealed;
        }
        else {
            int revealedStatus = GAME_GRID[yPosition][xPosition].reveal();
            if (revealedStatus == 2) { return -1; }
            else return revealedStatus;
        }
    }

    // Returns true if flagged, returns false if un-flagged.
    int flag(int xPosition, int yPosition) {
        return GAME_GRID[yPosition][xPosition].flag();
    }

    void paint(Graphics g) {
        g.setFont(new Font("Courier New", Font.PLAIN, 20));

        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                switch (GAME_GRID[i][j].getDisplayStatus()) {
                    case 0:  // hidden tile
                        g.setColor(Color.LIGHT_GRAY);
                        g.fillRect(BOARD_START_X + (j * SQUARE_SIZE),
                                BOARD_START_Y + (i * SQUARE_SIZE),
                                SQUARE_SIZE, SQUARE_SIZE);
                        break;
                    case 1:  // number tile
                        g.setColor(Color.WHITE);
                        g.fillRect(BOARD_START_X + (j * SQUARE_SIZE),
                                BOARD_START_Y + (i * SQUARE_SIZE),
                                SQUARE_SIZE, SQUARE_SIZE);
                        g.setColor(Color.BLACK);
                        g.drawString(GAME_GRID[i][j].getLabel(), BOARD_START_X + (j * SQUARE_SIZE) + 7,
                                BOARD_START_Y + ((i + 1) * SQUARE_SIZE) - 7);
                        break;
                    case 2:  // bomb tile
                        g.setColor(Color.RED);
                        g.fillRect(BOARD_START_X + (j * SQUARE_SIZE),
                                BOARD_START_Y + (i * SQUARE_SIZE),
                                SQUARE_SIZE, SQUARE_SIZE);
                        g.setColor(Color.WHITE);
                        g.drawString(GAME_GRID[i][j].getLabel(), BOARD_START_X + (j * SQUARE_SIZE) + 7,
                                BOARD_START_Y + ((i + 1) * SQUARE_SIZE) - 7);
                        break;
                    case 3: // flag tile
                        g.setColor(Color.BLACK);
                        g.fillRect(BOARD_START_X + (j * SQUARE_SIZE),
                                BOARD_START_Y + (i * SQUARE_SIZE),
                                SQUARE_SIZE, SQUARE_SIZE);
                        g.setColor(Color.WHITE);
                        g.drawString(GAME_GRID[i][j].getLabel(), BOARD_START_X + (j * SQUARE_SIZE) + 7,
                                BOARD_START_Y + ((i + 1) * SQUARE_SIZE) - 7);
                        break;
                }
            }
        }
    }
}
