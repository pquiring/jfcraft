package jfcraft.recipe;

/** Makes iron bars
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeBars extends Recipe {
  public RecipeBars() {
    super(3,2);
  }

  public Item make(Item items[]) {
    for(int a=0;a<6;a++) {
      if (items[a].id != Items.IRON_INGOT) return null;
    }

    return new Item(Blocks.BARS);
  }
}
