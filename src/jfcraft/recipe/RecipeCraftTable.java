package jfcraft.recipe;

/** Makes crafting table from 4 planks.
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.data.*;
import jfcraft.item.*;

public class RecipeCraftTable extends Recipe {
  public RecipeCraftTable() {
    super(2,2);
  }

  public Item make(Item items[]) {
    for(int a=0;a<4;a++) {
      if (items[a].id != Blocks.PLANKS) return null;
    }
    return new Item(Blocks.CRAFTTABLE);
  }
}
