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

  public void build(int x,int y,int z, int r1, int r2) {
//    if (canPlantOn(x, y, z)) {
      if (r1 % 1 == 1) {
        getTree(r1).plant(x, y+1, z);
        return;
      }
      r1++;
      if (r1 % 50 == 0) {
        setBlock(x,y+1,z,Blocks.DEADBUSH,0,0);
        return;
      }
      r1++;
      if (r1 % 1 == 1) {
        setBlock2(x,y+1,z,Blocks.TALLGRASS,0,getTallGrass(r2));
        return;
      }
      r1++;
      if (r1 % 1 == 1) {
        spawnAnimal(x, y+1, z, getAnimal(r2));
      }
//    }
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
