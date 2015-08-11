package jfcraft.recipe;

/** Makes Coin Block.
 *
 * @author pquiring
 *
 * Created : Jun 18, 2015
 */

import jfcraft.item.*;
import jfcraft.block.*;
import jfcraft.data.*;

public class RecipeCoinBlock extends Recipe {
  public RecipeCoinBlock() {
    super(3,3);
  }

  public Item make(Item items[]) {
    char planks = Blocks.PLANKS;
    char gold = Items.GOLD_NUGGET;
    if (items[0].id != planks) return null;
    if (items[1].id != planks) return null;
    if (items[2].id != planks) return null;

    if (items[3].id != planks) return null;
    if (items[4].id != gold) return null;
    if (items[5].id != planks) return null;

    if (items[6].id != planks) return null;
    if (items[7].id != planks) return null;
    if (items[8].id != planks) return null;

    return new Item(BlockCoinBlock.COIN_BLOCK, 0, 3);
  }
}
