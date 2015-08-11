package jfcraft.recipe;

/** Makes lead.
 *
 * @author pquiring
 *
 * Created : Sept 23, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeLead extends Recipe {
  public RecipeLead() {
    super(3,3);
  }

  public Item make(Item items[]) {
    if (items[0].id == Blocks.AIR) {
//      if (items[0].id != Blocks.AIR) return null;
      if (items[1].id != Items.STRING) return null;
      if (items[2].id != Items.STRING) return null;

      if (items[3].id != Blocks.AIR) return null;
      if (items[4].id != Items.SLIME_BALL) return null;
      if (items[5].id != Items.STRING) return null;

      if (items[6].id != Items.STRING) return null;
      if (items[7].id != Blocks.AIR) return null;
      if (items[8].id != Blocks.AIR) return null;
    } else {
      if (items[0].id != Items.STRING) return null;
      if (items[1].id != Items.STRING) return null;
      if (items[2].id != Blocks.AIR) return null;

      if (items[3].id != Items.STRING) return null;
      if (items[4].id != Items.SLIME_BALL) return null;
      if (items[5].id != Blocks.AIR) return null;

      if (items[6].id != Blocks.AIR) return null;
      if (items[7].id != Blocks.AIR) return null;
      if (items[8].id != Items.STRING) return null;
    }

    return new Item(Items.LEAD);
  }
}
