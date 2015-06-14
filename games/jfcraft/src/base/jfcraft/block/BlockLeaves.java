package jfcraft.block;

/**
 *
 * @author pquiring
 */

import jfcraft.data.*;
import static jfcraft.data.Direction.*;

public class BlockLeaves extends BlockOpaqueVarPerf {
  public BlockLeaves(String name, String names[], String textures[], String textures2[]) {
    super(name, names, textures, textures2);
  }
  public boolean canSupportBlock(Coords c) {
    return (c.block.id == Blocks.SNOW);
  }
  //TODO : decay leaves
}
