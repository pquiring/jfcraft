package jfcraft.recipe;

/** Makes a bucket.
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.data.*;
import jfcraft.item.*;

public class RecipeBucket extends Recipe {
  public RecipeBucket() {
    super(3,2);
  }

  public Item make(Item items[]) {
    if (items[0].id != Items.IRON_INGOT) return null;
    if (items[1].id != Blocks.AIR) return null;
    if (items[2].id != Items.IRON_INGOT) return null;

    if (items[3].id != Blocks.AIR) return null;
    if (items[4].id != Items.IRON_INGOT) return null;
    if (items[5].id != Blocks.AIR) return null;

    return new Item(Items.BUCKET);
  }
}
