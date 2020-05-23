package jfcraft.item;

/** Kelp
 *
 * @author pquiring
 */

import jfcraft.client.*;
import jfcraft.block.*;
import jfcraft.data.*;
import static jfcraft.data.Direction.*;

public class ItemKelp extends ItemBase {
  public ItemKelp(String name, String names[], String textures[]) {
    super(name,names,textures);
  }
  public ItemBase setPlant(String newID) {
    canPlace = true;
    isSeeds = true;
    seedPlantedName = newID;
    return this;
  }
  public boolean place(Client client, Coords c) {
    if (c.block.id != Blocks.SAND && c.block.id != Blocks.DIRT && c.block.id != Blocks.CLAY) {
      return false;
    }
    //check if under water
    if (c.chunk.getBlock2(c.gx,c.gy + 1,c.gz) != Blocks.WATER) {
      return false;
    }
    //place kelp
    c.chunk.setBlockIfEmpty(c.gx,c.gy,c.gz,seedPlantedID,0);
    Static.server.broadcastSetBlock(c.chunk.dim,c.x,c.y,c.z,seedPlantedID,0);
    return true;
  }
}
