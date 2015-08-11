package jfcraft.block;

import jfcraft.data.*;

/**
 *
 * @author pquiring
 */
public class BlockGrass extends BlockDirt {
  public BlockGrass(String id, String names[], String images[]) {
    super(id, names, images);
  }
  public void rtick(Chunk chunk, int gx,int gy,int gz) {
    int x = chunk.cx * 16 + gx;
    int y = gy;
    int z = chunk.cz * 16 + gz;
    //is covered ? convert to dirt
    char id1 = chunk.getID(gx, gy+1, gz);
    boolean solid = Static.blocks.blocks[id1].isSolid;
    char id2 = chunk.getID2(gx, gy+1, gz);
    if (solid || id2 == Blocks.WATER || id2 == Blocks.LAVA) {
      chunk.setBlock(gx, gy, gz, Blocks.DIRT, 0);
      Static.server.broadcastSetBlock(chunk.dim, x, y, z, Blocks.DIRT, 0);
    }
  }
}
