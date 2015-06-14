package jfcraft.recipe;

/** Makes block (coal, iron, gold, diamond, etc.)
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.data.*;
import jfcraft.item.*;

public class RecipeBlock3x3 extends Recipe {
  public RecipeBlock3x3() {
    super(3,3);
  }

  public Item make(Item items[]) {
    char id = items[0].id;
    for(int a=1;a<9;a++) {
      if (items[a].id != id) return null;
      if (items[a].var != 0) return null;  //charcoal doesn't work
    }
    if (id == Items.COAL) return new Item(Blocks.COAL_BLOCK);
    if (id == Items.RED_STONE_ITEM) return new Item(Blocks.REDSTONE_BLOCK);
    if (id == Items.IRON_INGOT) return new Item(Blocks.IRON_BLOCK);
    if (id == Items.GOLD_INGOT) return new Item(Blocks.GOLD_BLOCK);
    if (id == Items.DIAMOND) return new Item(Blocks.DIAMOND_BLOCK);
    if (id == Items.WHEAT_ITEM) return new Item(Blocks.HAYBALE);
    return null;
  }
}
