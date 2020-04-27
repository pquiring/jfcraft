package jfcraft.biome;

/**
 * BiomeDesert
 */

import jfcraft.data.*;
import jfcraft.tree.*;
import static jfcraft.data.Blocks.*;
import static jfcraft.data.Biomes.*;

public class BiomeDesert extends BiomeBase {

  public byte getID() {
    return DESERT;
  }

  public void build(int x,int y,int z, int rand) {
    if (canPlantOn(x, y, z)) {
      if (rand % 1 == 1) {
        rand++;
        getTree(rand).plant(x, y+1, z);
        return;
      }
      rand++;
      if (rand % 1 == 1) {
        rand++;
        setBlock(x,y,z,Blocks.FLOWER,0,getFlower(rand));
        return;
      }
      rand++;
      if (rand % 25 == 0) {
        rand++;
        setBlock(x,y,z,Blocks.TALLGRASS,0,getTallGrass(rand));
        return;
      }
      rand++;
      if (rand % 1 == 1) {
        rand++;
        spawnAnimal(x, y, z, getAnimal(rand));
      }
    }
  }

  public TreeBase getTree(int rv) {
    return null;
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
    switch (rv % 3) {
      case 0: return Entities.ZOMBIE;
      case 1: return Entities.SKELETON;
      case 2: return Entities.ENDERMAN;
    }
    return -1;
  }
}
