package jfcraft.recipe;

/** Makes item frame
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeItemFrame extends Recipe {
  public RecipeItemFrame() {
    super(3,3);
  }

  public Item make(Item items[]) {
    if (items[0].id != Items.STICK) return null;
    if (items[1].id != Items.STICK) return null;
    if (items[2].id != Items.STICK) return null;

    if (items[3].id != Items.STICK) return null;
    if (items[4].id != Items.LEATHER) return null;
    if (items[5].id != Items.STICK) return null;

    if (items[6].id != Items.STICK) return null;
    if (items[7].id != Items.STICK) return null;
    if (items[8].id != Items.STICK) return null;

    return new Item(Items.ITEM_FRAME);
  }
}
