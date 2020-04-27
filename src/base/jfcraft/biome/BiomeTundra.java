package jfcraft.biome;

/**
 * BiomeTundra
 */

import jfcraft.data.*;
import jfcraft.tree.*;
import static jfcraft.data.Blocks.*;
import static jfcraft.data.Biomes.*;

public class BiomeTundra extends BiomeBase {

  public byte getID() {
    return TUNDRA;
  }

  public void build(int x,int y,int z, int rand) {
    if (canPlantOn(x, y, z)) {
      if (rand % 500 == 0) {
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
      if (rand % 1 == 1) {
        rand++;
        setBlock(x,y,z,Blocks.TALLGRASS,0,getTallGrass(rand));
        return;
      }
      rand++;
      if (rand % 100 == 0) {
        rand++;
        spawnAnimal(x, y, z, getAnimal(rand));
      }
    }
  }


  public TreeBase getTree(int rv) {
    return Static.trees.trees[rv % 2];  //OAK or SPRUCE
  }

  public int getFlower(int rv) {
    return -1;
  }

  public int getTallGrass(int rv) {
    return -1;
  }

  public int getAnimal(int rv) {
    switch (rv % 4) {
      case 0: return Entities.COW;
      case 1: return Entities.PIG;
      case 2: return Entities.HORSE;
      case 3: return Entities.SHEEP;
    };
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
