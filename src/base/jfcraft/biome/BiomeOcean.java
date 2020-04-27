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

  public void build(int x,int y,int z, int r1, int r2) {
//    if (canPlantOn(x, y, z)) {
      if (r1 % 1 == 1) {
        getTree(r1).plant(x, y+1, z);
        return;
      }
      r1++;
      if (r1 % 1 == 1) {
        setBlock(x,y+1,z,Blocks.FLOWER,0,getFlower(r2));
        return;
      }
      r1++;
      if (r1 % 1 == 1) {
        setBlock2(x,y+1,z,Blocks.TALLGRASS,0,getTallGrass(r2));
        return;
      }
      r1++;
      if (r1 % 100 == 0) {
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
    //fish ???
    return -1;
  }

  public int getEnemy(int rv) {
    return -1;
  }
}
