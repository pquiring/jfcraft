package jfcraft.recipe;

/** Makes ender eye.
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeEnderEye extends Recipe {
  public RecipeEnderEye() {
    super(-1,-1);
  }

  public Item make(Item items[]) {
    int blazePowder = 0;
    int enderPearl = 0;
    for(int a=0;a<items.length;a++) {
      char id = items[a].id;
      if (id == Items.ENDER_PEARL) {enderPearl++; continue;}
      if (id == Items.BLAZE_POWDER) {blazePowder++; continue;}
      if (id == Blocks.AIR) continue;
      return null;
    }
    if (blazePowder == 1 && enderPearl == 1) return new Item(Items.ENDER_EYE);
    return null;
  }
}
