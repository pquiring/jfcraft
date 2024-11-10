package jfcraft.tree;

/**
 * TreeNormal
 */

import jfcraft.data.*;
import jfcraft.biome.*;
import static jfcraft.data.Direction.*;

public class TreeNormal extends TreeBase {
  public byte var;
  public boolean snow;
  public boolean vines;
  public TreeNormal setVar(byte var) {
    this.var = var;
    return this;
  }
  public TreeNormal setSnow(boolean snow) {
    this.snow = snow;
    return this;
  }
  public TreeNormal setVines(boolean vines) {
    this.vines = vines;
    return this;
  }
  private int leafProfile[] = {
    0,0,0,2,3,2
  };
  private int vineLength(BiomeData data, int x, int z, int max) {
    return (data.b1 | x | z) % max;
  }
  public void plant(int x, int y, int z, BiomeData data) {
    if (y <= Static.SEALEVEL) return;
    int vinesSize = 0;
    int vinesYY = 0;
    for(int yy=0;yy<leafProfile.length;yy++) {
      int wood = leafProfile.length - 2;
      if (yy < wood) {
        setBlock(x  ,y+yy,z  ,Blocks.WOOD, 0, var);
      }
      int leafSize = leafProfile[yy];
      if (leafSize >= vinesSize) {
        vinesSize = leafSize;
        vinesYY = yy;
      }
      if (leafSize == 0) continue;
      int neg = -leafSize;
      int pos = leafSize;
      for(int xx = neg; xx <= pos; xx++) {
        for(int zz = neg; zz <= pos; zz++) {
          setBlock(x+xx,y+yy,z+zz,Blocks.LEAVES, getDir(xx,zz), var);
          if (snow) {
            setBlock(x+xx,y+yy+1,z+zz,Blocks.SNOW, 0, 0);
          }
        }
      }
    }
    if (vines) {
      int neg = -vinesSize;
      int pos = vinesSize;
      vinesSize++;
      for(int xx = neg; xx <= pos; xx++) {
        {
          //S
          int zz = z + vinesSize;
          int vineBottom = vineLength(data,xx,zz,vinesYY);
          for(int yy = vinesYY; yy >= vineBottom; yy--) {
            setBlock(x+xx,y+yy,zz,Blocks.VINES,N,0);
          }
        }
        {
          //N
          int zz = z - vinesSize;
          int vineBottom = vineLength(data,xx,zz,vinesYY);
          for(int yy = vinesYY; yy >= vineBottom; yy--) {
            setBlock(x+xx,y+yy,zz,Blocks.VINES,S,0);
          }
        }
      }
      for(int zz = neg; zz <= pos; zz++) {
        {
          //E
          int xx = x + vinesSize;
          int vineBottom = vineLength(data,xx,zz,vinesYY);
          for(int yy = vinesYY; yy >= vineBottom; yy--) {
            setBlock(xx,y+yy,z+zz,Blocks.VINES,W,0);
          }
        }
        {
          //W
          int xx = x - vinesSize;
          int vineBottom = vineLength(data,xx,zz,vinesYY);
          for(int yy = vinesYY; yy >= vineBottom; yy--) {
            setBlock(xx,y+yy,z+zz,Blocks.VINES,E,0);
          }
        }
      }
    }
  }
}
