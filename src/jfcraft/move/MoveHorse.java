package jfcraft.move;

/** Moves a horse.
 *
 * @author pquiring
 */

import jfcraft.data.*;
import jfcraft.entity.*;
import static jfcraft.entity.EntityBase.*;

public class MoveHorse implements MoveBase {
  private int untameCounter;
  private static final int untameCounterMax = 20 * 3;  //min time before occupant gets ejected
  public void move(EntityBase entity) {
    Horse horse = (Horse)entity;
    boolean moved;
    boolean wasmoving = horse.mode != MODE_IDLE;
    //do AI
    horse.updateFlags(0,0,0);
    //random walking
    if (Static.debugRotate) {
      //test rotate in a spot
      horse.ang.y += 1.0f;
      if (horse.ang.y > 180f) { horse.ang.y = -180f; }
      horse.ang.x += 1.0f;
      if (horse.ang.x > 360.0f) { horse.ang.x = 0.0f; }
      horse.mode = MODE_WALK;
      moved = true;
    } else {
      if (horse.occupant != null && horse.isTamed() && horse.haveSaddle()) {
        horse.mode = MODE_IDLE;
        if (horse.up || horse.dn) {
          if (horse.run && horse.up)
            horse.mode = MODE_RUN;
          else
            horse.mode = MODE_WALK;
        } else {
          horse.mode = MODE_IDLE;
        }
        if (horse.onGround && horse.jump) {
          horse.jump();
        }
        horse.ang.y = horse.occupant.ang.y;
        if (horse.dn) horse.ang.y += 180f;
        moved = horse.moveEntity();
        if (horse.dn) horse.ang.y -= 180f;
      } else {
        horse.randomWalking();
        moved = horse.moveEntity();
        if (horse.occupant != null && !horse.isTamed()) {
          horse.tameCounter++;
          if (horse.tameCounter >= Horse.tameCounterMax) {
            horse.setTamed(true);
            Static.server.broadcastEntityFlags(horse);
          } else {
            untameCounter++;
            if ((untameCounter > untameCounterMax) && (r.nextInt(64) == 0)) {
              //dismount occupant
              untameCounter = 0;
              horse.occupant.vehicle = null;
              Static.server.broadcastRiding(horse, horse.occupant, false);
              horse.occupant = null;
            }
          }
        }
      }
    }
    if (moved || wasmoving) {
      Static.server.broadcastEntityMove(horse, false);
    }
    if (horse.occupant != null) {
      Chunk chunk1 = horse.occupant.getChunk();
      horse.occupant.pos.x = horse.pos.x;
      horse.occupant.pos.y = horse.pos.y - horse.occupant.legLength + 1.5f;
      horse.occupant.pos.z = horse.pos.z;
      Static.server.broadcastEntityMove(horse.occupant, true);
      Chunk chunk2 = horse.occupant.getChunk();
      if (chunk2 != chunk1) {
        chunk1.delEntity(horse.occupant);
        chunk2.addEntity(horse.occupant);
      }
    }
    if (horse.occupant != null && horse.sneak) {
      horse.occupant.vehicle = null;
      Static.server.broadcastRiding(horse, horse.occupant, false);
      horse.occupant = null;
      if (!horse.isTamed()) untameCounter = 0;
    }
  }
}
