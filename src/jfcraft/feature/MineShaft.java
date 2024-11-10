package jfcraft.feature;

/**
 * MineShaft - horizontal abandoned shafts.
 */

import jfcraft.gen.*;
import jfcraft.biome.*;
import jfcraft.data.*;
import static jfcraft.data.Direction.*;
import static jfcraft.data.Static.*;

public class MineShaft {
  private boolean top;
  private boolean bottom;
  private int wx, wz;
  private int level;
  private GeneratorChunk chunk;
  private static final int min_level = 1;
  private static final int max_level = 5;
  public void build(GeneratorChunk chunk, BiomeData data) {
    this.chunk = chunk;
    wx = chunk.cx * 16;
    wz = chunk.cz * 16;
    for(level = min_level; level <= max_level;level++) {
      wx++;
      wz++;
      int dir = Static.noiseInt(N_RANDOM1, 3, wx, wz);
      top = false;
      bottom = false;
      switch(dir) {
        case 0:
          int w_a_dir = Static.noiseInt(N_RANDOM1, 3, wx - 16 + 1, wz + 1);
          int e_b_dir = Static.noiseInt(N_RANDOM1, 3, wx + 16 - 1, wz - 1);
          if (level < max_level && w_a_dir == dir) {
            if (Static.noiseInt(N_RANDOM2, 10, wx - 16 + 1, wz + 1) == 0) {
              bottom = true;
              buildStepsNSBottomEast();
            }
          }
          if (level > min_level && e_b_dir == dir) {
            if (Static.noiseInt(N_RANDOM2, 10, wx, wz) == 0) {
              top = true;
              buildStepsNSTopWest();
            }
          }
          buildNS();
          break;
        case 1:
          int n_a_dir = Static.noiseInt(N_RANDOM1, 3, wx + 1, wz - 16 + 1);
          int s_b_dir = Static.noiseInt(N_RANDOM1, 3, wx - 1, wz + 16 - 1);
          if (level < max_level && n_a_dir == dir) {
            if (Static.noiseInt(N_RANDOM2, 10, wx + 1, wz - 16 + 1) == 0) {
              bottom = true;
              buildStepsWEBottomSouth();
            }
          }
          if (level > min_level && s_b_dir == dir) {
            if (Static.noiseInt(N_RANDOM2, 10, wx, wz) == 0) {
              top = true;
              buildStepsWETopNorth();
            }
          }
          buildWE();
          break;
        case 2:
          //junction
          buildNS();
          buildWE();
          break;
      }
    }
  }
  private void buildWE() {
    int zp = 0, yp = level * 7 + 4;
    if (top) {
      zp = 6;
    } else if (bottom) {
      zp = 7;
    } else {
      zp = Static.noiseInt(N_RANDOM2, 4, wx, wz) + 6;
    }
    for(int x=0;x<16;x++) {
      for(int y=0;y<3;y++) {
        for(int z=0;z<3;z++) {
          chunk.clearBlock(x, y + yp,z + zp);
        }
      }
      chunk.setBlock(x,yp,zp+1, Blocks.RAIL, Chunk.makeBits(E, W));
      if (x % 4 == 2) {
        //build support
        chunk.setBlock(x,yp+2,zp+0, Blocks.PLANKS, 0);
        chunk.setBlock(x,yp+2,zp+1, Blocks.PLANKS, 0);
        chunk.setBlock(x,yp+2,zp+2, Blocks.PLANKS, 0);
        chunk.setBlock(x,yp+1,zp+0, Blocks.FENCE, 0);
        chunk.setBlock(x,yp+1,zp+2, Blocks.FENCE, 0);
        chunk.setBlock(x,yp+0,zp+0, Blocks.FENCE, 0);
        chunk.setBlock(x,yp+0,zp+2, Blocks.FENCE, 0);
      }
    }
  }
  private void buildNS() {
    int xp = 0, yp = level * 7 + 4;
    if (top) {
      xp = 6;
    } else if (bottom) {
      xp = 7;
    } else {
      xp = Static.noiseInt(N_RANDOM2, 4, wx, wz) + 6;
    }
    for(int z=0;z<16;z++) {
      for(int y=0;y<3;y++) {
        for(int x=0;x<3;x++) {
          chunk.clearBlock(x + xp,y + yp,z);
        }
      }
      chunk.setBlock(xp+1,yp,z, Blocks.RAIL, Chunk.makeBits(N, S));
      if (z % 4 == 2) {
        //build support
        chunk.setBlock(xp+0,yp+2,z, Blocks.PLANKS, 0);
        chunk.setBlock(xp+1,yp+2,z, Blocks.PLANKS, 0);
        chunk.setBlock(xp+2,yp+2,z, Blocks.PLANKS, 0);
        chunk.setBlock(xp+0,yp+1,z, Blocks.FENCE, 0);
        chunk.setBlock(xp+2,yp+1,z, Blocks.FENCE, 0);
        chunk.setBlock(xp+0,yp+0,z, Blocks.FENCE, 0);
        chunk.setBlock(xp+2,yp+0,z, Blocks.FENCE, 0);
      }
    }
  }
  private void buildStepsNSTopWest() {
    int yp = level * 7 + 4;
    int xp = 9;
    int zp = 6;
    int cnt = 0;
    for(int step=0;step<7;step++) {
      for(int y=0;y<4;y++) {
        for(int z=0;z<3;z++) {
          chunk.clearBlock(xp,y + yp,z + zp);
        }
      }
      cnt++;
      if (cnt == 2) {
        yp--;
        cnt = 0;
      }
      xp++;
    }
  }
  private void buildStepsNSBottomEast() {
    int yp = level * 7 + 4;
    int xp = 6;
    int zp = 6;
    int cnt = 0;
    for(int step=0;step<7;step++) {
      for(int y=0;y<4;y++) {
        for(int z=0;z<3;z++) {
          chunk.clearBlock(xp,y + yp,z + zp);
        }
      }
      cnt++;
      if (cnt == 2) {
        yp++;
        cnt = 0;
      }
      xp--;
    }
  }
  private void buildStepsWETopNorth() {
    int yp = level * 7 + 4;
    int zp = 9;
    int xp = 6;
    int cnt = 0;
    for(int step=0;step<7;step++) {
      for(int y=0;y<4;y++) {
        for(int x=0;x<3;x++) {
          chunk.clearBlock(x + zp,y + yp,zp);
        }
      }
      cnt++;
      if (cnt == 2) {
        yp--;
        cnt = 0;
      }
      zp++;
    }
  }
  private void buildStepsWEBottomSouth() {
    int yp = level * 7 + 4;
    int zp = 6;
    int xp = 6;
    int cnt = 0;
    for(int step=0;step<7;step++) {
      for(int y=0;y<4;y++) {
        for(int x=0;x<3;x++) {
          chunk.clearBlock(x + xp,y + yp,zp);
        }
      }
      cnt++;
      if (cnt == 2) {
        yp++;
        cnt = 0;
      }
      zp--;
    }
  }
}
