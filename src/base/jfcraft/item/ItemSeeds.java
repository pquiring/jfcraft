package jfcraft.item;

/** Seeds (item)
 *
 * @author pquiring
 */

import jfcraft.client.*;
import jfcraft.block.*;
import jfcraft.data.*;
import static jfcraft.data.Direction.*;

public class ItemSeeds extends ItemBase {
  public ItemSeeds(String name, String names[], String textures[]) {
    super(name,names,textures);
  }
  public ItemBase setSeeds(String newID) {
    canPlace = true;
    isSeeds = true;
    seedPlantedName = newID;
    return this;
  }
  public boolean place(Client client, Coords c) {
    if (c.block.id != Blocks.DIRT) {
      Static.log("Seeds:not dirt");
      return false;
    }
    c.dir = B;
    c.adjacentBlock();
    int bits = c.chunk.getBits(c.gx, c.gy, c.gz);
//    Static.log("bits=" + bits);
    int var = Chunk.getVar(bits);
//    Static.log("var=" + var);
    if (var != BlockDirt.VAR_FARM_DRY && var != BlockDirt.VAR_FARM_WET) {
      Static.log("Seeds:not farm");
      return false;
    }
    c.dir = A;
    c.adjacentBlock();
    synchronized(c.chunk.lock) {
      //check if area is clear
      if (c.chunk.getID(c.gx, c.gy, c.gz) != 0) {
        Static.log("Seeds:no room to plant");
        return false;
      }
      //place seeds
      char id = seedPlantedID;
      if (id == 0) {
        Static.log("Seeds:not seeds?");
        return false;
      }
      c.chunk.setBlock(c.gx,c.gy,c.gz,id,0);
      Static.server.broadcastSetBlock(c.chunk.dim,c.x,c.y,c.z,id,0);
    }
    return true;
  }
}
