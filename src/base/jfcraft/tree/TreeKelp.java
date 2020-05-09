package jfcraft.tree;

/**
 * TreeKelp
 */

import jfcraft.biome.*;
import jfcraft.block.*;
import jfcraft.data.*;

public class TreeKelp extends TreeBase {

  public void plant(int x, int y, int z, BiomeData data) {
    int height = data.b1 % 22;
    int max = Static.SEALEVEL - 1;
    for(int a=0;a<height;a++) {
      if (y >= max) break;
      if (!setBlock(x,y,z,Blocks.KELPPLANT,0,0)) break;
      y++;
    }
    if (y > Static.SEALEVEL) return;
    setBlock(x,y,z,Blocks.KELPPLANT,0,BlockKelpPlant.VAR_TOP);
  }
}
