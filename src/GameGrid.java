import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.*;
import java.awt.*;
import java.lang.Math;
import java.io.File;

class GameGrid {
    private int BOARD_HEIGHT;
    private int BOARD_WIDTH;
    private int NUMBER_OF_MINES;
    private GameTile[][] GAME_GRID;
    private int BOARD_START_X;
    private int BOARD_START_Y;
    private int SQUARE_SIZE;
    private Image MINE;
    private Image FLAG;
    private Image FALSE_FLAG;

    // gameSize should be one of "beginner", "intermediate", or "expert".
    GameGrid(String gameSize, int boardXStart, int boardYStart, int boardSquareSize) {
        NUMBER_OF_MINES = 10;
        switch (gameSize) {
            case "beginner":
                BOARD_HEIGHT = 9;
                BOARD_WIDTH = 9;
                NUMBER_OF_MINES = 10;
                break;

            case "intermediate":
                BOARD_HEIGHT = 16;
                BOARD_WIDTH = 16;
                NUMBER_OF_MINES = 40;
                break;

            case "expert":
                BOARD_HEIGHT = 16;
                BOARD_WIDTH = 30;
                NUMBER_OF_MINES = 99;
                break;
        }

        try {
            MINE = ImageIO.read(new File("images/mine.png"));
            FLAG = ImageIO.read(new File("images/flag.png"));
            FALSE_FLAG = ImageIO.read(new File("images/false_flag.png"));
        } catch (IOException e) {
            MINE = null;
            FLAG = null;
            FALSE_FLAG = null;
        }

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

        // Generates random grid spaces to place mines.
        ArrayList<Integer> rowValues = new ArrayList<>();
        ArrayList<Integer> columnValues = new ArrayList<>();
        Random numberGenerator = new Random();
        for (int i = 0; i < NUMBER_OF_MINES; i++) {
            rowValues.add(numberGenerator.nextInt(BOARD_HEIGHT));
            columnValues.add(numberGenerator.nextInt(BOARD_WIDTH));
        }

        // Fills the grid with 99 mines in the spaces determined above.
        for (int i = 0; i < NUMBER_OF_MINES; i++) {
            // If the space already has a mine, relocate the mine.
            if (GAME_GRID[rowValues.get(i)][columnValues.get(i)].getActualStatus() == 2) {
                relocateMine(columnValues.get(i), rowValues.get(i));
            }
            GAME_GRID[rowValues.get(i)][columnValues.get(i)].setTileType(2);
        }

        // Updates the number of adjacent mines for each tile.
        updateAdjacentMines();
    }

    // Moves the mine at position (xPosition, yPosition) and also moves any directly adjacent mines.
    void moveMines(int xPosition, int yPosition) {
        Random numberGenerator = new Random();
        for (int adjacentYPosition = Math.max(0, yPosition-1); adjacentYPosition < Math.min(BOARD_HEIGHT, yPosition+2);
             adjacentYPosition++) {
            for (int adjacentXPosition = Math.max(0, xPosition-1);
                 adjacentXPosition < Math.min(BOARD_WIDTH, xPosition+2); adjacentXPosition++) {
                // If the adjacent position already has a mine, relocate the mine to some non-adjacent, free space.
                // Note that extra conditions have been added to the while loop not present in relocateMine(int, int).
                if (GAME_GRID[adjacentYPosition][adjacentXPosition].getActualStatus() == 2) {
                    GAME_GRID[adjacentYPosition][adjacentXPosition].setTileType(1);
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

        updateAdjacentMines();
    }

    // Find a new, random, empty space and place the mine there.
    private void relocateMine(int xPosition, int yPosition) {
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

    // Counts and stores the number of adjacent mines for each GameTile.
    private void updateAdjacentMines() {
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                int totalMines = 0;
                for (int iterator1 = Math.max(0, i-1); iterator1 < Math.min(BOARD_HEIGHT, i+2); iterator1++) {
                    for (int iterator2 = Math.max(0, j-1); iterator2 < Math.min(BOARD_WIDTH, j+2); iterator2++) {
                        totalMines = (GAME_GRID[iterator1][iterator2].getActualStatus() == 2) ? totalMines + 1 :
                                totalMines;
                    }
                }
                GAME_GRID[i][j].setNumberOfAdjacentMines(totalMines);
            }
        }
    }

    // Reveals position (xPosition, yPosition) if it has an adjacent mine.
    // If this position has no adjacent mines, it reveals the space and recursively reveals adjacent spaces.
    // If the space has already been revealed, it does nothing and returns 0.
    // Returns -1 if revealed space is a mine, and returns the number of total revealed spaces otherwise.
    int reveal(int xPosition, int yPosition) {
        if (GAME_GRID[yPosition][xPosition].getNumberOfAdjacentMines() == 0 &&
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

    // Gives a player a hint. If there exists a false flag, mark it as a false flag. If no false flags exist,
    // mark a flag in a place it would be the most useful (e.g. next to a tile where only one flag is left).
    int giveHint() {
        // Firstly, searches the grid to see if there are any tiles falsely flagged. If one exists, mark it as a false
        // flag and return.
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                GameTile current_game_tile = GAME_GRID[y][x];
                if (current_game_tile.getActualStatus() == 1 && current_game_tile.getDisplayStatus() == 3) {
                    current_game_tile.markAsFalseFlag();
                    return -1;
                }
            }
        }

        // Searches for the space with the smallest difference between the number of adjacent flags and the number
        // of adjacent mines. This space must have already been revealed.
        int lowest_mine_flag_difference_count = 9;
        int hint_centre_x_index = 0, hint_centre_y_index = 0;
        search: {
            for (int y = 0; y < BOARD_HEIGHT; y++) {
                for (int x = 0; x < BOARD_WIDTH; x++) {
                    if (GAME_GRID[y][x].getDisplayStatus() != 1) { continue; }

                    int number_of_adjacent_flags = 0;

                    // Counts the number of adjacent flags for GAME_TILE[y][x].
                    for (int i = Math.max(0, y - 1); i < Math.min(BOARD_HEIGHT, y + 2); i++) {
                        for (int j = Math.max(0, x - 1); j < Math.min(BOARD_WIDTH, x + 2); j++) {
                            if (GAME_GRID[i][j].getDisplayStatus() == 3) {
                                number_of_adjacent_flags++;
                            }
                        }
                    }

                    int diff = GAME_GRID[y][x].getNumberOfAdjacentMines() - number_of_adjacent_flags;
                    if (diff > 0 && diff < lowest_mine_flag_difference_count) {
                        lowest_mine_flag_difference_count = diff;
                        hint_centre_y_index = y;
                        hint_centre_x_index = x;
                    }
                    if (diff == 1) {
                        break search;
                    }
                }
            }
        }

        if (GAME_GRID[hint_centre_y_index][hint_centre_x_index].getDisplayStatus() != 1) { return 0; }

        for (int i = Math.max(0, hint_centre_y_index-1); i < Math.min(BOARD_HEIGHT, hint_centre_y_index+2); i++) {
            for (int j = Math.max(0, hint_centre_x_index - 1); j < Math.min(BOARD_WIDTH, hint_centre_x_index+2); j++) {
                if (GAME_GRID[i][j].getDisplayStatus() == 0 && GAME_GRID[i][j].getActualStatus() == 2) {
                    GAME_GRID[i][j].flag();
                    return 1;
                }
            }
        }

        return 0;
    }

    // Reveals all mines that are not currently flagged.
    void revealAllMines() {
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                if (GAME_GRID[y][x].getActualStatus() == 2) {
                    GAME_GRID[y][x].reveal();
                } else {
                    GAME_GRID[y][x].markAsFalseFlag();
                }
            }
        }
    }


    void reset() {
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                GAME_GRID[y][x].reset();
            }
        }
    }

    /* (Potentially) alters the flag status of the tile at GAME_GRID[yPosition][xPosition].
    / If this position is a hidden tile:
    / Returns 1 if position is originally un-flagged and becomes flagged.
    / returns -1 if position is originally flagged and becomes un-flagged.
    /
    / If this position is a revealed tile: this method does nothing and returns 0. */
    int flag(int xPosition, int yPosition) {
        return GAME_GRID[yPosition][xPosition].flag();
    }

    // Paints the grid in the view.
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
                    case 2:  // mine tile
                        g.drawImage(MINE, BOARD_START_X + (j * SQUARE_SIZE),
                                BOARD_START_Y + (i * SQUARE_SIZE), null);
                        break;
                    case 3: // flag tile
                        g.drawImage(FLAG, BOARD_START_X + (j * SQUARE_SIZE),
                                BOARD_START_Y + (i * SQUARE_SIZE), null);
                        break;
                    case 4: // false-flag tile
                        g.drawImage(FALSE_FLAG, BOARD_START_X + (j * SQUARE_SIZE),
                                BOARD_START_Y + (i * SQUARE_SIZE), null);
                        break;
                }
            }
        }
    }

    int getBoardWidth() {
        return BOARD_WIDTH;
    }

    int getBoardHeight() {
        return BOARD_HEIGHT;
    }

    int getTotalMines() {
        return NUMBER_OF_MINES;
    }
}
