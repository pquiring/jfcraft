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
  public BlockOpaqueVarPerf(String id, String names[], String images[]) {
    super(id, names, images);
    isVar = true;
    isPerf = true;
    clampAlpha = true;
  }
  public SubTexture getTexture(RenderData data) {
    return textures[data.var[data.side]];
  }
  public void reloadAll() {
    for(int a=0;a<textures.length;a++) {
      textures[a].ai.reload(Settings.current.fancy);
    }
  }
}
