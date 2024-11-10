package jfcraft.biome;

/**
 * BiomeSwamp
 */

import jfcraft.data.*;
import jfcraft.tree.*;
import static jfcraft.data.Blocks.*;
import static jfcraft.data.Biomes.*;
import static jfcraft.data.Direction.*;

public class BiomeSwamp extends BiomeBase {

  public byte getID() {
    return SWAMP;
  }

  private static final int TREE_ODDS = 150;
  private static final int FLOWER_CHUNK_ODDS = 1;
  private static final int FLOWER_BLOCK_ODDS = 3;
  private static final int TALLGRASS_CHUNK_ODDS = INF;
  private static final int TALLGRASS_BLOCK_ODDS = INF;
  private static final int ANIMAL_CHUNK_ODDS = 100;
  private static final int ANIMAL_BLOCK_ODDS = 100;

  public void build(int x,int y,int z, BiomeData data) {
    if (canPlantOn(x, y, z)) {
      if (data.b1 % TREE_ODDS == 0) {
        getTree(data.b2).plant(x, y+1, z, data);
        return;
      }
      if (data.c1 % FLOWER_CHUNK_ODDS == 0) {
        if (data.b1 % FLOWER_BLOCK_ODDS == 0) {
          if (chunk.getBlock(x,Static.SEALEVEL,z) == Blocks.WATER) {
            chunk.setBlock(x,Static.SEALEVEL+1,z,Blocks.LILLYPAD,Chunk.makeBits(B, 0));
          } else {
            //setBlock(x,y+1,z,Blocks.FLOWER,0,getFlower(data.c2));
          }
          return;
        }
      }
      if (data.c2 % TALLGRASS_CHUNK_ODDS == 0) {
        if (data.b1 % TALLGRASS_BLOCK_ODDS == 0) {
          setBlock2(x,y+1,z,Blocks.TALLGRASS,0,getTallGrass(data.c1));
          return;
        }
      }
      if (data.c3 % ANIMAL_CHUNK_ODDS == 0) {
        if (data.b1 % ANIMAL_BLOCK_ODDS == 0) {
          spawnAnimal(x, y+1, z, getAnimal(data.c1));
        }
      }
    }
  }

  public TreeBase getTree(int rv) {
    return Static.trees.trees[Trees.TREE_SWAMP];
  }

  public int getFlower(int rv) {
    return -1;
  }

  public int getTallGrass(int rv) {
    return -1;
  }

  public int getAnimal(int rv) {
    return -1;
  }

  public int getEnemy(int rv) {
    switch (rv % 4) {
      case 0: return Entities.ZOMBIE;
      case 1: return Entities.SKELETON;
      case 2: return Entities.ENDERMAN;
      case 3: return Entities.SLIME;
    }
    return -1;
  }
}
