abstract class TileImpl implements Tile {
  private TileStatus status;

  TileImpl() {
    status = TileStatus.HIDDEN;
  }

  @Override
  public void reveal() {
    if (status == TileStatus.FLAGGED) return;
    status = TileStatus.REVEALED;
  }

  @Override
  public void flag() {
    if (status == TileStatus.REVEALED) return;
    status = (status == TileStatus.FLAGGED) ? TileStatus.HIDDEN : TileStatus.FLAGGED;
  }

  @Override
  public void setFalseFlag() {  // TODO: Remove
    if (status != TileStatus.FLAGGED) return;
    status = TileStatus.FALSE_FLAGGED;
  }

  @Override
  public void reset() {
    status = TileStatus.HIDDEN;
  }

  @Override
  public TileStatus getStatus() {
    return status;
  }
}
