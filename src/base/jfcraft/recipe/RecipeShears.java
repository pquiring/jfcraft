package jfcraft.recipe;

/** Makes shears
 *
 * @author pquiring
 *
 * Created : Sept 23, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeShears extends Recipe {
  public RecipeShears() {
    super(2,2);
  }

  public Item make(Item items[]) {
    if (items[0].id == Blocks.AIR) {
//      if (items[0].id != Blocks.AIR) return null;
      if (items[1].id != Items.IRON_INGOT) return null;

      if (items[2].id != Items.IRON_INGOT) return null;
      if (items[3].id != Blocks.AIR) return null;

    } else {
      if (items[0].id != Items.IRON_INGOT) return null;
      if (items[1].id != Blocks.AIR) return null;

      if (items[2].id != Blocks.AIR) return null;
      if (items[3].id != Items.IRON_INGOT) return null;
    }

    return new Item(Items.SHEARS, 1.0f);
  }
}
