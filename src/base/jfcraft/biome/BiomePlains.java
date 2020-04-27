package jfcraft.biome;

/**
 * BiomePlains
 */

import jfcraft.data.*;
import jfcraft.tree.*;
import static jfcraft.data.Blocks.*;
import static jfcraft.data.Biomes.*;

public class BiomePlains extends BiomeBase {

  public byte getID() {
    return PLAINS;
  }

  public void build(int x,int y,int z, int rand) {
    if (canPlantOn(x, y, z)) {
      if (rand % 200 == 0) {
        rand++;
        getTree(rand).plant(x, y+1, z);
        return;
      }
      rand++;
      if (rand % 25 == 0) {
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
      if (rand % 100 == 0) {
        rand++;
        spawnAnimal(x, y, z, getAnimal(rand));
      }
    }
  }

  public TreeBase getTree(int rv) {
    return Static.trees.trees[rv % 3];  //OAK/SPRUCE/BIRCH
  }

  private byte flowers[] = {
    VAR_POPPY,
    VAR_BLUE_ORCHID,
    VAR_ALLIUM,
    VAR_AZURE_BLUET,
    VAR_TULIP_RED,
    VAR_TULIP_ORANGE,
    VAR_TULIP_WHITE,
    VAR_TULIP_PINK,
    VAR_OXEYE_DAISY,
  };

  public int getFlower(int rv) {
    return flowers[rv & flowers.length-1];
  }

  private int grasses[] = {
    0  //??? TODO : tall grass VARs ???
  };

  public int getTallGrass(int rv) {
    return grasses[rv % grasses.length];
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
