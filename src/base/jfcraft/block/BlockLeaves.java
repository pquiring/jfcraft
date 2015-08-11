package jfcraft.block;

/**
 *
 * @author pquiring
 */

import jfcraft.data.*;

public class BlockLeaves extends BlockOpaqueVarPerf {
  public BlockLeaves(String name, String names[], String textures[], String textures2[]) {
    super(name, names, textures, textures2);
    canSpawnOn = false;  //stop animals from spawning on trees
    absorbLight = 1;
    isDirFace = true;
  }
  public boolean canSupportBlock(Coords c) {
    return (c.block.id == Blocks.SNOW);
  }

  private static Coords supportingBlock = new Coords();
  private static Coords thisBlock = new Coords();

  public void tick(Chunk chunk, Tick tick) {
    tick.toWorldCoords(chunk, thisBlock);
    if (tick.t1 > 0) {
      tick.t1++;
      if (tick.t1 == 5) {
        destroy(null, thisBlock, true);
        chunk.delTick(tick);
      }
      return;
    }
    Static.server.world.getBlock(chunk.dim, thisBlock.x, thisBlock.y, thisBlock.z, thisBlock);
    supportingBlock.copy(thisBlock);
    supportingBlock.adjacentBlock();
    Static.server.world.getBlock(thisBlock.chunk.dim, supportingBlock.x, supportingBlock.y, supportingBlock.z, supportingBlock);
    char support_id = supportingBlock.block.id;
    if (support_id != Blocks.WOOD && support_id != Blocks.LEAVES) {
      tick.t1 = 1;
    } else {
      chunk.delTick(tick);
    }
  }
}
