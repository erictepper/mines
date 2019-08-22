import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.*;
import java.awt.*;
import java.lang.Math;
import java.io.File;

class GameGrid {
  private int boardHeight;
  private int boardWidth;
  private int numberOfMines;
  private GameTile[][] gameGrid;
  private int boardBeginningX;
  private int boardBeginningY;
  private static int squareSize;
  private static Image mineImage;
  private static Image flagImage;
  private static Image falseFlagImage;

  // gameSize should be one of "beginner", "intermediate", or "expert".
  GameGrid(String gameSize, int boardXStart, int boardYStart, int boardSquareSize) {
    // numberOfMines = 10;  // TODO: Check to make sure removing this doesn't break anything.
    switch (gameSize) {
      case "beginner":
        boardHeight = 9;
        boardWidth = 9;
        numberOfMines = 10;
        break;

      case "intermediate":
        boardHeight = 16;
        boardWidth = 16;
        numberOfMines = 40;
        break;

      case "expert":
        boardHeight = 16;
        boardWidth = 30;
        numberOfMines = 99;
        break;
      default:
        throw new IllegalArgumentException("gameSize must be one of \"beginner\", \"intermediate\", or \"expert\".");
    }

    try {
      mineImage = ImageIO.read(new File("resources/images/mine.png"));
      flagImage = ImageIO.read(new File("resources/images/flag.png"));
      falseFlagImage = ImageIO.read(new File("resources/images/false_flag.png"));
    } catch (IOException e) {
      mineImage = null;
      flagImage = null;
      falseFlagImage = null;
    }

    boardBeginningX = boardXStart;
    boardBeginningY = boardYStart;
    squareSize = boardSquareSize;

    // Initializes the game grid.
    gameGrid = new GameTile[boardHeight][boardWidth];
    for (int i = 0; i < boardHeight; i++) {
      for (int j = 0; j < boardWidth; j++) {
        gameGrid[i][j] = new GameTile();
      }
    }

    // Generates random grid spaces to place mines.
    ArrayList<Integer> rowValues = new ArrayList<>();
    ArrayList<Integer> columnValues = new ArrayList<>();
    Random numberGenerator = new Random();
    for (int i = 0; i < numberOfMines; i++) {
      rowValues.add(numberGenerator.nextInt(boardHeight));
      columnValues.add(numberGenerator.nextInt(boardWidth));
    }

    // Fills the grid with 99 mines in the spaces determined above.
    for (int i = 0; i < numberOfMines; i++) {
      // If the space already has a mine, relocate the mine.
      if (gameGrid[rowValues.get(i)][columnValues.get(i)].getActualStatus() == 2) {
        relocateMine(columnValues.get(i), rowValues.get(i));
      }
      gameGrid[rowValues.get(i)][columnValues.get(i)].setTileType(2);
    }

    // Updates the number of adjacent mines for each tile.
    updateAdjacentMines();
  }

  // Moves the mine at position (xPosition, yPosition) and also moves any directly adjacent mines.
  void moveMines(int xPosition, int yPosition) {
    Random numberGenerator = new Random();
    for (int adjacentYPosition = Math.max(0, yPosition-1); adjacentYPosition < Math.min(boardHeight, yPosition+2);
         adjacentYPosition++) {
      for (int adjacentXPosition = Math.max(0, xPosition-1);
           adjacentXPosition < Math.min(boardWidth, xPosition+2); adjacentXPosition++) {
        // If the adjacent position already has a mine, relocate the mine to some non-adjacent, free space.
        // Note that extra conditions have been added to the while loop not present in relocateMine(int, int).
        if (gameGrid[adjacentYPosition][adjacentXPosition].getActualStatus() == 2) {
          gameGrid[adjacentYPosition][adjacentXPosition].setTileType(1);
          int j = numberGenerator.nextInt(boardHeight);
          int k = numberGenerator.nextInt(boardWidth);
          while (gameGrid[j][k].getActualStatus() == 2 ||
              (j >= Math.max(0, yPosition-1) && j < Math.min(boardHeight, yPosition+2) &&
                  k >= Math.max(0, xPosition-1) && k < Math.min(boardWidth, xPosition+2))) {
            j = numberGenerator.nextInt(boardHeight);
            k = numberGenerator.nextInt(boardWidth);
          }
          gameGrid[j][k].setTileType(2);
        }
      }
    }

    updateAdjacentMines();
  }

  // Find a new, random, empty space and place the mine there.
  private void relocateMine(int xPosition, int yPosition) {
    Random numberGenerator = new Random();
    int j = numberGenerator.nextInt(boardHeight);
    int k = numberGenerator.nextInt(boardWidth);
    while (gameGrid[j][k].getActualStatus() == 2) {
      j = numberGenerator.nextInt(boardHeight);
      k = numberGenerator.nextInt(boardWidth);
    }
    gameGrid[j][k].setTileType(2);
    gameGrid[yPosition][xPosition].setTileType(1);
  }

  // Counts and stores the number of adjacent mines for each GameTile.
  private void updateAdjacentMines() {
    for (int i = 0; i < boardHeight; i++) {
      for (int j = 0; j < boardWidth; j++) {
        int totalMines = 0;
        for (int iterator1 = Math.max(0, i-1); iterator1 < Math.min(boardHeight, i+2); iterator1++) {
          for (int iterator2 = Math.max(0, j-1); iterator2 < Math.min(boardWidth, j+2); iterator2++) {
            totalMines = (gameGrid[iterator1][iterator2].getActualStatus() == 2) ? totalMines + 1 : totalMines;
          }
        }
        gameGrid[i][j].setAdjacentMinesCount(totalMines);
      }
    }
  }

  // Reveals position (xPosition, yPosition) if it has an adjacent mine.
  // If this position has no adjacent mines, it reveals the space and recursively reveals adjacent spaces.
  // If the space has already been revealed, it does nothing and returns 0.
  // Returns -1 if revealed space is a mine, and returns the number of total revealed spaces otherwise.
  int reveal(int xPosition, int yPosition) {
    if (gameGrid[yPosition][xPosition].getAdjacentMinesCount() == 0 &&
        gameGrid[yPosition][xPosition].getDisplayStatus() == 0) {
      int totalRevealed = gameGrid[yPosition][xPosition].reveal();
      for (int i = Math.max(0, yPosition-1); i < Math.min(boardHeight, yPosition+2); i++) {
        for (int j = Math.max(0, xPosition-1); j < Math.min(boardWidth, xPosition+2); j++) {
          if ((i != yPosition) || (j != xPosition)) { totalRevealed = totalRevealed + reveal(j, i); }
        }
      }
      return totalRevealed;
    } else {
      int revealedStatus = gameGrid[yPosition][xPosition].reveal();
      if (revealedStatus == 2) { return -1; }
      else return revealedStatus;
    }
  }

  // Gives a player a hint. If there exists a false flag, mark it as a false flag. If no false flags exist,
  // mark a flag in a place it would be the most useful (e.g. next to a tile where only one flag is left).
  int giveHint() {
    // Firstly, searches the grid to see if there are any tiles falsely flagged. If one exists, mark it as a false
    // flag and return.
    for (int y = 0; y < boardHeight; y++) {
      for (int x = 0; x < boardWidth; x++) {
        GameTile current_game_tile = gameGrid[y][x];
        if (current_game_tile.getActualStatus() == 1 && current_game_tile.getDisplayStatus() == 3) {
          current_game_tile.setFalseFlag();
          return -1;
        }
      }
    }

    // Searches for the space with the smallest difference between the number of adjacent flags and the number
    // of adjacent mines. This space must have already been revealed.
    int lowest_mine_flag_difference_count = 9;
    int hint_centre_x_index = 0, hint_centre_y_index = 0;
    search: {
      for (int y = 0; y < boardHeight; y++) {
        for (int x = 0; x < boardWidth; x++) {
          if (gameGrid[y][x].getDisplayStatus() != 1) { continue; }

          int number_of_adjacent_flags = 0;

          // Counts the number of adjacent flags for GAME_TILE[y][x].
          for (int i = Math.max(0, y - 1); i < Math.min(boardHeight, y + 2); i++) {
            for (int j = Math.max(0, x - 1); j < Math.min(boardWidth, x + 2); j++) {
              if (gameGrid[i][j].getDisplayStatus() == 3) {
                number_of_adjacent_flags++;
              }
            }
          }

          int diff = gameGrid[y][x].getAdjacentMinesCount() - number_of_adjacent_flags;
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

    if (gameGrid[hint_centre_y_index][hint_centre_x_index].getDisplayStatus() != 1) { return 0; }

    for (int i = Math.max(0, hint_centre_y_index-1); i < Math.min(boardHeight, hint_centre_y_index+2); i++) {
      for (int j = Math.max(0, hint_centre_x_index - 1); j < Math.min(boardWidth, hint_centre_x_index+2); j++) {
        if (gameGrid[i][j].getDisplayStatus() == 0 && gameGrid[i][j].getActualStatus() == 2) {
          gameGrid[i][j].flag();
          return 1;
        }
      }
    }

    return 0;
  }

  // Reveals all mines that are not currently flagged.
  void revealAllMines() {
    for (int y = 0; y < boardHeight; y++) {
      for (int x = 0; x < boardWidth; x++) {
        if (gameGrid[y][x].getActualStatus() == 2) {
          gameGrid[y][x].reveal();
        } else {
          gameGrid[y][x].setFalseFlag();
        }
      }
    }
  }


  void reset() {
    for (int y = 0; y < boardHeight; y++) {
      for (int x = 0; x < boardWidth; x++) {
        gameGrid[y][x].reset();
      }
    }
  }

  /* (Potentially) alters the flag status of the tile at gameGrid[yPosition][xPosition].
   / If this position is a hidden tile:
   / Returns 1 if position is originally un-flagged and becomes flagged.
   / returns -1 if position is originally flagged and becomes un-flagged.
   /
   / If this position is a revealed tile: this method does nothing and returns 0. */
  int flag(int xPosition, int yPosition) {
    return gameGrid[yPosition][xPosition].flag();
  }

  // Paints the grid in the view.
  void paint(Graphics g) {
    g.setFont(new Font("Courier New", Font.PLAIN, 20));

    for (int i = 0; i < boardHeight; i++) {
      for (int j = 0; j < boardWidth; j++) {
        switch (gameGrid[i][j].getDisplayStatus()) {
          case 0:  // hidden tile
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(boardBeginningX + (j * squareSize), boardBeginningY + (i * squareSize), squareSize,
                squareSize);
            break;
          case 1:  // number tile
            g.setColor(Color.WHITE);
            g.fillRect(boardBeginningX + (j * squareSize), boardBeginningY + (i * squareSize), squareSize,
                squareSize);
            g.setColor(Color.BLACK);
            g.drawString(gameGrid[i][j].label(), boardBeginningX + (j * squareSize) + 7,
                boardBeginningY + ((i + 1) * squareSize) - 7);
            break;
          case 2:  // mine tile
            g.drawImage(mineImage, boardBeginningX + (j * squareSize), boardBeginningY + (i * squareSize),
                null);
            break;
          case 3: // flag tile
            g.drawImage(flagImage, boardBeginningX + (j * squareSize), boardBeginningY + (i * squareSize),
                null);
            break;
          case 4: // false-flag tile
            g.drawImage(falseFlagImage, boardBeginningX + (j * squareSize), boardBeginningY + (i * squareSize),
                null);
            break;
        }
      }
    }
  }

  int getBoardWidth() {
    return boardWidth;
  }

  int getBoardHeight() {
    return boardHeight;
  }

  int getTotalMines() {
    return numberOfMines;
  }

  // Checks if the coordinates are within the bounds of the game grid.
  boolean inBounds(int x, int y) {
    return (x > boardBeginningX && x < boardBeginningX + (boardWidth * squareSize) && y > boardBeginningY
        && y < boardBeginningY + (boardHeight * squareSize));
  }
}
