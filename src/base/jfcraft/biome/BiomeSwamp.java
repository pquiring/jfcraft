package jfcraft.biome;

/**
 * BiomeSwamp
 */

import jfcraft.data.*;
import jfcraft.tree.*;
import static jfcraft.data.Blocks.*;
import static jfcraft.data.Biomes.*;

public class BiomeSwamp extends BiomeBase {

  public byte getID() {
    return SWAMP;
  }

  public void build(int x,int y,int z, int r1, int r2) {
    if (canPlantOn(x, y, z)) {
      if (r1 % 50 == 0) {
        r1++;
        getTree(r1).plant(x, y+1, z);
        return;
      }
      r1++;
      if (r1 % 1 == 1) {
        r1++;
        setBlock(x,y,z,Blocks.FLOWER,0,getFlower(r2));
        return;
      }
      r1++;
      if (r1 % 1 == 1) {
        r1++;
        setBlock2(x,y,z,Blocks.TALLGRASS,0,getTallGrass(r2));
        return;
      }
      r1++;
      if (r1 % 100 == 0) {
        r1++;
        spawnAnimal(x, y, z, getAnimal(r2));
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
