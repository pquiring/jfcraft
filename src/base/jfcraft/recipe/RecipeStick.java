package jfcraft.recipe;

/** Makes bread from 3 wheat.
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.data.*;
import jfcraft.item.*;

public class RecipeStick extends Recipe {
  public RecipeStick() {
    super(1,2);
  }

  public Item make(Item items[]) {
    for(int a=0;a<2;a++) {
      if (items[a].id != Blocks.PLANKS) return null;
    }
    return new Item(Items.STICK, (byte)0, (byte)4);
  }
}
