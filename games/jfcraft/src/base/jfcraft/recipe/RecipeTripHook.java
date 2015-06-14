package jfcraft.recipe;

/** Makes trip hook.
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeTripHook extends Recipe {
  public RecipeTripHook() {
    super(1,3);
  }

  public Item make(Item items[]) {
    if (items[0].id != Items.IRON_INGOT) return null;
    if (items[1].id != Items.STICK) return null;
    if (items[1].id != Blocks.PLANKS) return null;
    return new Item(Blocks.TRIP_HOOK, (byte)0, (byte)2);
  }
}
