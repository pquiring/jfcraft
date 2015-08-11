package jfcraft.recipe;

/** Makes chest
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.data.*;
import jfcraft.item.*;

public class RecipeChest extends Recipe {
  public RecipeChest() {
    super(3,3);
  }

  public Item make(Item items[]) {
    for(int a=0;a<9;a++) {
      if (a == 4) {
        if (items[a].id != Blocks.AIR) return null;
      } else {
        if (items[a].id != Blocks.PLANKS) return null;
      }
    }
    return new Item(Blocks.CHEST);
  }
}
