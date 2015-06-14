package jfcraft.recipe;

/** Makes compass
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeCompass extends Recipe {
  public RecipeCompass() {
    super(3,3);
  }

  public Item make(Item items[]) {
    if (items[0].id != Blocks.AIR) return null;
    if (items[1].id != Items.IRON_INGOT) return null;
    if (items[2].id != Blocks.AIR) return null;

    if (items[3].id != Items.IRON_INGOT) return null;
    if (items[4].id != Items.RED_STONE_ITEM) return null;
    if (items[5].id != Items.IRON_INGOT) return null;

    if (items[6].id != Blocks.AIR) return null;
    if (items[7].id != Items.IRON_INGOT) return null;
    if (items[8].id != Blocks.AIR) return null;

    return new Item(Items.COMPASS);
  }
}
