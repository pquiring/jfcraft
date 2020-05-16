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
import jfcraft.feature.*;
import jfcraft.tree.TreeBase;
import static jfcraft.data.Direction.*;
import static jfcraft.data.Biomes.*;
import static jfcraft.block.BlockStep.*;

public class GeneratorPhase3Earth implements GeneratorPhase3Base {
  private Chunk chunk;
  private BiomeData data = new BiomeData();

  public void getIDs() {
  }

  public void reset() {}

  public void generate(Chunk chunk) {
    this.chunk = chunk;

    chunk.needPhase3 = false;
    chunk.dirty = true;

    new TopSoil().build(chunk, data);

    if (Static.server.world.options.doSteps) smoothSteps();

    if ((data.c1 ^ data.c3) % 10000 == 0) {
      if (chunk.biome[0] == Biomes.OCEAN) {
        addBlueprint("shipwreck", 32, Static.SEALEVEL - 8);
      } else {
        addBlueprint("cabin", Static.SEALEVEL, 255);
      }
    }

    addStuff();
  }

  private void addBlueprint(String name, int elevMin, int elevMax) {
    BluePrint print = chunk.world.getBluePrint(name);
    int elev = (int)chunk.elev[8 * 16 + 8] + 1;
    if (elev + print.Y > elevMax) return;
    if (elev < elevMin) return;
    if (chunk.getID(8, elev+1, 8) != 0) return;
    int ang = data.c1 % 4;
    switch (ang) {
      case 0: break;  //no change
      case 1: print.rotateY(R90); break;
      case 2: print.rotateY(R180); break;
      case 3: print.rotateY(R270); break;
    }
    print.writeChunk(chunk, 0, 0, 0, 0, elev, 0, print.X, print.Y, print.Z);
    //rotate back
    switch (ang) {
      case 0: break;  //no change
      case 1: print.rotateY(R270); break;
      case 2: print.rotateY(R180); break;
      case 3: print.rotateY(R90); break;
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

  public void setBlock(int x,int y,int z,char id,int bits) {
    chunk.setBlock(x, y, z, id, bits);
  }

  private void addStepLower(int x,int y,int z, char sid) {
    boolean n = getBlock(x  , y, z-1).isSolid;
    boolean e = getBlock(x+1, y, z  ).isSolid;
    boolean s = getBlock(x  , y, z+1).isSolid;
    boolean w = getBlock(x-1, y, z  ).isSolid;
    char id = Static.blocks.blocks[sid].stepID;
    if (id == 0) return;
    int bits = 0;
    if (n) bits |= QLNE | QLNW;
    if (e) bits |= QLNE | QLSE;
    if (s) bits |= QLSE | QLSW;
    if (w) bits |= QLNW | QLSW;
    if (bits == 0) return;
    setBlock(x, y, z, id, bits);
  }

  private void addStepUpper(int x,int y,int z, char sid) {
    boolean n = getBlock(x  , y, z-1).isSolid;
    boolean e = getBlock(x+1, y, z  ).isSolid;
    boolean s = getBlock(x  , y, z+1).isSolid;
    boolean w = getBlock(x-1, y, z  ).isSolid;
    char id = Static.blocks.blocks[sid].stepID;
    if (id == 0) return;
    int bits = 0;
    if (n) bits |= QUNE | QUNW;
    if (e) bits |= QUNE | QUSE;
    if (s) bits |= QUSE | QUSW;
    if (w) bits |= QUNW | QUSW;
    if (bits == 0) return;
    setBlock(x, y, z, id, bits);
  }

  private void addStepSnow(int x, int y, int z) {
    char id1;
    char id2;
    id1 = getID (x  ,y+1,z-1);
    id2 = getID2(x  ,y+1,z-1);
    boolean n = getBlock(x  , y, z-1).isSolid && (id2 == Blocks.SNOW || id1 == Blocks.STEPSNOW);
    id1 = getID (x+1,y+1,z  );
    id2 = getID2(x+1,y+1,z  );
    boolean e = getBlock(x+1, y, z  ).isSolid && (id2 == Blocks.SNOW || id1 == Blocks.STEPSNOW);
    id1 = getID (x  ,y+1,z+1);
    id2 = getID2(x  ,y+1,z+1);
    boolean s = getBlock(x  , y, z+1).isSolid && (id2 == Blocks.SNOW || id1 == Blocks.STEPSNOW);
    id1 = getID (x-1,y+1,z  );
    id2 = getID2(x-1,y+1,z  );
    boolean w = getBlock(x-1, y, z  ).isSolid && (id2 == Blocks.SNOW || id1 == Blocks.STEPSNOW);
    if (!n && !e && !s && !w) return;
    chunk.clearBlock2(x, y, z);  //remove snow carpet
    int bits = QLNW | QLNE | QLSE | QLSW;
    if (n) bits |= QUNW | QUNE;
    if (e) bits |= QUNE | QUSE;
    if (s) bits |= QUSE | QUSW;
    if (w) bits |= QUSW | QUNW;
    setBlock(x,y,z,Blocks.STEPSNOW, bits);
  }

  public void smoothSteps() {
    for(int x=0;x<16;x++) {
      for(int z=0;z<16;z++) {
        if (Settings.current.doSteps) {
          //smooth out terrain with steps
          char lastId = chunk.getID(x, 0, z);
          for(int y=1;y<255;y++) {
            char id = chunk.getID(x, y, z);
            if (chunk.getID2(x,y,z) == Blocks.SNOW) {
              addStepSnow(x,y,z);
            }
            else if (lastId != 0 && id == 0 && Static.blocks.blocks[lastId].canSmooth) {
              addStepLower(x,y,z,lastId);
            }
            else if (lastId == 0 && id != 0 && Static.blocks.blocks[id].canSmooth) {
              addStepUpper(x,y,z,id);
            }
            lastId = id;
          }
        }
      }
    }
  }

  public void addStuff() {
    BiomeBase.setChunk(chunk);
    TreeBase.setChunk(chunk);
    data.setChunk(chunk);
    for(int x=0;x<16;x++) {
      for(int z=0;z<16;z++) {
        int p = z * 16 + x;
        int y = (int)Math.ceil(chunk.elev[p]);
        data.temp = chunk.temp[p];
        data.rain = chunk.rain[p];
        data.setBlock(x, z);
        BiomeBase biome = Static.biomes.biomes[chunk.biome[p]];
        biome.build(x, y, z, data);
      }
    }
  }
}
