package jfcraft.recipe;

/** Makes TNT (boom!)
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeTNT extends Recipe {
  public RecipeTNT() {
    super(3,3);
  }

  public Item make(Item items[]) {
    if (items[0].id != Items.GUN_POWDER) return null;
    if (items[1].id != Blocks.SAND) return null;
    if (items[2].id != Items.GUN_POWDER) return null;

    if (items[3].id != Blocks.SAND) return null;
    if (items[4].id != Items.GUN_POWDER) return null;
    if (items[5].id != Blocks.SAND) return null;

    if (items[6].id != Items.GUN_POWDER) return null;
    if (items[7].id != Blocks.SAND) return null;
    if (items[8].id != Items.GUN_POWDER) return null;

    return new Item(Blocks.TNT);
  }
}
