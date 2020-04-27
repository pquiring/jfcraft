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
import static jfcraft.data.Direction.*;

public class BlockOpaqueVarPerf extends BlockOpaque {
  public BlockOpaqueVarPerf(String id, String names[], String images[]) {
    super(id, names, images);
    isVar = true;
    isPerf = true;
    clampAlpha = true;
  }
  public SubTexture getTexture(RenderData data) {
    int idx = 0;
    if (isDir) {
      idx = data.var[data.side] & varMask;
    } else {
      idx = data.var[X] & varMask;
    }
    return textures[idx];
  }
  public void reloadAll() {
    for(int a=0;a<textures.length;a++) {
      textures[a].ai.reload(Settings.current.fancy);
    }
  }
}
