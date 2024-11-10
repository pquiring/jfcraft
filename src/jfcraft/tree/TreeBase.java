package jfcraft.tree;

/**
 * TreeBase
 */

import jfcraft.biome.BiomeData;
import jfcraft.data.*;
import static jfcraft.data.Direction.*;

public abstract class TreeBase {
  public abstract void plant(int x, int y, int z, BiomeData data);
  public static Chunk chunk;
  public static void setChunk(Chunk chunk) {
    TreeBase.chunk = chunk;
  }
  public boolean setBlock(int x, int y, int z, char id, int dir, int var) {
    if (y < 1) return false;  //do not change bedrock
    if (y > 255) return false;
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
    char orgid = c.getBlockType(x, y, z).id;
    if (orgid != Blocks.AIR && orgid != Blocks.SNOW && orgid != Blocks.WATER) return false;
    c.setBlock(x, y, z, id, Chunk.makeBits(dir,var));
    return true;
  }
  public int getDir(int xx, int zz) {
    if (xx < 0) return E;
    if (xx > 0) return W;
    if (zz < 0) return S;
    if (zz > 0) return N;
    return B;  //anchor below
  }
};
