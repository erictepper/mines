class GameTile {
    private int DISPLAY_STATUS;  // 0 for hidden tile, 1 for number tile, 2 for mine tile, 3 for flag tile, 4 for
                                 // false-flag tile
    private int TILE_TYPE;  // 1 for number tile, 2 for mine tile
    private int NUMBER_OF_ADJACENT_MINES;

    GameTile() {
        DISPLAY_STATUS = 0;
        TILE_TYPE = 1;
        NUMBER_OF_ADJACENT_MINES = 0;
    }

    void setTileType(int tileType) {
        TILE_TYPE = tileType;
    }

    void setNumberOfAdjacentMines(int numberOfAdjacentMines) {
        NUMBER_OF_ADJACENT_MINES = numberOfAdjacentMines;
    }

    // Returns 0 if the display status didn't change, 1 if it revealed a number square, and 2 if it revealed a
    // mine square.
    int reveal() {
        if (DISPLAY_STATUS == TILE_TYPE || DISPLAY_STATUS == 3) { return 0; }
        else {
            DISPLAY_STATUS = TILE_TYPE;
            return TILE_TYPE;
        }
    }

    // Marks the tile as a false flag if it is a number tile and has been flagged, otherwise does nothing.
    void markAsFalseFlag() {
        if (DISPLAY_STATUS == 3 && TILE_TYPE == 1) {
            DISPLAY_STATUS = 4;
        }
    }

    // Changes the flagged status of a hidden tile.
    // Returns 1 if this tile is originally un-flagged and becomes flagged,
    // returns -1 if it is originally flagged and becomes un-flagged,
    // else returns 0 if the position is already revealed (i.e. can't be flagged).
    int flag() {
        if (DISPLAY_STATUS == 0) { DISPLAY_STATUS = 3; return 1; }
        else if (DISPLAY_STATUS == 3) { DISPLAY_STATUS = 0; return -1; }
        else return 0;
    }

    void reset() {
        DISPLAY_STATUS = 0;
    }

    String getLabel() {
        if (DISPLAY_STATUS == 0) { return ""; }
        else if (DISPLAY_STATUS == 1) {
            if (NUMBER_OF_ADJACENT_MINES == 0) { return ""; }
            else return String.valueOf(NUMBER_OF_ADJACENT_MINES);
        }
        else if (DISPLAY_STATUS == 2) { return "M"; }
        else if (DISPLAY_STATUS == 3) { return "F"; }
        else {
            throw new IndexOutOfBoundsException();
        }
    }

    int getDisplayStatus() {
        return DISPLAY_STATUS;
    }

    int getActualStatus() {
        return TILE_TYPE;
    }

    int getNumberOfAdjacentMines() {
        return NUMBER_OF_ADJACENT_MINES;
    }
}
