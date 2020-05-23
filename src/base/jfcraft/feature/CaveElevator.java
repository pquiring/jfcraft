package jfcraft.feature;

/**
 * CaveElevator.
 *
 * Connects different levels of caves.
 *
 * Creates a 4x4 shaft with steps, hopefully they connect.
 * Use cobblestone so it's not removed.
 *
 */

import jfcraft.gen.*;
import jfcraft.biome.*;
import jfcraft.data.*;
import static jfcraft.data.Direction.*;

public class CaveElevator {
  public void build(GeneratorChunk chunk, BiomeData data, int y1, int y2) {
    int sx = 0;
    int sz = 0;
    int dir = E;
    for(int y=y1;y<=y2;y++) {
      for(int z=6;z<11;z++) {
        for(int x=6;x<11;x++) {
          chunk.clearBlock(x,y,z);
        }
      }
      chunk.setBlock(sx + 6,y,sz + 6,Blocks.COBBLESTONE,0);
      switch (dir) {
        case E:
          if (sx == 3) {
            dir = S;
            sz++;
          } else {
            sx++;
          }
          break;
        case S:
          if (sz == 3) {
            dir = W;
            sx--;
          } else {
            sz++;
          }
          break;
        case W:
          if (sx == 0) {
            dir = N;
            sz--;
          } else {
            sx--;
          }
          break;
        case N:
          if (sz == 0) {
            dir = E;
            sx++;
          } else {
            sz--;
          }
          break;
      }
    }
  }
}
