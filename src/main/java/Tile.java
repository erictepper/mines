public interface Tile {
  void reveal();

  void flag();

  void setFalseFlag();  // TODO: Remove

  void reset();

  TileStatus getStatus();

  boolean isMine();
}
