package jfcraft.recipe;

/** Makes cookie
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.data.*;
import jfcraft.item.*;
import static jfcraft.data.Types.*;

public class RecipeCookie extends Recipe {
  public RecipeCookie() {
    super(3,1);
  }

  public Item make(Item items[]) {
    if (items[0].id != Items.WHEAT_ITEM) return null;
    if (items[1].id != Items.COLOR) return null;
    if (items[1].var != Items.VAR_COCOA) return null;
    if (items[2].id != Items.WHEAT_ITEM) return null;
    return new Item(Items.BREAD);
  }
}
