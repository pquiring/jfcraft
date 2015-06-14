package jfcraft.recipe;

/** Makes cake
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeCake extends Recipe {
  public RecipeCake() {
    super(3,3);
  }

  public Item make(Item items[]) {
    if (items[0].id != Items.BUCKET_MILK) return null;
    if (items[1].id != Items.BUCKET_MILK) return null;
    if (items[2].id != Items.BUCKET_MILK) return null;

    if (items[3].id != Items.SUGAR) return null;
    if (items[4].id != Items.EGG) return null;
    if (items[5].id != Items.SUGAR) return null;

    if (items[6].id != Items.WHEAT_ITEM) return null;
    if (items[7].id != Items.WHEAT_ITEM) return null;
    if (items[8].id != Items.WHEAT_ITEM) return null;

    return new Item(Items.CAKE);
  }
}
