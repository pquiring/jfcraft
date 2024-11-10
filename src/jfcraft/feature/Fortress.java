package jfcraft.feature;

/**
 * Fortress.
 *
 * Horizontal abandoned fortress tunnels.
 *
 */

import jfcraft.biome.*;
import jfcraft.gen.*;

public class Fortress extends Eraser {
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
