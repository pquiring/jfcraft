package jfcraft.recipe;

/** Makes comparator
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeRedstoneComparator extends Recipe {
  public RecipeRedstoneComparator() {
    super(3,3);
  }

  public Item make(Item items[]) {
    if (items[0].id != Blocks.AIR) return null;
    if (items[1].id != Blocks.REDSTONE_TORCH) return null;
    if (items[2].id != Blocks.AIR) return null;

    if (items[3].id != Blocks.REDSTONE_TORCH) return null;
    if (items[4].id != Items.QUARTZ) return null;
    if (items[5].id != Blocks.REDSTONE_TORCH) return null;

    if (items[6].id != Blocks.COBBLESTONE) return null;
    if (items[7].id != Blocks.COBBLESTONE) return null;
    if (items[8].id != Blocks.COBBLESTONE) return null;

    return new Item(Items.REDSTONE_COMPARATOR_ITEM);
  }
}
