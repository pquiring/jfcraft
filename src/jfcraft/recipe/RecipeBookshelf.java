package jfcraft.recipe;

/** Makes bookshelf
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeBookshelf extends Recipe {
  public RecipeBookshelf() {
    super(3,3);
  }

  public Item make(Item items[]) {
    if (items[0].id != Blocks.PLANKS) return null;
    if (items[1].id != Blocks.PLANKS) return null;
    if (items[2].id != Blocks.PLANKS) return null;

    if (items[3].id != Items.BOOK) return null;
    if (items[4].id != Items.BOOK) return null;
    if (items[5].id != Items.BOOK) return null;

    if (items[6].id != Blocks.PLANKS) return null;
    if (items[7].id != Blocks.PLANKS) return null;
    if (items[8].id != Blocks.PLANKS) return null;

    return new Item(Blocks.BOOKSHELF);
  }
}
