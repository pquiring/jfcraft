package jfcraft.recipe;

/** Makes iron bars
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeWall extends Recipe {
  public RecipeWall() {
    super(3,2);
  }

  public Item make(Item items[]) {
    for(int a=0;a<6;a++) {
      if (items[a].id != Blocks.COBBLESTONE) return null;
    }

    return new Item(Blocks.WALL);
  }
}
