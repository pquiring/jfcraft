package jfcraft.data;

/** Registered items
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
import jfcraft.opengl.*;

public class Items {
  public static final int MAX_ID = 65536;
  public static final int FIRST_ID = 32768;
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
  public static char CLAY_ITEM;   //dup? remove?
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

  //tool types
  public final static int TOOL_SHOVEL = 1;
  public final static int TOOL_AXE = 2;
  public final static int TOOL_PICKAXE = 3;
  public final static int TOOL_HOE = 4;
  public final static int TOOL_FLINT_STEEL = 5;
  public final static int TOOL_SHEARS = 6;
  public final static int TOOL_OTHER = 7;

  //weapons types
  public final static int WEAPON_SWORD = 1;
  public final static int WEAPON_BOW = 2;

  //armor types
  public final static int ARMOR_HEAD = 0;
  public final static int ARMOR_CHEST = 1;
  public final static int ARMOR_LEGS = 2;
  public final static int ARMOR_FEET = 3;

  public final static byte VAR_COCOA = 3;
  public final static byte VAR_BONEMEAL = 15;

  public Texture stitched;
  public int texturesCount;
  public ArrayList<Texture> textures = new ArrayList<Texture>();  //other textures

  public void registerDefault() {
    //register items
    registerItem(new ItemBase("IRON_SHOVEL", new String[]{"Iron Shovel"},  new String[]{"iron_shovel"}).setTool(TOOL_SHOVEL).setDmg(4));
    registerItem(new ItemBase("IRON_PICKAXE", new String[]{"Iron Pickaxe"},  new String[]{"iron_pickaxe"}).setTool(TOOL_PICKAXE).setDmg(5));
    registerItem(new ItemBase("IRON_AXE", new String[]{"Iron Axe"},  new String[]{"iron_axe"}).setTool(TOOL_AXE).setDmg(6));
    registerItem(new ItemBase("FLINT_STEEL", new String[]{"Flint and Steel"},  new String[]{"flint_and_steel"}).setTool(TOOL_FLINT_STEEL));
    registerItem(new ItemBase("APPLE", new String[]{"Apple"},  new String[]{"apple"}).setFood(4f, 24.f));
    registerItem(new ItemBow("BOW", new String[]{"Bow"},  new String[]{"bow_standby"}).setWeapon(WEAPON_BOW));
    registerItem(new ItemBase("ARROW", new String[]{"Arrow"},  new String[]{"arrow"}));
    registerItem(new ItemBase("COAL", new String[]{"Coal", "Charcoal"},  new String[] {"coal", "charcoal"}).setVar().setFuel(80));
    registerItem(new ItemBase("DIAMOND", new String[]{"Diamond"},  new String[]{"diamond"}));
    registerItem(new ItemBase("IRON_INGOT", new String[]{"Iron Ingot"},  new String[]{"iron_ingot"}));
    registerItem(new ItemBase("GOLD_INGOT", new String[]{"Gold Ingot"},  new String[]{"gold_ingot"}));
    registerItem(new ItemBase("IRON_SWORD", new String[]{"Iron Sword"},  new String[]{"iron_sword"}).setWeapon(WEAPON_SWORD).setDmg(7));
    registerItem(new ItemBase("WOOD_SWORD", new String[]{"Wood Sword"},  new String[]{"wood_sword"}).setWeapon(WEAPON_SWORD).setFuel(10).setWood().setDmg(5));
    registerItem(new ItemBase("WOOD_SHOVEL", new String[]{"Wood Shovel"},  new String[]{"wood_shovel"}).setTool(TOOL_SHOVEL).setFuel(10).setWood().setDmg(2));
    registerItem(new ItemBase("WOOD_PICKAXE", new String[]{"Wood Pickaxe"},  new String[]{"wood_pickaxe"}).setTool(TOOL_PICKAXE).setFuel(10).setWood().setDmg(3));
    registerItem(new ItemBase("WOOD_AXE", new String[]{"Wood Axe"},  new String[]{"wood_axe"}).setTool(TOOL_AXE).setFuel(10).setWood().setDmg(4));
    registerItem(new ItemBase("STONE_SWORD", new String[]{"Stone Sword"},  new String[]{"stone_sword"}).setWeapon(WEAPON_SWORD).setDmg(6));
    registerItem(new ItemBase("STONE_SHOVEL", new String[]{"Stone Shovel"},  new String[]{"stone_shovel"}).setTool(TOOL_SHOVEL).setDmg(3));
    registerItem(new ItemBase("STONE_PICKAXE", new String[]{"Stone Pickaxe"},  new String[]{"stone_pickaxe"}).setTool(TOOL_PICKAXE).setDmg(4));
    registerItem(new ItemBase("STONE_AXE", new String[]{"Stone Axe"},  new String[]{"stone_axe"}).setTool(TOOL_AXE).setDmg(5));
    registerItem(new ItemBase("DIAMOND_SWORD", new String[]{"Diamond Sword"},  new String[]{"diamond_sword"}).setWeapon(WEAPON_SWORD).setDmg(8));
    registerItem(new ItemBase("DIAMOND_SHOVEL", new String[]{"Diamond Shovel"},  new String[]{"diamond_shovel"}).setTool(TOOL_SHOVEL).setDmg(5));
    registerItem(new ItemBase("DIAMOND_PICKAXE", new String[]{"Diamond Pickaxe"},  new String[]{"diamond_pickaxe"}).setTool(TOOL_PICKAXE).setDmg(6));
    registerItem(new ItemBase("DIAMOND_AXE", new String[]{"Diamond Axe"},  new String[]{"diamond_axe"}).setTool(TOOL_AXE).setDmg(7));
    registerItem(new ItemBase("STICK", new String[]{"Stick"},  new String[]{"stick"}).setFuel(5).setWood());
    registerItem(new ItemBase("BOWL", new String[]{"Bowl"},  new String[]{"bowl"}).setFuel(5).setWood());
    registerItem(new ItemBase("MUSHROOM_STEW", new String[]{"Mushroom Stew"},  new String[]{"mushroom_stew"}).setFood(6,7.2f));
    registerItem(new ItemBase("GOLD_SWORD", new String[]{"Gold Sword"},  new String[]{"gold_sword"}).setWeapon(WEAPON_SWORD).setDmg(5));
    registerItem(new ItemBase("GOLD_SHOVEL", new String[]{"Gold Shovel"},  new String[]{"gold_shovel"}).setTool(TOOL_SHOVEL).setDmg(2));
    registerItem(new ItemBase("GOLD_PICKAXE", new String[]{"Gold Pickaxe"},  new String[]{"gold_pickaxe"}).setTool(TOOL_PICKAXE).setDmg(3));
    registerItem(new ItemBase("GOLD_AXE", new String[]{"Gold Axe"},  new String[]{"gold_axe"}).setTool(TOOL_AXE).setDmg(4));
    registerItem(new ItemBase("STRING", new String[]{"String"},  new String[]{"string"}));
    registerItem(new ItemBase("FEATHER", new String[]{"Feather"},  new String[]{"feather"}));
    registerItem(new ItemBase("GUN_POWDER", new String[]{"Gun Powder"},  new String[]{"gunpowder"}));
    registerItem(new ItemBase("WOOD_HOE", new String[]{"Wood Hoe"},  new String[]{"wood_hoe"}).setTool(TOOL_HOE).setFuel(10).setWood());
    registerItem(new ItemBase("STONE_HOE", new String[]{"Stone Hoe"},  new String[]{"stone_hoe"}).setTool(TOOL_HOE));
    registerItem(new ItemBase("IRON_HOE", new String[]{"Iron Hoe"},  new String[]{"iron_hoe"}).setTool(TOOL_HOE));
    registerItem(new ItemBase("DIAMOND_HOE", new String[]{"Diamond Hoe"},  new String[]{"diamond_hoe"}).setTool(TOOL_HOE));
    registerItem(new ItemBase("GOLD_HOE", new String[]{"Gold Hoe"},  new String[]{"gold_hoe"}).setTool(TOOL_HOE));
    registerItem(new ItemSeeds("SEEDS", new String[]{"Seeds"},  new String[]{"seeds_wheat"}).setSeeds("WHEAT"));
    registerItem(new ItemBase("WHEAT_ITEM", new String[]{"Wheat"},  new String[]{"wheat"}));
    registerItem(new ItemBase("BREAD", new String[]{"Bread"},  new String[]{"bread"}).setFood(5, 6));
    registerItem(new ItemBase("LEATHER_CAP", new String[]{"Leather Cap"},  new String[]{"leather_helmet"}).setArmor(ARMOR_HEAD));
    registerItem(new ItemBase("LEATHER_CHEST", new String[]{"Leather Chest"},  new String[]{"leather_chestplate"}).setArmor(ARMOR_CHEST));
    registerItem(new ItemBase("LEATHER_PANTS", new String[]{"Leather Pants"},  new String[]{"leather_leggings"}).setArmor(ARMOR_LEGS));
    registerItem(new ItemBase("LEATHER_BOOTS", new String[]{"Leather Boots"},  new String[]{"leather_boots"}).setArmor(ARMOR_FEET));
    registerItem(new ItemBase("CHAIN", new String[]{"Chainmail"},  new String[]{"chainmail"}));
    registerItem(new ItemBase("CHAIN_HELMET", new String[]{"Chainmail Cap"},  new String[]{"chainmail_helmet"}).setArmor(ARMOR_HEAD));
    registerItem(new ItemBase("CHAIN_CHEST", new String[]{"Chainmail Chest"},  new String[]{"chainmail_chestplate"}).setArmor(ARMOR_CHEST));
    registerItem(new ItemBase("CHAIN_PANTS", new String[]{"Chainmail Pants"},  new String[]{"chainmail_leggings"}).setArmor(ARMOR_LEGS));
    registerItem(new ItemBase("CHAIN_BOOTS", new String[]{"Chainmail Boots"},  new String[]{"chainmail_boots"}).setArmor(ARMOR_FEET));
    registerItem(new ItemBase("IRON_HELMET", new String[]{"Iron Helmet"},  new String[]{"iron_helmet"}).setArmor(ARMOR_HEAD));
    registerItem(new ItemBase("IRON_CHEST", new String[]{"Iron Chest"},  new String[]{"iron_chestplate"}).setArmor(ARMOR_CHEST));
    registerItem(new ItemBase("IRON_PANTS", new String[]{"Iron Pants"},  new String[]{"iron_leggings"}).setArmor(ARMOR_LEGS));
    registerItem(new ItemBase("IRON_BOOTS", new String[]{"Iron Boots"},  new String[]{"iron_boots"}).setArmor(ARMOR_FEET));
    registerItem(new ItemBase("DIAMOND_HELMET", new String[]{"Diamond Helmet"},  new String[]{"diamond_helmet"}).setArmor(ARMOR_HEAD));
    registerItem(new ItemBase("DIAMOND_CHEST", new String[]{"Diamond Chest"},  new String[]{"diamond_chestplate"}).setArmor(ARMOR_CHEST));
    registerItem(new ItemBase("DIAMOND_PANTS", new String[]{"Diamond Pants"},  new String[]{"diamond_leggings"}).setArmor(ARMOR_LEGS));
    registerItem(new ItemBase("DIAMOND_BOOTS", new String[]{"Diamond Boots"},  new String[]{"diamond_boots"}).setArmor(ARMOR_FEET));
    registerItem(new ItemBase("GOLD_HELMET", new String[]{"Gold Helmet"},  new String[]{"gold_helmet"}).setArmor(ARMOR_HEAD));
    registerItem(new ItemBase("GOLD_CHEST", new String[]{"Gold Chest"},  new String[]{"gold_chestplate"}).setArmor(ARMOR_CHEST));
    registerItem(new ItemBase("GOLD_PANTS", new String[]{"Gold Pants"},  new String[]{"gold_leggings"}).setArmor(ARMOR_LEGS));
    registerItem(new ItemBase("GOLD_BOOTS", new String[]{"Gold Boots"},  new String[]{"gold_boots"}).setArmor(ARMOR_FEET));
    registerItem(new ItemBase("FLINT", new String[]{"Flint"},  new String[]{"flint"}));

    registerItem(new ItemBase("PORK_RAW", new String[]{"Raw Pork"},  new String[]{"porkchop_raw"}).setFood(0,0));
    registerItem(new ItemBase("PORK_COOKED", new String[]{"Cooked Pork"},  new String[]{"porkchop_cooked"}).setFood(8,12.8f));
    registerItem(new ItemBase("PAINTING", new String[]{"Item Frame"},  new String[]{"painting"}));
    registerItem(new ItemBase("APPLE_GOLDEN", new String[]{"Golden Apple"},  new String[]{"apple_golden"}).setFood(4,9.6f));
    registerItem(new ItemBase("SIGN_ITEM", new String[]{"Sign"},  new String[]{"sign"})
      .setCanPlace().setBlockID("SIGN").setDirFace()
    );
    registerItem(new ItemBase("WOOD_DOOR_ITEM", new String[]{"Wooden Door"},  new String[]{"door_wood"}).setFuel(10).setWood().setBlockID("WOOD_DOOR").setCanPlace().setDir());
    registerItem(new ItemBase("BUCKET", new String[]{"Bucket"},  new String[]{"bucket_empty"}));
    registerItem(new ItemBase("BUCKET_WATER", new String[]{"Bucket Water"},  new String[]{"bucket_water"}));
    registerItem(new ItemBase("BUCKET_LAVA", new String[]{"Bucket Lava"},  new String[]{"bucket_lava"}).setFuel(1000));
    registerItem(new ItemMinecart("MINECART", new String[]{"Minecart"},  new String[]{"minecart_normal"}));
    registerItem(new ItemBase("SADDLE", new String[]{"Saddle"},  new String[]{"saddle"}));
    registerItem(new ItemBase("IRON_DOOR_ITEM", new String[]{"Iron Door"},  new String[]{"door_iron"}).setBlockID("IRON_DOOR").setCanPlace());
    registerItem(new ItemBase("RED_STONE_ITEM", new String[]{"Redstone Dust"},  new String[]{"redstone_dust"}).setBlockID("RED_STONE").setCanPlace());
    registerItem(new ItemBase("SNOWBALL", new String[]{"Snowball"},  new String[]{"snowball"}));
    registerItem(new ItemBoat("BOAT", new String[]{"Boat"},  new String[]{"boat"}));
    registerItem(new ItemBase("LEATHER", new String[]{"Leather"},  new String[]{"leather"}));
    registerItem(new ItemBase("BUCKET_MILK", new String[]{"Bucket Milk"},  new String[]{"bucket_milk"}));
    registerItem(new ItemBase("BRICK_ITEM", new String[]{"Brick"},  new String[]{"brick"}));
    registerItem(new ItemBase("CLAY_ITEM", new String[]{"Clay"},  new String[]{"clay_ball"}));
    registerItem(new ItemBase("SUGAR_CANE", new String[]{"Sugar Cane"},  new String[]{"reeds"}));
    registerItem(new ItemBase("PAPER", new String[]{"Paper"},  new String[]{"paper"}));
    registerItem(new ItemBase("BOOK", new String[]{"Book"},  new String[]{"book_normal"}));
    registerItem(new ItemBase("SLIME_BALL", new String[]{"Slime Ball"},  new String[]{"slimeball"}));
    registerItem(new ItemBase("MINECART_CHEST", new String[]{"Minecart Chest"},  new String[]{"minecart_chest"}));
    registerItem(new ItemBase("MINECART_FURNACE", new String[]{"Minecart Furnace"},  new String[]{"minecart_furnace"}));
    registerItem(new ItemBase("EGG", new String[]{"Egg"},  new String[]{"egg"}));
    registerItem(new ItemCompass("COMPASS", new String[]{"Compass"},  new String[]{"compass"}));
    registerItem(new ItemBase("FISHING_ROD", new String[]{"Fishing Rod"},  new String[]{"fishing_rod_uncast"}).setFuel(10).setWood());
    registerItem(new ItemClock("CLOCK", new String[]{"Clock"},  new String[]{"clock"}));
    registerItem(new ItemBase("GLOWSTONE_DUST", new String[]{"Glowstone Dust"},  new String[]{"glowstone_dust"}));
    registerItem(new ItemBase("FISH_RAW", new String[]{"Fish Raw"},  new String[]{"fish_cod_raw"}).setFood(2,0.4f));  //variations
    registerItem(new ItemBase("FISH_COOKED", new String[]{"Fish Cooked"},  new String[]{"fish_cod_cooked"}).setFood(5,6));  //variations
    registerItem(new ItemBase("COLOR",
      new String[]{"Ink Sack", "Rose Red", "Cactus Green", "Cocoa Beans", "Lapis Lazuli", "Purple Dye", "Cyan Dye", "Light Gray Dye", "Gray Dye", "Pink Dye", "Lime Dye", "Dandelion Yellow", "Light Blue Dye", "Magenta Dye", "Orange Dye", "Bone Meal"},
      new String[]{"dye_powder_black", "dye_powder_red", "dye_powder_green", "dye_powder_brown", "dye_powder_blue", "dye_powder_purple", "dye_powder_cyan", "dye_powder_silver", "dye_powder_gray", "dye_powder_pink", "dye_powder_lime", "dye_powder_yellow", "dye_powder_light_blue", "dye_powder_magenta", "dye_powder_orange", "dye_powder_white"}).setVar());
    registerItem(new ItemBase("BONE", new String[]{"Bone"},  new String[]{"bone"}));
    registerItem(new ItemBase("SUGAR", new String[]{"Sugar"},  new String[]{"sugar"}));
    registerItem(new ItemBase("CAKE", new String[]{"Cake"},  new String[]{"cake"}));
    registerItem(new ItemBase("BED_ITEM", new String[]{"Bed"},  new String[]{"bed"})
      .setCanPlace().setBlockID("BED").setDir()
    );
    registerItem(new ItemBase("REDSTONE_REPEATER_ITEM", new String[]{"Redstone Repeater"},  new String[]{"repeater"}).setBlockID("REDSTONE_REPEATER").setCanPlace().setDir());
    registerItem(new ItemBase("COOKIE", new String[]{"Cookie"},  new String[]{"cookie"}).setFood(2,0.4f));
    registerItem(new ItemBase("MAP_USED", new String[]{"Map"},  new String[]{"map_filled"}));
    registerItem(new ItemBase("SHEARS", new String[]{"Shears"},  new String[]{"shears"}).setTool(TOOL_SHEARS));
    registerItem(new ItemBase("WATER_MELON", new String[]{"Watermelon"},  new String[]{"melon"}).setFood(2,1.2f));
    registerItem(new ItemSeeds("WATER_MELON_SEEDS", new String[]{"Watermelon Seeds"},  new String[]{"seeds_melon"}).setSeeds("WATER_MELON"));
    registerItem(new ItemSeeds("PUMPKIN_SEEDS", new String[]{"Pumpkin Seeds"},  new String[]{"seeds_pumpkin"}).setSeeds("PUMPKIN"));
    registerItem(new ItemBase("STEAK_RAW", new String[]{"Steak Raw"},  new String[]{"beef_raw"}).setFood(3,1.8f));
    registerItem(new ItemBase("STEAK_COOKED", new String[]{"Steak Cooked"},  new String[]{"beef_cooked"}).setFood(8,12.8f));
    registerItem(new ItemBase("CHICKEN_RAW", new String[]{"Chicken Raw"},  new String[]{"chicken_raw"}).setFood(2,1.2f));
    registerItem(new ItemBase("CHICKEN_COOKED", new String[]{"Chicken Cooked"},  new String[]{"chicken_cooked"}).setFood(6,7.2f));
    registerItem(new ItemBase("ROTTEN_FLESH", new String[]{"Rotten Flesh"},  new String[]{"rotten_flesh"}).setFood(4,0.8f));
    registerItem(new ItemBase("ENDER_PEARL", new String[]{"Ender Pearl"},  new String[]{"ender_pearl"}));
    registerItem(new ItemBase("BLAZE_ROD", new String[]{"Blaze Rod"},  new String[]{"blaze_rod"}).setFuel(120));
    registerItem(new ItemBase("GHAST_TEAR", new String[]{"Ghast Tear"},  new String[]{"ghast_tear"}));
    registerItem(new ItemBase("GOLD_NUGGET", new String[]{"Gold Nugget"},  new String[]{"gold_nugget"}));
    registerItem(new ItemBase("NETHER_WART", new String[]{"Nether Wart"},  new String[]{"nether_wart"}));
    registerItem(new ItemBase("BOTTLE_WATER", new String[]{"Bottle Water"},  new String[]{"potion_bottle_drinkable"}));
    registerItem(new ItemBase("BOTTLE", new String[]{"Bottle"},  new String[]{"potion_bottle_empty"}));
    registerItem(new ItemBase("SPIDER_EYE", new String[]{"Spider Eye"},  new String[]{"spider_eye"}).setFood(2,3.2f));
    registerItem(new ItemBase("SPIDER_EYE_FERMENTED", new String[]{"Spider Eye Fermented"},  new String[]{"spider_eye_fermented"}));
    registerItem(new ItemBase("BLAZE_POWDER", new String[]{"Blaze Powder"},  new String[]{"blaze_powder"}));
    registerItem(new ItemBase("MAGMA", new String[]{"Magma Cream"},  new String[]{"magma_cream"}));
    registerItem(new ItemBase("BREWING_STAND", new String[]{"Brewing Stand"},  new String[]{"brewing_stand"}));
    registerItem(new ItemBase("CAULDRON", new String[]{"Cauldron"},  new String[]{"cauldron"}));
    registerItem(new ItemBase("ENDER_EYE", new String[]{"Ender Eye"},  new String[]{"ender_eye"}).setUseable());
    registerItem(new ItemBase("WATER_MELON_GOLDEN", new String[]{"Golden Watermelon"},  new String[]{"melon_speckled"}));
    registerItem(new ItemBase("EGG_SPAWNER", new String[]{"Egg Spawner"},  new String[]{"spawn_egg"}));  //variations
    registerItem(new ItemBase("POTION", new String[]{"Potion"},  new String[]{"potion_bottle_empty"}));
    registerItem(new ItemBase("FIRE_SHOT", new String[]{"Fire Shot"},  new String[]{"fireball"}));
    registerItem(new ItemBase("BOOK_PEN", new String[]{"Book"},  new String[]{"book_writable"}));
    registerItem(new ItemBase("BOOK_WRITTEN", new String[]{"Book Written"},  new String[]{"book_written"}));
    registerItem(new ItemBase("EMERALD", new String[]{"Emerald"},  new String[]{"emerald"}));
    registerItem(new ItemBase("ITEM_FRAME", new String[]{"Item Frame"},  new String[]{"item_frame"}));
    registerItem(new ItemBase("POT", new String[]{"Flower Pot"},  new String[]{"flower_pot"}));
    registerItem(new ItemBase("CARROT", new String[]{"Carrot"},  new String[]{"carrot"}).setFood(3,4.8f));
    registerItem(new ItemBase("POTATO", new String[]{"Potato"},  new String[]{"potato"}).setFood(1,0.6f));
    registerItem(new ItemBase("POTATO_BAKED", new String[]{"Baked Potato"},  new String[]{"potato_baked"}).setFood(5,7.2f));
    registerItem(new ItemBase("POTATO_POISON", new String[]{"Poisonous Potato"},  new String[]{"potato_poisonous"}).setFood(2,1.2f));
    registerItem(new ItemBase("MAP_EMPTY", new String[]{"Map"},  new String[]{"map_empty"}));
    registerItem(new ItemBase("CARROT_GOLDEN", new String[]{"Golden Carrot"},  new String[]{"carrot_golden"}).setFood(6,14.4f));
    registerItem(new ItemBase("HEAD", new String[]{"Head"},  new String[]{"skull_steve"}));
    registerItem(new ItemBase("FISHING_ROD_CARROT", new String[]{"Carrot on a stick"},  new String[]{"carrot_on_a_stick"}));
    registerItem(new ItemBase("NETHER_STAR", new String[]{"Nether Star"},  new String[]{"nether_star"}));
    registerItem(new ItemBase("PUMPKIN_PIE", new String[]{"Pumpkin Pie"},  new String[]{"pumpkin_pie"}).setFood(8,4.8f));
    registerItem(new ItemBase("FIREWORKS", new String[]{"Fireworks"},  new String[]{"fireworks"}));
    registerItem(new ItemBase("FIRE_CHARGE", new String[]{"Fireworks Charge"},  new String[]{"fireworks_charge"}));
    registerItem(new ItemBase("BOOK_SPELL", new String[]{"Spell Book"},  new String[]{"book_enchanted"}).setFuel(15).setWood());
    registerItem(new ItemBase("REDSTONE_COMPARATOR_ITEM", new String[]{"Redstone Comparator"},  new String[]{"comparator"}).setBlockID("REDSTONE_COMPARATOR").setCanPlace().setDir());
    registerItem(new ItemBase("NETHER_BRICK_ITEM", new String[]{"Nether Brick"},  new String[]{"netherbrick"}));
    registerItem(new ItemBase("QUARTZ", new String[]{"Quartz"},  new String[]{"quartz"}));
    registerItem(new ItemBase("MINECART_TNT", new String[]{"Minecart TNT"},  new String[]{"minecart_tnt"}));
    registerItem(new ItemBase("MINECART_HOPPER", new String[]{"Minecart Hopper"},  new String[]{"minecart_hopper"}));
    registerItem(new ItemBase("HORSE_ARMOR_IRON", new String[]{"Iron Horse Armor"},  new String[]{"iron_horse_armor"}));
    registerItem(new ItemBase("HORSE_ARMOR_GOLD", new String[]{"Gold Horse Armor"},  new String[]{"gold_horse_armor"}));
    registerItem(new ItemBase("HORSE_ARMOR_DIAMOND", new String[]{"Diamond Horse Armor"},  new String[]{"diamond_horse_armor"}));
    registerItem(new ItemBase("LEAD", new String[]{"Lead"},  new String[]{"lead"}));
    registerItem(new ItemBase("NAME_TAG", new String[]{"Name Tag"},  new String[]{"name_tag"}));
    registerItem(new ItemBase("MINECART_COMMAND_BLOCK", new String[]{"Minecart Command Block"},  new String[]{"minecart_command_block"}));
    registerItem(new ItemBase("HOPPER_ITEM", new String[]{"Hopper"},  new String[]{"hopper"})
      .setCanPlace().setBlockID("HOPPER").setDirFace()
    );
  }

  public Texture getTexture(String name) {
    for(int a=0;a<textures.size();a++) {
      Texture t = textures.get(a);
      if (t.name.equals(name)) return t;
    }
    Static.log("Texture not found:" + name);
    return null;
  }

  public int getTextureIdx(String name) {
    for(int a=0;a<textures.size();a++) {
      Texture t = textures.get(a);
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
        AssetImage ai = Assets.getImage("items/" + item.images[b]);
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
    stitched = new Texture();
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
            JF.showError("Error", "Your texture pack size can not fit into your video cards max texture size\nPlease remove high resolution packs and restart.");
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
          JF.showError("Error", "Your texture pack size can not fit into your video cards max texture size\nPlease remove high resolution packs and restart.");
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

  public void initTexture(GL gl) {
    stitched.load(gl);
    //load all independant textures
    texturesCount = textures.size();
    for(int a=0;a<textures.size();a++) {
      Texture t = textures.get(a);
      AssetImage ai = others.get(a);
      t.load(gl, ai.image);
    }
  }
}
