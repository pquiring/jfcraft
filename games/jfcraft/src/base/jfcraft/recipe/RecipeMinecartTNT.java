package jfcraft.recipe;

/** Makes minecart w/ TNT (mobile boom!)
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeMinecartTNT extends Recipe {
  public RecipeMinecartTNT() {
    super(1,2);
  }

  public Item make(Item items[]) {
    if (items[0].id != Items.MINECART) return null;
    if (items[1].id != Blocks.TNT) return null;
    return new Item(Items.MINECART_TNT);
  }
}
