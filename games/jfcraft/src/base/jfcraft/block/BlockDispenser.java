package jfcraft.block;

import jfcraft.data.*;
import jfcraft.entity.*;
import jfcraft.item.*;
import static jfcraft.data.Direction.*;

/**
 *
 * @author pquiring
 */

public class BlockDispenser extends BlockDropper {
  public BlockDispenser(String id, String names[], String images[]) {
    super(id, names, images);
  }

  public void drop(Item item, float x, float y, float z, Chunk chunk, int dir) {
    if (item.id == Items.ARROW) {
      shot(new Arrow(),x,y,z,chunk,dir);
    } else {
      super.drop(item, x,y,z, chunk, dir);
    }
  }

  private void shot(EntityBase e, float x, float y, float z, Chunk chunk, int dir) {
    e.init();
    e.dim = chunk.dim;
    e.uid = Static.world().generateUID();
    e.pos.x = x;
    e.pos.y = y;
    e.pos.z = z;
    e.ang.x = 0;
    e.ang.y = 0;
    e.ang.z = 0;
    e.vel.x = 0;
    e.vel.y = 0;
    e.vel.z = 0;
    //max velocity = 60m/s
    float vel = 30f / 20f;  //vel/tick
    switch (dir) {
      case N: /*default ang*/ e.vel.z = -vel; e.pos.z -= 1.0f; e.ang.x = -15f; break;
      case E: e.ang.y =  90f; e.vel.x =  vel; e.pos.x += 1.0f; e.ang.x = -15f; break;
      case S: e.ang.y = 180f; e.vel.z =  vel; e.pos.z += 1.0f; e.ang.x = -15f; break;
      case W: e.ang.y = -90f; e.vel.x = -vel; e.pos.x -= 1.0f; e.ang.x = -15f; break;
      case A: e.ang.x = -90f; e.vel.y =  vel; e.pos.y += 1.0f; break;
      case B: e.ang.x =  90f; e.vel.y = -vel; e.pos.y -= 1.0f; break;
    }
    chunk.addEntity(e);
    Static.server.world.addEntity(e);
    Static.server.broadcastEntitySpawn(e);
  }
}
