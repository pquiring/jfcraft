package jfcraft.data;

/** Registered items
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
import jfcraft.entity.*;
import jfcraft.opengl.*;
import static jfcraft.data.Types.*;
import static jfcraft.data.Direction.*;

public class Items {
  public static final int MAX_ID = 65536;
  public static final char FIRST_ID = 32768;
  public int itemCount = 0;
  public ItemBase[] items;  //items (ordered by id)
  public ItemBase[] regItems = new ItemBase[MAX_ID];  //registered items (not ordered by id)
  public boolean valid;

  public static char getItemID(String name) {
    return Static.server.world.getItemID(name);
  }

  private ArrayList<AssetImage> tiles = new ArrayList<AssetImage>();  //main stitched images
  private ArrayList<AssetImage> others = new ArrayList<AssetImage>();  //other images

  public void registerItem(ItemBase item) {
    regItems[itemCount++] = item;
  }

  public void orderItems() {
    items = new ItemBase[MAX_ID];
    for(int a=0;a<MAX_ID;a++) {
      ItemBase ib = regItems[a];
      if (ib == null) continue;
      items[ib.id] = ib;
    }
    for(int a=0;a<MAX_ID;a++) {
      if (items[a] == null) {
        items[a] = items[0];  //air
      }
    }
  }

  public static char IRON_SHOVEL;
  public static char IRON_PICKAXE;
  public static char IRON_AXE;
  public static char FLINT_STEEL;
  public static char APPLE;
  public static char BOW;
  public static char ARROW;
  public static char COAL;
  public static char DIAMOND;
  public static char IRON_INGOT;
  public static char GOLD_INGOT;
  public static char IRON_SWORD;
  public static char WOOD_SWORD;
  public static char WOOD_SHOVEL;
  public static char WOOD_PICKAXE;
  public static char WOOD_AXE;
  public static char STONE_SWORD;
  public static char STONE_SHOVEL;
  public static char STONE_PICKAXE;
  public static char STONE_AXE;
  public static char DIAMOND_SWORD;
  public static char DIAMOND_SHOVEL;
  public static char DIAMOND_PICKAXE;
  public static char DIAMOND_AXE;
  public static char STICK;
  public static char BOWL;
  public static char MUSHROOM_STEW;
  public static char GOLD_SWORD;
  public static char GOLD_SHOVEL;
  public static char GOLD_PICKAXE;
  public static char GOLD_AXE;
  public static char STRING;
  public static char FEATHER;
  public static char GUN_POWDER;
  public static char WOOD_HOE;
  public static char STONE_HOE;
  public static char IRON_HOE;
  public static char DIAMOND_HOE;
  public static char GOLD_HOE;
  public static char SEEDS;
  public static char WHEAT_ITEM;  //dup?  remove it?
  public static char BREAD;
  public static char LEATHER_CAP;
  public static char LEATHER_CHEST;
  public static char LEATHER_PANTS;
  public static char LEATHER_BOOTS;
  public static char CHAIN_HELMET;
  public static char CHAIN_CHEST;
  public static char CHAIN_PANTS;
  public static char CHAIN_BOOTS;
  public static char IRON_HELMET;
  public static char IRON_CHEST;
  public static char IRON_PANTS;
  public static char IRON_BOOTS;
  public static char DIAMOND_HELMET;
  public static char DIAMOND_CHEST;
  public static char DIAMOND_PANTS;
  public static char DIAMOND_BOOTS;
  public static char GOLD_HELMET;
  public static char GOLD_CHEST;
  public static char GOLD_PANTS;
  public static char GOLD_BOOTS;
  public static char FLINT;
  public static char PORK_RAW;
  public static char PORK_COOKED;
  public static char PAINTING;
  public static char APPLE_GOLDEN;
  public static char SIGN_ITEM;
  public static char WOOD_DOOR_ITEM;
  public static char BUCKET;
  public static char BUCKET_WATER;
  public static char BUCKET_LAVA;
  public static char MINECART;
  public static char SADDLE;
  public static char IRON_DOOR_ITEM;
  public static char RED_STONE_ITEM;  //dup? remove?
  public static char SNOWBALL;
  public static char BOAT;
  public static char LEATHER;
  public static char BUCKET_MILK;
  public static char BRICK_ITEM;  //dup? remove?
  public static char CLAY_BALL;
  public static char SUGAR_CANE;
  public static char PAPER;
  public static char BOOK;
  public static char SLIME_BALL;
  public static char MINECART_CHEST;
  public static char MINECART_FURNACE;
  public static char EGG;
  public static char COMPASS;
  public static char FISHING_ROD;
  public static char CLOCK;
  public static char GLOWSTONE_DUST;
  public static char FISH_RAW;
  public static char FISH_COOKED;
  public static char COLOR;
  public static char BONE;
  public static char SUGAR;
  public static char CAKE;
  public static char BED_ITEM;
  public static char REDSTONE_REPEATER_ITEM;
  public static char COOKIE;
  public static char MAP_USED;
  public static char SHEARS;
  public static char WATER_MELON;
  public static char WATER_MELON_SEEDS;
  public static char PUMPKIN_SEEDS;
  public static char STEAK_RAW;
  public static char STEAK_COOKED;
  public static char CHICKEN_RAW;
  public static char CHICKEN_COOKED;
  public static char ROTTEN_FLESH;
  public static char ENDER_PEARL;
  public static char BLAZE_ROD;
  public static char GHAST_TEAR;
  public static char GOLD_NUGGET;
  public static char NETHER_WART;
  public static char BOTTLE_WATER;
  public static char BOTTLE;
  public static char SPIDER_EYE;
  public static char SPIDER_EYE_FERMENTED;
  public static char BLAZE_POWDER;
  public static char MAGMA;
  public static char BREWING_STAND;
  public static char CAULDRON;
  public static char ENDER_EYE;
  public static char WATER_MELON_GOLDEN;
  public static char EGG_SPAWNER;
  public static char POTION;
  public static char FIRE_SHOT;
  public static char BOOK_PEN;
  public static char BOOK_WRITTEN;
  public static char EMERALD;
  public static char ITEM_FRAME;
  public static char POT;
  public static char CARROT;
  public static char POTATO;
  public static char POTATO_BAKED;
  public static char POTATO_POISON;
  public static char MAP_EMPTY;
  public static char CARROT_GOLDEN;
  public static char HEAD;
  public static char FISHING_ROD_CARROT;
  public static char NETHER_STAR;
  public static char PUMPKIN_PIE;
  public static char FIREWORKS;
  public static char FIRE_CHARGE;
  public static char BOOK_SPELL;
  public static char REDSTONE_COMPARATOR_ITEM;
  public static char NETHER_BRICK_ITEM;  //dup? remove?
  public static char QUARTZ;
  public static char MINECART_TNT;
  public static char MINECART_HOPPER;
  public static char HORSE_ARMOR_IRON;
  public static char HORSE_ARMOR_GOLD;
  public static char HORSE_ARMOR_DIAMOND;
  public static char LEAD;
  public static char NAME_TAG;
  public static char MINECART_COMMAND_BLOCK;
  public static char CHAIN;
  public static char HOPPER_ITEM;
  public static char SHIELD;
  public static char KELP;

  public static void getIDs(World world) {
    IRON_SHOVEL = world.getItemID("iron_shovel");
    IRON_PICKAXE = world.getItemID("IRON_PICKAXE");
    IRON_AXE = world.getItemID("IRON_AXE");
    FLINT_STEEL = world.getItemID("FLINT_STEEL");
    APPLE = world.getItemID("APPLE");
    BOW = world.getItemID("BOW");
    ARROW = world.getItemID("ARROW");
    COAL = world.getItemID("COAL");
    DIAMOND = world.getItemID("DIAMOND");
    IRON_INGOT = world.getItemID("IRON_INGOT");
    GOLD_INGOT = world.getItemID("GOLD_INGOT");
    IRON_SWORD = world.getItemID("IRON_SWORD");
    WOOD_SWORD = world.getItemID("WOOD_SWORD");
    WOOD_SHOVEL = world.getItemID("WOOD_SHOVEL");
    WOOD_PICKAXE = world.getItemID("WOOD_PICKAXE");
    WOOD_AXE = world.getItemID("WOOD_AXE");
    STONE_SWORD = world.getItemID("STONE_SWORD");
    STONE_SHOVEL = world.getItemID("STONE_SHOVEL");
    STONE_PICKAXE = world.getItemID("STONE_PICKAXE");
    STONE_AXE = world.getItemID("STONE_AXE");
    DIAMOND_SWORD = world.getItemID("DIAMOND_SWORD");
    DIAMOND_SHOVEL = world.getItemID("DIAMOND_SHOVEL");
    DIAMOND_PICKAXE = world.getItemID("DIAMOND_PICKAXE");
    DIAMOND_AXE = world.getItemID("DIAMOND_AXE");
    STICK = world.getItemID("STICK");
    BOWL = world.getItemID("BOWL");
    MUSHROOM_STEW = world.getItemID("MUSHROOM_STEW");
    GOLD_SWORD = world.getItemID("GOLD_SWORD");
    GOLD_SHOVEL = world.getItemID("GOLD_SHOVEL");
    GOLD_PICKAXE = world.getItemID("GOLD_PICKAXE");
    GOLD_AXE = world.getItemID("GOLD_AXE");
    STRING = world.getItemID("STRING");
    FEATHER = world.getItemID("FEATHER");
    GUN_POWDER = world.getItemID("GUN_POWDER");
    WOOD_HOE = world.getItemID("WOOD_HOE");
    STONE_HOE = world.getItemID("STONE_HOE");
    IRON_HOE = world.getItemID("IRON_HOE");
    DIAMOND_HOE = world.getItemID("DIAMOND_HOE");
    GOLD_HOE = world.getItemID("GOLD_HOE");
    SEEDS = world.getItemID("SEEDS");
    WHEAT_ITEM = world.getItemID("WHEAT_ITEM");
    BREAD = world.getItemID("BREAD");
    LEATHER_CAP = world.getItemID("LEATHER_CAP");
    LEATHER_CHEST = world.getItemID("LEATHER_CHEST");
    LEATHER_PANTS = world.getItemID("LEATHER_PANTS");
    LEATHER_BOOTS = world.getItemID("LEATHER_BOOTS");
    CHAIN_HELMET = world.getItemID("CHAIN_HELMET");
    CHAIN_CHEST = world.getItemID("CHAIN_CHEST");
    CHAIN_PANTS = world.getItemID("CHAIN_PANTS");
    CHAIN_BOOTS = world.getItemID("CHAIN_BOOTS");
    IRON_HELMET = world.getItemID("IRON_HELMET");
    IRON_CHEST = world.getItemID("IRON_CHEST");
    IRON_PANTS = world.getItemID("IRON_PANTS");
    IRON_BOOTS = world.getItemID("IRON_BOOTS");
    DIAMOND_HELMET = world.getItemID("DIAMOND_HELMET");
    DIAMOND_CHEST = world.getItemID("DIAMOND_CHEST");
    DIAMOND_PANTS = world.getItemID("DIAMOND_PANTS");
    DIAMOND_BOOTS = world.getItemID("DIAMOND_BOOTS");
    GOLD_HELMET = world.getItemID("GOLD_HELMET");
    GOLD_CHEST = world.getItemID("GOLD_CHEST");
    GOLD_PANTS = world.getItemID("GOLD_PANTS");
    GOLD_BOOTS = world.getItemID("GOLD_BOOTS");
    FLINT = world.getItemID("FLINT");
    PORK_RAW = world.getItemID("PORK_RAW");
    PORK_COOKED = world.getItemID("PORK_COOKED");
    PAINTING = world.getItemID("PAINTING");
    APPLE_GOLDEN = world.getItemID("APPLE_GOLDEN");
    SIGN_ITEM = world.getItemID("SIGN_ITEM");
    WOOD_DOOR_ITEM = world.getItemID("WOOD_DOOR_ITEM");
    BUCKET = world.getItemID("BUCKET");
    BUCKET_WATER = world.getItemID("BUCKET_WATER");
    BUCKET_LAVA = world.getItemID("BUCKET_LAVA");
    MINECART = world.getItemID("MINECART");
    SADDLE = world.getItemID("SADDLE");
    IRON_DOOR_ITEM = world.getItemID("IRON_DOOR_ITEM");
    RED_STONE_ITEM = world.getItemID("RED_STONE_ITEM");
    SNOWBALL = world.getItemID("SNOWBALL");
    BOAT = world.getItemID("BOAT");
    LEATHER = world.getItemID("LEATHER");
    BUCKET_MILK = world.getItemID("BUCKET_MILK");
    BRICK_ITEM = world.getItemID("BRICK_ITEM");
    CLAY_BALL = world.getItemID("CLAY_BALL");
    SUGAR_CANE = world.getItemID("SUGAR_CANE");
    PAPER = world.getItemID("PAPER");
    BOOK = world.getItemID("BOOK");
    SLIME_BALL = world.getItemID("SLIME_BALL");
    MINECART_CHEST = world.getItemID("MINECART_CHEST");
    MINECART_FURNACE = world.getItemID("MINECART_FURNACE");
    EGG = world.getItemID("EGG");
    COMPASS = world.getItemID("COMPASS");
    FISHING_ROD = world.getItemID("FISHING_ROD");
    CLOCK = world.getItemID("CLOCK");
    GLOWSTONE_DUST = world.getItemID("GLOWSTONE_DUST");
    FISH_RAW = world.getItemID("FISH_RAW");
    FISH_COOKED = world.getItemID("FISH_COOKED");
    COLOR = world.getItemID("COLOR");
    BONE = world.getItemID("BONE");
    SUGAR = world.getItemID("SUGAR");
    CAKE = world.getItemID("CAKE");
    BED_ITEM = world.getItemID("BED_ITEM");
    REDSTONE_REPEATER_ITEM = world.getItemID("REDSTONE_REPEATER_ITEM");
    COOKIE = world.getItemID("COOKIE");
    MAP_USED = world.getItemID("MAP_USED");
    SHEARS = world.getItemID("SHEARS");
    WATER_MELON = world.getItemID("WATER_MELON");
    WATER_MELON_SEEDS = world.getItemID("WATER_MELON_SEEDS");
    PUMPKIN_SEEDS = world.getItemID("PUMPKIN_SEEDS");
    STEAK_RAW = world.getItemID("STEAK_RAW");
    STEAK_COOKED = world.getItemID("STEAK_COOKED");
    CHICKEN_RAW = world.getItemID("CHICKEN_RAW");
    CHICKEN_COOKED = world.getItemID("CHICKEN_COOKED");
    ROTTEN_FLESH = world.getItemID("ROTTEN_FLESH");
    ENDER_PEARL = world.getItemID("ENDER_PEARL");
    BLAZE_ROD = world.getItemID("BLAZE_ROD");
    GHAST_TEAR = world.getItemID("GHAST_TEAR");
    GOLD_NUGGET = world.getItemID("GOLD_NUGGET");
    NETHER_WART = world.getItemID("NETHER_WART");
    BOTTLE_WATER = world.getItemID("BOTTLE_WATER");
    BOTTLE = world.getItemID("BOTTLE");
    SPIDER_EYE = world.getItemID("SPIDER_EYE");
    SPIDER_EYE_FERMENTED = world.getItemID("SPIDER_EYE_FERMENTED");
    BLAZE_POWDER = world.getItemID("BLAZE_POWDER");
    MAGMA = world.getItemID("MAGMA");
    BREWING_STAND = world.getItemID("BREWING_STAND");
    CAULDRON = world.getItemID("CAULDRON");
    ENDER_EYE = world.getItemID("ENDER_EYE");
    WATER_MELON_GOLDEN = world.getItemID("WATER_MELON_GOLDEN");
    EGG_SPAWNER = world.getItemID("EGG_SPAWNER");
    POTION = world.getItemID("POTION");
    FIRE_SHOT = world.getItemID("FIRE_SHOT");
    BOOK_PEN = world.getItemID("BOOK_PEN");
    BOOK_WRITTEN = world.getItemID("BOOK_WRITTEN");
    EMERALD = world.getItemID("EMERALD");
    ITEM_FRAME = world.getItemID("ITEM_FRAME");
    POT = world.getItemID("POT");
    CARROT = world.getItemID("CARROT");
    POTATO = world.getItemID("POTATO");
    POTATO_BAKED = world.getItemID("POTATO_BAKED");
    POTATO_POISON = world.getItemID("POTATO_POISON");
    MAP_EMPTY = world.getItemID("MAP_EMPTY");
    CARROT_GOLDEN = world.getItemID("CARROT_GOLDEN");
    HEAD = world.getItemID("HEAD");
    FISHING_ROD_CARROT = world.getItemID("FISHING_ROD_CARROT");
    NETHER_STAR = world.getItemID("NETHER_STAR");
    PUMPKIN_PIE = world.getItemID("PUMPKIN_PIE");
    FIREWORKS = world.getItemID("FIREWORKS");
    FIRE_CHARGE = world.getItemID("FIRE_CHARGE");
    BOOK_SPELL = world.getItemID("BOOK_SPELL");
    REDSTONE_COMPARATOR_ITEM = world.getItemID("REDSTONE_COMPARATOR_ITEM");
    NETHER_BRICK_ITEM = world.getItemID("NETHER_BRICK_ITEM");
    QUARTZ = world.getItemID("QUARTZ");
    MINECART_TNT = world.getItemID("MINECART_TNT");
    MINECART_HOPPER = world.getItemID("MINECART_HOPPER");
    HORSE_ARMOR_IRON = world.getItemID("HORSE_ARMOR_IRON");
    HORSE_ARMOR_GOLD = world.getItemID("HORSE_ARMOR_GOLD");
    HORSE_ARMOR_DIAMOND = world.getItemID("HORSE_ARMOR_DIAMOND");
    LEAD = world.getItemID("LEAD");
    NAME_TAG = world.getItemID("NAME_TAG");
    MINECART_COMMAND_BLOCK = world.getItemID("MINECART_COMMAND_BLOCK");
    CHAIN = world.getItemID("CHAIN");
    HOPPER_ITEM = world.getItemID("HOPPER_ITEM");
    SHIELD = world.getItemID("SHIELD");
    KELP = world.getItemID("KELP");
  }

  //color (dye) VARs of "COLOR" item (see http://minecraft.gamepedia.com/Dye)
  public final static byte VAR_INK = 0;  //black (ink sack)
  public final static byte VAR_RED = 1;  //red (flower)
  public final static byte VAR_GREEN = 2;  //green (cactus)
  public final static byte VAR_COCOA = 3;  //brown (cocoa beans "as is")
  public final static byte VAR_LAPIS = 4;  //blue (lapis "as is")
  public final static byte VAR_PURPLE = 5;  //purple (blue + red)
  public final static byte VAR_CYAN = 6;  //cyan (blue + green)
  public final static byte VAR_LIGHT_GRAY = 7;  //light gray (flowers or mix black+2white or gray+1white)
  public final static byte VAR_GRAY = 8;  //gray (black + white)
  public final static byte VAR_PINK = 9;  //pink (flowers or red + white)
  public final static byte VAR_LIME = 10;  //lime (green + white)
  public final static byte VAR_YELLOW = 11;  //yellow (flower)
  public final static byte VAR_LIGHT_BLUE = 12;  //light blue (flower or blue + white)
  public final static byte VAR_MAGENTA = 13;  //magenta (flower or purple + pink or blue + white + 2red or pink + red + blue)
  public final static byte VAR_ORANGE = 14;  //orange (flower or red + yellow)
  public final static byte VAR_BONEMEAL = 15;  //white

  public TextureMap stitched;
  public int texturesCount;
  public ArrayList<TextureMap> textures = new ArrayList<TextureMap>();  //other textures

  public void registerDefault() {
    //register items
    registerItem(new ItemBase("IRON_SHOVEL", new String[]{"Iron Shovel"}, new String[]{"iron_shovel"})
      .setTool(TOOL_SHOVEL).setMaterial(MAT_IRON).setDmg(4).setDurability(251)
    );
    registerItem(new ItemBase("IRON_PICKAXE", new String[]{"Iron Pickaxe"}, new String[]{"iron_pickaxe"})
      .setTool(TOOL_PICKAXE).setMaterial(MAT_IRON).setDmg(5).setDurability(250)
    );
    registerItem(new ItemBase("IRON_AXE", new String[]{"Iron Axe"}, new String[]{"iron_axe"})
      .setTool(TOOL_AXE).setMaterial(MAT_IRON).setDmg(6).setDurability(250)
    );
    registerItem(new ItemFlintSteel("FLINT_STEEL", new String[]{"Flint and Steel"}, new String[]{"flint_and_steel"}).setTool(TOOL_FLINT_STEEL));
    registerItem(new ItemBase("APPLE", new String[]{"Apple"}, new String[]{"apple"}).setFood(4f, 24.f));
    registerItem(new ItemBow("BOW", new String[]{"Bow", "Bow", "Bow", "Bow"}, new String[]{"bow", "bow_pulling_0", "bow_pulling_1", "bow_pulling_2"})
      .setWeapon(WEAPON_BOW).setMaterial(MAT_WOOD).setDurability(384)
    );
    registerItem(new ItemBase("ARROW", new String[]{"Arrow"}, new String[]{"arrow"}).setMaterial(MAT_WOOD));
    registerItem(new ItemBase("COAL", new String[]{"Coal", "Charcoal"}, new String[] {"coal", "charcoal"}).setVar().setFuel(80));
    registerItem(new ItemBase("DIAMOND", new String[]{"Diamond"}, new String[]{"diamond"}));
    registerItem(new ItemBase("IRON_INGOT", new String[]{"Iron Ingot"}, new String[]{"iron_ingot"}));
    registerItem(new ItemBase("GOLD_INGOT", new String[]{"Gold Ingot"}, new String[]{"gold_ingot"}));
    registerItem(new ItemBase("IRON_SWORD", new String[]{"Iron Sword"}, new String[]{"iron_sword"}).setWeapon(WEAPON_SWORD).setDmg(7).setTool(TOOL_SWORD).setMaterial(MAT_IRON));
    registerItem(new ItemBase("WOOD_SWORD", new String[]{"Wood Sword"}, new String[]{"wooden_sword"}).setWeapon(WEAPON_SWORD).setFuel(10).setMaterial(MAT_WOOD).setDmg(5).setTool(TOOL_SWORD));
    registerItem(new ItemBase("WOOD_SHOVEL", new String[]{"Wood Shovel"}, new String[]{"wooden_shovel"})
      .setTool(TOOL_SHOVEL).setFuel(10).setMaterial(MAT_WOOD).setDmg(2).setDurability(60)
    );
    registerItem(new ItemBase("WOOD_PICKAXE", new String[]{"Wood Pickaxe"}, new String[]{"wooden_pickaxe"})
      .setTool(TOOL_PICKAXE).setFuel(10).setMaterial(MAT_WOOD).setDmg(3).setDurability(59)
    );
    registerItem(new ItemBase("WOOD_AXE", new String[]{"Wood Axe"}, new String[]{"wooden_axe"})
      .setTool(TOOL_AXE).setFuel(10).setMaterial(MAT_WOOD).setDmg(4).setDurability(59)
    );
    registerItem(new ItemBase("STONE_SWORD", new String[]{"Stone Sword"}, new String[]{"stone_sword"}).setWeapon(WEAPON_SWORD).setDmg(6).setTool(TOOL_SWORD).setMaterial(MAT_STONE));
    registerItem(new ItemBase("STONE_SHOVEL", new String[]{"Stone Shovel"}, new String[]{"stone_shovel"})
      .setTool(TOOL_SHOVEL).setMaterial(MAT_STONE).setDmg(3).setDurability(131)
    );
    registerItem(new ItemBase("STONE_PICKAXE", new String[]{"Stone Pickaxe"}, new String[]{"stone_pickaxe"})
      .setTool(TOOL_PICKAXE).setMaterial(MAT_STONE).setDmg(4).setDurability(131)
    );
    registerItem(new ItemBase("STONE_AXE", new String[]{"Stone Axe"}, new String[]{"stone_axe"})
      .setTool(TOOL_AXE).setMaterial(MAT_STONE).setDmg(5).setDurability(131)
    );
    registerItem(new ItemBase("DIAMOND_SWORD", new String[]{"Diamond Sword"}, new String[]{"diamond_sword"}).setWeapon(WEAPON_SWORD).setDmg(8).setTool(TOOL_SWORD).setMaterial(MAT_DIAMOND));
    registerItem(new ItemBase("DIAMOND_SHOVEL", new String[]{"Diamond Shovel"}, new String[]{"diamond_shovel"})
      .setTool(TOOL_SHOVEL).setMaterial(MAT_DIAMOND).setDmg(5).setDurability(1561)
    );
    registerItem(new ItemBase("DIAMOND_PICKAXE", new String[]{"Diamond Pickaxe"}, new String[]{"diamond_pickaxe"})
      .setTool(TOOL_PICKAXE).setMaterial(MAT_DIAMOND).setDmg(6).setDurability(1561)
    );
    registerItem(new ItemBase("DIAMOND_AXE", new String[]{"Diamond Axe"}, new String[]{"diamond_axe"})
      .setTool(TOOL_AXE).setMaterial(MAT_DIAMOND).setDmg(7).setDurability(1561)
    );
    registerItem(new ItemBase("STICK", new String[]{"Stick"}, new String[]{"stick"}).setFuel(5).setMaterial(MAT_WOOD));
    registerItem(new ItemBase("BOWL", new String[]{"Bowl"}, new String[]{"bowl"}).setFuel(5).setMaterial(MAT_WOOD));
    registerItem(new ItemBase("MUSHROOM_STEW", new String[]{"Mushroom Stew"}, new String[]{"mushroom_stew"}).setFood(6,7.2f));
    registerItem(new ItemBase("GOLD_SWORD", new String[]{"Gold Sword"}, new String[]{"golden_sword"}).setWeapon(WEAPON_SWORD).setDmg(5).setTool(TOOL_SWORD).setMaterial(MAT_GOLD));
    registerItem(new ItemBase("GOLD_SHOVEL", new String[]{"Gold Shovel"}, new String[]{"golden_shovel"})
      .setTool(TOOL_SHOVEL).setDmg(2).setMaterial(MAT_GOLD).setDurability(32)
    );
    registerItem(new ItemBase("GOLD_PICKAXE", new String[]{"Gold Pickaxe"}, new String[]{"golden_pickaxe"})
      .setTool(TOOL_PICKAXE).setDmg(3).setMaterial(MAT_GOLD).setDurability(32)
    );
    registerItem(new ItemBase("GOLD_AXE", new String[]{"Gold Axe"}, new String[]{"golden_axe"})
      .setTool(TOOL_AXE).setDmg(4).setMaterial(MAT_GOLD).setDurability(32)
    );
    registerItem(new ItemBase("STRING", new String[]{"String"}, new String[]{"string"}));
    registerItem(new ItemBase("FEATHER", new String[]{"Feather"}, new String[]{"feather"}));
    registerItem(new ItemBase("GUN_POWDER", new String[]{"Gun Powder"}, new String[]{"gunpowder"}));
    registerItem(new ItemBase("WOOD_HOE", new String[]{"Wood Hoe"}, new String[]{"wooden_hoe"})
      .setTool(TOOL_HOE).setFuel(10).setMaterial(MAT_WOOD).setDurability(59)
    );
    registerItem(new ItemBase("STONE_HOE", new String[]{"Stone Hoe"}, new String[]{"stone_hoe"})
      .setTool(TOOL_HOE).setMaterial(MAT_STONE).setDurability(131)
    );
    registerItem(new ItemBase("IRON_HOE", new String[]{"Iron Hoe"}, new String[]{"iron_hoe"})
      .setTool(TOOL_HOE).setMaterial(MAT_IRON).setDurability(250)
    );
    registerItem(new ItemBase("DIAMOND_HOE", new String[]{"Diamond Hoe"}, new String[]{"diamond_hoe"})
      .setTool(TOOL_HOE).setMaterial(MAT_DIAMOND).setDurability(1561)
    );
    registerItem(new ItemBase("GOLD_HOE", new String[]{"Gold Hoe"}, new String[]{"golden_hoe"})
      .setTool(TOOL_HOE).setMaterial(MAT_GOLD).setDurability(32)
    );
    registerItem(new ItemSeeds("SEEDS", new String[]{"Seeds"}, new String[]{"wheat_seeds"}).setSeeds("WHEAT"));
    registerItem(new ItemBase("WHEAT_ITEM", new String[]{"Wheat"}, new String[]{"wheat"}));
    registerItem(new ItemBase("BREAD", new String[]{"Bread"}, new String[]{"bread"}).setFood(5, 6));
    registerItem(new ItemBase("CHAIN", new String[]{"Chainmail"}, new String[]{"chainmail"}));

    registerItem(new ItemShield("SHIELD", new String[]{"Shield"}, new String[]{"shield"})
      .setDurability(337)
    );

    registerItem(new ItemBase("LEATHER_CAP", new String[]{"Leather Cap"}, new String[]{"leather_helmet"}).setArmor(ARMOR_HEAD)
      .setArmorTextures(new String[] {"models/armor/leather_layer_1", "models/armor/leather_layer_2"}
        , new float[] {1.2f}
        , new int[][] {{EntityBase.HEAD}}));
    registerItem(new ItemBase("LEATHER_CHEST", new String[]{"Leather Chest"}, new String[]{"leather_chestplate"}).setArmor(ARMOR_CHEST)
      .setArmorTextures(new String[] {"models/armor/leather_layer_1", "models/armor/leather_layer_2"}
        , new float[] {1.2f}
        , new int[][] {{EntityBase.BODY, EntityBase.L_ARM, EntityBase.R_ARM}}));
    registerItem(new ItemBase("LEATHER_PANTS", new String[]{"Leather Pants"}, new String[]{"leather_leggings"}).setArmor(ARMOR_LEGS)
      .setArmorTextures(new String[] {"models/armor/leather_layer_1", "models/armor/leather_layer_2"}
        , new float[] {1.1f}
        , new int[][] {{EntityBase.BODY, EntityBase.L_LEG, EntityBase.R_LEG}}));
    registerItem(new ItemBase("LEATHER_BOOTS", new String[]{"Leather Boots"}, new String[]{"leather_boots"}).setArmor(ARMOR_FEET)
      .setArmorTextures(new String[] {"models/armor/leather_layer_1", "models/armor/leather_layer_2"}
        , new float[] {1.2f}
        , new int[][] {{EntityBase.L_LEG, EntityBase.R_LEG}}));

    registerItem(new ItemBase("CHAIN_HELMET", new String[]{"Chainmail Cap"}, new String[]{"chainmail_helmet"}).setArmor(ARMOR_HEAD)
      .setArmorTextures(new String[] {"models/armor/chainmail_layer_1"}
        , new float[] {1.2f}
        , new int[][] {{EntityBase.HEAD}}));
    registerItem(new ItemBase("CHAIN_CHEST", new String[]{"Chainmail Chest"}, new String[]{"chainmail_chestplate"}).setArmor(ARMOR_CHEST)
      .setArmorTextures(new String[] {"models/armor/chainmail_layer_1"}
        , new float[] {1.2f}
        , new int[][] {{EntityBase.BODY, EntityBase.L_ARM, EntityBase.R_ARM}}));
    registerItem(new ItemBase("CHAIN_PANTS", new String[]{"Chainmail Pants"}, new String[]{"chainmail_leggings"}).setArmor(ARMOR_LEGS)
      .setArmorTextures(new String[] {"models/armor/chainmail_layer_2"}
        , new float[] {1.1f}
        , new int[][] {{EntityBase.BODY, EntityBase.L_LEG, EntityBase.R_LEG}}));
    registerItem(new ItemBase("CHAIN_BOOTS", new String[]{"Chainmail Boots"}, new String[]{"chainmail_boots"}).setArmor(ARMOR_FEET)
      .setArmorTextures(new String[] {"models/armor/chainmail_layer_1"}
        , new float[] {1.2f}
        , new int[][] {{EntityBase.L_LEG, EntityBase.R_LEG}}));

    registerItem(new ItemBase("IRON_HELMET", new String[]{"Iron Helmet"}, new String[]{"iron_helmet"}).setArmor(ARMOR_HEAD)
      .setArmorTextures(new String[] {"models/armor/iron_layer_1"}
        , new float[] {1.2f}
        , new int[][] {{EntityBase.HEAD}}));
    registerItem(new ItemBase("IRON_CHEST", new String[]{"Iron Chest"}, new String[]{"iron_chestplate"}).setArmor(ARMOR_CHEST)
      .setArmorTextures(new String[] {"models/armor/iron_layer_1"}
        , new float[] {1.2f}
        , new int[][] {{EntityBase.BODY, EntityBase.L_ARM, EntityBase.R_ARM}}));
    registerItem(new ItemBase("IRON_PANTS", new String[]{"Iron Pants"}, new String[]{"iron_leggings"}).setArmor(ARMOR_LEGS)
      .setArmorTextures(new String[] {"models/armor/iron_layer_2"}
        , new float[] {1.1f}
        , new int[][] {{EntityBase.BODY, EntityBase.L_LEG, EntityBase.R_LEG}}));
    registerItem(new ItemBase("IRON_BOOTS", new String[]{"Iron Boots"}, new String[]{"iron_boots"}).setArmor(ARMOR_FEET)
      .setArmorTextures(new String[] {"models/armor/iron_layer_1"}
        , new float[] {1.2f}
        , new int[][] {{EntityBase.L_LEG, EntityBase.R_LEG}}));

    registerItem(new ItemBase("DIAMOND_HELMET", new String[]{"Diamond Helmet"}, new String[]{"diamond_helmet"}).setArmor(ARMOR_HEAD)
      .setArmorTextures(new String[] {"models/armor/diamond_layer_1"}
        , new float[] {1.2f}
        , new int[][] {{EntityBase.HEAD}}));
    registerItem(new ItemBase("DIAMOND_CHEST", new String[]{"Diamond Chest"}, new String[]{"diamond_chestplate"}).setArmor(ARMOR_CHEST)
      .setArmorTextures(new String[] {"models/armor/diamond_layer_1"}
        , new float[] {1.2f}
        , new int[][] {{EntityBase.BODY, EntityBase.L_ARM, EntityBase.R_ARM}}));
    registerItem(new ItemBase("DIAMOND_PANTS", new String[]{"Diamond Pants"}, new String[]{"diamond_leggings"}).setArmor(ARMOR_LEGS)
      .setArmorTextures(new String[] {"models/armor/diamond_layer_2"}
        , new float[] {1.1f}
        , new int[][] {{EntityBase.BODY, EntityBase.L_LEG, EntityBase.R_LEG}}));
    registerItem(new ItemBase("DIAMOND_BOOTS", new String[]{"Diamond Boots"}, new String[]{"diamond_boots"}).setArmor(ARMOR_FEET)
      .setArmorTextures(new String[] {"models/armor/diamond_layer_1"}
        , new float[] {1.2f}
        , new int[][] {{EntityBase.L_LEG, EntityBase.R_LEG}}));

    registerItem(new ItemBase("GOLD_HELMET", new String[]{"Gold Helmet"}, new String[]{"golden_helmet"}).setArmor(ARMOR_HEAD)
      .setArmorTextures(new String[] {"models/armor/gold_layer_1"}
        , new float[] {1.2f}
        , new int[][] {{EntityBase.HEAD}}));
    registerItem(new ItemBase("GOLD_CHEST", new String[]{"Gold Chest"}, new String[]{"golden_chestplate"}).setArmor(ARMOR_CHEST)
      .setArmorTextures(new String[] {"models/armor/gold_layer_1"}
        , new float[] {1.2f}
        , new int[][] {{EntityBase.BODY, EntityBase.L_ARM, EntityBase.R_ARM}}));
    registerItem(new ItemBase("GOLD_PANTS", new String[]{"Gold Pants"}, new String[]{"golden_leggings"}).setArmor(ARMOR_LEGS)
      .setArmorTextures(new String[] {"models/armor/gold_layer_2"}
        , new float[] {1.1f}
        , new int[][] {{EntityBase.BODY, EntityBase.L_LEG, EntityBase.R_LEG}}));
    registerItem(new ItemBase("GOLD_BOOTS", new String[]{"Gold Boots"}, new String[]{"golden_boots"}).setArmor(ARMOR_FEET)
      .setArmorTextures(new String[] {"models/armor/gold_layer_1"}
        , new float[] {1.2f}
        , new int[][] {{EntityBase.L_LEG, EntityBase.R_LEG}}));

    registerItem(new ItemBase("FLINT", new String[]{"Flint"}, new String[]{"flint"}));
    registerItem(new ItemBase("PORK_RAW", new String[]{"Raw Pork"}, new String[]{"porkchop"}).setFood(0,0));
    registerItem(new ItemBase("PORK_COOKED", new String[]{"Cooked Pork"}, new String[]{"cooked_porkchop"}).setFood(8,12.8f));
    registerItem(new ItemBase("PAINTING", new String[]{"Item Frame"}, new String[]{"painting"}));
    registerItem(new ItemBase("APPLE_GOLDEN", new String[]{"Golden Apple"}, new String[]{"golden_apple"}).setFood(4,9.6f));
    registerItem(new ItemBase("SIGN_ITEM", new String[]{"Sign"}, new String[]{"sign"})
      .setCanPlace().setBlockID("SIGN").setDirFace()
    );
    registerItem(new ItemBase("WOOD_DOOR_ITEM"
      , new String[]{"Wooden Door"}
      , new String[]{
        "oak_door",
        "spruce_door",
        "birch_door",
        "jungle_door",
        "acacia_door",
        "dark_oak_door",
      }
    ).setFuel(10).setMaterial(MAT_WOOD).setBlockID("WOOD_DOOR").setCanPlace().setDir());
    registerItem(new ItemBucket("BUCKET", new String[]{"Bucket"}, new String[]{"bucket"}).setCanUseWater());
    registerItem(new ItemBucket("BUCKET_WATER", new String[]{"Bucket Water"}, new String[]{"water_bucket"}).setFilled("WATER"));
    registerItem(new ItemBucket("BUCKET_LAVA", new String[]{"Bucket Lava"}, new String[]{"lava_bucket"}).setFilled("LAVA").setFuel(1000));
    registerItem(new ItemMinecart("MINECART", new String[]{"Minecart"}, new String[]{"minecart"}));
    registerItem(new ItemBase("SADDLE", new String[]{"Saddle"}, new String[]{"saddle"}));
    registerItem(new ItemBase("IRON_DOOR_ITEM", new String[]{"Iron Door"}, new String[]{"iron_door"}).setBlockID("IRON_DOOR").setCanPlace());
    registerItem(new ItemBase("RED_STONE_ITEM", new String[]{"Redstone Dust"}, new String[]{"redstone"}).setBlockID("RED_STONE").setCanPlace());
    registerItem(new ItemBase("SNOWBALL", new String[]{"Snowball"}, new String[]{"snowball"}));
    registerItem(new ItemBoat("BOAT", new String[]{"Boat"}, new String[]{
      "oak_boat",
      "spruce_boat",
      "birch_boat",
      "jungle_boat",
      "acacia_boat",
      "dark_oak_boat",
    }));
    registerItem(new ItemBase("LEATHER", new String[]{"Leather"}, new String[]{"leather"}));
    registerItem(new ItemBase("BUCKET_MILK", new String[]{"Bucket Milk"}, new String[]{"milk_bucket"}));
    registerItem(new ItemBase("BRICK_ITEM", new String[]{"Brick"}, new String[]{"brick"}));
    registerItem(new ItemBase("CLAY_BALL", new String[]{"Clay"}, new String[]{"clay_ball"}));
    registerItem(new ItemBase("SUGAR_CANE", new String[]{"Sugar Cane"}, new String[]{"sugar_cane"}));
    registerItem(new ItemBase("PAPER", new String[]{"Paper"}, new String[]{"paper"}));
    registerItem(new ItemBase("BOOK", new String[]{"Book"}, new String[]{"book"}));
    registerItem(new ItemBase("SLIME_BALL", new String[]{"Slime Ball"}, new String[]{"slime_ball"}));
    registerItem(new ItemBase("MINECART_CHEST", new String[]{"Minecart Chest"}, new String[]{"chest_minecart"}));
    registerItem(new ItemBase("MINECART_FURNACE", new String[]{"Minecart Furnace"}, new String[]{"furnace_minecart"}));
    registerItem(new ItemBase("EGG", new String[]{"Egg"}, new String[]{"egg"}));
    registerItem(new ItemCompass("COMPASS", new String[]{"Compass"}, new String[]{"compass_00"}));
    registerItem(new ItemBase("FISHING_ROD", new String[]{"Fishing Rod"}, new String[]{"fishing_rod"})
      .setFuel(10).setMaterial(MAT_WOOD).setDurability(64)
    );
    registerItem(new ItemClock("CLOCK", new String[]{"Clock"}, new String[]{"clock_00"}));
    registerItem(new ItemBase("GLOWSTONE_DUST", new String[]{"Glowstone Dust"}, new String[]{"glowstone_dust"}));
    registerItem(new ItemBase("FISH_RAW", new String[]{"Fish Raw"}, new String[]{"cod"}).setFood(2,0.4f));  //variations
    registerItem(new ItemBase("FISH_COOKED", new String[]{"Fish Cooked"}, new String[]{"cooked_cod"}).setFood(5,6));  //variations
    registerItem(new ItemBase("COLOR",
      new String[]{"Ink Sack", "Rose Red", "Cactus Green", "Cocoa Beans", "Lapis Lazuli", "Purple Dye", "Cyan Dye", "Light Gray Dye", "Gray Dye", "Pink Dye", "Lime Dye", "Dandelion Yellow", "Light Blue Dye", "Magenta Dye", "Orange Dye", "Bone Meal"},
      new String[]{"ink_sac",  "rose_red", "cactus_green", "cocoa_beans", "lapis_lazuli", "purple_dye", "cyan_dye", "light_gray_dye", "gray_dye", "pink_dye", "lime_dye", "dandelion_yellow", "light_blue_dye", "magenta_dye", "orange_dye", "bone_meal"}).setVar());
    registerItem(new ItemBase("BONE", new String[]{"Bone"}, new String[]{"bone"}));
    registerItem(new ItemBase("SUGAR", new String[]{"Sugar"}, new String[]{"sugar"}));
    registerItem(new ItemBase("CAKE", new String[]{"Cake"}, new String[]{"cake"}));
    registerItem(new ItemBase("BED_ITEM", new String[]{"Bed"}, new String[]{"bed"})
      .setCanPlace().setBlockID("BED").setDir()
    );
    registerItem(new ItemBase("REDSTONE_REPEATER_ITEM", new String[]{"Redstone Repeater"}, new String[]{"repeater"}).setBlockID("REDSTONE_REPEATER").setCanPlace().setDir());
    registerItem(new ItemBase("COOKIE", new String[]{"Cookie"}, new String[]{"cookie"}).setFood(2,0.4f));
    registerItem(new ItemBase("MAP_USED", new String[]{"Map"}, new String[]{"filled_map"}));
    registerItem(new ItemBase("SHEARS", new String[]{"Shears"}, new String[]{"shears"})
      .setTool(TOOL_SHEARS).setDurability(238)
    );
    registerItem(new ItemBase("WATER_MELON", new String[]{"Watermelon"}, new String[]{"melon_slice"}).setFood(2,1.2f));
    registerItem(new ItemSeeds("WATER_MELON_SEEDS", new String[]{"Watermelon Seeds"}, new String[]{"melon_seeds"}).setSeeds("WATER_MELON"));
    registerItem(new ItemSeeds("PUMPKIN_SEEDS", new String[]{"Pumpkin Seeds"}, new String[]{"pumpkin_seeds"}).setSeeds("PUMPKIN"));
    registerItem(new ItemBase("STEAK_RAW", new String[]{"Steak Raw"}, new String[]{"beef"}).setFood(3,1.8f));
    registerItem(new ItemBase("STEAK_COOKED", new String[]{"Steak Cooked"}, new String[]{"cooked_beef"}).setFood(8,12.8f));
    registerItem(new ItemBase("CHICKEN_RAW", new String[]{"Chicken Raw"}, new String[]{"chicken"}).setFood(2,1.2f));
    registerItem(new ItemBase("CHICKEN_COOKED", new String[]{"Chicken Cooked"}, new String[]{"cooked_chicken"}).setFood(6,7.2f));
    registerItem(new ItemBase("ROTTEN_FLESH", new String[]{"Rotten Flesh"}, new String[]{"rotten_flesh"}).setFood(4,0.8f));
    registerItem(new ItemBase("ENDER_PEARL", new String[]{"Ender Pearl"}, new String[]{"ender_pearl"}));
    registerItem(new ItemBase("BLAZE_ROD", new String[]{"Blaze Rod"}, new String[]{"blaze_rod"}).setFuel(120));
    registerItem(new ItemBase("GHAST_TEAR", new String[]{"Ghast Tear"}, new String[]{"ghast_tear"}));
    registerItem(new ItemBase("GOLD_NUGGET", new String[]{"Gold Nugget"}, new String[]{"gold_nugget"}));
    registerItem(new ItemBase("NETHER_WART", new String[]{"Nether Wart"}, new String[]{"nether_wart"}));
    registerItem(new ItemBase("BOTTLE_WATER", new String[]{"Bottle Water"}, new String[]{"splash_potion"}));
    registerItem(new ItemBase("BOTTLE", new String[]{"Bottle"}, new String[]{"glass_bottle"}));
    registerItem(new ItemBase("SPIDER_EYE", new String[]{"Spider Eye"}, new String[]{"spider_eye"}).setFood(2,3.2f));
    registerItem(new ItemBase("SPIDER_EYE_FERMENTED", new String[]{"Spider Eye Fermented"}, new String[]{"fermented_spider_eye"}));
    registerItem(new ItemBase("BLAZE_POWDER", new String[]{"Blaze Powder"}, new String[]{"blaze_powder"}));
    registerItem(new ItemBase("MAGMA", new String[]{"Magma Cream"}, new String[]{"magma_cream"}));
    registerItem(new ItemBase("BREWING_STAND", new String[]{"Brewing Stand"}, new String[]{"brewing_stand"}));
    registerItem(new ItemBase("CAULDRON", new String[]{"Cauldron"}, new String[]{"cauldron"}));
    registerItem(new ItemBase("ENDER_EYE", new String[]{"Ender Eye"}, new String[]{"ender_eye"}).setUseable());
    registerItem(new ItemBase("WATER_MELON_GOLDEN", new String[]{"Golden Watermelon"}, new String[]{"glistering_melon_slice"}));
    registerItem(new ItemBase("EGG_SPAWNER", new String[]{"Egg Spawner"}, new String[]{"spawn_egg"}));  //variations
    registerItem(new ItemBase("POTION", new String[]{"Potion"}, new String[]{"glass_bottle"}));
    registerItem(new ItemBase("FIRE_SHOT", new String[]{"Fire Shot"}, new String[]{"fire_charge"}));
    registerItem(new ItemBase("BOOK_PEN", new String[]{"Book"}, new String[]{"writable_book"}));
    registerItem(new ItemBase("BOOK_WRITTEN", new String[]{"Book Written"}, new String[]{"written_book"}));
    registerItem(new ItemBase("EMERALD", new String[]{"Emerald"}, new String[]{"emerald"}));
    registerItem(new ItemBase("ITEM_FRAME", new String[]{"Item Frame"}, new String[]{"item_frame"}));
    registerItem(new ItemBase("POT", new String[]{"Flower Pot"}, new String[]{"flower_pot"}));
    registerItem(new ItemBase("CARROT", new String[]{"Carrot"}, new String[]{"carrot"}).setFood(3,4.8f));
    registerItem(new ItemBase("POTATO", new String[]{"Potato"}, new String[]{"potato"}).setFood(1,0.6f));
    registerItem(new ItemBase("POTATO_BAKED", new String[]{"Baked Potato"}, new String[]{"baked_potato"}).setFood(5,7.2f));
    registerItem(new ItemBase("POTATO_POISON", new String[]{"Poisonous Potato"}, new String[]{"poisonous_potato"}).setFood(2,1.2f));
    registerItem(new ItemBase("MAP_EMPTY", new String[]{"Map"}, new String[]{"map"}));
    registerItem(new ItemBase("CARROT_GOLDEN", new String[]{"Golden Carrot"}, new String[]{"golden_carrot"}).setFood(6,14.4f));
//    registerItem(new ItemBase("HEAD", new String[]{"Head"}, new String[]{"skull_steve"}));
    registerItem(new ItemBase("FISHING_ROD_CARROT", new String[]{"Carrot on a stick"}, new String[]{"carrot_on_a_stick"})
      .setFuel(10).setMaterial(MAT_WOOD).setDurability(25)
    );
    registerItem(new ItemBase("NETHER_STAR", new String[]{"Nether Star"}, new String[]{"nether_star"}));
    registerItem(new ItemBase("PUMPKIN_PIE", new String[]{"Pumpkin Pie"}, new String[]{"pumpkin_pie"}).setFood(8,4.8f));
    registerItem(new ItemBase("FIREWORKS", new String[]{"Fireworks"}, new String[]{"firework_rocket"}));
    registerItem(new ItemBase("FIRE_CHARGE", new String[]{"Fireworks Charge"}, new String[]{"fire_charge"}));
    registerItem(new ItemBase("BOOK_SPELL", new String[]{"Spell Book"}, new String[]{"enchanted_book"}).setFuel(15).setMaterial(MAT_WOOD));
    registerItem(new ItemBase("REDSTONE_COMPARATOR_ITEM", new String[]{"Redstone Comparator"}, new String[]{"comparator"}).setBlockID("REDSTONE_COMPARATOR").setCanPlace().setDir());
    registerItem(new ItemBase("NETHER_BRICK_ITEM", new String[]{"Nether Brick"}, new String[]{"nether_brick"}));
    registerItem(new ItemBase("QUARTZ", new String[]{"Quartz"}, new String[]{"quartz"}));
    registerItem(new ItemBase("MINECART_TNT", new String[]{"Minecart TNT"}, new String[]{"tnt_minecart"}));
    registerItem(new ItemBase("MINECART_HOPPER", new String[]{"Minecart Hopper"}, new String[]{"hopper_minecart"}));
    registerItem(new ItemBase("HORSE_ARMOR_IRON", new String[]{"Iron Horse Armor"}, new String[]{"iron_horse_armor"}));
    registerItem(new ItemBase("HORSE_ARMOR_GOLD", new String[]{"Gold Horse Armor"}, new String[]{"golden_horse_armor"}));
    registerItem(new ItemBase("HORSE_ARMOR_DIAMOND", new String[]{"Diamond Horse Armor"}, new String[]{"diamond_horse_armor"}));
    registerItem(new ItemBase("LEAD", new String[]{"Lead"}, new String[]{"lead"}));
    registerItem(new ItemBase("NAME_TAG", new String[]{"Name Tag"}, new String[]{"name_tag"}));
    registerItem(new ItemBase("MINECART_COMMAND_BLOCK", new String[]{"Minecart Command Block"}, new String[]{"command_block_minecart"}));
    registerItem(new ItemBase("HOPPER_ITEM", new String[]{"Hopper"}, new String[]{"hopper"})
      .setCanPlace().setBlockID("HOPPER").setDirFace()
    );
    registerItem(new ItemKelp("KELP", new String[]{"Kelp"}, new String[]{"kelp"}).setPlant("KELPTOP"));
  }

  public TextureMap getTexture(String name) {
    for(int a=0;a<textures.size();a++) {
      TextureMap t = textures.get(a);
      if (t.name.equals(name)) return t;
    }
    Static.log("Texture not found:" + name);
    return null;
  }

  public int getTextureIdx(String name) {
    for(int a=0;a<textures.size();a++) {
      TextureMap t = textures.get(a);
      if (t.name.equals(name)) return a;
    }
    Static.log("Texture not found:" + name);
    return -1;
  }

  public void stitchTiles() {
    for(int idx=0;idx<MAX_ID;idx++) {
      if (regItems[idx] == null) continue;
      ItemBase item = regItems[idx];
      if (item.images == null) continue;
      item.ai = new AssetImage[item.images.length];
      for(int b=0;b<item.images.length;b++) {
        AssetImage ai = Assets.getImage("item/" + item.images[b]);
        tiles.add(ai);
        item.ai[b] = ai;
      }
    }
    Collections.sort(tiles, new Comparator() {
      public int compare(Object o1, Object o2) {
        AssetImage ai1 = (AssetImage)o1;
        AssetImage ai2 = (AssetImage)o2;
        if (ai1.image.getWidth() < ai2.image.getWidth()) return -1;
        if (ai1.image.getWidth() > ai2.image.getWidth()) return 1;
        return 0;
      }
    });
    stitched = new TextureMap();
    stitched.initImage(512, 512);
    stitched.initUsage();
    for(int a=0;a<tiles.size();a++) {
      AssetImage ai = tiles.get(a);
      int w = ai.image.getWidth();
      int h = ai.image.getHeight();
      ai.isAnimated = w != h;
      if (ai.isAnimated) {
        //animation
        int cnt = h / w;
        ai.noFrames = cnt;
        ai.images = new JFImage[cnt];
        ai.w = w;
        ai.h = w;
        ai.xs = new int[cnt];
        ai.ys = new int[cnt];
        for(int b=0;b<cnt;b++) {
          JFImage frame = new JFImage();
          frame.setSize(w, w);
          frame.putPixels(ai.image.getPixels(0, b * w, w, w), 0, 0, w, w, 0);
          ai.images[b] = frame;
          int loc[] = stitched.placeSubTexture(ai.images[b].getPixels(), w, w);
          if (loc == null) {
            JFAWT.showError("Error", "Your texture pack size can not fit into your video cards max texture size\nPlease remove high resolution packs and restart.");
            valid = false;
            return;
          }
          ai.xs[b] = loc[0];
          ai.ys[b] = loc[1];
        }
      } else {
        //normal
        ai.w = w;
        ai.h = h;
        int loc[] = stitched.placeSubTexture(ai.image.getPixels(), w, h);
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
    stitched.image.savePNG("items-stitched.png");
    for(int a=0;a<MAX_ID;a++) {
      if (regItems[a] == null) continue;
      ItemBase item = regItems[a];
      if (item.images == null) continue;
      item.textures = new SubTexture[item.images.length];
      for(int b=0;b<item.images.length;b++) {
        AssetImage ai = item.ai[b];
        SubTexture st = new SubTexture();
        st.texture = stitched;
        st.ai = ai;
        if (!ai.isAnimated) {
          st.texture = stitched;
          st.x1 = ((float)ai.x) / ((float)stitched.sx);
          st.y1 = ((float)ai.y) / ((float)stitched.sy);
          st.x2 = ((float)(ai.x + ai.w - 1)) / ((float)stitched.sx);
          st.y2 = ((float)(ai.y + ai.h - 1)) / ((float)stitched.sy);
          st.width = st.x2 - st.x1 + (1.0f/stitched.sx);
          st.height = st.y2 - st.y1 + (1.0f/stitched.sy);
        } else {
          st.isAnimated = ai.isAnimated;
          st.x1s = new float[ai.noFrames];
          st.y1s = new float[ai.noFrames];
          st.x2s = new float[ai.noFrames];
          st.y2s = new float[ai.noFrames];
          for(int c=0;c<ai.noFrames;c++) {
            st.x1s[c] = ((float)ai.xs[c]) / ((float)stitched.sx);
            st.y1s[c] = ((float)ai.ys[c]) / ((float)stitched.sy);
            st.x2s[c] = ((float)(ai.xs[c] + ai.w - 1)) / ((float)stitched.sx);
            st.y2s[c] = ((float)(ai.ys[c] + ai.h - 1)) / ((float)stitched.sy);
          }
          st.width = st.x2 - st.x1;
          st.height = st.y2 - st.y1;
        }
        item.textures[b] = st;
      }
    }
  }

  public void initTexture() {
    stitched.load();
    //load all independant textures
    texturesCount = textures.size();
    for(int a=0;a<textures.size();a++) {
      TextureMap t = textures.get(a);
      AssetImage ai = others.get(a);
      t.load(ai.image);
    }
  }

  public void initBuffers() {
    for(int a=0;a<MAX_ID;a++) {
      if (regItems[a] == null) continue;
      ItemBase item = regItems[a];
      if (item.cantGive) continue;
      int vars = 1;
      if (item.isVar) {
        vars = item.names.length;
      }
      item.bufs = new RenderDest[vars];
      item.voxel = new Voxel[vars];
      ItemBase.data.bl[X] = 0;  //entities only use sunlight
      for(int var=0;var<vars;var++) {
        try {
          //create item object for inventory screens
          item.bufs[var] = new RenderDest(1);
          item.buildBuffers(item.bufs[var], ItemBase.data);
          item.bufs[var].getBuffers(0).copyBuffers();
          //create item object for rendering in world (voxels)
          item.voxel[var] = new Voxel(item, var);
          item.voxel[var].buildBuffers(item.voxel[var].dest, ItemBase.data);
          item.voxel[var].dest.getBuffers(0).copyBuffers();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      ItemBase.data.clr = null;
    }
  }
}
