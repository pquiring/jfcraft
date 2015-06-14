package jfcraft.recipe;

/** Makes pumpkin lit
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipePumpkinLit extends Recipe {
  public RecipePumpkinLit() {
    super(1,2);
  }

  public Item make(Item items[]) {
    if (items[0].id != Blocks.PUMPKIN) return null;
    if (items[1].id != Blocks.TORCH) return null;
    return new Item(Blocks.PUMPKIN_LIT);
  }
}
