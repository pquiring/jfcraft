package jfcraft.recipe;

/** Makes pressure plate
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.data.*;
import jfcraft.item.*;

public class RecipePressurePlate extends Recipe {
  public RecipePressurePlate() {
    super(2,1);
  }

  public Item make(Item items[]) {
    if (items[0].id != items[1].id) return null;
    char id = items[0].id;
    if ((id == Blocks.COBBLESTONE) || (id == Blocks.STONE))
      return new Item(Blocks.PRESSURE_PLATE, (byte)1);
    if (id == Blocks.PLANKS)
      return new Item(Blocks.PRESSURE_PLATE);
    return null;
  }
}
