class GameTile {
    private int DISPLAY_STATUS;  // 0 for hidden tile, 1 for number tile, 2 for bomb tile, 3 for flag tile
    private int TILE_TYPE;  // 1 for number tile, 2 for bomb tile
    private int NUMBER_OF_ADJACENT_BOMBS;

    GameTile() {
        DISPLAY_STATUS = 0;
        TILE_TYPE = 1;
        NUMBER_OF_ADJACENT_BOMBS = 0;
    }

    void setTileType(int tileType) {
        TILE_TYPE = tileType;
    }

    void setNumberOfAdjacentBombs(int numberOfAdjacentBombs) {
        NUMBER_OF_ADJACENT_BOMBS = numberOfAdjacentBombs;
    }

    // Returns 0 if the display status didn't change, 1 if it revealed a number square, and 2 if it revealed a
    // bomb square.
    int reveal() {
        if (DISPLAY_STATUS == TILE_TYPE || DISPLAY_STATUS == 3) { return 0; }
        else {
            DISPLAY_STATUS = TILE_TYPE;
            return TILE_TYPE;
        }
    }

    // Returns 1 if flagged, returns -1 if un-flagged, else returns 0.
    int flag() {
        if (DISPLAY_STATUS == 0) { DISPLAY_STATUS = 3; return 1; }
        else if (DISPLAY_STATUS == 3) { DISPLAY_STATUS = 0; return -1; }
        else return 0;
    }

    String getLabel() {
        if (DISPLAY_STATUS == 0) { return ""; }
        else if (DISPLAY_STATUS == 1) {
            if (NUMBER_OF_ADJACENT_BOMBS == 0) { return ""; }
            else return String.valueOf(NUMBER_OF_ADJACENT_BOMBS);
        }
        else if (DISPLAY_STATUS == 2) { return "B"; }
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

    int getNumberOfAdjacentBombs() {
        return NUMBER_OF_ADJACENT_BOMBS;
    }
}
