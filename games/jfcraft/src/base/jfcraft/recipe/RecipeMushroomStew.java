package jfcraft.recipe;

/** Makes mushroom stew
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeMushroomStew extends Recipe {
  public RecipeMushroomStew() {
    super(-1,-1);
  }

  public Item make(Item items[]) {
    int bowl = 0;
    int brown = 0;
    int red = 0;
    for(int a=0;a<items.length;a++) {
      char id = items[a].id;
      if (id == Blocks.MUSHROOM_BROWN) {brown++; continue;}
      if (id == Blocks.MUSHROOM_RED) {red++; continue;}
      if (id == Items.BOWL) {bowl++; continue;}
      if (id == Blocks.AIR) continue;
      return null;
    }
    if (bowl == 1 && brown == 1 && red == 1) return new Item(Items.MUSHROOM_STEW);
    return null;
  }
}
