package jfcraft.block;

/** Corner Screw
 *
 * @author pquiring
 */

import jfcraft.data.*;

public class BlockScrew extends BlockBase {
  public static char SCREW;
  public BlockScrew(String name, String names[], String images[]) {
    super(name, names, images);
  }
  public void getIDs(World world) {
    SCREW = world.getBlockID("SCREW");
  }
}
