package jfcraft.recipe;

/** Makes furnace
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.data.*;
import jfcraft.item.*;

public class RecipeFurnace extends Recipe {
  public RecipeFurnace() {
    super(3,3);
  }

  public Item make(Item items[]) {
    for(int a=0;a<9;a++) {
      if (a == 4) {
        if (items[a].id != Blocks.AIR) return null;
      } else {
        if (items[a].id != Blocks.COBBLESTONE) return null;
      }
    }
    return new Item(Blocks.FURNACE);
  }
}
