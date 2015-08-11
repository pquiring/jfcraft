package jfcraft.recipe;

/** Makes shovel.
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeArrow extends Recipe {
  public RecipeArrow() {
    super(1,3);
  }

  public Item make(Item items[]) {
    if (items[0].id != Items.FLINT) return null;
    if (items[1].id != Items.STICK) return null;
    if (items[2].id != Items.FEATHER) return null;

    return new Item(Items.ARROW);
  }
}
