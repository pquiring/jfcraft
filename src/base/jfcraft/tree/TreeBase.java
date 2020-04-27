package jfcraft.tree;

/**
 * TreeBase
 */

import jfcraft.data.*;
import static jfcraft.data.Direction.*;

public abstract class TreeBase {
  public abstract void plant(int x,int y, int z, Chunk chunk);
  public Chunk chunk;
  public void setBlock(int x, int y, int z, char id, int dir, int var) {
    if (y < 1) return;  //do not change bedrock
    if (y > 255) return;
    Chunk c = chunk;
    while (x < 0) {
      c = c.W;
      x += 16;
    }
    while (x > 15) {
      c = c.E;
      x -= 16;
    }
    while (z < 0) {
      c = c.N;
      z += 16;
    }
    while (z > 15) {
      c = c.S;
      z -= 16;
    }
    char orgid = c.getBlock(x, y, z).id;
    if (orgid != Blocks.AIR && orgid != Blocks.SNOW) return;
    c.setBlock(x, y, z, id, Chunk.makeBits(dir,var));
  }
};
