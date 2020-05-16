package jfcraft.gen;

/**
 * Cavern (under ground room)
 */

import java.util.*;

import jfcraft.biome.*;
import jfcraft.data.*;

public class Cavern {
  private Chunk chunk;
  private BiomeData data;
  private int cx16, cz16;

  private void setBlock(int x, int y, int z, char id, int bits) {
    if (id == 0) {
      clearBlock(x,y,z);
      return;
    }
    if (y < 1) return;  //do not change bedrock
    if (y > 255) return;
    chunk.setBlock(x, y, z, id, bits);
  }
  private void clearBlock(int x, int y, int z) {
    if (y < 1) return;  //do not change bedrock
    if (y > 255) return;
    chunk.clearBlock(x, y, z);
    chunk.clearBlock2(x, y, z);
    if (y < 10) {
      chunk.setBlock(x, y, z, Blocks.LAVA, 0);
    }
  }

  private void lineNS(float x, float y, float z, float dz, char id) {
    int cnt = 0;
    while (cnt < 120 && Static.noises[Static.N_ELEV4].noise_3d(cx16 + x,y,cz16 + z) > threshold) {
      if (id != 0) {
        clearBlock((int)x,(int)y,(int)z);
      }
      setBlock((int)x,(int)y,(int)z,id,0);
      z += dz;
      cnt++;
    }
  }

  private void lineEW(float x, float y, float z, float dx, char id) {
    int cnt = 0;
    while (cnt < 120 && Static.noises[Static.N_ELEV4].noise_3d(cx16 + x,y,cz16 + z) > threshold) {
      if (id != 0) {
        clearBlock((int)x,(int)y,(int)z);
      }
      setBlock((int)x,(int)y,(int)z,id,0);
      lineNS(x,y,z,1.0f,id);
      lineNS(x,y,z,-1.0f,id);
      x += dx;
      cnt++;
    }
  }

  private float threshold = 0.5f;

  public void addCavern(Chunk chunk, BiomeData data) {
    this.chunk = chunk;
    this.data = data;
    cx16 = chunk.cx * 16;
    cz16 = chunk.cz * 16;

    Random r = new Random();
    r.setSeed(data.c1);

    int x = Static.abs(r.nextInt(16));
    int z = Static.abs(r.nextInt(16));
    int y = Static.abs(r.nextInt(50)) + 5;
    while (y > 5 && Static.noises[Static.N_ELEV4].noise_3d(cx16 + x,y,cz16 + z) < threshold) {
      y--;
    }
    while (y < 55 && Static.noises[Static.N_ELEV4].noise_3d(cx16 + x,y,cz16 + z) < threshold) {
      y++;
    }
    if (y == 55) {
      return;
    }
    //move to the top
    while (y < 60.0f && Static.noises[Static.N_ELEV4].noise_3d(cx16 + x,y+1,cz16 + z) > threshold) {
      y++;
    }
//    Static.log("room@" + x + "," + y + "," + z + ":" + chunk.cx + "," + chunk.cz);
    int gap = r.nextInt(3) + 3;
    char id = Blocks.AIR;
    char liquid;
    if (y > 40.0f) {
      liquid = Blocks.WATER;
    } else {
      liquid = Blocks.LAVA;
    }
    while (y > 2.0f && Static.noises[Static.N_ELEV4].noise_3d(cx16 + x,y,cz16 + z) > threshold) {
      //set level
      lineEW(x,y,z,1.0f,id);
      lineEW(x,y,z,-1.0f,id);
      //move down
      y--;
      if (gap > 0) {
        gap--;
        if (gap == 0) {
          id = liquid;
        }
      }
    }
  }

}
