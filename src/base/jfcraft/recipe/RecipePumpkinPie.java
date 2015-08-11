package jfcraft.recipe;

/** Makes pumpking pie
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipePumpkinPie extends Recipe {
  public RecipePumpkinPie() {
    super(-1,-1);
  }

  public Item make(Item items[]) {
    int pumpkin = 0;
    int sugar = 0;
    int egg = 0;
    for(int a=0;a<items.length;a++) {
      char id = items[a].id;
      if (id == Items.SUGAR) {sugar++; continue;}
      if (id == Items.EGG) {egg++; continue;}
      if (id == Blocks.PUMPKIN) {pumpkin++; continue;}
      if (id == Blocks.AIR) {continue;}
      return null;
    }
    if (pumpkin == 1 && sugar == 1 && egg == 1) return new Item(Items.PUMPKIN_PIE);
    return null;
  }
}
