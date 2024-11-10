package jfcraft.tree;

/**
 * TreeCactus (ok, not really a tree)
 */

import jfcraft.biome.*;
import jfcraft.data.*;

public class TreeCactus extends TreeBase {

  public void plant(int x, int y, int z, BiomeData data) {
    if (y <= Static.SEALEVEL) return;
    int height = data.b1 % 3 + 2;  //2-4
    for(int a=0;a<height;a++) {
      if (!setBlock(x,y,z,Blocks.CACTUS,0,0)) break;
      y++;
    }
  }
}
