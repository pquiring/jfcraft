package jfcraft.item;

/** Boat (item)
 *
 * @author pquiring
 */

import jfcraft.client.*;
import jfcraft.data.*;
import jfcraft.entity.*;

public class ItemBoat extends ItemBase {
  public ItemBoat(String name, String names[], String texture[]) {
    super(name,names,texture);
    canPlace = true;
    canPlaceInWater = true;
  }
  public boolean place(Client client, Coords c) {
    Boat b = new Boat();
    b.init(Static.server.world);
    b.dim = c.chunk.dim;
    b.uid = Static.server.world.generateUID();
    b.pos.x = c.x + 0.5f;
    b.pos.y = c.y + 1;
    b.pos.z = c.z + 0.5f;
    c.chunk.addEntity(b);
    Static.server.world.addEntity(b);
    Static.server.broadcastEntitySpawn(b);
    return true;
  }
}
