package jfcraft.tree;

/**
 * TreeNormal
 */

import jfcraft.data.*;
import static jfcraft.data.Direction.*;

public class TreeNormal extends TreeBase {
  public byte var;
  public boolean snow;
  public TreeNormal setVar(byte var) {
    this.var = var;
    return this;
  }
  public TreeNormal setSnow(boolean snow) {
    this.snow = snow;
    return this;
  }
  private int leaves[] = {
    0,0,0,2,3,2
  };
  public void plant(int x,int y, int z) {
    for(int yy=0;yy<leaves.length;yy++) {
      int wood = leaves.length - 2;
      if (yy < wood) {
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
