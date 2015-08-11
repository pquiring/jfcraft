package jfcraft.recipe;

/** Makes ender chest
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.data.*;
import jfcraft.item.*;

public class RecipeEnderChest extends Recipe {
  public RecipeEnderChest() {
    super(3,3);
  }

  public Item make(Item items[]) {
    for(int a=0;a<9;a++) {
      if (a == 4) {
        if (items[a].id != Items.ENDER_EYE) return null;
      } else {
        if (items[a].id != Blocks.OBSIDIAN) return null;
      }
    }
    return new Item(Blocks.ENDER_CHEST);
  }
}
