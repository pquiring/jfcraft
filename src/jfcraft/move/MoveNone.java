package jfcraft.move;

/** Does not move an entity.
 *
 * @author pquiring
 */

import jfcraft.entity.EntityBase;

public class MoveNone implements MoveBase {
  public void move(EntityBase entity) {}
}
