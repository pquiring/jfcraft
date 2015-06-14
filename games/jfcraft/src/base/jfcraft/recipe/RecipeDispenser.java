package jfcraft.recipe;

/** Makes dispenser
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeDispenser extends Recipe {
  public RecipeDispenser() {
    super(3,3);
  }

  public Item make(Item items[]) {
    if (items[0].id != Blocks.COBBLESTONE) return null;
    if (items[1].id != Blocks.COBBLESTONE) return null;
    if (items[2].id != Blocks.COBBLESTONE) return null;

    if (items[3].id != Blocks.COBBLESTONE) return null;
    if (items[4].id != Items.BOW) return null;
    if (items[4].dmg != 1.0f) return null;
    if (items[5].id != Blocks.COBBLESTONE) return null;

    if (items[6].id != Blocks.COBBLESTONE) return null;
    if (items[7].id != Items.RED_STONE_ITEM) return null;
    if (items[8].id != Blocks.COBBLESTONE) return null;

    return new Item(Blocks.DISPENSER);
  }
}
