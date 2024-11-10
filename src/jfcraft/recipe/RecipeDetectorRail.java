package jfcraft.recipe;

/** Makes powered rail
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeDetectorRail extends Recipe {
  public RecipeDetectorRail() {
    super(3,3);
  }

  public Item make(Item items[]) {
    if (items[0].id != Items.IRON_INGOT) return null;
    if (items[1].id != Blocks.AIR) return null;
    if (items[2].id != Items.IRON_INGOT) return null;

    if (items[3].id != Items.IRON_INGOT) return null;
    if (items[4].id != Blocks.PRESSURE_PLATE) return null;
    if (items[4].var != 1) return null;
    if (items[5].id != Items.IRON_INGOT) return null;

    if (items[6].id != Items.IRON_INGOT) return null;
    if (items[7].id != Items.RED_STONE_ITEM) return null;
    if (items[8].id != Items.IRON_INGOT) return null;

    return new Item(Blocks.RAIL_DETECTOR, (byte)0, (byte)6);
  }
}
