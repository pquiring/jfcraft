package jfcraft.move;

/** Moves a boat.
 *
 * @author pquiring
 */

import jfcraft.data.*;
import jfcraft.entity.*;
import static jfcraft.entity.EntityBase.*;

public class MoveBoat implements MoveBase {
  public void move(EntityBase entity) {
    Boat boat = (Boat)entity;
    boat.updateFlags(0,0,0);
    if (boat.occupant != null) {
      float speed = 0;
      if (boat.onWater) {
        if (boat.run)
          speed = boat.fastWaterSpeed;
        else
          speed = boat.waterSpeed;
      }
      else {
        speed = boat.landSpeed;
      }
      if (boat.up || boat.dn) {
        boat.occupant.calcVectors(speed / 20.0f, move_vectors);
        float xv = 0, zv = 0;
        if (boat.up) {
          xv += move_vectors.forward.v[0];
          zv += move_vectors.forward.v[2];
        }
        if (boat.dn) {
          xv += -move_vectors.forward.v[0];
          zv += -move_vectors.forward.v[2];
        }
        if (xv != 0) boat.setXVel(xv);
        if (zv != 0) boat.setZVel(zv);
        boat.ang.y = boat.occupant.ang.y;
      }
    }
    boolean moved = boat.move(false, true, false, -1, AVOID_NONE);
    if (moved) {
      Static.server.broadcastEntityMove(boat, false);
    }
    if (boat.occupant != null) {
      Chunk chunk1 = boat.occupant.getChunk();
      boat.occupant.pos.x = boat.pos.x;
      boat.occupant.pos.y = boat.pos.y - boat.occupant.legLength;
      boat.occupant.pos.z = boat.pos.z;
      Static.server.broadcastEntityMove(boat.occupant, true);
      Chunk chunk2 = boat.occupant.getChunk();
      if (chunk2 != chunk1) {
        chunk1.delEntity(boat.occupant);
        chunk2.addEntity(boat.occupant);
      }
      if (boat.sneak) {
        boat.occupant.vehicle = null;
        Static.server.broadcastRiding(boat, boat.occupant, false);
        boat.occupant = null;
      }
    }
  }
}
