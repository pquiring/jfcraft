package jfcraft.recipe;

/** Makes hoe.
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeHoe extends Recipe {
  public RecipeHoe() {
    super(3,3);
  }

  public Item make(Item items[]) {
    char id = items[1].id;

    if (((items[0].id != id) || (items[2].id != Blocks.AIR)) &&
     ((items[2].id != id) || (items[0].id != Blocks.AIR))) return null;

    if (items[3].id != Blocks.AIR) return null;
    if (items[4].id != Items.STICK) return null;
    if (items[5].id != Blocks.AIR) return null;

    if (items[6].id != Blocks.AIR) return null;
    if (items[7].id != Items.STICK) return null;
    if (items[8].id != Blocks.AIR) return null;

    if (id == Items.DIAMOND) return new Item(Items.DIAMOND_HOE, 1.0f);
    if (id == Items.GOLD_INGOT) return new Item(Items.GOLD_HOE, 1.0f);
    if (id == Items.IRON_INGOT) return new Item(Items.IRON_HOE, 1.0f);
    if (id == Blocks.COBBLESTONE) return new Item(Items.STONE_HOE, 1.0f);
    if (id == Blocks.PLANKS) return new Item(Items.WOOD_HOE, 1.0f);
    return null;
  }
}
