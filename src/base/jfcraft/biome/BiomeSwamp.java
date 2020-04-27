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

  public boolean hasTree(int rv) {
    return rv % 50 == 0;
  }

  public TreeBase getTree(int rv) {
    return Static.trees.trees[Trees.TREE_SWAMP];
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
    return false;
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
