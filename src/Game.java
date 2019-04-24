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
    private Boolean DISPLAY_REVEAL_MINES_DIALOGUE;
    private Boolean DISPLAY_HINT_PENALTY_DIALOGUE;
    private Image FLAG;
    private int FLAGS_LAID;
    private int NUMBER_OF_REVEALED_NUMBERS;
    private int SECONDS_ELAPSED;
    private int TIME_PENALTY;

    Game() {
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

    void showRevealDialogue() {
        DISPLAY_REVEAL_MINES_DIALOGUE = true;
        repaint();
    }

    void hideRevealDialogue() {
        DISPLAY_REVEAL_MINES_DIALOGUE = false;
        repaint();
    }

    void showHintPenaltyDialogue() {
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!getGameStatus() && DISPLAY_HINT_PENALTY_DIALOGUE) {
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

        if (GRID_HIDDEN) { return; }

        GAME_GRID.paint(g);
    }

}
