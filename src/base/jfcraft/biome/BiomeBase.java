package jfcraft.biome;

/**
 * BiomeBase interface
 */

import jfcraft.tree.*;

public abstract class BiomeBase {
  public abstract byte getID();
  public boolean hasTree(int rv) {return false;}
  public TreeBase getTree(int rv) {return null;}
  public boolean hasFlower(int rv) {return false;}
  public int getFlower(int rv) {return -1;}
  public boolean hasTallGrass(int rv) {return false;}
  public int getTallGrass(int rv) {return -1;}
  public boolean hasAnimal(int rv) {return false;}
  public int getAnimal(int rv) {return -1;}
  public int getEnemy(int rv) {return -1;}
};
