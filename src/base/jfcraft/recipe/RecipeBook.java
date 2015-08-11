package jfcraft.recipe;

/** Makes book
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeBook extends Recipe {
  public RecipeBook() {
    super(-1,-1);
  }

  public Item make(Item items[]) {
    int paper = 0;
    int leather = 0;
    for(int a=0;a<items.length;a++) {
      char id = items[a].id;
      if (id == Items.PAPER) {paper++; continue;}
      if (id == Items.LEATHER) {leather++; continue;}
      if (id == Blocks.AIR) continue;
      return null;
    }
    if (paper == 3 && leather == 1) return new Item(Items.BOOK);
    return null;
  }
}
