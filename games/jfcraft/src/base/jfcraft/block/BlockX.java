package jfcraft.block;

/** Block with X pattern (flowers, etc.)
 *
 * @author pquiring
 *
 * Created : Mar 25, 2014
 */

import javaforce.gl.*;

import jfcraft.data.*;
import jfcraft.opengl.*;

import static jfcraft.data.Direction.*;

public class BlockX extends BlockBase {
  private static GLModel model;
  public BlockX(String id, String names[], String images[]) {
    super(id, names, images);
    isOpaque = false;
    isAlpha = false;
    isComplex = true;
    isSolid = false;
    if (model == null) {
      model = Assets.getModel("x").model;
    }
    resetBoxes(Type.BOTH);
  }
  public void buildBuffers(RenderDest dest, RenderData data) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    buildBuffers(model.getObject("X"), buf, data, getTexture(data));
  }
}
