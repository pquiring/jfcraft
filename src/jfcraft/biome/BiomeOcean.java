package jfcraft.biome;

/**
 * BiomeOcean
 */

import jfcraft.data.*;
import jfcraft.tree.*;
import static jfcraft.data.Blocks.*;
import static jfcraft.data.Biomes.*;

public class BiomeOcean extends BiomeBase {

  public byte getID() {
    return OCEAN;
  }

  private static final int WEEDS_ODDS = 10;
  private static final int ANIMAL_CHUNK_ODDS = INF;
  private static final int ANIMAL_BLOCK_ODDS = INF;

  public void build(int x,int y,int z, BiomeData data) {
    if (canPlantOn(x, y, z)) {
      if (data.b1 % WEEDS_ODDS == 0) {
        int odds = data.b2 % 3;
        if (odds == 0) {
          getTree(data.c1).plant(x, y+1, z, data);
          return;
        }
        if (odds == 1 && y <= 63) {
          setBlock(x,y+1,z,Blocks.SEAWEEDS,0,0);
          return;
        }
        if (odds == 2 && y <= 62) {
          setBlock2(x,y+1,z,Blocks.TALLSEAWEEDS,0,0);
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
    return Static.trees.trees[Trees.KELP];
  }

  public int getFlower(int rv) {
    return Static.blocks.SEAWEEDS;
  }

  public int getTallGrass(int rv) {
    return Static.blocks.TALLSEAWEEDS;
  }

  public int getAnimal(int rv) {
    //fish ???
    return -1;
  }

  public int getEnemy(int rv) {
    return -1;
  }
}
