package jfcraft.recipe;

/** Makes shovel.
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeShovel extends Recipe {
  public RecipeShovel() {
    super(1,3);
  }

  public Item make(Item items[]) {
    char id = items[0].id;
    if (items[1].id != Items.STICK) return null;
    if (items[2].id != Items.STICK) return null;

    if (id == Items.DIAMOND)
      return new Item(Items.DIAMOND_PICKAXE, 1.0f);
    if (id == Items.GOLD_INGOT)
      return new Item(Items.GOLD_PICKAXE, 1.0f);
    if (id == Items.IRON_INGOT)
      return new Item(Items.IRON_PICKAXE, 1.0f);
    if (id == Blocks.COBBLESTONE)
      return new Item(Items.STONE_PICKAXE, 1.0f);
    if (id == Blocks.PLANKS)
      return new Item(Items.WOOD_PICKAXE, 1.0f);
    return null;
  }
}
