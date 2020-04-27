package jfcraft.biome;

/**
 * BiomeForest
 */

import jfcraft.data.*;
import jfcraft.tree.*;
import static jfcraft.data.Blocks.*;
import static jfcraft.data.Biomes.*;

public class BiomeForest extends BiomeBase {

  public byte getID() {
    return FOREST;
  }

  public boolean hasTree(int rv) {
    return rv % 30 == 0;
  }

  public TreeBase getTree(int rv) {
    return Static.trees.trees[rv % 2];  //OAK or SPRUCE
  }

  public boolean hasFlower(int rv) {
    return rv % 25 == 0;
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

  public boolean hasTallGrass(int rv) {
    return rv % 50 == 0;
  }

  private int grasses[] = {
    0  //??? TODO : tall grass VARs ???
  };

  public int getTallGrass(int rv) {
    return grasses[rv % grasses.length];
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
