package jfcraft.recipe;

/** Makes door
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeDoor extends Recipe {
  public RecipeDoor() {
    super(2,3);
  }

  public Item make(Item items[]) {
    char id = items[0].id;
    for(int a=0;a<items.length;a++) {
      if (items[a].id != id) return null;
    }

    if (id == Items.IRON_INGOT) return new Item(Items.IRON_DOOR_ITEM);
    if (id == Blocks.PLANKS) return new Item(Items.WOOD_DOOR_ITEM);
    return null;
  }
}
