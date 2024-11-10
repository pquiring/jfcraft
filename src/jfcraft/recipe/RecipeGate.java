package jfcraft.recipe;

/** Makes gate
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeGate extends Recipe {
  public RecipeGate() {
    super(3,2);
  }

  public Item make(Item items[]) {
    byte var;
    if (items[0].id != Items.STICK) return null;
    if (items[2].id != Items.STICK) return null;
    if (items[3].id != Items.STICK) return null;
    if (items[5].id != Items.STICK) return null;

    var = items[1].var;

    if (items[1].id != Blocks.PLANKS) return null;
//    if (items[1].var != var) return null;

    if (items[4].id != Blocks.PLANKS) return null;
    if (items[4].var != var) return null;

    return new Item(Blocks.GATE, (byte)var);
  }
}
