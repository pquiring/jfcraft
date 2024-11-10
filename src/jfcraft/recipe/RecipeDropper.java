package jfcraft.recipe;

/** Makes dropper
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeDropper extends Recipe {
  public RecipeDropper() {
    super(3,3);
  }

  public Item make(Item items[]) {
    if (items[0].id != Blocks.COBBLESTONE) return null;
    if (items[1].id != Blocks.COBBLESTONE) return null;
    if (items[2].id != Blocks.COBBLESTONE) return null;

    if (items[3].id != Blocks.COBBLESTONE) return null;
    if (items[4].id != Blocks.AIR) return null;
    if (items[5].id != Blocks.COBBLESTONE) return null;

    if (items[6].id != Blocks.COBBLESTONE) return null;
    if (items[7].id != Items.RED_STONE_ITEM) return null;
    if (items[8].id != Blocks.COBBLESTONE) return null;

    return new Item(Blocks.DROPPER);
  }
}
