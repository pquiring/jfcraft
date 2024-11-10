package jfcraft.item;

/** Minecart (item)
 *
 * @author pquiring
 */

import jfcraft.client.*;
import jfcraft.block.*;
import jfcraft.data.*;
import jfcraft.entity.*;

public class ItemMinecart extends ItemBase {
  public ItemMinecart(String id, String names[], String texture[]) {
    super(id,names,texture);
    canPlace = true;
  }
  public boolean place(Client client, Coords c) {
    Minecart e = new Minecart();
    e.init(Static.server.world);
    e.dim = c.chunk.dim;
    e.uid = Static.server.world.generateUID();
    e.pos.x = c.x + 0.5f;
    e.pos.y = c.y;
    e.pos.z = c.z + 0.5f;
    c.chunk.addEntity(e);
    Static.server.world.addEntity(e);
    Static.server.broadcastEntitySpawn(e);
    return true;
  }
  public boolean canPlace(Coords c) {
    //can only place on rails
    BlockBase block1 = Static.blocks.blocks[c.chunk.getBlock(c.gx,c.gy,c.gz)];
    BlockBase block2 = Static.blocks.blocks[c.chunk.getBlock2(c.gx,c.gy,c.gz)];
    return BlockRail.isRail(block1.id) && block2.canReplace;
  }
}
