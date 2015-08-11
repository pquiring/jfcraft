package jfcraft.recipe;

/** Makes flint and steel
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeFlintAndSteel extends Recipe {
  public RecipeFlintAndSteel() {
    super(-1,-1);
  }

  public Item make(Item items[]) {
    int flint = 0;
    int steel = 0;
    for(int a=0;a<items.length;a++) {
      char id = items[a].id;
      if (id == Items.IRON_INGOT) {steel++; continue;}
      if (id == Items.FLINT) {flint++; continue;}
      if (id == Blocks.AIR) continue;
      return null;
    }
    if (flint == 1 && steel == 1) return new Item(Items.FLINT_STEEL, 1.0f);
    return null;
  }
}
