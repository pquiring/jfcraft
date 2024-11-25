package jfcraft.recipe;

/** Makes Stone Slab
 *
 * TODO : other variants.
 *
 * @author pquiring
 *
 * Created : Nov 25, 2024
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeSlab extends Recipe {
  public RecipeSlab() {
    super(3,1);
  }

  public Item make(Item items[]) {
    char id = items[0].id;
    byte var = items[0].var;
    if (id != Blocks.STONE && id != Blocks.PLANKS) return null;
    if (items[1].id != id) return null;
    if (items[1].var != var) return null;
    if (items[2].id != id) return null;
    if (items[2].var != var) return null;
    return new Item(Blocks.STONE_SLAB, items[0].var, 6);
  }
}
