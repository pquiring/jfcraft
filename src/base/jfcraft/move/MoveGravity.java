package jfcraft.move;

/** Moves an entity with just gravity.
 *  This is a client-side only class (use in ctick())
 *
 * @author pquiring
 */

import jfcraft.entity.*;

public class MoveGravity implements MoveBase {
  public void move(EntityBase entity) {
    entity.move(false, false, false, -1, EntityBase.AVOID_NONE);
  }
}
