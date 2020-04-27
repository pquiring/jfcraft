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

  public boolean hasTree(int rv) {
    return false;
  }

  public TreeBase getTree(int rv) {
    return null;
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
    switch (rv % 3) {
      case 0: return Entities.ZOMBIE;
      case 1: return Entities.SKELETON;
      case 2: return Entities.ENDERMAN;
    }
    return -1;
  }
}
