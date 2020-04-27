package jfcraft.gen;

/** Chunk generator phase 3 : final touches
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
import static jfcraft.data.Direction.*;

public class GeneratorPhase3End implements GeneratorPhase3Base {
  private Chunk chunk;
  private Random r = new Random();

  public void getIDs() {}

  public void reset() {}

  public void generate(Chunk chunk) {
    this.chunk = chunk;

    synchronized(chunk.lock) {
      chunk.needPhase3 = false;
      chunk.dirty = true;

      r.setSeed(chunk.seed);

//      if (Static.doSteps) smoothSteps();

//      addStuff();
    }
  }
  private void setBlock(int x, int y, int z, char id, int dir, int var) {
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
    c.setBlock(x, y, z, id, Chunk.makeBits(dir,var));
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

  public void smoothSteps() {
    for(int x=0;x<16;x++) {
      for(int z=0;z<16;z++) {
        if (Settings.current.doSteps) {
          //smooth out terrain with steps
          char lastId = chunk.getID(x, 0, z);
          for(int y=1;y<255;y++) {
            char id = chunk.getID(x, y, z);
            if (lastId != 0 && id == 0 && Static.blocks.blocks[lastId].canSmooth) {
              //on top
              boolean n = getBlock(x, y, z-1).isSolid;
              boolean e = getBlock(x+1, y, z).isSolid;
              boolean s = getBlock(x, y, z+1).isSolid;
              boolean w = getBlock(x-1, y, z).isSolid;
              id = Static.blocks.blocks[lastId].stepID;
              if (n) {
                if (e)
                  setBlock(x, y, z, id, NE, 0);
                else if (w)
                  setBlock(x, y, z, id, NW, 0);
                else
                  setBlock(x, y, z, id, N, 0);
              } else if (s) {
                if (e)
                  setBlock(x, y, z, id, SE, 0);
                else if (w)
                  setBlock(x, y, z, id, SW, 0);
                else
                  setBlock(x, y, z, id, S, 0);
              } else if (e) {
                setBlock(x, y, z, id, E, 0);
              } else if (w) {
                setBlock(x, y, z, id, W, 0);
              }
            }
            else if (lastId == 0 && id != 0 && Static.blocks.blocks[id].canSmooth) {
              //underneath
              y--;
              boolean n = getBlock(x, y, z-1).isSolid;
              boolean e = getBlock(x+1, y, z).isSolid;
              boolean s = getBlock(x, y, z+1).isSolid;
              boolean w = getBlock(x-1, y, z).isSolid;
              id = Static.blocks.blocks[id].stepID;
              if (n) {
                if (e)
                  setBlock(x, y, z, id, NE, 1);
                else if (w)
                  setBlock(x, y, z, id, NW, 1);
                else
                  setBlock(x, y, z, id, N, 1);
              } else if (s) {
                if (e)
                  setBlock(x, y, z, id, SE, 1);
                else if (w)
                  setBlock(x, y, z, id, SW, 1);
                else
                  setBlock(x, y, z, id, S, 1);
              } else if (e) {
                setBlock(x, y, z, id, E, 1);
              } else if (w) {
                setBlock(x, y, z, id, W, 1);
              }
              y++;
            }
            lastId = id;
          }
        }
      }
    }
  }

  public void addStuff() {
    for(int x=0;x<16;x++) {
      for(int z=0;z<16;z++) {
        int p = z * 16 + x;
        int elev = (int)Math.ceil(chunk.elev[p]);
        if (elev < 64) continue;  //under water
//        float temp = chunk.temp[p];
//        float rain = chunk.rain[p];
      }
    }
  }
}
