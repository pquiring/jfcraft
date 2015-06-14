package jfcraft.recipe;

/** Makes carpet
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.data.*;
import jfcraft.item.*;

public class RecipeCarpet extends Recipe {
  public RecipeCarpet() {
    super(2,1);
  }

  public Item make(Item items[]) {
    if (items[0].id != Blocks.WOOL) return null;
    if (items[1].id != Blocks.WOOL) return null;
    if (items[0].var != items[1].var) return null;
    return new Item(Blocks.CARPET, items[0].var, (byte)3);
  }
}
