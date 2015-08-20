package jfcraft.move;

/** Moves a minecart.
 *
 * @author pquiring
 */

import jfcraft.data.*;
import jfcraft.entity.*;
import static jfcraft.entity.EntityBase.*;

public class MoveMinecart implements MoveBase {
  public static Coords coords = new Coords();
  public void move(EntityBase entity) {
    Minecart minecart = (Minecart)entity;
    boolean moved = false;
    boolean wasOnRail = minecart.onRail;
    minecart.updateFlags(0,0,0);
    switch (minecart.moveOnRails(minecart.getChunk())) {
      case 1:
        moved = true;
        //no break
      case 0:
        if (minecart.occupant != null) {
          if (minecart.up) {
            if (minecart.speed < minecart.pushSpeed / 2f) {
              minecart.speed = minecart.pushSpeed;
              minecart.occupant.getDir(coords);
              minecart.dir = coords.dir_xz;
            }
          } else if (minecart.dn) {
            if (minecart.speed < minecart.pushSpeed) {
              minecart.speed = minecart.pushSpeed;
              minecart.occupant.getDir(coords);
              minecart.dir = Direction.opposite(coords.dir_xz);
            }
          }
        }
        break;
      case -1:
        moved = minecart.move(false, false, false, -1, AVOID_NONE);
        break;
    }
    if (moved || (wasOnRail != minecart.onRail)) {
      Static.server.broadcastEntityMove(minecart, false);
    }
    if (minecart.occupant != null) {
      Chunk chunk1 = minecart.occupant.getChunk();
      minecart.occupant.pos.x = minecart.pos.x;
      minecart.occupant.pos.y = minecart.pos.y - minecart.occupant.legLength + 0.3f;
      minecart.occupant.pos.z = minecart.pos.z;
      Static.server.broadcastEntityMove(minecart.occupant, true);
      Chunk chunk2 = minecart.occupant.getChunk();
      if (chunk2 != chunk1) {
        chunk1.delEntity(minecart.occupant);
        chunk2.addEntity(minecart.occupant);
      }
      if (minecart.sneak) {
        minecart.occupant.vehicle = null;
        Static.server.broadcastRiding(minecart, minecart.occupant, false);
        minecart.occupant = null;
      }
    }
  }
}
