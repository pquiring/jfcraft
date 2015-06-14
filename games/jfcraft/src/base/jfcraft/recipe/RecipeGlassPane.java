package jfcraft.recipe;

/** Makes glass pane
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeGlassPane extends Recipe {
  public RecipeGlassPane() {
    super(3,2);
  }

  public Item make(Item items[]) {
    for(int a=0;a<6;a++) {
      if (items[a].id != Blocks.GLASSBLOCK) return null;
    }

    return new Item(Blocks.GLASS_PANE);
  }
}
