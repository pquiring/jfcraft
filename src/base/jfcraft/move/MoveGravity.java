package jfcraft.move;

/** Moves an entity with just gravity.
 *  This is a client-side only class (use in ctick())
 *
 * @author pquiring
 */

import jfcraft.entity.*;
import jfcraft.data.*;

public class MoveGravity implements MoveBase {
  public void move(EntityBase entity) {
    Chunk chunk1 = entity.getChunk();
    entity.move(false, false, false, -1, EntityBase.AVOID_NONE);
    Chunk chunk2 = entity.getChunk();
    if (chunk1 != chunk2) {
      chunk1.delEntity(entity);
      chunk2.addEntity(entity);
    }
  }
}
