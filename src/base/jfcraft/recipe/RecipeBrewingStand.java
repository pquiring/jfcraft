package jfcraft.recipe;

/** Makes brewing stand
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeBrewingStand extends Recipe {
  public RecipeBrewingStand() {
    super(3,2);
  }

  public Item make(Item items[]) {
    if (items[0].id != Blocks.AIR) return null;
    if (items[1].id != Items.BLAZE_ROD) return null;
    if (items[2].id != Blocks.AIR) return null;

    if (items[3].id != Blocks.COBBLESTONE) return null;
    if (items[4].id != Blocks.COBBLESTONE) return null;
    if (items[5].id != Blocks.COBBLESTONE) return null;

    return new Item(Items.BREWING_STAND);
  }
}
