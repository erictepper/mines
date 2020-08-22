import javax.imageio.ImageIO;
import java.util.*;
import java.awt.*;
import java.lang.Math;

class GameGrid {
  private static int squareSize;
  private static Image mineImage;
  private static Image flagImage;
  private static Image falseFlagImage;

  private final int boardHeight;
  private final int boardWidth;
  private final int numberOfMines;
  private final Tile[][] gameGrid;
  private final int boardBeginningX;
  private final int boardBeginningY;

  // gameSize should be one of "beginner", "intermediate", or "expert".
  GameGrid(String gameSize, int boardXStart, int boardYStart, int boardSquareSize) {
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
      mineImage = ImageIO.read(getClass().getResource("images/mine.png"));
      flagImage = ImageIO.read(getClass().getResource("images/flag.png"));
      falseFlagImage = ImageIO.read(getClass().getResource("images/false_flag.png"));
    } catch (Exception e) {
      mineImage = null;
      flagImage = null;
      falseFlagImage = null;
    }

    boardBeginningX = boardXStart;
    boardBeginningY = boardYStart;
    squareSize = boardSquareSize;

    // Initializes the game grid.
    gameGrid = new Tile[boardHeight][boardWidth];
    for (int i = 0; i < boardHeight; i++) {
      for (int j = 0; j < boardWidth; j++) {
        gameGrid[i][j] = new NumberTile();
      }
    }

    // Generates random grid spaces to place mines.
    Random numberGenerator = new Random();
    for (int i = 0; i < numberOfMines; i++) {
      int row = numberGenerator.nextInt(boardHeight);
      int column = numberGenerator.nextInt(boardWidth);
      if (gameGrid[row][column].isMine()) {
        i--;
        continue;
      }

      gameGrid[row][column] = new MineTile();
    }

    // Updates the number of adjacent mines for each tile.
    updateAdjacentMines();
  }

  /**
   * Moves the mine at position (column, row) and also moves any directly adjacent mines.
   * @param row the row position around which to move mines
   * @param column the column position around which to move the mines
   */
  void moveMines(int row, int column) {
    Random numberGenerator = new Random();
    for (int adjacentRow = Math.max(0, row-1); adjacentRow < Math.min(boardHeight, row+2);
         adjacentRow++) {
      for (int adjacentColumn = Math.max(0, column-1); adjacentColumn < Math.min(boardWidth, column+2);
           adjacentColumn++) {
        if (!gameGrid[adjacentRow][adjacentColumn].isMine()) continue;

        gameGrid[adjacentRow][adjacentColumn] = new NumberTile();
        int j = numberGenerator.nextInt(boardHeight);
        int k = numberGenerator.nextInt(boardWidth);
        while (gameGrid[j][k].isMine() ||
            (j >= Math.max(0, row-1) && j < Math.min(boardHeight, row+2) &&
                k >= Math.max(0, column-1) && k < Math.min(boardWidth, column+2))) {
          j = numberGenerator.nextInt(boardHeight);
          k = numberGenerator.nextInt(boardWidth);
        }
        gameGrid[j][k] = new MineTile();
      }
    }

    updateAdjacentMines();
  }

  /**
   * Reveals position (row, column) if it has an adjacent mine.
   * If this position has no adjacent mines, it reveals the space and recursively reveals adjacent spaces.
   * If the space has already been revealed, it does nothing.
   * @param row the row position of the tile to reveal
   * @param column the column position of the tile to reveal
   * @return the total number of tiles revealed, or -1 if the tile is a mine
   */
  int reveal(int row, int column) {
    Tile currentTile = gameGrid[row][column];
    // TODO - change bottom check to currentTile.getStatus() != TileStatus.HIDDEN when false flag is removed
    if (currentTile.getStatus() == TileStatus.REVEALED || currentTile.getStatus() == TileStatus.FLAGGED) return 0;

    currentTile.reveal();

    if (currentTile.isMine()) return -1;
    if (((NumberTile) currentTile).getAdjacentMinesCount() != 0) return 1;

    int totalRevealed = 1;
    for (int adjacentRow = Math.max(0, row-1); adjacentRow < Math.min(boardHeight, row+2); adjacentRow++) {
      for (int adjacentColumn = Math.max(0, column-1); adjacentColumn < Math.min(boardWidth, column+2);
           adjacentColumn++) {
        if ((adjacentRow != row) || (adjacentColumn != column)) {
          totalRevealed += reveal(adjacentRow, adjacentColumn);
        }
      }
    }
    return totalRevealed;
  }

  // Gives a player a hint. If there exists a false flag, mark it as a false flag. If no false flags exist,
  // mark a flag in a place it would be the most useful (e.g. next to a tile where only one flag is left).
  int giveHint() {
    // Firstly, searches the grid to see if there are any tiles falsely flagged. If one exists, mark it as a false
    // flag and return.
    for (int y = 0; y < boardHeight; y++) {
      for (int x = 0; x < boardWidth; x++) {
        Tile currentTile = gameGrid[y][x];
        if (!currentTile.isMine() && currentTile.getStatus() == TileStatus.FLAGGED) {
          currentTile.setFalseFlag();
          return -1;
        }
      }
    }

    // Searches for the space with the smallest difference between the number of adjacent flags and the number
    // of adjacent mines. This space must have already been revealed.
    int lowestMineFlagDifferenceCount = 9;
    int hintCentreXIndex = 0, hintCentreYIndex = 0;
    search: {
      for (int y = 0; y < boardHeight; y++) {
        for (int x = 0; x < boardWidth; x++) {
          if (gameGrid[y][x].isMine() || gameGrid[y][x].getStatus() != TileStatus.REVEALED) continue;

          int numberOfAdjacentFlags = 0;

          // Counts the number of adjacent flags for GAME_TILE[y][x].
          for (int i = Math.max(0, y - 1); i < Math.min(boardHeight, y + 2); i++) {
            for (int j = Math.max(0, x - 1); j < Math.min(boardWidth, x + 2); j++) {
              if (gameGrid[i][j].getStatus() == TileStatus.FLAGGED) {
                numberOfAdjacentFlags++;
              }
            }
          }

          int diff = ((NumberTile) gameGrid[y][x]).getAdjacentMinesCount() - numberOfAdjacentFlags;
          if (diff > 0 && diff < lowestMineFlagDifferenceCount) {
            lowestMineFlagDifferenceCount = diff;
            hintCentreYIndex = y;
            hintCentreXIndex = x;
          }
          if (diff == 1) {
            break search;
          }
        }
      }
    }

    if (gameGrid[hintCentreYIndex][hintCentreXIndex].getStatus() != TileStatus.REVEALED) { return 0; }

    for (int i = Math.max(0, hintCentreYIndex-1); i < Math.min(boardHeight, hintCentreYIndex+2); i++) {
      for (int j = Math.max(0, hintCentreXIndex - 1); j < Math.min(boardWidth, hintCentreXIndex+2); j++) {
        if (gameGrid[i][j].getStatus() == TileStatus.HIDDEN && gameGrid[i][j].isMine()) {
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
        if (gameGrid[y][x].isMine()) {
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
    Tile current = gameGrid[yPosition][xPosition];
    if (current.getStatus() == TileStatus.REVEALED) return 0;

    current.flag();
    return (current.getStatus() == TileStatus.FLAGGED) ? 1 : -1;
  }

  // Paints the grid in the view.
  void paint(Graphics g) {
    g.setFont(new Font("Courier New", Font.PLAIN, 20));

    for (int i = 0; i < boardHeight; i++) {
      for (int j = 0; j < boardWidth; j++) {
        switch (gameGrid[i][j].getStatus()) {
          case HIDDEN:  // hidden tile
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(boardBeginningX + (j * squareSize), boardBeginningY + (i * squareSize), squareSize,
                squareSize);
            break;
          case FLAGGED: // flag tile
            g.drawImage(flagImage, boardBeginningX + (j * squareSize), boardBeginningY + (i * squareSize),
                null);
            break;
          case FALSE_FLAGGED: // false-flag tile
            g.drawImage(falseFlagImage, boardBeginningX + (j * squareSize), boardBeginningY + (i * squareSize),
                null);
            break;
          default:
            if (gameGrid[i][j].isMine()) {
              g.drawImage(mineImage, boardBeginningX + (j * squareSize), boardBeginningY + (i * squareSize),
                  null);
            } else {
              g.setColor(Color.WHITE);
              g.fillRect(boardBeginningX + (j * squareSize), boardBeginningY + (i * squareSize), squareSize,
                  squareSize);
              g.setColor(Color.BLACK);
              int adjacentMinesCount = ((NumberTile) gameGrid[i][j]).getAdjacentMinesCount();
              String label = (adjacentMinesCount == 0) ? "" : String.valueOf(adjacentMinesCount);
              g.drawString(
                  label,
                  boardBeginningX + (j * squareSize) + 7,
                  boardBeginningY + ((i + 1) * squareSize) - 7
              );
            }
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

  /**
   * Counts and stores the number of adjacent mines for each GameTile.
   */
  private void updateAdjacentMines() {
    for (int row = 0; row < boardHeight; row++) {
      for (int column = 0; column < boardWidth; column++) {
        if (gameGrid[row][column].isMine()) continue;

        int totalMines = 0;
        for (int neighbourRow = Math.max(0, row-1); neighbourRow < Math.min(boardHeight, row+2); neighbourRow++) {
          for (int neighbourColumn = Math.max(0, column-1); neighbourColumn < Math.min(boardWidth, column+2);
               neighbourColumn++) {
            totalMines = (gameGrid[neighbourRow][neighbourColumn].isMine()) ? totalMines + 1 : totalMines;
          }
        }

        ((NumberTile) gameGrid[row][column]).setAdjacentMinesCount(totalMines);
      }
    }
  }
}
