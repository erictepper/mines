import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.lang.*;

class Game extends JPanel {
  private static final int SQUARE_SIZE = 30;
  private static Image mineImage;
  private static Image flagImage;

  private int boardBeginningX;
  private int boardBeginningY;
  private GameGrid gameGrid;
  private boolean hasGameStarted;  // true if the game has been started, false if not.
  private boolean isGameLost;  // true if lost, false if not lost.
  private boolean isGameWon; // true if won, false if not won.
  private boolean isGridHidden;
  private boolean areInstructionsShown;
  private boolean isRevealMinesDialogueShown;
  private boolean isHintPenaltyDialogueShown;
  private int flagsLaidCount;
  private int revealedNumbersCount;
  private int secondsElapsed;
  private int timePenalty;

  Game() {
    newGame("expert");
    try {
      mineImage = ImageIO.read(getClass().getResource("images/mine.png"));
      flagImage = ImageIO.read(getClass().getResource("images/flag.png"));
    } catch (Exception e) {
      System.out.println("Package is missing image(s) for mines and flags.");
      mineImage = null;  // TODO: create an alternative when the image cannot be found
      flagImage = null;  // TODO: create an alternative when the image cannot be found
    }
  }

  void newGame(String difficulty) {
    switch (difficulty) {
      case "beginner":
        boardBeginningX = 365;
        boardBeginningY = 340;
        gameGrid = new GameGrid(difficulty, boardBeginningX, boardBeginningY, SQUARE_SIZE);
        break;

      case "intermediate":
        boardBeginningX = 260;
        boardBeginningY = 240;
        gameGrid = new GameGrid(difficulty, boardBeginningX, boardBeginningY, SQUARE_SIZE);
        break;

      case "expert":
        boardBeginningX = 50;
        boardBeginningY = 240;
        gameGrid = new GameGrid(difficulty, boardBeginningX, boardBeginningY, SQUARE_SIZE);
        break;
    }

    hasGameStarted = false;
    isGameLost = false;
    isGameWon = false;
    isGridHidden = false;
    areInstructionsShown = false;
    isRevealMinesDialogueShown = false;
    isHintPenaltyDialogueShown = false;
    flagsLaidCount = 0;
    revealedNumbersCount = 0;
    secondsElapsed = 0;
    timePenalty = 0;
  }

  void reset() {
    isGameLost = false;
    isGameWon = false;
    isGridHidden = false;
    flagsLaidCount = 0;
    revealedNumbersCount = 0;
    gameGrid.reset();
  }

  void showGameInstructions() {
    areInstructionsShown = true;
  }

  void hideGameInstructions() {
    areInstructionsShown = false;
  }

  void showRevealDialogue() {
    isRevealMinesDialogueShown = true;
    repaint();
  }

  void hideRevealDialogue() {
    isRevealMinesDialogueShown = false;
    repaint();
  }

  void showHintPenaltyDialogue() {
    // if the game is over or has not started yet, no point in showing the dialogue - return.
    // if the 'new game' dialogue is up, also no point in showing the hint dialogue - return.
    if (getGameStatus() || !getGameStartedStatus() || isGridHidden()) { return; }
    isHintPenaltyDialogueShown = true;
    repaint();
  }

  void hideHintPenaltyDialogue() {
    isHintPenaltyDialogueShown = false;
    repaint();
  }

  void revealAllMines() {
    gameGrid.revealAllMines();
  }

  void giveHint() {
    int flags_change = gameGrid.giveHint();
    flagsLaidCount = flagsLaidCount + flags_change;
    if (flags_change != 0) { hintPenalty(); }
  }

  void hideGrid() {
    isGridHidden = true;
  }

  void showGrid() {
    isGridHidden = false;
  }

  boolean isGridHidden() { return isGridHidden; }

  // Ticks the timer and returns the current number of seconds elapsed.
  void timerTick() {
    ++secondsElapsed;
  }

  // Adds a hint penalty to the timer.
  private void hintPenalty() {
    timePenalty += 30;
  }

  // Handles the interaction of the mouse with the game.
  void mousePressed(int xPosition, int yPosition, int button) {
    if (!inBounds(xPosition, yPosition)) { return; }
    int gridIndexXPosition = (xPosition - boardBeginningX) / SQUARE_SIZE;
    int gridIndexYPosition = (yPosition - boardBeginningY) / SQUARE_SIZE;

    if (!hasGameStarted && button == 1) {
      gameGrid.moveMines(gridIndexYPosition, gridIndexXPosition);
      hasGameStarted = true;
    }
    if (button == 3) {
      flagsLaidCount = flagsLaidCount + gameGrid.flag(gridIndexXPosition, gridIndexYPosition);
      repaint();
    }
    else if (button == 1) {
      int typeRevealed = gameGrid.reveal(gridIndexYPosition, gridIndexXPosition);
      if (typeRevealed == -1) {
        isGameLost = true;
      }
      else {
        revealedNumbersCount = revealedNumbersCount + typeRevealed;
        isGameWon = revealedNumbersCount >= gameGrid.getBoardHeight() * gameGrid.getBoardWidth() -
            gameGrid.getTotalMines();
      }
    }
  }

  boolean getGameStartedStatus() { return hasGameStarted; }

  boolean getGameLostStatus() { return isGameLost; }

  boolean getGameStatus() {
    return (isGameLost || isGameWon);
  }

  // Checks if the coordinates are within the bounds of the game grid.
  boolean inBounds(int x, int y) {
    return gameGrid.inBounds(x, y);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    if (isHintPenaltyDialogueShown) {
      g.setFont(new Font("Courier New", Font.PLAIN, 10));
      g.setColor(Color.RED);
      g.drawString("You will incur a", 190, 165);
      g.drawString("penalty of 30 seconds", 175, 180);
      g.drawString("if you use a hint!", 185, 195);
    }

    g.setFont(new Font("Courier New", Font.PLAIN, 20));
    g.setColor(Color.BLACK);

    int seconds_mod_60_elapsed = (secondsElapsed + timePenalty) % 60;
    int minutes_elapsed = (secondsElapsed + timePenalty) / 60;
    String seconds_display, minutes_display;
    if (seconds_mod_60_elapsed < 10) { seconds_display = "0" + seconds_mod_60_elapsed; } else {
      seconds_display = Integer.toString(seconds_mod_60_elapsed);
    }
    if (minutes_elapsed < 10) { minutes_display = "0" + minutes_elapsed; } else {
      minutes_display = Integer.toString(minutes_elapsed);
    }
    String timer_display = minutes_display + ":" + seconds_display;
    g.drawString(timer_display, 500, 187);

    // Draws the time penalty if it is above 0.
    if (timePenalty > 0) {
      int penalty_seconds_mod_60_elapsed = timePenalty % 60;
      int penalty_minutes_elapsed = timePenalty / 60;
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

    if (gameGrid.getTotalMines() - flagsLaidCount > 9) {
      g.drawString(Integer.toString(gameGrid.getTotalMines() - flagsLaidCount), 640, 187);
    } else {
      g.drawString("0" + (gameGrid.getTotalMines() - flagsLaidCount), 640, 187);
    }
    if (flagImage != null) {
      g.drawImage(flagImage, 670, 165, null);
      g.drawRect(635, 165, 65, 30);
      } else {
      g.drawString("F", 600, 190);
    }

    if (isGameLost) {
      g.setColor(Color.RED);
      g.setFont(new Font("Courier New", Font.PLAIN, 60));
      g.drawString("GAME OVER", 350, 100);
    } else if (isGameWon) {
      g.setColor(new Color(0,204,0));
      g.setFont(new Font("Courier New", Font.PLAIN, 60));
      g.drawString("YOU WIN!", 370, 100);
    } else {
      g.setFont(new Font("Courier New", Font.PLAIN, 60));
      g.drawString("Mines", 440, 100);
    }

    if (isRevealMinesDialogueShown) {
      g.setFont(new Font("Courier New", Font.PLAIN, 12));
      g.setColor(Color.BLACK);
      g.drawString("Would you like to reveal all mines?", 335,225);
    }

    if (areInstructionsShown) {
      paintGameInstructions(g);
    }

    if (isGridHidden) { return; }

    gameGrid.paint(g);
  }

  private void paintGameInstructions(Graphics g) {
    g.setFont(new Font("Courier New", Font.PLAIN, 14));
    g.setColor(Color.BLACK);
    g.drawString("The game is made up of a grid of tiles that start off hidden.", 60, 249);
    g.drawString("There are two types of tiles: a MINE TILE and a NUMBER TILE.", 60, 279);
    g.drawString(" - BEGINNER mode contains 10 mine tiles and 71 number tiles.", 60, 299);
    g.drawString(" - INTERMEDIATE mode contains 40 mine tiles and 216 number tiles.", 60, 319);
    g.drawString(" - EXPERT mode contains 99 mine tiles and 381 number tiles.", 60, 339);
    g.drawString("A MINE TILE contains a mine.", 60, 369);
    g.drawString("A NUMBER TILE is a space without a mine. A number tile will instead contain a number that counts the number", 60, 399);
    g.drawString("of adjacent mines. This number counts all mines touching the space - top, bottom, left, right, *and*", 60, 413);
    g.drawString("diagonally adjacent mines. You may see an example of this at the top-right.", 60, 427);
    paintExampleTiles(g);
    g.drawString("The GOAL of the game is to reveal all number tiles without revealing a single mine tile. If you reveal", 60, 457);
    g.drawString("a mine tile, you lose.", 60, 471);
    g.drawString("To REVEAL a tile, put your mouse over the tile and press LEFT-CLICK on your mouse. Remember to try to only", 60, 501);
    g.drawString("reveal tiles where you do not think a mine exists.", 60, 515);
    g.drawString("You may find it helpful to place a FLAG on spaces where you think a mine may be hiding. This will give you", 60, 545);
    g.drawString("a visual indicator of where you think the mines are, which could help you reveal more number tiles. To", 60, 559);
    g.drawString("place a FLAG, you can either mouse over a tile and press RIGHT-CLICK on your mouse, OR you can mouse over", 60, 573);
    g.drawString("a tile and press CTRL on your keyboard and LEFT-CLICK on your mouse at the same time.", 60, 587);
    g.drawString("A TIMER will track your progress. ", 60, 617);
    g.drawString("You may receive a HINT, but you will receive a 30-second penalty on the timer if you use a hint.", 60, 647);
    g.drawString("If you LOSE, you may choose to reveal all mines. This may help you see your mistakes and help you learn.", 60, 677);
    g.drawString("You may RESET the game (but not if you have chosen to have the mines revealed for you). This will give you", 60, 707);
    g.drawString("another chance if you lose, HOWEVER the timer will keep running.", 60, 721);
  }

  private void paintExampleTiles(Graphics g) {
    g.setFont(new Font("Courier New", Font.PLAIN, 20));

    Tile[][] example_grid = new Tile[3][3];
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        example_grid[i][j] = new NumberTile();
      }
    }

    example_grid[0][0] = new MineTile();
    ((NumberTile) example_grid[0][1]).setAdjacentMinesCount(3);
    example_grid[0][2] = new MineTile();
    example_grid[1][0] = new MineTile();
    ((NumberTile) example_grid[1][1]).setAdjacentMinesCount(4);
    ((NumberTile) example_grid[1][2]).setAdjacentMinesCount(2);
    ((NumberTile) example_grid[2][0]).setAdjacentMinesCount(2);
    example_grid[2][1] = new MineTile();
    ((NumberTile) example_grid[2][2]).setAdjacentMinesCount(1);

    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        example_grid[i][j].reveal();
      }
    }

    int paint_start_x = 700;
    int paint_start_y = 259;

    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        if (!example_grid[i][j].isMine()) {
          g.setColor(Color.WHITE);
          g.fillRect(paint_start_x + (j * SQUARE_SIZE), paint_start_y + (i * SQUARE_SIZE), SQUARE_SIZE,
              SQUARE_SIZE);
          g.setColor(Color.BLACK);
          int adjacentMinesCount = ((NumberTile) example_grid[i][j]).getAdjacentMinesCount();
          String label = (adjacentMinesCount == 0) ? "" : String.valueOf(adjacentMinesCount);
          g.drawString(
              label,
              paint_start_x + (j * SQUARE_SIZE) + 7,
              paint_start_y + ((i + 1) * SQUARE_SIZE) - 7
          );
        } else {
          g.drawImage(mineImage, paint_start_x + (j * SQUARE_SIZE),
                paint_start_y + (i * SQUARE_SIZE), null);
        }
      }
    }

    g.setFont(new Font("Courier New", Font.PLAIN, 14));
  }
}
