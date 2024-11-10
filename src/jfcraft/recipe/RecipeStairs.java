package jfcraft.recipe;

/** Makes stairs
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeStairs extends Recipe {
  public RecipeStairs() {
    super(3,3);
  }

  public Item make(Item items[]) {
    byte var;
    if (items[0].id == Blocks.AIR) {
      var = items[2].var;

//      if (items[0].id != Blocks.AIR) return null;
      if (items[1].id != Blocks.AIR) return null;
      if (items[2].id != Blocks.PLANKS) return null;
      if (items[2].var != var) return null;

      if (items[3].id != Blocks.AIR) return null;
      if (items[4].id != Blocks.PLANKS) return null;
      if (items[4].var != var) return null;
      if (items[5].id != Blocks.PLANKS) return null;
      if (items[5].var != var) return null;

      if (items[6].id != Blocks.PLANKS) return null;
      if (items[6].var != var) return null;
      if (items[7].id != Blocks.PLANKS) return null;
      if (items[7].var != var) return null;
      if (items[8].id != Blocks.PLANKS) return null;
      if (items[8].var != var) return null;
    } else {
      var = items[0].var;

      if (items[0].id != Blocks.PLANKS) return null;
      if (items[0].var != var) return null;
      if (items[1].id != Blocks.AIR) return null;
      if (items[2].id != Blocks.AIR) return null;

      if (items[3].id != Blocks.PLANKS) return null;
      if (items[3].var != var) return null;
      if (items[4].id != Blocks.PLANKS) return null;
      if (items[4].var != var) return null;
      if (items[5].id != Blocks.AIR) return null;

      if (items[6].id != Blocks.PLANKS) return null;
      if (items[6].var != var) return null;
      if (items[7].id != Blocks.PLANKS) return null;
      if (items[7].var != var) return null;
      if (items[8].id != Blocks.PLANKS) return null;
      if (items[8].var != var) return null;
    }

    return new Item(Blocks.STAIRS_WOOD, (byte)var);
  }
}
