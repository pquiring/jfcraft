package jfcraft.data;

/** Inventory slot to be rendered.
 *
 * @author pquiring
 */

import jfcraft.item.*;

public class Slot {
  public Item item;
  public int x,y;  //bottom left pixel
  public boolean renderName;
}
