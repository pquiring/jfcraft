package jfcraft.tree;

/**
 * TreeBush
 */

import jfcraft.biome.BiomeData;
import jfcraft.data.*;
import static jfcraft.data.Direction.*;

public class TreeBush extends TreeBase {
  public byte var;
  public boolean snow;
  public TreeBush setVar(byte var) {
    this.var = var;
    return this;
  }
  public TreeBush setSnow(boolean snow) {
    this.snow = snow;
    return this;
  }
  private int leaves[] = {
    3,4,3,2
  };
  public void plant(int x, int y, int z, BiomeData data) {
    if (y <= Static.SEALEVEL) return;
    for(int yy=0;yy<leaves.length;yy++) {
      if (yy == 0) {
        setBlock(x  ,y+yy,z  ,Blocks.WOOD, 0, var);
      }
      int leaveCnt = leaves[yy];
      if (leaveCnt == 0) continue;
      int neg = -leaveCnt;
      int pos = leaveCnt;
      for(int xx = neg; xx <= pos; xx++) {
        for(int zz = neg; zz <= pos; zz++) {
          setBlock(x+xx,y+yy,z+zz,Blocks.LEAVES, getDir(xx,zz), var);
          if (snow) {
            setBlock(x+xx,y+yy+1,z+zz,Blocks.SNOW, 0, 0);
          }
        }
      }
    }
  }
}
