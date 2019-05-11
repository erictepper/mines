import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

class Game extends JPanel {
    private int BOARD_START_X;
    private int BOARD_START_Y;
    private static int SQUARE_SIZE = 30;
    private GameGrid GAME_GRID;
    private boolean GAME_STARTED;  // true if the game has been started, false if not.
    private boolean GAME_LOST;  // true if lost, false if not lost.
    private boolean GAME_WON; // true if won, false if not won.
    private boolean GRID_HIDDEN;
    private boolean DISPLAY_GAME_INSTRUCTIONS;
    private boolean DISPLAY_REVEAL_MINES_DIALOGUE;
    private boolean DISPLAY_HINT_PENALTY_DIALOGUE;
    private Image MINE;
    private Image FLAG;
    private int FLAGS_LAID;
    private int NUMBER_OF_REVEALED_NUMBERS;
    private int SECONDS_ELAPSED;
    private int TIME_PENALTY;

    Game() {
        newGame("expert");
        try {
            MINE = ImageIO.read(new File("images/mine.png"));
            FLAG = ImageIO.read(new File("images/flag.png"));
        } catch (IOException e) {
            MINE = null;
            FLAG = null;
        }
    }

    void newGame(String difficulty) {
        switch (difficulty) {
            case "beginner":
                BOARD_START_X = 365;
                BOARD_START_Y = 340;
                GAME_GRID = new GameGrid(difficulty, BOARD_START_X, BOARD_START_Y, SQUARE_SIZE);
                break;

            case "intermediate":
                BOARD_START_X = 260;
                BOARD_START_Y = 240;
                GAME_GRID = new GameGrid(difficulty, BOARD_START_X, BOARD_START_Y, SQUARE_SIZE);
                break;

            case "expert":
                BOARD_START_X = 50;
                BOARD_START_Y = 240;
                GAME_GRID = new GameGrid(difficulty, BOARD_START_X, BOARD_START_Y, SQUARE_SIZE);
                break;
        }
        GAME_STARTED = false;
        GAME_LOST = false;
        GAME_WON = false;
        GRID_HIDDEN = false;
        DISPLAY_GAME_INSTRUCTIONS = false;
        DISPLAY_REVEAL_MINES_DIALOGUE = false;
        DISPLAY_HINT_PENALTY_DIALOGUE = false;
        FLAGS_LAID = 0;
        NUMBER_OF_REVEALED_NUMBERS = 0;
        SECONDS_ELAPSED = 0;
        TIME_PENALTY = 0;
    }

    void reset() {
        GAME_LOST = false;
        GAME_WON = false;
        GRID_HIDDEN = false;
        FLAGS_LAID = 0;
        NUMBER_OF_REVEALED_NUMBERS = 0;
        GAME_GRID.reset();
    }

    void showGameInstructions() {
        DISPLAY_GAME_INSTRUCTIONS = true;
    }

    void hideGameInstructions() {
        DISPLAY_GAME_INSTRUCTIONS = false;
    }

    void showRevealDialogue() {
        DISPLAY_REVEAL_MINES_DIALOGUE = true;
        repaint();
    }

    void hideRevealDialogue() {
        DISPLAY_REVEAL_MINES_DIALOGUE = false;
        repaint();
    }

    void showHintPenaltyDialogue() {
        // if the game is over or has not started yet, no point in showing the dialogue - return.
        // if the 'new game' dialogue is up, also no point in showing the hint dialogue - return.
        if (getGameStatus() || !getGameStartedStatus() || isGridHidden()) { return; }
        DISPLAY_HINT_PENALTY_DIALOGUE = true;
        repaint();
    }

    void hideHintPenaltyDialogue() {
        DISPLAY_HINT_PENALTY_DIALOGUE = false;
        repaint();
    }

    void revealAllMines() {
        GAME_GRID.revealAllMines();
    }

    void giveHint() {
        int flags_change = GAME_GRID.giveHint();
        FLAGS_LAID = FLAGS_LAID + flags_change;
        if (flags_change != 0) { hintPenalty(); }
    }

    void hideGrid() {
        GRID_HIDDEN = true;
    }

    void showGrid() {
        GRID_HIDDEN = false;
    }

    boolean isGridHidden() { return GRID_HIDDEN; }

    // Ticks the timer and returns the current number of seconds elapsed.
    void timerTick() {
        ++SECONDS_ELAPSED;
    }

    // Adds a hint penalty to the timer.
    private void hintPenalty() {
        TIME_PENALTY += 30;
    }

    // Handles the interaction of the mouse with the game.
    void mousePressed(int xPosition, int yPosition, int button) {
        if (!inBounds(xPosition, yPosition)) { return; }
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
            }
            else {
                NUMBER_OF_REVEALED_NUMBERS = NUMBER_OF_REVEALED_NUMBERS + typeRevealed;
                GAME_WON = NUMBER_OF_REVEALED_NUMBERS >= GAME_GRID.getBoardHeight() * GAME_GRID.getBoardWidth() -
                        GAME_GRID.getTotalMines();
            }
        }
    }

    boolean getGameStartedStatus() { return GAME_STARTED; }

    boolean getGameLostStatus() { return GAME_LOST; }

    boolean getGameStatus() {
        return (GAME_LOST || GAME_WON);
    }

    // Checks if the coordinates are within the bounds of the game grid.
    boolean inBounds(int x, int y) {
        return GAME_GRID.inBounds(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (DISPLAY_HINT_PENALTY_DIALOGUE) {
            g.setFont(new Font("Courier New", Font.PLAIN, 10));
            g.setColor(Color.RED);
            g.drawString("You will incur a", 190, 165);
            g.drawString("penalty of 30 seconds", 175, 180);
            g.drawString("if you use a hint!", 185, 195);
        }

        g.setFont(new Font("Courier New", Font.PLAIN, 20));
        g.setColor(Color.BLACK);

        int seconds_mod_60_elapsed = (SECONDS_ELAPSED + TIME_PENALTY) % 60;
        int minutes_elapsed = (SECONDS_ELAPSED + TIME_PENALTY) / 60;
        String seconds_display;
        String minutes_display;
        if (seconds_mod_60_elapsed < 10) { seconds_display = "0" + seconds_mod_60_elapsed; } else {
            seconds_display = Integer.toString(seconds_mod_60_elapsed);
        }
        if (minutes_elapsed < 10) { minutes_display = "0" + minutes_elapsed; } else {
            minutes_display = Integer.toString(minutes_elapsed);
        }
        String timer_display = minutes_display + ":" + seconds_display;
        g.drawString(timer_display, 500, 187);

        // Draws the time penalty if it is above 0.
        if (TIME_PENALTY > 0) {
            int penalty_seconds_mod_60_elapsed = TIME_PENALTY % 60;
            int penalty_minutes_elapsed = TIME_PENALTY / 60;
            String penalty_seconds_display;
            String penalty_minutes_display;
            if (penalty_seconds_mod_60_elapsed < 10) {
                penalty_seconds_display = "0" + penalty_seconds_mod_60_elapsed;
            } else {
                penalty_seconds_display = Integer.toString(penalty_seconds_mod_60_elapsed);
            }
            if (penalty_minutes_elapsed < 10) {
                penalty_minutes_display = "0" + penalty_minutes_elapsed;
            } else {
                penalty_minutes_display = Integer.toString(penalty_minutes_elapsed);
            }
            String penalty_display = "+ " + penalty_minutes_display + ":" + penalty_seconds_display;
            g.setFont(new Font("Courier New", Font.PLAIN, 14));
            g.setColor(Color.RED);
            g.drawString(penalty_display, 500, 207);

            g.setFont(new Font("Courier New", Font.PLAIN, 20));
            g.setColor(Color.BLACK);
        }

        if (GAME_GRID.getTotalMines() - FLAGS_LAID > 9) {
            g.drawString(Integer.toString(GAME_GRID.getTotalMines() - FLAGS_LAID), 640, 187);
        } else {
            g.drawString("0" + (GAME_GRID.getTotalMines() - FLAGS_LAID), 640, 187);
        }
        if (FLAG != null) {
            g.drawImage(FLAG, 670, 165, null);
            g.drawRect(635, 165, 65, 30);
        } else {
            g.drawString("Flags", 600, 190);
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

        if (DISPLAY_REVEAL_MINES_DIALOGUE) {
            g.setFont(new Font("Courier New", Font.PLAIN, 12));
            g.setColor(Color.BLACK);
            g.drawString("Would you like to reveal all mines?", 335,225);
        }

        if (DISPLAY_GAME_INSTRUCTIONS) {
            paintGameInstructions(g);
        }

        if (GRID_HIDDEN) { return; }

        GAME_GRID.paint(g);
    }

    private void paintGameInstructions(Graphics g) {
        g.setFont(new Font("Courier New", Font.PLAIN, 14));
        g.setColor(Color.BLACK);
        g.drawString("The game is made up of a grid of tiles that start off hidden.", 60, 254);
        g.drawString("There are two types of tiles: a MINE TILE and a NUMBER TILE.", 60, 284);
        g.drawString(" - BEGINNER mode contains 10 mine tiles and 71 number tiles.", 60, 304);
        g.drawString(" - INTERMEDIATE mode contains 40 mine tiles and 216 number tiles.", 60, 324);
        g.drawString(" - EXPERT mode contains 99 mine tiles and 381 number tiles.", 60, 344);
        g.drawString("A MINE TILE contains a mine.", 60, 374);
        g.drawString("A NUMBER TILE is a space without a mine. A number tile will instead contain a number that counts the number", 60, 404);
        g.drawString("of adjacent mines. This number counts all mines touching the space - top, bottom, left, right, *and*", 60, 418);
        g.drawString("diagonally adjacent mines. You may see an example of this at the top-right.", 60, 432);
        paintExampleTiles(g);
        g.drawString("The GOAL of the game is to reveal all number tiles without revealing a single mine tile. If you reveal", 60, 462);
        g.drawString("a mine tile, you lose.", 60, 476);
        g.drawString("To REVEAL a tile, put your mouse over the tile and press LEFT-CLICK on your mouse. Remember to try to only", 60, 506);
        g.drawString("reveal tiles where you do not think a mine exists.", 60, 520);
        g.drawString("You may find it helpful to place a FLAG on spaces where you think a mine may be hiding. This will give you", 60, 550);
        g.drawString("a visual indicator of where you think the mines are, which could help you reveal more number tiles. To", 60, 564);
        g.drawString("place a FLAG, you can either mouse over a tile and press RIGHT-CLICK on your mouse, OR you can mouse over", 60, 578);
        g.drawString("a tile and press CTRL on your keyboard and LEFT-CLICK on your mouse at the same time.", 60, 592);
        g.drawString("A TIMER will track your progress. ", 60, 622);
        g.drawString("You may receive a HINT, but you will receive a 30-second penalty on the timer if you use a hint.", 60, 652);
        g.drawString("If you LOSE, you may choose to reveal all mines. This may help you see your mistakes and help you learn.", 60, 682);
        g.drawString("You may RESET the game (but not if you have chosen have the mines revealed for you). This will give you", 60, 712);
        g.drawString("another chance if you lose, HOWEVER the timer will keep running.", 60, 726);
    }

    private void paintExampleTiles(Graphics g) {
        g.setFont(new Font("Courier New", Font.PLAIN, 20));

        GameTile[][] example_grid = new GameTile[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                example_grid[i][j] = new GameTile();
            }
        }

        example_grid[0][0].setTileType(2);
        example_grid[0][1].setNumberOfAdjacentMines(3);
        example_grid[0][2].setTileType(2);
        example_grid[1][0].setTileType(2);
        example_grid[1][1].setNumberOfAdjacentMines(4);
        example_grid[1][2].setNumberOfAdjacentMines(2);
        example_grid[2][0].setNumberOfAdjacentMines(2);
        example_grid[2][1].setTileType(2);
        example_grid[2][2].setNumberOfAdjacentMines(1);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                example_grid[i][j].reveal();
            }
        }

        int paint_start_x = 700;
        int paint_start_y = 274;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                switch (example_grid[i][j].getDisplayStatus()) {
                    case 1:  // number tile
                        g.setColor(Color.WHITE);
                        g.fillRect(paint_start_x + (j * SQUARE_SIZE),
                                paint_start_y + (i * SQUARE_SIZE),
                                SQUARE_SIZE, SQUARE_SIZE);
                        g.setColor(Color.BLACK);
                        g.drawString(example_grid[i][j].getLabel(), paint_start_x + (j * SQUARE_SIZE) + 7,
                                paint_start_y + ((i + 1) * SQUARE_SIZE) - 7);
                        break;
                    case 2:  // mine tile
                        g.drawImage(MINE, paint_start_x + (j * SQUARE_SIZE),
                                paint_start_y + (i * SQUARE_SIZE), null);
                        break;
                }
            }
        }

        g.setFont(new Font("Courier New", Font.PLAIN, 14));
    }

}
