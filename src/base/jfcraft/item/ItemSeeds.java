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
    //place seeds
    c.chunk.setBlockIfEmpty(c.gx,c.gy,c.gz,seedPlantedID,0);
    Static.server.broadcastSetBlock(c.chunk.dim,c.x,c.y,c.z,id,0);
    return true;
  }
}
