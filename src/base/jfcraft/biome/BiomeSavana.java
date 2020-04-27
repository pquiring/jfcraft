package jfcraft.biome;

/**
 * BiomeSavana
 */

import jfcraft.data.*;
import jfcraft.tree.*;
import static jfcraft.data.Blocks.*;
import static jfcraft.data.Biomes.*;

public class BiomeSavana extends BiomeBase {

  public byte getID() {
    return SAVANA;
  }

  public boolean hasTree(int rv) {
    return rv % 500 == 0;
  }

  public TreeBase getTree(int rv) {
    return Static.trees.trees[Trees.TREE_SAVANA];
  }

  public boolean hasFlower(int rv) {
    return false;
  }

  public int getFlower(int rv) {
    return -1;
  }

  public boolean hasTallGrass(int rv) {
    return false;
  }

  public int getTallGrass(int rv) {
    return -1;
  }

  public boolean hasAnimal(int rv) {
    return rv % 100 == 0;
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
