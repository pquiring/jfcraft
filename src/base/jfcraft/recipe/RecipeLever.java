package jfcraft.recipe;

/** Makes lever
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeLever extends Recipe {
  public RecipeLever() {
    super(1,2);
  }

  public Item make(Item items[]) {
    if (items[0].id != Items.STICK) return null;
    if (items[1].id != Blocks.COBBLESTONE) return null;
    return new Item(Blocks.LEVER);
  }
}
