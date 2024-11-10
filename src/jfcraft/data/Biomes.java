package jfcraft.data;

/** Biomes
 *
 */

import jfcraft.biome.*;

public class Biomes {
  public BiomeBase biomes[];

  //biome types
  public static final byte TUNDRA = 0;       //snow plains
  public static final byte TAIGA = 1;        //snow forest
  public static final byte PLAINS = 2;       //dry plains (few trees)
  public static final byte DESERT = 3;       //dry sand
  public static final byte FOREST = 4;       //lots o trees
  public static final byte SWAMP = 5;        //swamp/marsh area
  public static final byte JUNGLE = 6;       //thick trees/bush/plants
  public static final byte OCEAN = 7;        //the sea/rivers
  public static final byte NETHER = 8;       //nether
  public static final byte DARK_FOREST = 9;  //darker larger trees
  public static final byte END = 10;         //end world
  public static final byte SAVANA = 11;      //savana
  public static final byte BADLANDS = 12;    //badlands

  private static final int COUNT = 13;

  public Biomes() {
    biomes = new BiomeBase[COUNT];
    biomes[0] = new BiomeTundra();
    biomes[1] = new BiomeTaiga();
    biomes[2] = new BiomePlains();
    biomes[3] = new BiomeDesert();
    biomes[4] = new BiomeForest();
    biomes[5] = new BiomeSwamp();
    biomes[6] = new BiomeJungle();
    biomes[7] = new BiomeOcean();
    biomes[8] = new BiomeNether();
    biomes[9] = new BiomeDarkForest();
    biomes[10] = new BiomeEnd();
    biomes[11] = new BiomeSavana();
    biomes[12] = new BiomeBadLands();
  }

  public static String getBiomeName(byte type) {
    switch (type) {
      case TUNDRA: return "TUNDRA";
      case TAIGA: return "TAIGA";
      case PLAINS: return "PLAINS";
      case DESERT: return "DESERT";
      case FOREST: return "FOREST";
      case SWAMP: return "SWAMP";
      case JUNGLE: return "JUNGLE";
      case OCEAN: return "OCEAN";
      case NETHER: return "NETHER";
      case DARK_FOREST: return "DARK FOREST";
      case END: return "END";
      case SAVANA: return "SAVANA";
      case BADLANDS: return "BAD LANDS";
    }
    return null;
  }
}
