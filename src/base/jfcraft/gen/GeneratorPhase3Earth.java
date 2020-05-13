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
import jfcraft.biome.*;
import jfcraft.block.*;
import jfcraft.entity.*;
import jfcraft.tree.TreeBase;
import static jfcraft.data.Direction.*;
import static jfcraft.data.Biomes.*;
import static jfcraft.block.BlockStep.*;

public class GeneratorPhase3Earth implements GeneratorPhase3Base {
  private Chunk chunk;
  private Random r = new Random();
  private BiomeData data = new BiomeData();

  public void getIDs() {
  }

  public void reset() {}

  public void generate(Chunk chunk) {
    this.chunk = chunk;

    chunk.needPhase3 = false;
    chunk.dirty = true;

    r.setSeed(chunk.seed);

    if (Static.server.world.options.doSteps) smoothSteps();

    addStuff();
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
//    if (c.getBlock(x, y, z).id != Blocks.AIR) return;  //only replace air
    c.setBlock(x, y, z, id, Chunk.makeBits(dir,var));
  }
  private char getID(int x,int y,int z) {
    return chunk.getID(x,y,z);
  }
  private char getID2(int x,int y,int z) {
    return chunk.getID2(x,y,z);
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

  private void addStep(int x,int y,int z, int dy, char sid, boolean upper) {
    boolean n = getBlock(x, y, z-1).isSolid;
    boolean e = getBlock(x+1, y, z).isSolid;
    boolean s = getBlock(x, y, z+1).isSolid;
    boolean w = getBlock(x-1, y, z).isSolid;
    char id = Static.blocks.blocks[sid].stepID;
    if (id == 0) return;
    char bid = chunk.getID(x,y+dy,z);
    int var = BlockStep.getVar(bid);
    if (var == -1) return;
    if (upper) var |= VAR_UPPER;
    if (n) {
      if (e)
        setBlock(x, y, z, id, NE, var);
      else if (w)
        setBlock(x, y, z, id, NW, var);
      else
        setBlock(x, y, z, id, N, var);
    } else if (s) {
      if (e)
        setBlock(x, y, z, id, SE, var);
      else if (w)
        setBlock(x, y, z, id, SW, var);
      else
        setBlock(x, y, z, id, S, var);
    } else if (e) {
      setBlock(x, y, z, id, E, var);
    } else if (w) {
      setBlock(x, y, z, id, W, var);
    }
  }

  private void addStep2(int x,int y,int z, int dy, char sid, boolean upper) {
    boolean n = getBlock(x, y, z-1).isSolid;
    boolean e = getBlock(x+1, y, z).isSolid;
    boolean s = getBlock(x, y, z+1).isSolid;
    boolean w = getBlock(x-1, y, z).isSolid;
    char id = Static.blocks.blocks[sid].stepID;
    if (id == 0) {
      Static.log("Error:stepID==0");
      return;
    }
    char bid = chunk.getID2(x,y+dy,z);
    int var = BlockStep.getVar(bid);
    if (var == -1) {
      Static.log("Error:var==-1:");
      return;
    }
    if (upper) var |= VAR_UPPER;
    if (n) {
      if (e)
        setBlock(x, y, z, id, NE, var);
      else if (w)
        setBlock(x, y, z, id, NW, var);
      else
        setBlock(x, y, z, id, N, var);
    } else if (s) {
      if (e)
        setBlock(x, y, z, id, SE, var);
      else if (w)
        setBlock(x, y, z, id, SW, var);
      else
        setBlock(x, y, z, id, S, var);
    } else if (e) {
      setBlock(x, y, z, id, E, var);
    } else if (w) {
      setBlock(x, y, z, id, W, var);
    }
  }

  public void smoothSteps() {
    for(int x=0;x<16;x++) {
      for(int z=0;z<16;z++) {
        if (Settings.current.doSteps) {
          //smooth out terrain with steps
          char lastId = chunk.getID(x, 0, z);
          char lastId2 = chunk.getID2(x, 0, z);
          for(int y=1;y<255;y++) {
            char id = chunk.getID(x, y, z);
            char id2 = chunk.getID2(x, y, z);
            if (lastId != 0 && id == 0 && Static.blocks.blocks[lastId].canSmooth) {
              //on top
              addStep(x,y,z,-1,lastId,false);
            }
            else if (lastId == 0 && id != 0 && Static.blocks.blocks[id].canSmooth) {
              //underneath
              addStep(x,y,z,+1,id,true);
            }
            if (lastId2 != 0 && id2 == 0 && Static.blocks.blocks[lastId2].canSmooth) {
              //on top
              if (chunk.getID(x,y,z) == 0) {
                addStep2(x,y-1,z,0,lastId2,false);
              }
            }
            else if (lastId2 == 0 && id2 != 0 && Static.blocks.blocks[id2].canSmooth) {
              //underneath
              if (chunk.getID(x,y,z) == 0) {
                addStep2(x,y,z,0,id2,true);
              }
            }
            lastId = id;
            lastId2 = id2;
          }
        }
      }
    }
  }

  //random values must be 1 thru <MAXINT
  public int nextInt() {
    //Random.nextInt(int value) returns 0 thru value-1
    return r.nextInt(Integer.MAX_VALUE - 1) + 1;
  }

  public void addStuff() {
    BiomeBase.setChunk(chunk);
    TreeBase.setChunk(chunk);
    data.c1 = nextInt();
    data.c2 = nextInt();
    data.c3 = nextInt();
    for(int x=0;x<16;x++) {
      for(int z=0;z<16;z++) {
        int p = z * 16 + x;
        int y = (int)Math.ceil(chunk.elev[p]);
        data.temp = chunk.temp[p];
        data.rain = chunk.rain[p];
        data.b1 = nextInt();
        data.b2 = nextInt();
        data.b3 = nextInt();
        BiomeBase biome = Static.biomes.biomes[chunk.biome[p]];
        biome.build(x, y, z, data);
      }
    }
  }
}
