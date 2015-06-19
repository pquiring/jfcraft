package jfcraft.data;

/** Registered blocks
 *
 * @author pquiring
 *
 * Created : Mar 25, 2014
 */

import java.util.*;

import javaforce.*;
import javaforce.gl.*;

import jfcraft.block.*;
import jfcraft.item.*;
import jfcraft.data.*;
import jfcraft.opengl.*;

public class Blocks {
  private static final int MAX_ID = 65536;
  private ArrayList<AssetImage> tiles = new ArrayList<AssetImage>();  //main stitched images

  public int blockCount = 0;
  public BlockBase[] blocks = new BlockBase[MAX_ID];  //blocks (in order of id)
  public BlockBase[] regBlocks = new BlockBase[MAX_ID];  //registered blocks (not in order of id)
  public Texture stitched;  //main stitched texture (including animated textures and cracks)
  public Texture cracks;  //cracks
  public SubTexture subcracks[];

  public boolean valid;

  public static char getBlockID(String name) {
    return Static.server.world.getBlockID(name);
  }

  public void registerBlock(BlockBase block) {
    regBlocks[blockCount++] = block;
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
  public static char SNOW;
  public static char WATER;
  public static char LAVA;
  public static char SAND;
  public static char CLAY;
  public static char HARDENED_CLAY;
  public static char STAINED_CLAY;
  public static char OIL;
  public static char GRAVEL;
  public static char STONE;
  public static char COBBLESTONE;
  public static char PLANKS;
  public static char SAPLING;
  public static char BEDROCK;
  public static char GOLDORE;
  public static char IRONORE;
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
  public static char TALLGRASS;
  public static char DEADBUSH;
  public static char PISTON;
  public static char WOOL;
  public static char FLOWER;
  public static char MUSHROOM_BROWN;
  public static char MUSHROOM_RED;
  public static char GOLD_BLOCK;
  public static char IRON_BLOCK;
  public static char STONE_VARS;
  public static char SLAB;
  public static char BRICK;
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
  public static char NETHER_BRICK;
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
  public static char STEP;
  public static char SIGN;
  public static char BED;
  public static char WOOD_DOOR;
  public static char IRON_DOOR;
  public static char BARRIER;
  public static char RED_STONE;
  public static char WHEAT;
  public static char SOLID;  //solid color block

  public static char TEST_ARROW;

  //wood vars
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

  public void registerDefault() {
    registerBlock(new BlockAir("AIR"));
    registerBlock(new BlockOpaque("STONE", new String[] {"Stone"}, new String[] {"stone"})
      .setDrop("COBBLESTONE").setSmooth("STEP"));
    registerBlock(new BlockGrass("GRASS", new String[] {"Grass"}, new String[] {"grass_top", "grass_side", "dirt"})
      .setGreenTop().setSupportsPlant().setSmooth("STEP").setDrop("DIRT"));
    registerBlock(new BlockCarpet("SNOW", new String[] {"Snow"}, new String[] {"snow"})
      .setDrop("AIR").setSupported().setBlocks2().setCanReplace());
    registerBlock(new BlockDirt("DIRT", new String[] {"Dirt", "Podzol", "Farmland", "Farmland"}
      , new String[] {
        "dirt", "dirt", "dirt",
        "dirt_podzol_top", "dirt_podzol_side", "dirt",
        "farmland_dry", "dirt", "dirt",
        "farmland_wet", "dirt", "dirt"
      })
      .setSupportsPlant().setSmooth("STEP").setVar());
    registerBlock(new BlockOpaque("COBBLESTONE", new String[] {"Cobble Stone"}, new String[] {"cobblestone"})
      .setSmooth("STEP"));
    registerBlock(new BlockOpaqueVar("PLANKS"
      , new String[] {"Oak Wood Planks", "Spruce Wood Planks", "Birch Wood Planks", "Jungle Wood Planks", "Acacia Wood Planks", "Dark Oak Wood Planks"}
      , new String[] {"planks_oak", "planks_spruce", "planks_birch", "planks_jungle", "planks_acacia", "planks_big_oak"})
      .setFuel(15).setWood()
    );
    registerBlock(new BlockXVar("SAPLING"
      , new String[] {"Oak Wood Sapling", "Spruce Wood Sapling", "Birch Wood Sapling", "Jungle Wood Sapling", "Acacia Wood Sapling", "Dark Oak Wood Sapling"}
      , new String[] {"sapling_oak", "sapling_spruce", "sapling_birch", "sapling_jungle", "sapling_acacia", "sapling_roofed_oak"})
      .setFuel(5).setWood()
    );
    registerBlock(new BlockOpaque("BEDROCK", new String[] {"Bedrock"}, new String[] {"bedrock"}));
    registerBlock(new BlockLiquid("WATER", new String[] {"Water"}, new String[] {"water_still", "water_flow"}).setFlowRate(1).setRenews(true));
    registerBlock(new BlockLiquid("LAVA", new String[] {"Lava"}, new String[] {"lava_still", "lava_flow"}).setFlowRate(3).setRenews(false));
    registerBlock(new BlockFalling("SAND", new String[] {"Sand", "Red Sand"}, new String[] {"sand", "red_sand"})
      .setBake("GLASSBLOCK").setVar()
    );
    registerBlock(new BlockFalling("GRAVEL", new String[] {"Gravel"}, new String[] {"gravel"}));
    registerBlock(new BlockOpaque("GOLDORE", new String[] {"Gold Ore"}, new String[] {"gold_ore"}).setBake("GOLD_INGOT"));
    registerBlock(new BlockOpaque("IRONORE", new String[] {"Iron Ore"}, new String[] {"iron_ore"}).setBake("IRON_INGOT"));
    registerBlock(new BlockOpaque("COALORE", new String[] {"Coal Ore"}, new String[] {"coal_ore"}).setDrop("COAL"));
    registerBlock(new BlockOpaqueVar("WOOD"
      , new String[] {"Oak Wood", "Spruce Wood", "Birch Wood", "Jungle Wood", "Acacia Wood", "Dark Oak Wood"}
      , new String[] {
        "log_oak_top", "log_oak",
        "log_spruce_top", "log_spruce",
        "log_birch_top", "log_birch",
        "log_jungle_top", "log_jungle",
        "log_acacia_top", "log_acacia",
        "log_big_oak_top", "log_big_oak"
      })
      .setFuel(15).setWood().setDir()
    );
    registerBlock(new BlockLeaves("LEAVES"
      , new String[] {"Oak Wood Leaves", "Spruce Wood Leaves", "Birch Wood Leaves", "Jungle Wood Leaves", "Acacia Wood Leaves", "Dark Oak Wood Leaves"}
      , new String[] {"leaves_oak", "leaves_spruce", "leaves_birch", "leaves_jungle", "leaves_acacia", "leaves_big_oak"}
      , new String[] {"leaves_oak_opaque", "leaves_spruce_opaque", "leaves_birch_opaque", "leaves_jungle_opaque", "leaves_acacia_opaque", "leaves_big_oak_opaque"}
    ).setGreenAllSides().setPerf().setDrop("AIR"));
    registerBlock(new BlockOpaque("SPONGE", new String[] {"Sponge"}, new String[] {"sponge"}));
    registerBlock(new BlockTrans("GLASSBLOCK", new String[] {"Glass Block"}, new String[] {"glass"}));
    registerBlock(new BlockOpaque("LAPIS_ORE", new String[] {"Lapis Ore"}, new String[] {"lapis_ore"}));
    registerBlock(new BlockOpaque("LAPIS_BLOCK", new String[] {"Lapis Block"}, new String[] {"lapis_block"}));
    registerBlock(new BlockDispenser("DISPENSER", new String[] {"Dispenser"}
      , new String[] {"dispenser_front_horizontal", "dispenser_front_vertical", "piston_bottom"}));
    registerBlock(new BlockOpaque("SAND_STONE", new String[] {"Sand Stone"}, new String[] {"sandstone_top", "sandstone_normal", "sandstone_bottom"}));
    registerBlock(new BlockOpaque("NOTE_BLOCK", new String[] {"Note Block"}, new String[] {"noteblock"}));
    registerBlock(new BlockRail("RAIL_POWERED", new String[] {"Rail Powered"}, new String[] {"rail_golden", "rail_golden_powered"}).setRedstone());
    registerBlock(new BlockRail("RAIL_DETECTOR", new String[] {"Rail Detector"}, new String[] {"rail_detector", "rail_detector_powered"}).setRedstone());
    registerBlock(new BlockPiston("PISTON_STICKY", new String[] {"Sticky Piston"}, new String[] {"piston_bottom", "piston_side", "piston_inner", "piston_side", "piston_top_normal", "piston_side", "piston_top_sticky"}).setSticky());
    registerBlock(new BlockX("WEB", new String[] {"Web"}, new String[] {"web"}).addBox(0, 0, 0, 15, 15, 15,BlockHitTest.Type.SELECTION));
    registerBlock(new BlockXVar("TALLGRASS"
      , new String[] {"Tall Grass", "Fern"}
      , new String[] {"tallgrass", "fern"})
      .setGreenAllSides().setDrop("SEEDS").setSupported().setDropVar(false).addBox(0, 0, 0, 15, 15, 15,BlockHitTest.Type.SELECTION)
    );
    registerBlock(new BlockX("DEADBUSH"
      , new String[] {"Dead Bush"}
      , new String[] {"deadbush"}).setSupported().addBox(6, 0, 6, 10, 10, 10,BlockHitTest.Type.SELECTION)
    );
    registerBlock(new BlockPiston("PISTON", new String[] {"Piston"}, new String[] {"piston_bottom", "piston_side", "piston_inner", "piston_side", "piston_top_normal", "piston_side", "piston_top_normal"}));
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
        "wool_colored_white",
        "wool_colored_orange",
        "wool_colored_magenta",
        "wool_colored_light_blue",
        "wool_colored_yellow",
        "wool_colored_lime",
        "wool_colored_pink",
        "wool_colored_gray",
        "wool_colored_silver",
        "wool_colored_cyan",
        "wool_colored_purple",
        "wool_colored_blue",
        "wool_colored_brown",
        "wool_colored_green",
        "wool_colored_red",
        "wool_colored_black"
      }
    ).setVar());
    registerBlock(new BlockXVar("FLOWER"
      , new String[] {"Flower",           "Flower",        "Flower",             "Flower",           "Flower",             "Flower",         "Flower",      "Flower",              "Flower",            "Flower",           "Flower"}
      , new String[] {"flower_dandelion", "flower_allium", "flower_blue_orchid", "flower_houstonia", "flower_oxeye_daisy", "flower_paeonia", "flower_rose", "flower_tulip_orange", "flower_tulip_pink", "flower_tulip_red", "flower_tulip_white"})
      .resetBoxes(BlockHitTest.Type.SELECTION).addBox(6, 0, 6, 10, 10, 10,BlockHitTest.Type.SELECTION)
      .setSupported().setPlant().setShowAsItem()
    );
    registerBlock(new BlockX("MUSHROOM_BROWN", new String[] {"Brown Mushroom"}, new String[] {"mushroom_brown"}).setShowAsItem().addBox(6, 0, 6, 10, 10, 10,BlockHitTest.Type.SELECTION));
    registerBlock(new BlockX("MUSHROOM_RED", new String[] {"Red Mushroom"}, new String[] {"mushroom_red"}).setShowAsItem().addBox(6, 0, 6, 10, 10, 10,BlockHitTest.Type.SELECTION));
    registerBlock(new BlockOpaque("GOLD_BLOCK", new String[] {"Gold Block"}, new String[] {"gold_block"}));
    registerBlock(new BlockOpaque("IRON_BLOCK", new String[] {"Iron Block"}, new String[] {"iron_block"}));
    registerBlock(new BlockSlab("SLAB", new String[] {"Slab"}, new String[] {"stone_slab_top", "stone_slab_side"}));
    registerBlock(new BlockOpaque("BRICK", new String[] {"Brick"}, new String[] {"brick"}));
    registerBlock(new BlockOpaque("TNT", new String[] {"TNT"}, new String[] {"tnt_top", "tnt_side", "tnt_bottom"}));
    registerBlock(new BlockOpaque("BOOKSHELF", new String[] {"Book Shelf"}, new String[] {"bookshelf"}));
    registerBlock(new BlockObsidian("OBSIDIAN", new String[] {"Obsidian"}, new String[] {"obsidian"}));
    registerBlock(new BlockTorch("TORCH", new String[] {"Torch"}, new String[] {"torch_on"})
      .setLight((byte)14).setShowAsItem());
    registerBlock(new BlockFire("FIRE", new String[] {"Fire"}, new String[] {"fire_layer_0"})
      .setLight((byte)15));
    registerBlock(new BlockStairs("STAIRS_WOOD"
      , new String[] {"Oak Stairs", "Spruce Stairs", "Birch Stairs", "Jungle Stairs", "Acacia Stairs", "Dark Oak Stairs"}
      , new String[] {
        "planks_oak",
        "planks_spruce",
        "planks_birch",
        "planks_jungle",
        "planks_acacia",
        "planks_big_oak",
      })
      .setFuel(15).setWood()
    );
    registerBlock(new BlockChest("CHEST"));
    registerBlock(new BlockOpaque("DIAMOND_ORE", new String[] {"Diamond Ore"}, new String[] {"diamond_ore"}));
    registerBlock(new BlockOpaque("DIAMOND_BLOCK", new String[] {"Diamond Block"}, new String[] {"diamond_block"}));
    registerBlock(new BlockCraftTable("CRAFTTABLE", new String[] {"Crafting Table"}
      , new String[] {"crafting_table_top", "crafting_table_front", "crafting_table_side", "planks_oak"})
    );
    registerBlock(new BlockFurnace("FURNACE", new String[] {"Furnace"}
      , new String[] {"furnace_top", "furnace_front_off", "furnace_side", "cobblestone"})
    );
    registerBlock(new BlockFurnace("FURNACE_ACTIVE", new String[] {"Furnace"}
      , new String[] {"furnace_top", "furnace_front_on", "furnace_side", "cobblestone"})
      .setLight((byte)13)
    );
    registerBlock(new BlockFace("LADDER", new String[] {"Ladder"}, new String[] {"ladder"}));
    registerBlock(new BlockRail("RAIL", new String[] {"Rail"}, new String[] {"rail_normal", "rail_normal_turned"}));
    registerBlock(new BlockStairs("STAIRS_STONE", new String[] {"Stairs"}, new String[] {"stone"}));  //fix me
    registerBlock(new BlockLever("LEVER", new String[] {"Lever"}, new String[] {"lever", "stone"}));
    registerBlock(new BlockPressurePlate("PRESSURE_PLATE", new String[] {"Wood Pressure Plate", "Stone Pressure Plate"}, new String[] {"planks_oak", "stone"}));
    registerBlock(new BlockOpaque("REDSTONE_ORE", new String[] {"Red Stone Ore"}, new String[] {"redstone_ore"})
      .setBake("RED_STONE")
    );
    registerBlock(new BlockRedStoneTorch("REDSTONE_TORCH", new String[] {"Red Stone Torch"}, new String[] {"redstone_torch_on"}));
    registerBlock(new BlockButton("BUTTON", new String[] {"Wood Button", "Stone Button"}, new String[] {"planks_oak", "stone"}));
    registerBlock(new BlockAlpha("ICEBLOCK", new String[] {"Ice"}, new String[] {"ice"}).setDrop("AIR"));
    registerBlock(new BlockCactus("CACTUS", new String[] {"Cactus"}, new String[] {"cactus_top", "cactus_side", "cactus_bottom"}));
    registerBlock(new BlockFence("FENCE",
      new String[] {"Oak Wood Fence", "Spruce Wood Fence", "Birch Wood Fence", "Jungle Wood Fence", "Acacia Wood Fence", "Dark Oak Wood Fence"},
      new String[] {"planks_oak", "planks_spruce", "planks_birch", "planks_jungle", "planks_acacia", "planks_big_oak"})
    );
    registerBlock(new BlockOpaque("CLAY", new String[] {"Clay Block"}, new String[] {"clay"}).setDrop("CLAY_BALL", 4));
    registerBlock(new BlockOpaque("HARDENED_CLAY", new String[] {"Hardened Clay"}, new String[] {"hardened_clay"}));
    registerBlock(new BlockOpaque("STAINED_CLAY",
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
        "hardened_clay_stained_white",
        "hardened_clay_stained_orange",
        "hardened_clay_stained_magenta",
        "hardened_clay_stained_light_blue",
        "hardened_clay_stained_yellow",
        "hardened_clay_stained_lime",
        "hardened_clay_stained_pink",
        "hardened_clay_stained_gray",
        "hardened_clay_stained_silver",
        "hardened_clay_stained_cyan",
        "hardened_clay_stained_purple",
        "hardened_clay_stained_blue",
        "hardened_clay_stained_brown",
        "hardened_clay_stained_green",
        "hardened_clay_stained_red",
        "hardened_clay_stained_black"
      }
    ).setVar());
    registerBlock(new BlockLiquid("OIL", new String[] {"Oil"}, new String[] {"water_still"}));  //TODO
    registerBlock(new BlockOpaque("MUSIC_BOX", new String[] {"Music Box"}, new String[] {"planks_oak"}));
    registerBlock(new BlockOpaque("PUMPKIN", new String[] {"Pumpkin"}, new String[] {"pumpkin_top", "pumpkin_face_off", "pumpkin_side", "pumpkin_side"}));
    registerBlock(new BlockOpaque("NETHER_RACK", new String[] {"Nether Rack"}, new String[] {"netherrack"}));
    registerBlock(new BlockOpaque("SOUL_SAND", new String[] {"Soul Sand"}, new String[] {"soul_sand"}));
    registerBlock(new BlockOpaque("GLOWSTONE", new String[] {"Glowstone"}, new String[] {"glowstone"}));
    registerBlock(new BlockNetherPortal("NETHER_PORTAL", new String[] {"Nether Portal"}, new String[] {"portal"}));
    registerBlock(new BlockOpaque("PUMPKIN_LIT", new String[] {"Pumpkin Lit"}, new String[] {"pumpkin_top", "pumpkin_face_on", "pumpkin_side", "pumpkin_side"}));
    registerBlock(new BlockRedStoneRepeater("REDSTONE_REPEATER", new String[] {"-item-"}, new String[] {"repeater_off", "repeater_on", "stone", "redstone_torch_off", "redstone_torch_on"}));
    registerBlock(new BlockOpaque("GLASSBLOCK_COLOR", new String[] {"Glass Block"}, new String[] {"glass"}));
    registerBlock(new BlockTrapDoor("TRAP_DOOR", new String[] {"Trap Door"}, new String[] {"trapdoor"}));
    registerBlock(new BlockPane("BARS", new String[] {"Iron Bars"}, new String[] {"iron_bars", "iron_bars"}));
    registerBlock(new BlockPane("GLASS_PANE", new String[] {"Glass Pane"}, new String[] {"glass", "glass_pane_top"}));
    registerBlock(new BlockOpaque("MELON", new String[] {"Melon"}, new String[] {"melon_top", "melon_side"}));
    registerBlock(new BlockVine("VINES", new String[] {"Vines"}, new String[] {"vine"}).setGreenAllSides());
    registerBlock(new BlockGate("GATE"
      , new String[] {"Oak Gate", "Spruce Gate", "Birch Gate", "Jungle Gate", "Acacia Gate", "Dark Oak Gate"}
      , new String[] {
        "planks_oak",
        "planks_spruce",
        "planks_birch",
        "planks_jungle",
        "planks_acacia",
        "planks_big_oak",
      }
    ));
    registerBlock(new BlockStairs("STAIRS_BRICK", new String[] {"Stairs"}, new String[] {"brick"}));
    registerBlock(new BlockStairs("STAIRS_BLOCK", new String[] {"Stairs"}, new String[] {"planks_oak"}));
    registerBlock(new BlockOpaque("MYCELIUM", new String[] {"Mycelium"}, new String[] {"mycelium_top", "mycelium_side", "dirt"}));
    registerBlock(new BlockFace("LILLYPAD", new String[] {"Lilypad"}, new String[] {"waterlily"}).setGreenAllSides().setShowAsItem());
    registerBlock(new BlockOpaque("NETHER_BRICK", new String[] {"Nether Brick"}, new String[] {"nether_brick"}));
    registerBlock(new BlockOpaque("NETHER_FENCE", new String[] {"Nether Fence"}, new String[] {"nether_brick"}));  //fix me
    registerBlock(new BlockStairs("STAIRS_NETHER", new String[] {"Nether Stairs"}, new String[] {"nether_brick"}));  //fix me
    registerBlock(new BlockOpaque("ENCHANTING_TABLE", new String[] {"Enchanting Table"}, new String[] {"enchanting_table_top", "enchanting_table_side", "enchanting_table_bottom"}));
    registerBlock(new BlockEndPortal("END_PORTAL", new String[] {"End Portal"}, new String[] {}));
    registerBlock(new BlockEndFrame("END_PORTAL_FRAME", new String[] {"End Portal"}, new String[] {"endframe_top", "endframe_side", "end_stone", "endframe_eye"}));
    registerBlock(new BlockOpaque("END_STONE", new String[] {"End Stone"}, new String[] {"end_stone"}));
    registerBlock(new BlockOpaque("COCOA", new String[] {"Cocoa", "Cocoa", "Cocoa"}, new String[] {"cocoa_stage_0", "cocoa_stage_1", "cocoa_stage_2"}));  //fix me
    registerBlock(new BlockOpaque("EMERALD_ORE", new String[] {"Emerald Ore"}, new String[] {"emerald_ore"}));
    registerBlock(new BlockEnderChest("ENDER_CHEST"));
    registerBlock(new BlockFace("TRIP_HOOK", new String[] {"Trip Line"}, new String[] {"trip_wire"}));
    registerBlock(new BlockOpaque("EMERALD_BLOCK", new String[] {"Emerald Block"}, new String[] {"emerald_block"}));
    registerBlock(new BlockOpaque("COMMAND_BLOCK", new String[] {"Command Block"}, new String[] {"command_block"}));  //fix me
    registerBlock(new BlockOpaque("BEACON", new String[] {"Beacon"}, new String[] {"beacon"}));
    registerBlock(new BlockWall("WALL", new String[] {"Stone Wall"}, new String[] {"stone"}));
    registerBlock(new BlockOpaque("ANVIL", new String[] {"Anvil"}, new String[] {"anvil_base"}));  //TODO
//    registerBlock(new BlockOpaque("CHEST_TRAP, new String[] {"Chest Trapped"}, new String[] {""}));  //TODO
    registerBlock(new BlockCarpet("PLATE_GOLD", new String[] {"Gold Pressure Plate"}, new String[] {"gold_block"}));
    registerBlock(new BlockCarpet("PLATE_IRON", new String[] {"Iron Pressure Plate"}, new String[] {"iron_block"}));
    registerBlock(new BlockRedStoneComparator("REDSTONE_COMPARATOR", new String[] {"-item-"}, new String[] {"comparator_off", "comparator_on", "stone", "redstone_torch_off", "redstone_torch_on"}));
    registerBlock(new BlockDaylightSensor("SOLAR_PANEL", new String[] {"Daylight Sensor"}, new String[] {"daylight_detector_top", "daylight_detector_side"}));
    registerBlock(new BlockOpaque("REDSTONE_BLOCK", new String[] {"Redstone Block"}, new String[] {"redstone_block"}));

    registerBlock(new BlockOpaque("QUARTZ_ORE", new String[] {"Quartz"}, new String[] {"quartz_ore"}).setDrop("QUARTZ"));
    registerBlock(new BlockHopper("HOPPER", new String[] {"-item-"}, new String[] {"hopper_top", "hopper_inside", "hopper_outside"}));
    registerBlock(new BlockOpaque("QUARTZ_BLOCK", new String[] {"Quartz Block"}, new String[] {"quartz_block_top"}));
    registerBlock(new BlockStairs("STAIRS_QUARTZ", new String[] {"Quartz Stairs"}, new String[] {"quartz_block_top"}));
    registerBlock(new BlockRail("RAIL_ACTIVATOR", new String[] {"Rail Activator"}, new String[] {"rail_activator", "rail_activator_powered"}).setRedstone());
    registerBlock(new BlockDropper("DROPPER", new String[] {"Dropper"}, new String[] {"dropper_front_horizontal", "dropper_front_vertical", "piston_bottom"}));
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
        "glass_white", "glass_pane_top_white",
        "glass_orange", "glass_pane_top_orange",
        "glass_magenta", "glass_pane_top_magenta",
        "glass_light_blue", "glass_pane_top_light_blue",
        "glass_yellow", "glass_pane_top_yellow",
        "glass_lime", "glass_pane_top_lime",
        "glass_pink", "glass_pane_top_pink",
        "glass_gray", "glass_pane_top_gray",
        "glass_silver", "glass_pane_top_silver",
        "glass_cyan", "glass_pane_top_cyan",
        "glass_purple", "glass_pane_top_purple",
        "glass_blue", "glass_pane_top_blue",
        "glass_brown", "glass_pane_top_brown",
        "glass_green", "glass_pane_top_green",
        "glass_red", "glass_pane_top_red",
        "glass_black", "glass_pane_top_black"
      }
    ));
    registerBlock(new BlockOpaque("HAYBALE", new String[] {"Hay Bale"}, new String[] {"hay_block_top", "hay_block_side"}).setDir());
    registerBlock(new BlockCarpet("CARPET", new String[] {"Carpet"}, new String[] {"wool_colored_white"}));
    registerBlock(new BlockOpaque("COAL_BLOCK", new String[] {"Coal Block"}, new String[] {"coal_block"}).setFuel(800));
    registerBlock(new BlockOpaque("SNOW_PACKED", new String[] {"Packed Snow"}, new String[] {"snow"}));

    registerBlock(new BlockStep("STEP", new String[] {"Step"}, new String[] {/*"grass_top", "grass_side",*/ "dirt"}));
    registerBlock(new BlockBed("BED", new String[] {"Bed"}, new String[] {"planks_oak", "bed_feet_end", "bed_head_end", "bed_feet_side", "bed_head_side", "bed_feet_top", "bed_head_top"}));
    registerBlock(new BlockDoor("WOOD_DOOR", new String[] {"-item-"}, new String[] {"door_wood_upper", "door_wood_lower"})
      .setDrop("WOOD_DOOR_ITEM")
    );
    registerBlock(new BlockDoor("IRON_DOOR", new String[] {"Iron Door"}, new String[] {"door_wood_upper", "door_wood_lower"})
      .setDrop("IRON_DOOR_ITEM")
    );

    registerBlock(new BlockBarrier("BARRIER", new String[] {"Barrier"}, new String[] {}));

    //items as blocks

    registerBlock(new BlockWheat("WHEAT", new String[] {
      "Wheat", "Wheat", "Wheat", "Wheat", "Wheat", "Wheat", "Wheat", "Wheat",
    }, new String[] {
      "wheat_stage_0", "wheat_stage_1", "wheat_stage_2", "wheat_stage_3", "wheat_stage_4", "wheat_stage_5", "wheat_stage_6", "wheat_stage_7"
    }));
    registerBlock(new BlockRedStoneDust("RED_STONE", new String[] {"Red Stone Dust"}, new String[] {"redstone_dust_cross", "redstone_dust_line"}));

    registerBlock(new BlockSign("SIGN", new String[] {"Sign"}, new String[] {"planks_oak"}));

    registerBlock(new BlockOpaque("SOLID",
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
        "solid_silver",
        "solid_cyan",
        "solid_purple",
        "solid_blue",
        "solid_brown",
        "solid_green",
        "solid_red",
        "solid_black"
      }
    ).setVar());

    registerBlock(new BlockOpaque("TEST_ARROW", new String[] {"test"}, new String[] {"arrow"}).setDir());

  }

  public static char x = 0;

  private void addSubTexture(BlockBase block, String name) {
    name = "blocks/" + name;
    //check if asset already loaded
    for(int a=0;a<tiles.size();a++) {
      AssetImage ai = tiles.get(a);
      if (ai.name.equals(name)) {
        return;
      }
    }
    //asset not loaded yet, load it now
    AssetImage ai = Assets.getImage(name);
    if (ai.image.getWidth() != ai.image.getHeight()) {
      //animated image
      ai.isAnimated = true;
    }
    tiles.add(ai);
  }

  public void getIDs() {
  }

  public void stitchTiles() {
    stitched = new Texture();
    stitched.initImage(512, 512);
    stitched.initUsage();
    cracks = new Texture();
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
      AssetImage ai = Assets.getImage("blocks/destroy_stage_" + a);
      ai.isCrack = true;
      tiles.add(ai);
    }
    //sort list big to small (animated first)
    //NOTE:tiles.sort(Comparator) is only available in JDK8+
    Collections.sort(tiles, new Comparator() {
      public int compare(Object o1, Object o2) {
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
      Texture texture;
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
        int loc[] = texture.placeSubTexture(ai.images[0].getPixels(), w, w);
        if (loc == null) {
          JF.showError("Error", "Your texture pack size can not fit into your video cards max texture size\nPlease remove high resolution packs and restart.");
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
        int loc[] = texture.placeSubTexture(ai.image.getPixels(), w, h);
        if (loc == null) {
          JF.showError("Error", "Your texture pack size can not fit into your video cards max texture size\nPlease remove high resolution packs and restart.");
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
    name = "blocks/" + name;
    for(int a=0;a<tiles.size();a++) {
      AssetImage ai = tiles.get(a);
      if (ai.name.equals(name)) {
        boolean isFlow = ai.name.endsWith("_flow");
        SubTexture st = new SubTexture();
        Texture texture;
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

  public void initTexture(GL gl) {
    stitched.load(gl);
    cracks.load(gl);
  }

  public void initPerf() {
    for(int a=0;a<MAX_ID;a++) {
      if (regBlocks[a] == null) continue;
      BlockBase block = regBlocks[a];
      if (!block.isPerf) continue;
      block.isOpaque = !Settings.current.isFancy;
      block.isComplex = Settings.current.isFancy;
      block.isSolid = !Settings.current.isFancy;
    }
  }
}
