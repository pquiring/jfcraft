package jfcraft.data;

/** Registered blocks
 *
 * @author pquiring
 *
 * Created : Mar 25, 2014
 */

import java.util.*;

import javaforce.*;
import javaforce.awt.*;
import javaforce.gl.*;

import jfcraft.block.*;
import jfcraft.item.*;
import jfcraft.data.*;
import jfcraft.opengl.*;
import static jfcraft.data.Types.*;
import static jfcraft.data.Direction.*;

public class Blocks {
  private static final int MAX_ID = 65536;
  private ArrayList<AssetImage> tiles = new ArrayList<AssetImage>();  //main stitched images

  public int blockCount = 0;
  public BlockBase[] blocks = new BlockBase[MAX_ID];  //blocks (in order of id)
  public BlockBase[] regBlocks = new BlockBase[MAX_ID];  //registered blocks (not in order of id)
  public TextureMap stitched;  //main stitched texture (including animated textures)
  public TextureMap cracks;  //cracks
  public SubTexture subcracks[];
  public BlockBase solid;

  public boolean valid;

  public static char getBlockID(String name) {
    return Static.server.world.getBlockID(name);
  }

  public BlockBase registerBlock(BlockBase block) {
    regBlocks[blockCount++] = block;
    return block;
  }

  public BlockBase getBlock(int id) {
    return blocks[id];
  }

  public BlockBase getRegisteredBlock(String name) {
    for(int idx=0;idx<MAX_ID;idx++) {
      BlockBase bb = regBlocks[idx];
      if (bb == null) continue;
      if (bb.getName().equalsIgnoreCase(name)) {
        return bb;
      }
    }
    return null;
  }

  public void orderBlocks() {
    blocks = new BlockBase[MAX_ID];
    for(int a=0;a<MAX_ID;a++) {
      BlockBase bb = regBlocks[a];
      if (bb == null) continue;
      blocks[bb.id] = bb;
      Static.items.items[bb.id] = bb;
    }
    for(int a=0;a<MAX_ID;a++) {
      if (blocks[a] == null) {
        blocks[a] = blocks[0];  //air
      }
    }
  }

  public static char AIR;  //will be zero
  public static char DIRT;
  public static char GRASS;
  public static char GRASSBANK;
  public static char WEEDS;
  public static char SNOW;
  public static char WATER;
  public static char LAVA;
  public static char SAND;
  public static char CLAY;
  public static char TERRACOTA;
  public static char OIL;
  public static char GRAVEL;
  public static char STONE;
  public static char COBBLESTONE;
  public static char MOSSY_COBBLESTONE;
  public static char STONE_BRICKS;
  public static char MOSSY_STONE_BRICKS;
  public static char CRACKED_STONE_BRICKS;
  public static char BRICKS;
  public static char DEEPSLATE;
  public static char DEEPSLATE_COAL_ORE;
  public static char DEEPSLATE_IRON_ORE;
  public static char DEEPSLATE_COPPER_ORE;
  public static char DEEPSLATE_GOLD_ORE;
  public static char DEEPSLATE_DIAMOND_ORE;
  public static char DEEPSLATE_EMERALD_ORE;
  public static char DEEPSLATE_LAPIS_ORE;
  public static char DEEPSLATE_REDSTONE_ORE;
  public static char PLANKS;
  public static char SAPLING;
  public static char BEDROCK;
  public static char GOLDORE;
  public static char IRONORE;
  public static char COPPERORE;
  public static char COALORE;
  public static char WOOD;
  public static char LEAVES;
  public static char SPONGE;
  public static char GLASSBLOCK;
  public static char LAPIS_ORE;
  public static char LAPIS_BLOCK;
  public static char DISPENSER;
  public static char SAND_STONE;
  public static char NOTE_BLOCK;
  public static char RAIL_POWERED;
  public static char RAIL_DETECTOR;
  public static char PISTON_STICKY;
  public static char WEB;
  public static char FERN;
  public static char TALLGRASS;
  public static char TALLPLANT;
  public static char DEADBUSH;
  public static char PISTON;
  public static char WOOL;
  public static char DANDELION;
  public static char FLOWER;
  public static char MUSHROOM_BROWN;
  public static char MUSHROOM_RED;
  public static char GOLD_BLOCK;
  public static char IRON_BLOCK;
  public static char STONE_SLAB;
  public static char COBBLESTONE_SLAB;
  public static char MOSSY_COBBLESTONE_SLAB;
  public static char STONE_BRICKS_SLAB;
  public static char MOSSY_STONE_BRICKS_SLAB;
  public static char CRACKED_STONE_BRICKS_SLAB;
  public static char BRICKS_SLAB;
  public static char WOOD_SLAB;
  public static char TNT;
  public static char BOOKSHELF;
  public static char OBSIDIAN;
  public static char TORCH;
  public static char FIRE;
  public static char SPAWNER;
  public static char STAIRS_WOOD;
  public static char STAIRS_STONE;
  public static char STAIRS_BRICK;
  public static char STAIRS_BLOCK;
  public static char STAIRS_NETHER;
  public static char STAIRS_QUARTZ;
  public static char CHEST;
  public static char DIAMOND_ORE;
  public static char DIAMOND_BLOCK;
  public static char CRAFTTABLE;
  public static char FURNACE;
  public static char FURNACE_ACTIVE;
  public static char LADDER;
  public static char RAIL;
  public static char LEVER;
  public static char PRESSURE_PLATE;
  public static char REDSTONE_ORE;
  public static char REDSTONE_TORCH;
  public static char BUTTON;
  public static char ICEBLOCK;
  public static char CACTUS;
  public static char MUSIC_BOX;
  public static char FENCE;
  public static char PUMPKIN;
  public static char NETHER_RACK;
  public static char SOUL_SAND;
  public static char GLOWSTONE;
  public static char NETHER_PORTAL;
  public static char PUMPKIN_LIT;
  public static char REDSTONE_REPEATER;
  public static char GLASSBLOCK_COLOR;
  public static char TRAP_DOOR;
  public static char BARS;
  public static char GLASS_PANE;
  public static char MELON;
  public static char VINES;
  public static char GATE;
  public static char MYCELIUM;
  public static char LILLYPAD;
  public static char NETHER_BRICKS;
  public static char NETHER_FENCE;
  public static char ENCHANTING_TABLE;
  public static char END_PORTAL;
  public static char END_PORTAL_FRAME;
  public static char END_STONE;
  public static char DRAGON_EGG;
  public static char COCOA;
  public static char EMERALD_ORE;
  public static char ENDER_CHEST;
  public static char TRIP_HOOK;
  public static char EMERALD_BLOCK;
  public static char COMMAND_BLOCK;
  public static char BEACON;
  public static char WALL;
  public static char ANVIL;
  public static char CHEST_TRAP;
  public static char PLATE_GOLD;
  public static char PLATE_IRON;
  public static char REDSTONE_COMPARATOR;
  public static char SOLAR_PANEL;
  public static char REDSTONE_BLOCK;
  public static char QUARTZ_ORE;
  public static char HOPPER;
  public static char QUARTZ_BLOCK;
  public static char RAIL_ACTIVATOR;
  public static char DROPPER;
  public static char GLASS_PANE_COLOR;
  public static char HAYBALE;
  public static char CARPET;
  public static char COAL_BLOCK;
  public static char SNOW_PACKED;
  public static char STEPGRASS;
  public static char STEPDIRT;
  public static char STEPSTONE;
  public static char STEPSAND;
  public static char STEPSNOW;
  public static char SIGN;
  public static char BED;
  public static char WOOD_DOOR;
  public static char IRON_DOOR;
  public static char BARRIER;
  public static char RED_STONE;
  public static char WHEAT;
  public static char SOLID;  //solid color block
  public static char KELPPLANT;
  public static char TEST_ARROW;
  public static char SEAWEEDS;
  public static char TALLSEAWEEDS;

  public static void getIDs(World world) {
    AIR = world.getBlockID("AIR");
    DIRT = world.getBlockID("DIRT");
    GRASS = world.getBlockID("GRASS");
    GRASSBANK = world.getBlockID("GRASSBANK");
    WEEDS = world.getBlockID("WEEDS");
    SNOW = world.getBlockID("SNOW");
    WATER = world.getBlockID("WATER");
    LAVA = world.getBlockID("LAVA");
    SAND = world.getBlockID("SAND");
    CLAY = world.getBlockID("CLAY");
    TERRACOTA = world.getBlockID("TERRACOTA");
    OIL = world.getBlockID("OIL");
    GRAVEL = world.getBlockID("GRAVEL");
    STONE = world.getBlockID("STONE");
    COBBLESTONE = world.getBlockID("COBBLESTONE");
    MOSSY_COBBLESTONE = world.getBlockID("MOSSY_COBBLESTONE");
    BRICKS = world.getBlockID("BRICKS");
    STONE_BRICKS = world.getBlockID("STONE_BRICKS");
    MOSSY_STONE_BRICKS = world.getBlockID("MOSSY_STONE_BRICKS");
    CRACKED_STONE_BRICKS = world.getBlockID("CRACKED_STONE_BRICKS");
    DEEPSLATE = world.getBlockID("DEEPSLATE");
    DEEPSLATE_COAL_ORE = world.getBlockID("DEEPSLATE_COAL_ORE");
    DEEPSLATE_IRON_ORE = world.getBlockID("DEEPSLATE_IRON_ORE");
    DEEPSLATE_COPPER_ORE = world.getBlockID("DEEPSLATE_COPPER_ORE");
    DEEPSLATE_GOLD_ORE = world.getBlockID("DEEPSLATE_GOLD_ORE");
    DEEPSLATE_DIAMOND_ORE = world.getBlockID("DEEPSLATE_DIAMOND_ORE");
    DEEPSLATE_EMERALD_ORE = world.getBlockID("DEEPSLATE_EMERALD_ORE");
    DEEPSLATE_LAPIS_ORE = world.getBlockID("DEEPSLATE_LAPIS_ORE");
    DEEPSLATE_REDSTONE_ORE = world.getBlockID("DEEPSLATE_REDSTONE_ORE");
    PLANKS = world.getBlockID("PLANKS");
    SAPLING = world.getBlockID("SAPLING");
    BEDROCK = world.getBlockID("BEDROCK");
    GOLDORE = world.getBlockID("GOLDORE");
    IRONORE = world.getBlockID("IRONORE");
    COPPERORE = world.getBlockID("COPPERORE");
    COALORE = world.getBlockID("COALORE");
    WOOD = world.getBlockID("WOOD");
    LEAVES = world.getBlockID("LEAVES");
    SPONGE = world.getBlockID("SPONGE");
    GLASSBLOCK = world.getBlockID("GLASSBLOCK");
    LAPIS_ORE = world.getBlockID("LAPIS_ORE");
    LAPIS_BLOCK = world.getBlockID("LAPIS_BLOCK");
    DISPENSER = world.getBlockID("DISPENSER");
    SAND_STONE = world.getBlockID("SAND_STONE");
    NOTE_BLOCK = world.getBlockID("NOTE_BLOCK");
    RAIL_POWERED = world.getBlockID("RAIL_POWERED");
    RAIL_DETECTOR = world.getBlockID("RAIL_DETECTOR");
    PISTON_STICKY = world.getBlockID("PISTON_STICKY");
    WEB = world.getBlockID("WEB");
    FERN = world.getBlockID("FERN");
    TALLGRASS = world.getBlockID("TALLGRASS");
    TALLPLANT = world.getBlockID("TALLPLANT");
    DEADBUSH = world.getBlockID("DEADBUSH");
    PISTON = world.getBlockID("PISTON");
    WOOL = world.getBlockID("WOOL");
    DANDELION = world.getBlockID("DANDELION");
    FLOWER = world.getBlockID("FLOWER");
    MUSHROOM_BROWN = world.getBlockID("MUSHROOM_BROWN");
    MUSHROOM_RED = world.getBlockID("MUSHROOM_RED");
    GOLD_BLOCK = world.getBlockID("GOLD_BLOCK");
    IRON_BLOCK = world.getBlockID("IRON_BLOCK");
    STONE_SLAB = world.getBlockID("STONE_SLAB");
    COBBLESTONE_SLAB = world.getBlockID("COBBLESTONE_SLAB");
    MOSSY_COBBLESTONE_SLAB = world.getBlockID("MOSSY_COBBLESTONE_SLAB");
    STONE_BRICKS_SLAB = world.getBlockID("STONE_BRICKS_SLAB");
    MOSSY_STONE_BRICKS_SLAB = world.getBlockID("MOSSY_STONE_BRICKS_SLAB");
    CRACKED_STONE_BRICKS_SLAB = world.getBlockID("CRACKED_STONE_BRICKS_SLAB");
    BRICKS_SLAB = world.getBlockID("BRICKS_SLAB");
    WOOD_SLAB = world.getBlockID("WOOD_SLAB");
    TNT = world.getBlockID("TNT");
    BOOKSHELF = world.getBlockID("BOOKSHELF");
    OBSIDIAN = world.getBlockID("OBSIDIAN");
    TORCH = world.getBlockID("TORCH");
    FIRE = world.getBlockID("FIRE");
    SPAWNER = world.getBlockID("SPAWNER");
    STAIRS_WOOD = world.getBlockID("STAIRS_WOOD");
    STAIRS_STONE = world.getBlockID("STAIRS_STONE");
    STAIRS_BRICK = world.getBlockID("STAIRS_BRICK");
    STAIRS_BLOCK = world.getBlockID("STAIRS_BLOCK");
    STAIRS_NETHER = world.getBlockID("STAIRS_NETHER");
    STAIRS_QUARTZ = world.getBlockID("STAIRS_QUARTZ");
    CHEST = world.getBlockID("CHEST");
    DIAMOND_ORE = world.getBlockID("DIAMOND_ORE");
    DIAMOND_BLOCK = world.getBlockID("DIAMOND_BLOCK");
    CRAFTTABLE = world.getBlockID("CRAFTTABLE");
    FURNACE = world.getBlockID("FURNACE");
    FURNACE_ACTIVE = world.getBlockID("FURNACE_ACTIVE");
    LADDER = world.getBlockID("LADDER");
    RAIL = world.getBlockID("RAIL");
    LEVER = world.getBlockID("LEVER");
    PRESSURE_PLATE = world.getBlockID("PRESSURE_PLATE");
    REDSTONE_ORE = world.getBlockID("REDSTONE_ORE");
    REDSTONE_TORCH = world.getBlockID("REDSTONE_TORCH");
    BUTTON = world.getBlockID("BUTTON");
    ICEBLOCK = world.getBlockID("ICEBLOCK");
    CACTUS = world.getBlockID("CACTUS");
    MUSIC_BOX = world.getBlockID("MUSIC_BOX");
    FENCE = world.getBlockID("FENCE");
    PUMPKIN = world.getBlockID("PUMPKIN");
    NETHER_RACK = world.getBlockID("NETHER_RACK");
    SOUL_SAND = world.getBlockID("SOUL_SAND");
    GLOWSTONE = world.getBlockID("GLOWSTONE");
    NETHER_PORTAL = world.getBlockID("NETHER_PORTAL");
    PUMPKIN_LIT = world.getBlockID("PUMPKIN_LIT");
    REDSTONE_REPEATER = world.getBlockID("REDSTONE_REPEATER");
    GLASSBLOCK_COLOR = world.getBlockID("GLASSBLOCK_COLOR");
    TRAP_DOOR = world.getBlockID("TRAP_DOOR");
    BARS = world.getBlockID("BARS");
    GLASS_PANE = world.getBlockID("GLASS_PANE");
    MELON = world.getBlockID("MELON");
    VINES = world.getBlockID("VINES");
    GATE = world.getBlockID("GATE");
    MYCELIUM = world.getBlockID("MYCELIUM");
    LILLYPAD = world.getBlockID("LILLYPAD");
    NETHER_BRICKS = world.getBlockID("NETHER_BRICKS");
    NETHER_FENCE = world.getBlockID("NETHER_FENCE");
    ENCHANTING_TABLE = world.getBlockID("ENCHANTING_TABLE");
    END_PORTAL = world.getBlockID("END_PORTAL");
    END_PORTAL_FRAME = world.getBlockID("END_PORTAL_FRAME");
    END_STONE = world.getBlockID("END_STONE");
    DRAGON_EGG = world.getBlockID("DRAGON_EGG");
    COCOA = world.getBlockID("COCOA");
    EMERALD_ORE = world.getBlockID("EMERALD_ORE");
    ENDER_CHEST = world.getBlockID("ENDER_CHEST");
    TRIP_HOOK = world.getBlockID("TRIP_HOOK");
    EMERALD_BLOCK = world.getBlockID("EMERALD_BLOCK");
    COMMAND_BLOCK = world.getBlockID("COMMAND_BLOCK");
    BEACON = world.getBlockID("BEACON");
    WALL = world.getBlockID("WALL");
    ANVIL = world.getBlockID("ANVIL");
    CHEST_TRAP = world.getBlockID("CHEST_TRAP");
    PLATE_GOLD = world.getBlockID("PLATE_GOLD");
    PLATE_IRON = world.getBlockID("PLATE_IRON");
    REDSTONE_COMPARATOR = world.getBlockID("REDSTONE_COMPARATOR");
    SOLAR_PANEL = world.getBlockID("SOLAR_PANEL");
    REDSTONE_BLOCK = world.getBlockID("REDSTONE_BLOCK");
    QUARTZ_ORE = world.getBlockID("QUARTZ_ORE");
    HOPPER = world.getBlockID("HOPPER");
    QUARTZ_BLOCK = world.getBlockID("QUARTZ_BLOCK");
    RAIL_ACTIVATOR = world.getBlockID("RAIL_ACTIVATOR");
    DROPPER = world.getBlockID("DROPPER");
    GLASS_PANE_COLOR = world.getBlockID("GLASS_PANE_COLOR");
    HAYBALE = world.getBlockID("HAYBALE");
    CARPET = world.getBlockID("CARPET");
    COAL_BLOCK = world.getBlockID("COAL_BLOCK");
    SNOW_PACKED = world.getBlockID("SNOW_PACKED");
    STEPGRASS = world.getBlockID("STEPGRASS");
    STEPDIRT = world.getBlockID("STEPDIRT");
    STEPSTONE = world.getBlockID("STEPSTONE");
    STEPSAND = world.getBlockID("STEPSAND");
    STEPSNOW = world.getBlockID("STEPSNOW");
    SIGN = world.getBlockID("SIGN");
    BED = world.getBlockID("BED");
    WOOD_DOOR = world.getBlockID("WOOD_DOOR");
    IRON_DOOR = world.getBlockID("IRON_DOOR");
    BARRIER = world.getBlockID("BARRIER");
    RED_STONE = world.getBlockID("RED_STONE");
    WHEAT = world.getBlockID("WHEAT");
    SOLID = world.getBlockID("SOLID");
    TEST_ARROW = world.getBlockID("TEST_ARROW");
    KELPPLANT = world.getBlockID("KELPPLANT");
    SEAWEEDS = world.getBlockID("SEAWEEDS");
    TALLSEAWEEDS = world.getBlockID("TALLSEAWEEDS");
  }

  //upper flag (door, tall grass)
  public static final byte VAR_UPPER = 8;

  //wood/leaves vars
  public static final byte VAR_OAK = 0;
  public static final byte VAR_SPRUCE = 1;
  public static final byte VAR_BIRCH = 2;
  public static final byte VAR_JUNGLE = 3;
  public static final byte VAR_ACACIA = 4;
  public static final byte VAR_DARK_OAK = 5;

  //stone vars
  public static final byte VAR_NORMAL_STONE = 0;
  public static final byte VAR_CHISELED_STONE = 1;
  //etc...

  //button vars
  public static final byte VAR_BUTTON_WOOD = 0;
  public static final byte VAR_BUTTON_STONE = 1;

  //color vars
  public static final byte VAR_WHITE = 0;
  public static final byte VAR_ORANGE = 1;
  public static final byte VAR_MAGENTA = 2;
  public static final byte VAR_LIGHT_BLUE = 3;
  public static final byte VAR_YELLOW = 4;
  public static final byte VAR_LIME = 5;
  public static final byte VAR_PINK = 6;
  public static final byte VAR_GRAY = 7;
  public static final byte VAR_SILVER = 8;  //light gray
  public static final byte VAR_CYAN = 9;
  public static final byte VAR_PURPLE = 10;
  public static final byte VAR_BLUE = 11;
  public static final byte VAR_BROWN = 12;
  public static final byte VAR_GREEN = 13;
  public static final byte VAR_RED = 14;
  public static final byte VAR_BLACK = 15;
  //...

  //flower vars
  public static final byte VAR_POPPY = 0;
  public static final byte VAR_BLUE_ORCHID = 1;
  public static final byte VAR_ALLIUM = 2;
  public static final byte VAR_AZURE_BLUET = 3;
  public static final byte VAR_TULIP_RED = 4;
  public static final byte VAR_TULIP_ORANGE = 5;
  public static final byte VAR_TULIP_WHITE = 6;
  public static final byte VAR_TULIP_PINK = 7;
  public static final byte VAR_OXEYE_DAISY = 8;

  //tallgrass vars
  public static final byte VAR_TALL_GRASS = 0;
  public static final byte VAR_LARGE_FERN = 1;

  //tallplant vars
  public static final byte VAR_LILAC = 0;
  public static final byte VAR_ROSE_BUSH = 1;
  public static final byte VAR_PEONY = 2;

  public void registerDefault() {
    registerBlock(new BlockAir("AIR"));
    registerBlock(new BlockOpaque("STONE", new String[] {"Stone"}, new String[] {"stone"})
      .setDrop("COBBLESTONE").setSmooth("STEPSTONE").setHardness(2f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockOpaque("DEEPSLATE", new String[] {"DeepSlate"}, new String[] {"deepslate"})
      .setDrop("AIR").setHardness(3f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockOpaque("DEEPSLATE_COAL_ORE", new String[] {"DeepSlate Iron Ore"}, new String[] {"deepslate_coal_ore"})
      .setDrop("COAL").setHardness(3f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockOpaque("DEEPSLATE_IRON_ORE", new String[] {"DeepSlate Iron Ore"}, new String[] {"deepslate_iron_ore"})
      .setBake("IRON_INGOT").setHardness(3f, TOOL_PICKAXE, CLS_STONE));
    registerBlock(new BlockOpaque("DEEPSLATE_GOLD_ORE", new String[] {"DeepSlate Gold Ore"}, new String[] {"deepslate_gold_ore"})
      .setBake("IRON_INGOT").setHardness(3f, TOOL_PICKAXE, CLS_IRON));
    registerBlock(new BlockOpaque("DEEPSLATE_EMERALD_ORE", new String[] {"DeepSlate Emerald Ore"}, new String[] {"deepslate_gold_ore"})
      .setDrop("EMERALD").setHardness(3f, TOOL_PICKAXE, CLS_IRON));
    registerBlock(new BlockOpaque("DEEPSLATE_LAPIS_ORE", new String[] {"DeepSlate Lapis Ore"}, new String[] {"deepslate_lapis_ore"})
      .setDrop("LAPIS_LAZULI").setHardness(3f, TOOL_PICKAXE, CLS_IRON));
    registerBlock(new BlockOpaque("DEEPSLATE_REDSTONE_ORE", new String[] {"DeepSlate RedStone Ore"}, new String[] {"deepslate_redstone_ore"})
      .setDrop("REDSTONE").setHardness(3f, TOOL_PICKAXE, CLS_IRON));
    registerBlock(new BlockGrass("GRASS", new String[] {"Grass"}, new String[] {"grass_block_top", "grass_block_top", "dirt"})
      .setGreenTopSide().setSupportsPlant().setSmooth("STEPGRASS").setDrop("DIRT").setHardness(0.6f, TOOL_SHOVEL, CLS_NONE));
    registerBlock(new BlockGrass("GRASSBANK", new String[] {"Grass"}, new String[] {"grass_block_top", "grass_block_side", "dirt"})
      .setGreenTop().setSupportsPlant().setDrop("DIRT").setHardness(0.6f, TOOL_SHOVEL, CLS_NONE));
    registerBlock(new BlockCarpet("SNOW", new String[] {"Snow"}, new String[] {"snow"})
      .setDrop("AIR").setSupported().setBlocks2().setCanReplace().setHardness(0.2f, TOOL_SHOVEL, CLS_NONE)
    );
    registerBlock(new BlockDirt("DIRT", new String[] {"Dirt", "Podzol", "Farmland", "Farmland"}
      , new String[] {
        "dirt", "dirt", "dirt",
        "podzol_top", "podzol_side", "dirt",
        "farmland", "dirt", "dirt",
        "farmland_moist", "dirt", "dirt"
      })
      .setSupportsPlant().setSmooth("STEPDIRT").setVar().setHardness(0.6f, TOOL_SHOVEL, CLS_NONE));
    registerBlock(new BlockOpaque("COBBLESTONE", new String[] {"Cobblestone"}, new String[] {"cobblestone"})
      .setHardness(2.0f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockOpaque("MOSSY_COBBLESTONE", new String[] {"Mossy Cobblestone"}, new String[] {"mossy_cobblestone"})
      .setHardness(2.0f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockOpaque("STONE_BRICKS", new String[] {"Stone Bricks"}, new String[] {"stone_bricks"})
      .setDrop("COBBLESTONE").setHardness(2f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockOpaque("MOSSY_STONE_BRICKS", new String[] {"Mossy Stone Bricks"}, new String[] {"mossy_stone_bricks"})
      .setDrop("COBBLESTONE").setHardness(2f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockOpaque("CRACKED_STONE_BRICKS", new String[] {"Cracked Stone Bricks"}, new String[] {"cracked_stone_bricks"})
      .setDrop("COBBLESTONE").setHardness(2f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockOpaque("BRICKS", new String[] {"Bricks"}, new String[] {"bricks"}).setHardness(2f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockOpaqueVar("PLANKS"
      , new String[] {"Oak Wood Planks", "Spruce Wood Planks", "Birch Wood Planks", "Jungle Wood Planks", "Acacia Wood Planks", "Dark Oak Wood Planks"}
      , new String[] {"oak_planks", "spruce_planks", "birch_planks", "jungle_planks", "acacia_planks", "dark_oak_planks"})
      .setFuel(15).setMaterial(MAT_WOOD).setHardness(2.0f, TOOL_AXE, CLS_NONE)
    );
    registerBlock(new BlockXVar("SAPLING"
      , new String[] {"Oak Wood Sapling", "Spruce Wood Sapling", "Birch Wood Sapling", "Jungle Wood Sapling", "Acacia Wood Sapling", "Dark Oak Wood Sapling"}
      , new String[] {"oak_sapling", "spruce_sapling", "birch_sapling", "jungle_sapling", "acacia_sapling", "dark_oak_sapling"})
      .setFuel(5).setMaterial(MAT_WOOD)
    );
    registerBlock(new BlockOpaque("BEDROCK", new String[] {"Bedrock"}, new String[] {"bedrock"}).setHardness(-1f, TOOL_NONE, CLS_NONE));
    registerBlock(new BlockLiquid("WATER", new String[] {"Water"}, new String[] {"water_still", "water_flow"}).setFlowRate(1).setRenews(true).setHardness(100f, TOOL_NONE, CLS_NONE).setBlue());
    registerBlock(new BlockLiquid("LAVA", new String[] {"Lava"}, new String[] {"lava_still", "lava_flow"}).setFlowRate(3).setRenews(false).setHardness(100f, TOOL_NONE, CLS_NONE));
    registerBlock(new BlockFalling("SAND", new String[] {"Sand", "Red Sand"}, new String[] {"sand", "red_sand"})
      .setBake("GLASSBLOCK").setVar().setHardness(0.5f, TOOL_SHOVEL, CLS_NONE)
      .setSmooth("STEPSAND")
    );
    registerBlock(new BlockFalling("GRAVEL", new String[] {"Gravel"}, new String[] {"gravel"}).setHardness(0.6f, TOOL_SHOVEL, CLS_NONE));
    registerBlock(new BlockOpaque("GOLDORE", new String[] {"Gold Ore"}, new String[] {"gold_ore"}).setBake("GOLD_INGOT").setHardness(3f, TOOL_PICKAXE, CLS_IRON));
    registerBlock(new BlockOpaque("IRONORE", new String[] {"Iron Ore"}, new String[] {"iron_ore"}).setBake("IRON_INGOT").setHardness(3f, TOOL_PICKAXE, CLS_STONE));
    registerBlock(new BlockOpaque("COPPERORE", new String[] {"Copper Ore"}, new String[] {"copper_ore"}).setBake("COPPER_INGOT").setHardness(3f, TOOL_PICKAXE, CLS_STONE));
    registerBlock(new BlockOpaque("COALORE", new String[] {"Coal Ore"}, new String[] {"coal_ore"}).setDrop("COAL").setHardness(3f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockOpaqueVar("WOOD"
      , new String[] {"Oak Wood", "Spruce Wood", "Birch Wood", "Jungle Wood", "Acacia Wood", "Dark Oak Wood"}
      , new String[] {
        "oak_log_top", "oak_log",
        "spruce_log_top", "spruce_log",
        "birch_log_top", "birch_log",
        "jungle_log_top", "jungle_log",
        "acacia_log_top", "acacia_log",
        "dark_oak_log_top", "dark_oak_log"
      })
      .setFuel(15).setMaterial(MAT_WOOD).setDir().setHardness(2f, TOOL_AXE, CLS_NONE)
    );
    registerBlock(new BlockLeaves("LEAVES"
      , new String[] {"Oak Wood Leaves", "Spruce Wood Leaves", "Birch Wood Leaves", "Jungle Wood Leaves", "Acacia Wood Leaves", "Dark Oak Wood Leaves"}
      , new String[] {"oak_leaves", "spruce_leaves", "birch_leaves", "jungle_leaves", "acacia_leaves", "dark_oak_leaves"}
    ).setGreenAllSides().setPerf().setDrop("AIR").setHardness(0.2f, TOOL_NONE, CLS_NONE));
    registerBlock(new BlockOpaque("SPONGE", new String[] {"Sponge"}, new String[] {"sponge"}).setHardness(0.6f, TOOL_NONE, CLS_NONE));
    registerBlock(new BlockTrans("GLASSBLOCK", new String[] {"Glass Block"}, new String[] {"glass"}).setHardness(0.3f, TOOL_NONE, CLS_NONE));
    registerBlock(new BlockOpaque("LAPIS_ORE", new String[] {"Lapis Ore"}, new String[] {"lapis_ore"}).setDrop("LAPIS_LAZULI").setHardness(3f, TOOL_PICKAXE, CLS_STONE));
    registerBlock(new BlockOpaque("LAPIS_BLOCK", new String[] {"Lapis Block"}, new String[] {"lapis_block"}).setHardness(3f, TOOL_PICKAXE, CLS_STONE));
    registerBlock(new BlockDispenser("DISPENSER", new String[] {"Dispenser"}
      , new String[] {"dispenser_front", "dispenser_front_vertical", "piston_bottom"}).setHardness(3.5f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockOpaque("SAND_STONE", new String[] {"Sand Stone"}, new String[] {"sandstone_top", "sandstone", "sandstone_bottom"}).setHardness(0.8f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockOpaque("NOTE_BLOCK", new String[] {"Note Block"}, new String[] {"note_block"}).setHardness(0.8f, TOOL_AXE, CLS_NONE));
    registerBlock(new BlockRail("RAIL_POWERED", new String[] {"Rail Powered"}, new String[] {"powered_rail", "powered_rail_on"}).setRedstone().setHardness(0.7f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockRail("RAIL_DETECTOR", new String[] {"Rail Detector"}, new String[] {"detector_rail", "detector_rail_on"}).setRedstone().setHardness(0.7f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockPiston("PISTON_STICKY", new String[] {"Sticky Piston"}, new String[] {"piston_bottom", "piston_side", "piston_inner", "piston_side", "piston_top", "piston_side", "piston_top_sticky"}).setSticky().setHardness(0.5f, TOOL_NONE, CLS_NONE));
    registerBlock(new BlockX("WEB", new String[] {"Web"}, new String[] {"cobweb"}).addBox(0, 0, 0, 15, 15, 15,BlockHitTest.Type.SELECTION).setHardness(4f, TOOL_SWORD, CLS_NONE));
    registerBlock(new BlockX("FERN"
      , new String[] {"Fern"}
      , new String[] {"fern"}
      )
      .setGreenAllSides().setDrop("SEEDS").setSupported().setDropVar(false).setCanReplace()
      .addBox(0, 0, 0, 15, 15, 15,BlockHitTest.Type.SELECTION).setMaterial(MAT_WOOD)
    );
    registerBlock(new BlockX("WEEDS"
      , new String[] {"Weeds"}
      , new String[] {"grass_block_top"}
      )
      .setGreenAllSides().setDrop("SEEDS").setSupported().setDropVar(false).setCanReplace()
      .addBox(0, 0, 0, 15, 15, 15,BlockHitTest.Type.SELECTION).setMaterial(MAT_WOOD)
    );
    registerBlock(new BlockX("DEADBUSH"
      , new String[] {"Dead Bush"}
      , new String[] {"dead_bush"}).
      setSupported().addBox(6, 0, 6, 10, 10, 10,BlockHitTest.Type.SELECTION).setMaterial(MAT_WOOD).setDrop("STICK")
    );
    registerBlock(new BlockPiston("PISTON", new String[] {"Piston"}, new String[] {"piston_bottom", "piston_side", "piston_inner", "piston_side", "piston_top", "piston_side", "piston_top"}).setHardness(0.5f, TOOL_NONE, CLS_NONE));
    registerBlock(new BlockOpaque("WOOL",
      new String[] {
        "Wool White", "Wool Orange",
        "Wool Magenta", "Wool Light Blue",
        "Wool Yellow", "Wool Lime",
        "Wool Pink", "Wool Gray",
        "Wool Light Gray", "Wool Cyan",
        "Wool Purple", "Wool Blue",
        "Wool Brown", "Wool Green",
        "Wool Red", "Wool Black"
      },
      new String[] {
        "white_wool",
        "orange_wool",
        "magenta_wool",
        "light_blue_wool",
        "yellow_wool",
        "lime_wool",
        "pink_wool",
        "gray_wool",
        "light_gray_wool",
        "cyan_wool",
        "purple_wool",
        "blue_wool",
        "brown_wool",
        "green_wool",
        "red_wool",
        "black_wool"
      }
    ).setVar().setHardness(0.8f, TOOL_NONE, CLS_NONE));
    registerBlock(new BlockX("DANDELION"
      , new String[] {"Dandelion"}
      , new String[] {"dandelion"})
      .resetBoxes(BlockHitTest.Type.SELECTION).addBox(6, 0, 6, 10, 10, 10,BlockHitTest.Type.SELECTION)
      .setSupported().setPlant().setShowAsItem()
    );
    registerBlock(new BlockXVar("FLOWER"
      , new String[] {"Poppy", "Blue Orchid", "Allium", "Azure Bluet", "Red Tulip", "Orange Tulip", "White Tulip", "Pink Tulip", "Oxeye Daisy"}
      , new String[] {"poppy", "blue_orchid", "allium", "azure_bluet", "red_tulip", "orange_tulip", "white_tulip", "pink_tulip", "oxeye_daisy"}
      ).resetBoxes(BlockHitTest.Type.SELECTION).addBox(6, 0, 6, 10, 10, 10,BlockHitTest.Type.SELECTION)
      .setSupported().setPlant().setShowAsItem()
    );
    registerBlock(new BlockXVar2("TALLGRASS"
      , new String[] {
        "Tall Grass", "Large Fern"
      }
      , new String[] {
        "tall_grass_top", "tall_grass_bottom",
        "large_fern_top", "large_fern_bottom",
      })
      .resetBoxes(BlockHitTest.Type.SELECTION).addBox(0, 0, 0, 15, 15, 15,BlockHitTest.Type.SELECTION)
      .setSupported().setPlant().setShowAsItem().setDrop("SEEDS").setDropVar(false).setGreenAllSides()
      .setCanReplace()
    );
    registerBlock(new BlockXVar2("TALLPLANT"
      , new String[] {
        "Lilac", "Rose Bush", "Peony"
      }
      , new String[] {
        "lilac_top", "lilac_bottom",
        "rose_bush_top", "rose_bush_bottom",
        "peony_top", "peony_bottom"
      })
      .resetBoxes(BlockHitTest.Type.SELECTION).addBox(0, 0, 0, 15, 15, 15,BlockHitTest.Type.SELECTION)
      .setSupported().setPlant().setShowAsItem().setDrop("SEEDS").setDropVar(false)
    );
    registerBlock(new BlockX("MUSHROOM_BROWN", new String[] {"Brown Mushroom"}, new String[] {"brown_mushroom"}).setShowAsItem().addBox(6, 0, 6, 10, 10, 10,BlockHitTest.Type.SELECTION));
    registerBlock(new BlockX("MUSHROOM_RED", new String[] {"Red Mushroom"}, new String[] {"red_mushroom"}).setShowAsItem().addBox(6, 0, 6, 10, 10, 10,BlockHitTest.Type.SELECTION));
    registerBlock(new BlockOpaque("GOLD_BLOCK", new String[] {"Gold Block"}, new String[] {"gold_block"}).setHardness(3f, TOOL_PICKAXE, CLS_IRON));
    registerBlock(new BlockOpaque("IRON_BLOCK", new String[] {"Iron Block"}, new String[] {"iron_block"}).setHardness(5f, TOOL_PICKAXE, CLS_STONE));
    registerBlock(new BlockSlab("STONE_SLAB", new String[] {"Stone Slab"}, new String[] {"stone"}).setHardness(2f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockSlab("COBBLESTONE_SLAB", new String[] {"Cobblestone Slab"}, new String[] {"cobblestone"}).setHardness(2f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockSlab("MOSSY_COBBLESTONE_SLAB", new String[] {"Mossy Cobblestone Slab"}, new String[] {"mossy_cobblestone"}).setHardness(2f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockSlab("STONE_BRICKS_SLAB", new String[] {"Stone Bricks Slab"}, new String[] {"stone_bricks"}).setHardness(2f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockSlab("MOSSY_STONE_BRICKS_SLAB", new String[] {"Mossy Stone Bricks Slab"}, new String[] {"mossy_stone_bricks"}).setHardness(2f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockSlab("CRACKED_STONE_BRICKS_SLAB", new String[] {"Cracked Stone Bricks Slab"}, new String[] {"cracked_stone_bricks"}).setHardness(2f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockSlab("BRICKS_SLAB", new String[] {"Bricks Slab"}, new String[] {"bricks"}).setHardness(2f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockSlab("WOOD_SLAB"
      , new String[] {"Oak Wood Slab", "Spruce Wood Slab", "Birch Wood Slab", "Jungle Wood Slab", "Acacia Wood Slab", "Dark Oak Wood Slab"}
      , new String[] {"oak_planks", "spruce_planks", "birch_planks", "jungle_planks", "acacia_planks", "dark_oak_planks"})
      .setHardness(2f, TOOL_AXE, CLS_NONE));
    registerBlock(new BlockOpaque("TNT", new String[] {"TNT"}, new String[] {"tnt_top", "tnt_side", "tnt_bottom"}));
    registerBlock(new BlockOpaque("BOOKSHELF", new String[] {"Book Shelf"}, new String[] {"bookshelf"}).setHardness(1.5f, TOOL_AXE, CLS_NONE));
    registerBlock(new BlockObsidian("OBSIDIAN", new String[] {"Obsidian"}, new String[] {"obsidian"}).setHardness(50f, TOOL_PICKAXE, CLS_DIAMOND));
    registerBlock(new BlockTorch("TORCH", new String[] {"Torch"}, new String[] {"torch"})
      .setLight((byte)14).setShowAsItem());
    registerBlock(new BlockFire("FIRE", new String[] {"Fire"}, new String[] {"fire_0"})
      .setLight((byte)15));
    registerBlock(new BlockStairs("STAIRS_WOOD"
      , new String[] {"Oak Stairs", "Spruce Stairs", "Birch Stairs", "Jungle Stairs", "Acacia Stairs", "Dark Oak Stairs"}
      , new String[] {
        "oak_planks",
        "spruce_planks",
        "birch_planks",
        "jungle_planks",
        "acacia_planks",
        "dark_oak_planks",
      })
      .setFuel(15).setMaterial(MAT_WOOD).setHardness(2f, TOOL_AXE, CLS_NONE)
    );
    registerBlock(new BlockChest("CHEST").setHardness(2.5f, TOOL_AXE, CLS_NONE));
    registerBlock(new BlockOpaque("DIAMOND_ORE", new String[] {"Diamond Ore"}, new String[] {"diamond_ore"}).setHardness(3f, TOOL_PICKAXE, CLS_IRON));
    registerBlock(new BlockOpaque("DIAMOND_BLOCK", new String[] {"Diamond Block"}, new String[] {"diamond_block"}).setHardness(5f, TOOL_PICKAXE, CLS_IRON));
    registerBlock(new BlockCraftTable("CRAFTTABLE", new String[] {"Crafting Table"}
      , new String[] {"crafting_table_top", "crafting_table_front", "crafting_table_side", "oak_planks"}).setHardness(2.5f, TOOL_AXE, CLS_NONE)
    );
    registerBlock(new BlockFurnace("FURNACE", new String[] {"Furnace"}
      , new String[] {"furnace_top", "furnace_front", "furnace_side", "cobblestone"}).setHardness(3.5f, TOOL_PICKAXE, CLS_NONE)
    );
    registerBlock(new BlockFurnace("FURNACE_ACTIVE", new String[] {"Furnace"}
      , new String[] {"furnace_top", "furnace_front_on", "furnace_side", "cobblestone"})
      .setLight((byte)13).setHardness(3.5f, TOOL_PICKAXE, CLS_NONE)
    );
    registerBlock(new BlockLadder("LADDER", new String[] {"Ladder"}, new String[] {"ladder"}).setHardness(0.4f, TOOL_AXE, CLS_NONE));
    registerBlock(new BlockRail("RAIL", new String[] {"Rail"}, new String[] {"rail", "rail_corner"}).setHardness(0.7f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockStairs("STAIRS_STONE", new String[] {"Stairs"}, new String[] {"stone"}).setHardness(2f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockLever("LEVER", new String[] {"Lever"}, new String[] {"lever", "stone"}).setHardness(0.5f, TOOL_NONE, CLS_NONE));
    registerBlock(new BlockPressurePlate("PRESSURE_PLATE", new String[] {"Wood Pressure Plate", "Stone Pressure Plate"}, new String[] {"oak_planks", "stone"}).setHardness(0.5f, TOOL_PICKAXE, CLS_NONE));  //fix me : hardness prefered tool varies
    registerBlock(new BlockOpaque("REDSTONE_ORE", new String[] {"Red Stone Ore"}, new String[] {"redstone_ore"})
      .setDrop("RED_STONE").setHardness(3f, TOOL_PICKAXE, CLS_IRON)
    );
    registerBlock(new BlockRedStoneTorch("REDSTONE_TORCH", new String[] {"Red Stone Torch"}, new String[] {"redstone_torch"}));
    registerBlock(new BlockButton("BUTTON", new String[] {"Wood Button", "Stone Button"}, new String[] {"oak_planks", "stone"}).setHardness(0.5f, TOOL_NONE, CLS_NONE));
    registerBlock(new BlockAlpha("ICEBLOCK", new String[] {"Ice"}, new String[] {"ice"}).setDrop("AIR").setHardness(0.5f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockCactus("CACTUS", new String[] {"Cactus"}, new String[] {"cactus_top", "cactus_side", "cactus_bottom"}).setHardness(0.4f, TOOL_NONE, CLS_NONE));
    registerBlock(new BlockFence("FENCE",
      new String[] {"Oak Wood Fence", "Spruce Wood Fence", "Birch Wood Fence", "Jungle Wood Fence", "Acacia Wood Fence", "Dark Oak Wood Fence"},
      new String[] {"oak_planks", "spruce_planks", "birch_planks", "jungle_planks", "acacia_planks", "dark_oak_planks"})
      .setHardness(2f, TOOL_AXE, CLS_NONE)
    );
    registerBlock(new BlockOpaque("CLAY", new String[] {"Clay Block"}, new String[] {"clay"}).setDrop("CLAY_BALL", 4).setHardness(0.6f, TOOL_SHOVEL, CLS_NONE));
    registerBlock(new BlockOpaque("TERRACOTA",
      new String[] {
        "Stained Clay White", "Stained Clay Orange",
        "Stained Clay Magenta", "Stained Clay Light Blue",
        "Stained Clay Yellow", "Stained Clay Lime",
        "Stained Clay Pink", "Stained Clay Gray",
        "Stained Clay Light Gray", "Stained Clay Cyan",
        "Stained Clay Purple", "Stained Clay Blue",
        "Stained Clay Brown", "Stained Clay Green",
        "Stained Clay Red", "Stained Clay Black"
      },
      new String[] {
        "white_terracotta",
        "orange_terracotta",
        "magenta_terracotta",
        "light_blue_terracotta",
        "yellow_terracotta",
        "lime_terracotta",
        "pink_terracotta",
        "gray_terracotta",
        "light_gray_terracotta",
        "cyan_terracotta",
        "purple_terracotta",
        "blue_terracotta",
        "brown_terracotta",
        "green_terracotta",
        "red_terracotta",
        "black_terracotta"
      }
    ).setVar().setHardness(1.25f, TOOL_PICKAXE, CLS_NONE));

    registerBlock(new BlockLiquid("OIL", new String[] {"Oil"}, new String[] {"water_still"}).setHardness(100f, TOOL_NONE, CLS_NONE));  //TODO
    registerBlock(new BlockOpaque("MUSIC_BOX", new String[] {"Music Box"}, new String[] {"oak_planks"}).setHardness(2f, TOOL_AXE, CLS_NONE));
    registerBlock(new BlockOpaque("PUMPKIN", new String[] {"Pumpkin"}, new String[] {"pumpkin_top", "carved_pumpkin", "pumpkin_side", "pumpkin_side"}).setHardness(1f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockOpaque("NETHER_RACK", new String[] {"Nether Rack"}, new String[] {"netherrack"}).setHardness(0.4f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockOpaque("SOUL_SAND", new String[] {"Soul Sand"}, new String[] {"soul_sand"}).setHardness(0.5f, TOOL_SHOVEL, CLS_NONE));
    registerBlock(new BlockOpaque("GLOWSTONE", new String[] {"Glowstone"}, new String[] {"glowstone"}).setHardness(0.3f, TOOL_NONE, CLS_NONE));
    registerBlock(new BlockNetherPortal("NETHER_PORTAL", new String[] {"Nether Portal"}, new String[] {"nether_portal"}).setHardness(-1f, TOOL_NONE, CLS_NONE));
    registerBlock(new BlockOpaque("PUMPKIN_LIT", new String[] {"Pumpkin Lit"}, new String[] {"pumpkin_top", "jack_o_lantern", "pumpkin_side", "pumpkin_side"}).setHardness(1f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockRedStoneRepeater("REDSTONE_REPEATER", new String[] {"-item-"}, new String[] {"repeater", "repeater_on", "stone", "redstone_torch_off", "redstone_torch"}));
    registerBlock(new BlockOpaque("GLASSBLOCK_COLOR", new String[] {"Glass Block"}, new String[] {"glass"}).setHardness(0.3f, TOOL_NONE, CLS_NONE));
    registerBlock(new BlockTrapDoor("TRAP_DOOR",
      new String[] {"Oak Wood Trapdoor", "Spruce Wood Trapdoor", "Birch Wood Trapdoor", "Jungle Wood Trapdoor", "Acacia Wood Trapdoor", "Dark Oak Wood Trapdoor"},
      new String[] {"oak_trapdoor", "spruce_trapdoor", "birch_trapdoor", "jungle_trapdoor", "acacia_trapdoor", "dark_oak_trapdoor"}
    ).setHardness(3f, TOOL_AXE, CLS_NONE));
    registerBlock(new BlockPane("BARS", new String[] {"Iron Bars"}, new String[] {"iron_bars", "iron_bars"}).setHardness(5f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockPane("GLASS_PANE", new String[] {"Glass Pane"}, new String[] {"glass", "glass_pane_top"}).setHardness(0.3f, TOOL_NONE, CLS_NONE));
    registerBlock(new BlockOpaque("MELON", new String[] {"Melon"}, new String[] {"melon_top", "melon_side"}).setHardness(1f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockVine("VINES", new String[] {"Vines"}, new String[] {"vine"}).setGreenAllSides().setHardness(0.2f, TOOL_AXE, CLS_NONE));
    registerBlock(new BlockGate("GATE"
      , new String[] {"Oak Gate", "Spruce Gate", "Birch Gate", "Jungle Gate", "Acacia Gate", "Dark Oak Gate"}
      , new String[] {
        "oak_planks",
        "spruce_planks",
        "birch_planks",
        "jungle_planks",
        "acacia_planks",
        "dark_oak_planks",
      }
    ).setHardness(2f, TOOL_AXE, CLS_NONE));
    registerBlock(new BlockStairs("STAIRS_BRICK", new String[] {"Stairs"}, new String[] {"bricks"}).setHardness(2f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockStairs("STAIRS_BLOCK", new String[] {"Stairs"}, new String[] {"oak_planks"}));
    registerBlock(new BlockOpaque("MYCELIUM", new String[] {"Mycelium"}, new String[] {"mycelium_top", "mycelium_side", "dirt"}));
    registerBlock(new BlockFace("LILLYPAD", new String[] {"Lilypad"}, new String[] {"lily_pad"}).setGreenAllSides().setShowAsItem().setHardness(0f, TOOL_NONE, CLS_NONE).setDrop("AIR"));
    registerBlock(new BlockOpaque("NETHER_BRICKS", new String[] {"Nether Bricks"}, new String[] {"nether_bricks"}).setHardness(2f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockFence("NETHER_FENCE", new String[] {"Nether Fence"}, new String[] {"nether_bricks"}).setHardness(2f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockStairs("STAIRS_NETHER", new String[] {"Nether Stairs"}, new String[] {"nether_bricks"}).setHardness(2f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockOpaque("ENCHANTING_TABLE", new String[] {"Enchanting Table"}, new String[] {"enchanting_table_top", "enchanting_table_side", "enchanting_table_bottom"}).setHardness(5f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockEndPortal("END_PORTAL", new String[] {"End Portal"}, new String[] {}).setHardness(-1f, TOOL_NONE, CLS_NONE));
    registerBlock(new BlockEndFrame("END_PORTAL_FRAME", new String[] {"End Portal"}, new String[] {"end_portal_frame_top", "end_portal_frame_side", "end_stone", "end_portal_frame_eye"}).setHardness(-1f, TOOL_NONE, CLS_NONE));
    registerBlock(new BlockOpaque("END_STONE", new String[] {"End Stone"}, new String[] {"end_stone"}).setHardness(3f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockOpaque("COCOA", new String[] {"Cocoa", "Cocoa", "Cocoa"}, new String[] {"cocoa_stage0", "cocoa_stage1", "cocoa_stage2"}));  //fix me
    registerBlock(new BlockOpaque("EMERALD_ORE", new String[] {"Emerald Ore"}, new String[] {"emerald_ore"}).setDrop("EMERALD").setHardness(3f, TOOL_PICKAXE, CLS_IRON));
    registerBlock(new BlockEnderChest("ENDER_CHEST").setHardness(22.5f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockFace("TRIP_HOOK", new String[] {"Trip Line"}, new String[] {"tripwire_hook"}));
    registerBlock(new BlockOpaque("EMERALD_BLOCK", new String[] {"Emerald Block"}, new String[] {"emerald_block"}).setHardness(5f, TOOL_PICKAXE, CLS_IRON));
    registerBlock(new BlockOpaque("COMMAND_BLOCK", new String[] {"Command Block"}, new String[] {"command_block_back", "command_block_front", "command_block_side", "command_block_side"}));
    registerBlock(new BlockOpaque("BEACON", new String[] {"Beacon"}, new String[] {"beacon"}));
    registerBlock(new BlockWall("WALL", new String[] {"Stone Wall"}, new String[] {"stone"}).setHardness(2f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockOpaque("ANVIL", new String[] {"Anvil"}, new String[] {"anvil", "anvil_top"}).setHardness(5f, TOOL_PICKAXE, CLS_NONE));  //TODO
//    registerBlock(new BlockOpaque("CHEST_TRAP, new String[] {"Chest Trapped"}, new String[] {""}).setHardness(2.5f, TOOL_AXE, CLS_NONE));  //TODO
    registerBlock(new BlockCarpet("PLATE_GOLD", new String[] {"Gold Pressure Plate"}, new String[] {"gold_block"}));
    registerBlock(new BlockCarpet("PLATE_IRON", new String[] {"Iron Pressure Plate"}, new String[] {"iron_block"}));
    registerBlock(new BlockRedStoneComparator("REDSTONE_COMPARATOR", new String[] {"-item-"}, new String[] {"comparator", "comparator_on", "stone", "redstone_torch_off", "redstone_torch"}));
    registerBlock(new BlockDaylightSensor("SOLAR_PANEL", new String[] {"Daylight Sensor"}, new String[] {"daylight_detector_top", "daylight_detector_side"}).setHardness(0.2f, TOOL_AXE, CLS_NONE));
    registerBlock(new BlockOpaque("REDSTONE_BLOCK", new String[] {"Redstone Block"}, new String[] {"redstone_block"}).setHardness(5f, TOOL_PICKAXE, CLS_WOOD));

    registerBlock(new BlockOpaque("QUARTZ_ORE", new String[] {"Quartz"}, new String[] {"nether_quartz_ore"}).setDrop("QUARTZ").setHardness(3f, TOOL_PICKAXE, CLS_WOOD));
    registerBlock(new BlockHopper("HOPPER", new String[] {"-item-"}, new String[] {"hopper_top", "hopper_inside", "hopper_outside"}).setHardness(3f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockOpaque("QUARTZ_BLOCK", new String[] {"Quartz Block"}, new String[] {"quartz_block_top"}).setHardness(0.8f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockStairs("STAIRS_QUARTZ", new String[] {"Quartz Stairs"}, new String[] {"quartz_block_top"}).setHardness(0.8f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockRail("RAIL_ACTIVATOR", new String[] {"Rail Activator"}, new String[] {"activator_rail", "activator_rail_on"}).setRedstone().setHardness(0.7f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockDropper("DROPPER", new String[] {"Dropper"}, new String[] {"dropper_front", "dropper_front_vertical", "piston_bottom"}).setHardness(3.5f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockPane("GLASS_PANE_COLOR",
      new String[] {
        "Glass Pane White", "Glass Pane Orange",
        "Glass Pane Magenta", "Glass Pane Light Blue",
        "Glass Pane Yellow", "Glass Pane Lime",
        "Glass Pane Pink", "Glass Pane Gray",
        "Glass Pane Light Gray", "Glass Pane Cyan",
        "Glass Pane Purple", "Glass Pane Blue",
        "Glass Pane Brown", "Glass Pane Green",
        "Glass Pane Red", "Glass Pane Black"
      },
      new String[] {
        "white_stained_glass", "white_stained_glass_pane_top",
        "orange_stained_glass", "orange_stained_glass_pane_top",
        "magenta_stained_glass", "magenta_stained_glass_pane_top",
        "light_blue_stained_glass", "light_blue_stained_glass_pane_top",
        "yellow_stained_glass", "yellow_stained_glass_pane_top",
        "lime_stained_glass", "lime_stained_glass_pane_top",
        "pink_stained_glass", "pink_stained_glass_pane_top",
        "gray_stained_glass", "gray_stained_glass_pane_top",
        "light_gray_stained_glass", "light_gray_stained_glass_pane_top",
        "cyan_stained_glass", "cyan_stained_glass_pane_top",
        "purple_stained_glass", "purple_stained_glass_pane_top",
        "blue_stained_glass", "blue_stained_glass_pane_top",
        "brown_stained_glass", "brown_stained_glass_pane_top",
        "green_stained_glass", "green_stained_glass_pane_top",
        "red_stained_glass", "red_stained_glass_pane_top",
        "black_stained_glass", "black_stained_glass_pane_top"
      }
    ));
    registerBlock(new BlockOpaque("HAYBALE", new String[] {"Hay Bale"}, new String[] {"hay_block_top", "hay_block_side"}).setDir().setHardness(0.5f, TOOL_NONE, CLS_NONE));
    registerBlock(new BlockCarpet("CARPET", new String[] {"Carpet"}, new String[] {"white_wool"}).setHardness(0.1f, TOOL_NONE, CLS_NONE));
    registerBlock(new BlockOpaque("COAL_BLOCK", new String[] {"Coal Block"}, new String[] {"coal_block"}).setFuel(800).setHardness(5f, TOOL_PICKAXE, CLS_NONE));
    registerBlock(new BlockOpaque("SNOW_PACKED", new String[] {"Packed Snow"}, new String[] {"snow"}));

    registerBlock(new BlockStep("STEPGRASS", new String[] {"Step Grass"}, new String[] {"grass_block_top"}).setGreenAllSides());
    registerBlock(new BlockStep("STEPDIRT", new String[] {"Step Dirt"}, new String[] {"dirt"}));
    registerBlock(new BlockStep("STEPSTONE", new String[] {"Step Stone"}, new String[] {"stone"}));
    registerBlock(new BlockStep("STEPSAND", new String[] {"Step Sand"}, new String[] {"sand"}));
    registerBlock(new BlockStep("STEPSNOW", new String[] {"Step Snow"}, new String[] {"snow"}));
    registerBlock(new BlockBed("BED", new String[] {"Bed"}, new String[] {"oak_planks", "bed_feet_end", "bed_head_end", "bed_feet_side", "bed_head_side", "bed_feet_top", "bed_head_top"})
      .setHardness(0.2f, TOOL_NONE, CLS_NONE)
    );
    registerBlock(new BlockDoor("WOOD_DOOR"
      , new String[] {"Oak Wood Door", "Spruce Wood Door", "Birch Wood Door", "Jungle Wood Door", "Acacia Wood Door", "Dark Oak Wood Door"}
      , new String[] {
        "oak_door_top", "oak_door_bottom",
        "spruce_door_top", "spruce_door_bottom",
        "birch_door_top", "birch_door_bottom",
        "jungle_door_top", "jungle_door_bottom",
        "acacia_door_top", "acacia_door_bottom",
        "dark_oak_door_top", "dark_oak_door_bottom"
      }
      ).setDrop("WOOD_DOOR_ITEM").setHardness(3f, TOOL_AXE, CLS_NONE)
    );
    registerBlock(new BlockDoor("IRON_DOOR", new String[] {"Iron Door"}, new String[] {"iron_door_top", "iron_door_bottom"})
      .setDrop("IRON_DOOR_ITEM").setHardness(5f, TOOL_PICKAXE, CLS_NONE)
    );

    registerBlock(new BlockBarrier("BARRIER", new String[] {"Barrier"}, new String[] {}));

    //items as blocks

    registerBlock(new BlockWheat("WHEAT", new String[] {
      "Wheat", "Wheat", "Wheat", "Wheat", "Wheat", "Wheat", "Wheat", "Wheat",
    }, new String[] {
      "wheat_stage0", "wheat_stage1", "wheat_stage2", "wheat_stage3", "wheat_stage4", "wheat_stage5", "wheat_stage6", "wheat_stage7"
    }));
    registerBlock(new BlockRedStoneDust("RED_STONE", new String[] {"Red Stone Dust"}, new String[] {"redstone_dust_dot", "redstone_dust_line0", "redstone_dust_line1", "redstone_dust_overlay"}));

    registerBlock(new BlockSign("SIGN", new String[] {"Sign"}, new String[] {"oak_planks"}).setHardness(1f, TOOL_PICKAXE, CLS_NONE));

    solid = registerBlock(new BlockOpaque("SOLID",  //jfasset
      new String[] {
        "Solid Block White",
        "Solid Block Ornage",
        "Solid Block Magenta",
        "Solid Block Light Blue",
        "Solid Block Yellow",
        "Solid Block Lime",
        "Solid Block Pink",
        "Solid Block Gray",
        "Solid Block Light Gray",
        "Solid Block Cyan",
        "Solid Block Purple",
        "Solid Block Blue",
        "Solid Block Brown",
        "Solid Block Green",
        "Solid Block Red",
        "Solid Block Black"
      },
      new String[] {
        "solid_white",
        "solid_orange",
        "solid_magenta",
        "solid_light_blue",
        "solid_yellow",
        "solid_lime",
        "solid_pink",
        "solid_gray",
        "solid_light_gray",
        "solid_cyan",
        "solid_purple",
        "solid_blue",
        "solid_brown",
        "solid_green",
        "solid_red",
        "solid_black"
      }
    ).setVar());
    registerBlock(new BlockKelpPlant("KELPPLANT"
      , new String[] {"Kelp", "Kelp"}
      , new String[] {"kelp_plant", "kelp"}
      )
      .setGreenAllSides().setDrop("KELP").setDropVar(false)
      .addBox(0, 0, 0, 15, 15, 15,BlockHitTest.Type.SELECTION).setMaterial(MAT_WOOD)
    );
    registerBlock(new BlockOpaque("TEST_ARROW", new String[] {"test"}, new String[] {"arrow"}).setDir());  //jfasset
    registerBlock(new BlockX("SEAWEEDS"
      , new String[] {"Seaweeds"}
      , new String[] {"seagrass"}
      )
      .setDrop("AIR").setSupported().setCanReplace()
      .addBox(0, 0, 0, 15, 15, 15,BlockHitTest.Type.SELECTION).setMaterial(MAT_WOOD)
    );
    registerBlock(new BlockX2("TALLSEAWEEDS"
      , new String[] {"Tall Seaweeds"}
      , new String[] {"tall_seagrass_top", "tall_seagrass_bottom"})
      .setDrop("AIR").setSupported().setCanReplace()
      .addBox(0, 0, 0, 15, 15, 15,BlockHitTest.Type.SELECTION).setMaterial(MAT_WOOD)
    );

  }

  private void addSubTexture(BlockBase block, String name) {
    name = "block/" + name;
    //check if asset already loaded
    for(int a=0;a<tiles.size();a++) {
      AssetImage ai = tiles.get(a);
      if (ai.name.equals(name)) {
        return;
      }
    }
    //asset not loaded yet, load it now
    AssetImage ai = Assets.getImage(name);
    ai.isPerf = block.isPerf;
    if (block.clampAlpha) {
      int px[] = ai.image.getBuffer();
      int pxs = px.length;
      for(int a=0;a<pxs;a++) {
        int alpha = (px[a] & 0xff000000) >>> 24;
        if (alpha == 0 || alpha == 0xff) continue;
        if (alpha < 128) {
          px[a] &= 0xffffff;  //remove alpha
        } else {
          px[a] |= 0xff000000;  //full alpha
        }
      }
    }
    if (ai.image.getWidth() != ai.image.getHeight()) {
      //animated image
      ai.isAnimated = true;
    }
    tiles.add(ai);
  }

  public void stitchTiles() {
    stitched = new TextureMap();
    stitched.initImage(512, 512);
    stitched.initUsage();
    cracks = new TextureMap();
    cracks.unit = 1;
    cracks.initImage(64, 64);
    cracks.initUsage();
    for(int idx=0;idx<MAX_ID;idx++) {
      if (regBlocks[idx] == null) continue;
      BlockBase block = regBlocks[idx];
      //set default buffersIdx (may change if animated)
      if (block.isAlpha) {
        block.buffersIdx = Chunk.DEST_ALPHA;
      } else {
        block.buffersIdx = Chunk.DEST_NORMAL;
      }
      for(int i=0;i<block.images.length;i++) {
        addSubTexture(block, block.images[i]);
      }
      for(int i=0;i<block.images2.length;i++) {
        addSubTexture(block, block.images2[i]);
      }
    }
    for(int a=0;a<=9;a++) {
      AssetImage ai = Assets.getImage("block/destroy_stage_" + a);
      ai.isCrack = true;
      tiles.add(ai);
    }
    //sort list big to small (animated first)
    tiles.sort(new Comparator<AssetImage>() {
      public int compare(AssetImage o1, AssetImage o2) {
        AssetImage ai1 = (AssetImage)o1;
        AssetImage ai2 = (AssetImage)o2;
        if (ai1.isAnimated && !ai2.isAnimated) return -1;  //put animated first
        if (!ai1.isAnimated && ai2.isAnimated) return 1;  //put animated first
        int w1 = ai1.image.getWidth();
        int w2 = ai2.image.getWidth();
        if (w1 < w2) return -1;
        if (w1 > w2) return 1;
        return 0;
      }
    });
    for(int a=0;a<tiles.size();a++) {
      AssetImage ai = tiles.get(a);
      int w = ai.image.getWidth();
      int h = ai.image.getHeight();
      TextureMap texture;
      if (ai.isCrack) {
        texture = cracks;
      } else {
        texture = stitched;
      }
      if (ai.isAnimated) {
        //animation
        int cnt = h / w;
        ai.noFrames = cnt;
        ai.images = new JFImage[cnt];
        ai.w = w;
        ai.h = w;
        for(int b=0;b<cnt;b++) {
          JFImage frame = new JFImage();
          frame.setSize(w, w);
          int px[] = ai.image.getPixels(0, b * w, w, w);
          frame.putPixels(px, 0, 0, w, w, 0);
          ai.images[b] = frame;
        }
        int loc[] = texture.placeSubTexture(ai.getPixels(0), w, w);
        if (loc == null) {
          JFAWT.showError("Error", "Your texture pack size can not fit into your video cards max texture size\nPlease remove high resolution packs and restart.");
          valid = false;
          return;
        }
        ai.x = loc[0];
        ai.y = loc[1];
        RenderEngine.animatedTextures.add(ai);
      } else {
        //normal
        ai.w = w;
        ai.h = h;
        int loc[] = texture.placeSubTexture(ai.getPixels(), w, h);
        if (loc == null) {
          JFAWT.showError("Error", "Your texture pack size can not fit into your video cards max texture size\nPlease remove high resolution packs and restart.");
          valid = false;
          return;
        }
        ai.x = loc[0];
        ai.y = loc[1];
      }
    }
    valid = true;
    stitched.image.savePNG("blocks-stitched.png");
    stitched.usage.savePNG("blocks-stitched-usage.png");
    for(int a=0;a<MAX_ID;a++) {
      if (regBlocks[a] == null) continue;
      BlockBase block = regBlocks[a];
      block.textures = new SubTexture[block.images.length];
      for(int i=0;i<block.images.length;i++) {
        block.textures[i] = getSubTexture(block.images[i]);
      }
      block.textures2 = new SubTexture[block.images2.length];
      for(int i=0;i<block.images2.length;i++) {
        block.textures2[i] = getSubTexture(block.images2[i]);
      }
    }
    subcracks = new SubTexture[10];
    for(int a=0;a<=9;a++) {
      subcracks[a] = getSubTexture("destroy_stage_" + a);
    }
  }

  private Face face = new Face();

  private SubTexture getSubTexture(String name) {
    name = "block/" + name;
    for(int a=0;a<tiles.size();a++) {
      AssetImage ai = tiles.get(a);
      if (ai.name.equals(name)) {
        boolean isFlow = ai.name.endsWith("_flow");
        SubTexture st = new SubTexture();
        st.ai = ai;
        TextureMap texture;
        if (ai.isCrack)
          texture = cracks;
        else
          texture = stitched;
        st.texture = texture;
        st.isAnimated = ai.isAnimated;
        float x, y;
        float w, h;
        if (isFlow) {
          w = ai.w / 2;
          h = ai.h / 2;
          x = ai.x + w/2;
          y = ai.y + h/2;
        } else {
          x = ai.x;
          y = ai.y;
          w = ai.w;
          h = ai.h;
        }
        st.x1 = x / texture.sx;
        st.y1 = y / texture.sy;
        st.x2 = (x + w - 1) / texture.sx;
        st.y2 = (y + h - 1) / texture.sy;
        st.width = st.x2 - st.x1 + (1.0f/texture.sx);
        st.height = st.y2 - st.y1 + (1.0f/texture.sy);
        if (isFlow) {
          //calc 45 degree coords
          //this is why the flow's are 2x2 of the same subimage
          float w2 = st.width/2f;
          float h2 = st.height/2f;
          if (false) {
            //calc it (not working yet)
            face.x[0] = -w2;
            face.y[0] = h2;
            face.z[0] = 0;
            face.x[1] = w2;
            face.y[1] = h2;
            face.z[1] = 0;
            face.x[2] = w2;
            face.y[2] = -h2;
            face.z[2] = 0;
            face.x[3] = -w2;
            face.y[3] = -h2;
            face.z[3] = 0;
            face.rotate(0, 0, 45);
            st.fx1 = st.x1 + (face.x[0] + w2);
            st.fy1 = st.y1 + (face.y[0] - h2);
            st.fx2 = st.x2 + (face.x[1] - w2);
            st.fy2 = st.y1 + (face.y[1] - h2);
            st.fx3 = st.x2 + (face.x[2] - w2);
            st.fy3 = st.y2 + (face.y[2] + h2);
            st.fx4 = st.x1 + (face.x[3] + w2);
            st.fy4 = st.y2 + (face.y[3] + h2);
          } else {
            //approx it
            st.fx1 = st.x1 + w2;
            st.fy1 = st.y1 + h2 * 0.3535f;
            st.fx2 = st.x2 + w2 * 0.3535f;
            st.fy2 = st.y1 - h2;
            st.fx3 = st.x2 - w2;
            st.fy3 = st.y2 - h2 * 0.3535f;
            st.fx4 = st.x1 - w2 * 0.3535f;
            st.fy4 = st.y2 + h2;
          }
        }
        return st;
      }
    }
    Static.log("Error:Blocks.getSubTexture() Failed for:" + name);
    return null;
  }

  public void initTexture() {
    stitched.load();
    cracks.load();
  }

  public void initPerf(boolean reload) {
    for(int a=0;a<MAX_ID;a++) {
      if (regBlocks[a] == null) continue;
      BlockBase block = regBlocks[a];
      if (!block.isPerf) continue;
      block.isOpaque = !Settings.current.fancy;
      block.isComplex = Settings.current.fancy;
      block.isSolid = !Settings.current.fancy;
      if (reload) block.reloadAll();
    }
  }

  public void initBuffers() {
    Static.log("Blocks.initBuffers()");
    Static.data.chunk = new Chunk(null);
    for(int a=0;a<MAX_ID;a++) {
      BlockBase block = regBlocks[a];
      if (block == null) continue;
      if (block.cantGive) continue;
      if (block.renderAsEntity) continue;
      int vars = 1;
      if (block.isVar) {
        vars = block.names.length;
      }
      block.bufs = new RenderDest[vars];
      if (block.renderAsItem) {
        block.voxel = new Voxel[vars];
      }
      try {
        for(int var=0;var<vars;var++) {
          block.bufs[var] = new RenderDest(Chunk.DEST_COUNT);
          if (block.renderAsItem) {
            block.addFaceInvItem(block.bufs[var].getBuffers(0), var, block.isGreen);
            block.bufs[var].preferedIdx = 0;
            //also create voxel for render in hand
            block.createVoxel(var);
          } else {
            Static.data.reset();
            int dir = block.getPreferredDir();
            Static.data.dir[X] = dir;
            Static.data.dir2[X] = dir;
            Static.data.var[X] = block.isVar ? var : 0;
            Static.data.var2[X] = block.isVar ? var : 0;
            block.buildBuffers(block.bufs[var]);
            block.bufs[var].preferedIdx = block.buffersIdx;
          }
          block.bufs[var].getBuffers(block.bufs[var].preferedIdx).copyBuffers();
        }
      } catch (Exception e) {
        Static.log("initBuffers:" + block);
        Static.log(e);
      }
    }
  }
}
