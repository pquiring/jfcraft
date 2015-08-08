package jfcraft.data;

/** Item/block various types
 *
 * @author pquiring
 */
public class Types {
  //tool types
  public final static int TOOL_NONE = 0;
  public final static int TOOL_SHOVEL = 1;
  public final static int TOOL_AXE = 2;
  public final static int TOOL_PICKAXE = 3;
  public final static int TOOL_HOE = 4;
  public final static int TOOL_FLINT_STEEL = 5;
  public final static int TOOL_SHEARS = 6;
  public final static int TOOL_SWORD = 8;  //is a weapon type too
  public final static int TOOL_OTHER = 9;

  //weapons types
  public final static int WEAPON_SWORD = 1;
  public final static int WEAPON_BOW = 2;

  //armor types
  public final static int ARMOR_HEAD = 0;
  public final static int ARMOR_CHEST = 1;
  public final static int ARMOR_LEGS = 2;
  public final static int ARMOR_FEET = 3;

  //material types
  public final static int MAT_UNKNOWN = 0;
  public final static int MAT_WOOD = 1;
  public final static int MAT_STONE = 2;
  public final static int MAT_IRON = 3;
  public final static int MAT_GOLD = 4;
  public final static int MAT_DIAMOND = 5;

  //class types
  public final static int CLS_NONE = 0;
  public final static int CLS_WOOD = 1;
  public final static int CLS_STONE = 2;
  public final static int CLS_IRON = 3;
  public final static int CLS_DIAMOND = 4;

  //color vars
  public final static byte VAR_COCOA = 3;
  public final static byte VAR_BONEMEAL = 15;
}
