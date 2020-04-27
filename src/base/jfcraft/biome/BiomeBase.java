package jfcraft.biome;

/**
 * BiomeBase interface
 */

import javaforce.*;

import jfcraft.block.BlockBase;
import jfcraft.tree.*;
import jfcraft.data.*;
import jfcraft.entity.EntityBase;
import static jfcraft.data.Blocks.*;

public abstract class BiomeBase {
  public static Chunk chunk;
  public abstract byte getID();
  /** Adds trees/flowers/etc to coords in chunk.
   * @param x,y,z = coord of top block of soil in chunk
   * @param rand = random int value (absolute)
   */
  public abstract void build(int x,int y,int z, int r1, int r2);
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
  public void setBlock(int x, int y, int z, char id, int dir, int var) {
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
    if (c.getBlock(x, y, z).id != Blocks.AIR) return;  //only replace air
    c.setBlock(x, y, z, id, Chunk.makeBits(dir,var));
  }
  public void setBlock2(int x, int y, int z, char id, int dir, int var) {
    if (var > 6 || var < 0) {
      JFLog.log("Debug:Error:invalid var spawned:" + var);
    }
    setBlock(x,y,z,id,dir,var);
    setBlock(x,y+1,z,id,dir,var + VAR_UPPER);
  }
  public boolean canPlantOn(int x,int y,int z) {
    BlockBase block = getBlock(x,y,z);
    BlockBase blockA = getBlock(x,y+1,z);
    return block.canPlantOn && blockA.id == Blocks.AIR;
  }
  public void spawnAnimal(int x,int y,int z, int id) {
    if (id == -1) return;
    EntityBase e = Static.entities.getEntity(id).spawn(chunk);
    if (e == null) return;  //failed to spawn
    e.uid = Static.server.world.generateUID();
    chunk.addEntity(e);
    Static.server.world.addEntity(e);
  }
};
