package jfcraft.move;

/** Moves an entity.
 *
 * This will be the foundation for A.I. for animals, creatures, etc.
 *
 * @author pquiring
 */

import jfcraft.entity.*;

public interface MoveBase {
  public void move(EntityBase entity);
}
