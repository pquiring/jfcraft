package jfcraft.recipe;

/** Makes helmet
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeHelmet extends Recipe {
  public RecipeHelmet() {
    super(3,2);
  }

  public Item make(Item items[]) {
    char id = items[0].id;
    if (items[1].id != id) return null;
    if (items[2].id != id) return null;

    if (items[3].id != id) return null;
    if (items[4].id != Blocks.AIR) return null;
    if (items[5].id != id) return null;

    if (id == Items.DIAMOND) return new Item(Items.DIAMOND_HELMET, 1.0f);
    if (id == Items.GOLD_INGOT) return new Item(Items.GOLD_HELMET, 1.0f);
    if (id == Items.IRON_INGOT) return new Item(Items.IRON_HELMET, 1.0f);
    if (id == Items.CHAIN) return new Item(Items.CHAIN_HELMET, 1.0f);
    if (id == Items.LEATHER) return new Item(Items.LEATHER_CAP, 1.0f);
    return null;
  }
}
