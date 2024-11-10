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
import static jfcraft.data.Biomes.*;
import static jfcraft.data.Blocks.*;
import jfcraft.dim.DimMarioWorld;

public class GeneratorPhase3Mario implements GeneratorPhase3Base {
  private Chunk chunk;
  private Random r = new Random();

  private byte trees[] = {Blocks.VAR_OAK, Blocks.VAR_SPRUCE, Blocks.VAR_JUNGLE};

  private EntityBase animals[];

  public void getIDs() {
    animals = Static.entities.listGenerate(DimMarioWorld.MARIO_WORLD);
  }

  public void reset() {}

  public void generate(Chunk chunk) {
    this.chunk = chunk;

    synchronized(chunk.lock) {
      chunk.needPhase3 = false;
      chunk.dirty = true;

      r.setSeed(chunk.seed);

      addStuff();
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
  public void setBlock2(int x, int y, int z, char id, int dir, int var) {
    setBlock(x,y,z,id,dir,var);
    setBlock(x,y+1,z,id,dir,var + VAR_UPPER);
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
    return Static.blocks.blocks[c.getBlock(x,y,z)];
  }
  private void addTree(int x, int y, int z, byte var, boolean snow) {
    setBlock(x  ,y  ,z  ,Blocks.WOOD, 0, var);
    setBlock(x  ,y+1,z  ,Blocks.WOOD, 0, var);
    setBlock(x  ,y+2,z  ,Blocks.WOOD, 0, var);
    setBlock(x  ,y+3,z  ,Blocks.WOOD, 0, var);
    setBlock(x  ,y+4,z  ,Blocks.WOOD, 0, var);

    setBlock(x  ,y+5,z  ,Blocks.LEAVES, 0, var);
    setBlock(x  ,y+4,z-1,Blocks.LEAVES, 0, var);
    setBlock(x  ,y+4,z+1,Blocks.LEAVES, 0, var);
    setBlock(x-1,y+4,z  ,Blocks.LEAVES, 0, var);
    setBlock(x-1,y+4,z-1,Blocks.LEAVES, 0, var);
    setBlock(x-1,y+4,z+1,Blocks.LEAVES, 0, var);
    setBlock(x+1,y+4,z  ,Blocks.LEAVES, 0, var);
    setBlock(x+1,y+4,z-1,Blocks.LEAVES, 0, var);
    setBlock(x+1,y+4,z+1,Blocks.LEAVES, 0, var);
    if (!snow) return;
    //place snow on top of tree

    //BUG : no snow and leaves are in the dark
    setBlock(x  ,y+6,z  ,Blocks.SNOW, 0, 0);
    setBlock(x  ,y+5,z-1,Blocks.SNOW, 0, 0);
    setBlock(x  ,y+5,z+1,Blocks.SNOW, 0, 0);
    setBlock(x-1,y+5,z  ,Blocks.SNOW, 0, 0);
    setBlock(x-1,y+5,z-1,Blocks.SNOW, 0, 0);
    setBlock(x-1,y+5,z+1,Blocks.SNOW, 0, 0);
    setBlock(x+1,y+5,z  ,Blocks.SNOW, 0, 0);
    setBlock(x+1,y+5,z-1,Blocks.SNOW, 0, 0);
    setBlock(x+1,y+5,z+1,Blocks.SNOW, 0, 0);

  }
  public void spawnAnimal(int x,int y,int z) {
    int idx = r.nextInt(animals.length);
    EntityBase e = animals[idx].spawn(chunk);
    if (e == null) return;  //failed to spawn
    e.uid = Static.server.world.generateUID();
    chunk.addEntity(e);
    Static.server.world.addEntity(e);
  }

  public void addStuff() {
    for(int x=0;x<16;x++) {
      for(int z=0;z<16;z++) {
        int p = z * 16 + x;
        int elev = (int)Math.ceil(chunk.elev[p]);
        if (elev < 64) continue;  //under water
//        float temp = chunk.temp[p];
//        float rain = chunk.rain[p];
        int bt = chunk.biome[p];
        BlockBase block;
        switch (bt) {
          default:
          case TAIGA:
          case FOREST:
            block = getBlock(x,elev,z);
            if (block.canPlantOn) {
              if (block.id != Blocks.GRASS && block.id != Blocks.DIRT) {
                System.out.println("canPlantOn != soil:" + (int)block.id);
              }
              else if (r.nextInt() % 200 == 0) {
                addTree(x, elev+1, z, trees[r.nextInt(2)], bt == TAIGA);
              }
              else if (r.nextInt() % 100 == 0) {
                setBlock(x, elev+1, z, Blocks.FLOWER, 0, r.nextInt(11));
              }
              else if (r.nextInt() % 50 == 0) {
                setBlock2(x, elev+1, z, Blocks.TALLGRASS, 0, r.nextInt(2));
              }
              else if (r.nextInt() % 50 == 0) {
                spawnAnimal(x, elev+1, z);
              }
            }
            break;
        }
      }
    }
  }
}
