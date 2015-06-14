package jfcraft.recipe;

/** Makes pants
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipePants extends Recipe {
  public RecipePants() {
    super(3,3);
  }

  public Item make(Item items[]) {
    char id = items[0].id;
    if (items[1].id != id) return null;
    if (items[2].id != id) return null;

    if (items[3].id != id) return null;
    if (items[4].id != Blocks.AIR) return null;
    if (items[5].id != id) return null;

    if (items[6].id != id) return null;
    if (items[7].id != Blocks.AIR) return null;
    if (items[8].id != id) return null;

    if (id == Items.DIAMOND) return new Item(Items.DIAMOND_PANTS, 1.0f);
    if (id == Items.GOLD_INGOT) return new Item(Items.GOLD_PANTS, 1.0f);
    if (id == Items.IRON_INGOT) return new Item(Items.IRON_PANTS, 1.0f);
    if (id == Items.CHAIN) return new Item(Items.CHAIN_PANTS, 1.0f);
    if (id == Items.LEATHER) return new Item(Items.LEATHER_PANTS, 1.0f);
    return null;
  }
}
