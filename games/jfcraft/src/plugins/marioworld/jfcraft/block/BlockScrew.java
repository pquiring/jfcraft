package jfcraft.block;

/** Corner Screw
 *
 * @author pquiring
 */

public class BlockScrew extends BlockBase {
  public static char SCREW;
  public BlockScrew(String name, String names[], String images[]) {
    super(name, names, images);
  }
  public Class getIDClass() {
    return BlockScrew.class;
  }
}
