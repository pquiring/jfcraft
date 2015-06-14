package jfcraft.recipe;

import jfcraft.item.Item;
import jfcraft.data.*;

/** Makes planks from wood.
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;

public class RecipePlanks extends Recipe {
  public RecipePlanks() {
    super(1,1);
  }

  public Item make(Item items[]) {
    if (items[0].id == Blocks.WOOD) return new Item(Blocks.PLANKS, items[0].var, (byte)4);
    return null;
  }
}
