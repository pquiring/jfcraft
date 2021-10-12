package jfcraft.block;

/** Thin blocks (carpet, etc.)
 *
 * @author pquiring
 *
 * Created : Mar 31, 2014
 */

import javaforce.gl.*;

import jfcraft.data.*;
import jfcraft.opengl.*;

public class BlockCarpet extends BlockBase {
  private static Model model;
  public BlockCarpet(String name, String names[], String images[]) {
    super(name, names, images);
    isOpaque = false;
    isAlpha = false;
    isComplex = true;
    isSolid = false;
    resetBoxes(Type.BOTH);
    addBox(0,0,0, 16,1,16,Type.SELECTION);
    if (model == null) {
      model = Assets.getModel("carpet").model;
    }
  }

  public void buildBuffers(RenderDest dest, RenderData data) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    buildBuffers(model.getObject("CARPET"), buf, data, getTexture(data));
  }
}
