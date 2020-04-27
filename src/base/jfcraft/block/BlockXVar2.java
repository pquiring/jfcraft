package jfcraft.block;

/** Block with X pattern w/variables 2 blocks high
 *
 * @author pquiring
 *
 * Created : Mar 25, 2014
 */

import jfcraft.data.*;
import jfcraft.opengl.*;

public class BlockXVar2 extends BlockX2 {
  public BlockXVar2(String id, String names[], String images[]) {
    super(id, names, images);
    isOpaque = false;
    isAlpha = false;
    isVar = true;
    isComplex = true;
    isSolid = false;
    resetBoxes(Type.BOTH);
  }
}
