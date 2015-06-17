package jfcraft.recipe;

/** Makes dominos from 2 clays stacked vertical.
 *
 * @author pquiring
 */

import jfcraft.block.*;
import jfcraft.data.*;
import jfcraft.item.*;

public class RecipeDomino extends Recipe {
  public RecipeDomino() {
    super(1,2);
  }

  public Item make(Item items[]) {
    for(int a=0;a<2;a++) {
      if (items[a].id != Blocks.CLAY) return null;
    }
    return new Item(BlockDomino.DOMINO, (byte)0, (byte)16);
  }
}
