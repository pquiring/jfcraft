package jfcraft.recipe;

/** Makes sign
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeSign extends Recipe {
  public RecipeSign() {
    super(3,3);
  }

  public Item make(Item items[]) {
    if (items[0].id != Blocks.PLANKS) return null;
    if (items[1].id != Blocks.PLANKS) return null;
    if (items[2].id != Blocks.PLANKS) return null;

    if (items[3].id != Blocks.PLANKS) return null;
    if (items[4].id != Blocks.PLANKS) return null;
    if (items[5].id != Blocks.PLANKS) return null;

    if (items[6].id != Blocks.AIR) return null;
    if (items[7].id != Items.STICK) return null;
    if (items[8].id != Blocks.AIR) return null;

    return new Item(Items.SIGN_ITEM, (byte)0, (byte)4);
  }
}
