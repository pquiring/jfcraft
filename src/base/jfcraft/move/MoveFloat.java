package jfcraft.move;

/** Move an entity which just floats in the air.
 *  (moves in a fixed direction)
 *  This is for client-side only (use in ctick())
 *
 * @author pquiring
 */

import jfcraft.entity.*;

public class MoveFloat implements MoveBase {
  private float dx, dy, dz;
  public MoveFloat(float x, float y, float z) {
    dx = x;
    dy = y;
    dz = z;
  }
  public void move(EntityBase entity) {
    entity.pos.x += dx;
    entity.pos.y += dy;
    entity.pos.z += dz;
  }
}
