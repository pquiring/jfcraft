package jfcraft.block;

/** Block that can fall : sand / gravel.
 *
 * @author pquiring
 */

import jfcraft.data.*;
import jfcraft.entity.*;
import static jfcraft.data.Direction.*;

public class BlockFalling extends BlockOpaque {
  public BlockFalling(String id, String names[], String images[]) {
    super(id, names, images);
  }
  private Coords c = new Coords();
  public void tick(Chunk chunk, Tick tick) {
    //check if block below is empty
//    Static.log("tick falling:" + chunk + ":" + tick);
    char id1 = chunk.getID(tick.x, tick.y-1, tick.z);
    if (id1 == Blocks.AIR) {
      //convert to entity
      tick.toWorldCoords(chunk, c);
      MovingBlock mb = new MovingBlock();
      mb.init();
      mb.dim = chunk.dim;
      mb.uid = Static.world().generateUID();
      mb.pos.x = c.x;
      mb.pos.y = c.y;
      mb.pos.z = c.z;
      mb.blockid = id;
      mb.type = MovingBlock.FALL;
      mb.dir = B;
      chunk.addEntity(mb);
      Static.server.world.addEntity(mb);
      chunk.clearBlock(tick.x, tick.y, tick.z);
      Static.server.broadcastB2E(chunk.dim, c.x, c.y, c.z, mb.uid);
    }
    super.tick(chunk, tick);
  }
}
