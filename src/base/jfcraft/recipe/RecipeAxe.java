package jfcraft.recipe;

/** Makes axe.
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeAxe extends Recipe {
  public RecipeAxe() {
    super(3,3);
  }

  public Item make(Item items[]) {
    char id = items[0].id;
    if (items[1].id != id) return null;
    if (items[2].id != id) return null;
    if (items[3].id != id) return null;

    if (items[4].id != Items.STICK) return null;
    if (items[5].id != Blocks.AIR) return null;

    if (items[6].id != Blocks.AIR) return null;
    if (items[7].id != Items.STICK) return null;
    if (items[8].id != Blocks.AIR) return null;

    if (id == Items.DIAMOND) return new Item(Items.DIAMOND_AXE, 1.0f);
    if (id == Items.GOLD_INGOT) return new Item(Items.GOLD_AXE, 1.0f);
    if (id == Items.IRON_INGOT) return new Item(Items.IRON_AXE, 1.0f);
    if (id == Blocks.COBBLESTONE) return new Item(Items.STONE_AXE, 1.0f);
    if (id == Blocks.PLANKS) return new Item(Items.WOOD_AXE, 1.0f);
    return null;
  }
}
