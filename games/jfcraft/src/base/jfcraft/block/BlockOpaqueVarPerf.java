package jfcraft.block;

/** Block Solid with Variations (performance : sometimes transparent)
 * ie: leaves
 *
 * @author pquiring
 *
 * Created : Mar 29, 2014
 */

import jfcraft.data.*;
import jfcraft.opengl.*;

public class BlockOpaqueVarPerf extends BlockOpaque {
  public BlockOpaqueVarPerf(String id, String names[], String images[], String images_opaque[]) {
    super(id, names, images);
    this.images2 = images_opaque;
    isVar = true;
    isPerf = true;
  }
  public void addFace(RenderBuffers obj, RenderData data) {
    if (Settings.current.isFancy)
      addFace(obj, data, textures[data.dir[data.side]]);
    else
      addFace(obj, data, textures2[data.dir[data.side]]);
  }
}
