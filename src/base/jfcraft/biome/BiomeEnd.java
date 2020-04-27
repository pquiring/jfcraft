package jfcraft.biome;

/**
 * BiomeEnd
 */

import jfcraft.data.*;
import jfcraft.tree.*;
import static jfcraft.data.Blocks.*;
import static jfcraft.data.Biomes.*;

public class BiomeEnd extends BiomeBase {

  public byte getID() {
    return END;
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
    return rv % 100 == 0;
  }

  public int getAnimal(int rv) {
    return Entities.ENDERMAN;
  }

  public int getEnemy(int rv) {
    return Entities.ENDERMAN;
  }
}
