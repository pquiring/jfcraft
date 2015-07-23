package jfcraft.gen;

/** Chunk generator phase 2 : structures
 *
 * Any structure can only span 8 chunks in any direction,
 *  for a total of 17 chunks span (that's 272 blocks).
 *
 * @author pquiring
 *
 * Created : May 8, 2014
 */

import java.util.*;

import javaforce.*;
import javaforce.gl.*;

import jfcraft.data.*;
import jfcraft.block.*;
import jfcraft.entity.*;

public class GeneratorPhase2End implements GeneratorPhase2Base {
  private Chunk chunk;
  private Random r = new Random();

  public void getIDs() {}

  public void reset() {}

  public void generate(Chunk chunk) {
    this.chunk = chunk;

    synchronized(chunk.lock) {
      chunk.needPhase2 = false;
      chunk.dirty = true;

      if (chunk.cx > 6) return;
      if (chunk.cx < -6) return;
      if (chunk.cz > 6) return;
      if (chunk.cz < -6) return;

      r.setSeed(chunk.seed);

      if (chunk.cx == 0 && chunk.cz == 0) {
        Static.server.makeEndFountainPortal(chunk);
      } else {
        if (r.nextInt(20) == 0) {
          addPillar();
        }
      }
    }
  }

  private void setBlock(int x, int y, int z, char id, int bits) {
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
    c.setBlock(x, y, z, id, bits);
  }
  private void clearBlock(int x, int y, int z) {
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
    c.clearBlock(x, y, z);
    if (y < 10) {
      c.setBlock(x, y, z, Blocks.LAVA, 0);
    }
  }
  private BlockBase getBlock(int x, int y, int z) {
    if (y < 0) return null;
    if (y > 255) return null;
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
    return Static.blocks.blocks[c.getID(x,y,z)];
  }

  public void addPillar() {
    int h = r.nextInt(30) + 10;
    int y2 = 64 + h;
    for(int x=5;x<10;x++) {
      for(int z=5;z<10;z++) {
        if (x == 5 && (z == 5 || z == 9)) continue;
        if (x == 9 && (z == 5 || z == 9)) continue;
        int p = z * 16 + x;
        int y1 = (int)chunk.elev[p];
        for(int y=y1;y<=y2;y++) {
          setBlock(x,y,z,Blocks.OBSIDIAN,0);
        }
      }
    }
    //TODO : add thingy on top
  }
}
