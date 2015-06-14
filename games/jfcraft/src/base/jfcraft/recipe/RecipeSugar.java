package jfcraft.recipe;

/** Makes sugar
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeSugar extends Recipe {
  public RecipeSugar() {
    super(1,1);
  }

  public Item make(Item items[]) {
    if (items[0].id != Items.SUGAR_CANE) return null;
    return new Item(Items.SUGAR);
  }
}
