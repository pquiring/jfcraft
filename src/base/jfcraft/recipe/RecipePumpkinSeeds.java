package jfcraft.recipe;

/** Makes pumpkin seeds
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipePumpkinSeeds extends Recipe {
  public RecipePumpkinSeeds() {
    super(1,1);
  }

  public Item make(Item items[]) {
    if (items[0].id != Blocks.PUMPKIN) return null;
    return new Item(Items.PUMPKIN_SEEDS, (byte)0, (byte)4);
  }
}
