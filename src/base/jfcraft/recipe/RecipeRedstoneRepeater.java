package jfcraft.recipe;

/** Makes repeater
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeRedstoneRepeater extends Recipe {
  public RecipeRedstoneRepeater() {
    super(3,2);
  }

  public Item make(Item items[]) {
    if (items[0].id != Blocks.REDSTONE_TORCH) return null;
    if (items[1].id != Items.RED_STONE_ITEM) return null;
    if (items[2].id != Blocks.REDSTONE_TORCH) return null;

    if (items[3].id != Blocks.COBBLESTONE) return null;
    if (items[4].id != Blocks.COBBLESTONE) return null;
    if (items[5].id != Blocks.COBBLESTONE) return null;

    return new Item(Items.REDSTONE_REPEATER_ITEM);
  }
}
