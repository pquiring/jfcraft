package jfcraft.recipe;

/** Makes bed
 *
 * @author pquiring
 *
 * Created : May 6, 2014
 */

import jfcraft.item.*;
import jfcraft.data.*;

public class RecipeSolarPanel extends Recipe {
  public RecipeSolarPanel() {
    super(3,3);
  }

  public Item make(Item items[]) {
    if (items[0].id != Blocks.GLASSBLOCK) return null;
    if (items[1].id != Blocks.GLASSBLOCK) return null;
    if (items[2].id != Blocks.GLASSBLOCK) return null;

    if (items[3].id != Items.QUARTZ) return null;
    if (items[4].id != Items.QUARTZ) return null;
    if (items[5].id != Items.QUARTZ) return null;

    if (items[6].id != Blocks.SLAB) return null;
    if (items[7].id != Blocks.SLAB) return null;
    if (items[8].id != Blocks.SLAB) return null;

    return new Item(Blocks.SOLAR_PANEL);
  }
}
