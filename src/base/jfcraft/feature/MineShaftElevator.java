package jfcraft.feature;

/**
 * MineShaftElevator.
 *
 * Ladders to connect different levels.
 *
 */

public class MineShaftElevator extends Eraser {
  public boolean setup() {
    return false;
  }

  public void move() {
  }

  public boolean endPath() {
    return false;
  }

  public boolean nextPath() {
    return false;
  }

  public void preErase() {
  }

  public void postErase() {
    //add random rails and support beams
  }
}
