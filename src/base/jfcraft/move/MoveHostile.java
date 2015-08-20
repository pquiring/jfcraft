package jfcraft.move;

/** Moves a hostile (zombie, skeleton, etc.)
 *
 * @author pquiring
 */

import jfcraft.data.*;
import jfcraft.entity.*;
import static jfcraft.entity.EntityBase.*;

public class MoveHostile implements MoveBase {
  public void move(EntityBase entity) {
    CreatureBase creature = (CreatureBase)entity;
    if (entity.target == null) {
      //getTarget();  //test!
    } else {
      if (entity.target.health == 0 || entity.target.offline) {
        entity.target = null;
      }
    }
    boolean moved;
    boolean wasmoving = entity.mode != MODE_IDLE;
    if (Static.debugRotate) {
      //test rotate in a spot
      entity.ang.y += 1.0f;
      if (entity.ang.y > 180f) { entity.ang.y = -180f; }
      entity.ang.x += 1.0f;
      if (entity.ang.x > 45.0f) { entity.ang.x = -45.0f; }
      entity.mode = MODE_WALK;
      moved = true;
    } else {
      if (entity.target != null) {
        creature.moveToTarget();
      } else {
        creature.randomWalking();
      }
      moved = creature.moveEntity();
    }
    if (entity.target != null || moved || wasmoving) Static.server.broadcastEntityMove(entity, false);
  }
}
