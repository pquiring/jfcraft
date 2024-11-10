package jfcraft.recipe;

/** Makes a boat.
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.data.*;
import jfcraft.item.*;

public class RecipeBoat extends Recipe {
  public RecipeBoat() {
    super(3,2);
  }

  public Item make(Item items[]) {
    if (items[0].id != Blocks.PLANKS) return null;
    if (items[1].id != Blocks.AIR) return null;
    if (items[2].id != Blocks.PLANKS) return null;

    if (items[3].id != Blocks.PLANKS) return null;
    if (items[4].id != Blocks.PLANKS) return null;
    if (items[5].id != Blocks.PLANKS) return null;

    return new Item(Items.BOAT);
  }
}
