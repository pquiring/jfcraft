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
    if (items[1].id != id) return null;
    if (items[1].var != var) return null;
    if (items[2].id != id) return null;
    if (items[2].var != var) return null;
    if (id == Blocks.PLANKS) return new Item(Blocks.WOOD_SLAB, var, 6);
    if (var != 0) return null;
    if (id == Blocks.STONE) return new Item(Blocks.STONE_SLAB, 0, 6);
    if (id == Blocks.COBBLESTONE) return new Item(Blocks.COBBLESTONE_SLAB, 0, 6);
    if (id == Blocks.STONE_BRICKS) return new Item(Blocks.STONE_BRICKS_SLAB, 0, 6);
    return null;
  }
}
