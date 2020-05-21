class NumberTile extends TileImpl {
  private int adjacentMinesCount;

  NumberTile() {
    super();
    adjacentMinesCount = 0;
  }

  @Override
  public boolean isMine() {
    return false;
  }

  public int getAdjacentMinesCount() {
    return adjacentMinesCount;
  }

  public void setAdjacentMinesCount(int count) {
    adjacentMinesCount = count;
  }
}
