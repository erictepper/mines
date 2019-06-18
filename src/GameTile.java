class GameTile {
    private int displayStatus;  // 0 for hidden tile, 1 for number tile, 2 for mine tile, 3 for flag tile, 4 for
                                 // false-flag tile
    private int tileType;  // 1 for number tile, 2 for mine tile
    private int adjacentMinesCount;

    GameTile() {
        displayStatus = 0;
        tileType = 1;
        adjacentMinesCount = 0;
    }

    void setTileType(int tileType) {
        this.tileType = tileType;
    }

    void setAdjacentMinesCount(int count) {
        adjacentMinesCount = count;
    }

    // Returns 0 if the display status didn't change, 1 if it revealed a number square, and 2 if it revealed a
    // mine square.
    int reveal() {
        if (displayStatus == tileType || displayStatus == 3) { return 0; }
        else {
            displayStatus = tileType;
            return tileType;
        }
    }

    // Marks the tile as a false flag if it is a number tile and has been flagged, otherwise does nothing.
    void setFalseFlag() {
        if (displayStatus == 3 && tileType == 1) {
            displayStatus = 4;
        }
    }

    // Changes the flagged status of a hidden tile.
    // Returns 1 if this tile is originally un-flagged and becomes flagged,
    // returns -1 if it is originally flagged and becomes un-flagged,
    // else returns 0 if the position is already revealed (i.e. can't be flagged).
    int flag() {
        if (displayStatus == 0) { displayStatus = 3; return 1; }
        else if (displayStatus == 3) { displayStatus = 0; return -1; }
        else return 0;
    }

    void reset() {
        displayStatus = 0;
    }

    String label() {
        if (displayStatus == 0) { return ""; }
        else if (displayStatus == 1) {
            if (adjacentMinesCount == 0) { return ""; }
            else return String.valueOf(adjacentMinesCount);
        }
        else if (displayStatus == 2) { return "M"; }
        else if (displayStatus == 3) { return "F"; }
        else {
            throw new IndexOutOfBoundsException();
        }
    }

    int getDisplayStatus() {
        return displayStatus;
    }

    int getActualStatus() {
        return tileType;
    }

    int getAdjacentMinesCount() {
        return adjacentMinesCount;
    }
}
