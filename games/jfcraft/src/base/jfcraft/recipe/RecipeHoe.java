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
    super(2,3);
  }

  public Item make(Item items[]) {
    char id = items[0].id;

    if (items[1].id != id) return null;

    if (items[2].id == Blocks.AIR) {
      if (items[3].id != Items.STICK) return null;
      if (items[4].id != Blocks.AIR) return null;
      if (items[5].id != Items.STICK) return null;
    } else if (items[2].id == Items.STICK) {
      if (items[3].id != Blocks.AIR) return null;
      if (items[4].id != Items.STICK) return null;
      if (items[5].id != Blocks.AIR) return null;
    } else {
      return null;
    }

    if (id == Items.DIAMOND) return new Item(Items.DIAMOND_HOE, 1.0f);
    if (id == Items.GOLD_INGOT) return new Item(Items.GOLD_HOE, 1.0f);
    if (id == Items.IRON_INGOT) return new Item(Items.IRON_HOE, 1.0f);
    if (id == Blocks.COBBLESTONE) return new Item(Items.STONE_HOE, 1.0f);
    if (id == Blocks.PLANKS) return new Item(Items.WOOD_HOE, 1.0f);
    return null;
  }
}
