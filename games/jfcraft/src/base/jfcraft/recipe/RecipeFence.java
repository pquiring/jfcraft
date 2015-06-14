package jfcraft.recipe;

/** Makes fence
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeFence extends Recipe {
  public RecipeFence() {
    super(3,2);
  }

  public Item make(Item items[]) {
    byte var;
    if (items[1].id != Items.STICK) return null;
    if (items[4].id != Items.STICK) return null;

    var = items[0].var;

    if (items[0].id != Blocks.PLANKS) return null;
//    if (items[0].var != var) return null;

    if (items[2].id != Blocks.PLANKS) return null;
    if (items[2].var != var) return null;

    if (items[3].id != Blocks.PLANKS) return null;
    if (items[3].var != var) return null;

    if (items[5].id != Blocks.PLANKS) return null;
    if (items[5].var != var) return null;

    return new Item(Blocks.FENCE, (byte)var);
  }
}
