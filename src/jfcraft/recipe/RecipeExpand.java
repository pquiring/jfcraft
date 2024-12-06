package jfcraft.recipe;

/** Makes expands blocks to 9 or 4 items
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeExpand extends Recipe {
  public RecipeExpand() {
    super(1,1);
  }

  public Item make(Item items[]) {
    if (items[0].id == Blocks.COAL_BLOCK) return new Item(Items.COAL, (byte)0, (byte)9);
    if (items[0].id == Blocks.REDSTONE_BLOCK) return new Item(Items.RED_STONE_ITEM, (byte)0, (byte)9);
    if (items[0].id == Blocks.IRON_BLOCK) return new Item(Items.IRON_INGOT, (byte)0, (byte)9);
    if (items[0].id == Blocks.GOLD_BLOCK) return new Item(Items.GOLD_INGOT, (byte)0, (byte)9);
    if (items[0].id == Blocks.DIAMOND_BLOCK) return new Item(Items.DIAMOND, (byte)0, (byte)9);
    if (items[0].id == Blocks.NETHER_QUARTZ_BLOCK) return new Item(Items.QUARTZ, (byte)0, (byte)4);
    if (items[0].id == Blocks.HAYBALE) return new Item(Items.WHEAT_ITEM, (byte)0, (byte)9);
    return null;
  }
}
