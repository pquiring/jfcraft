package jfcraft.block;

/** Full blocks WITH transparent parts (but no alpha blending)
 * ie: glass block, etc.
 *
 * @author pquiring
 *
 * Created : Mar 25, 2014
 */

import jfcraft.data.*;
import jfcraft.opengl.*;

public class BlockTrans extends BlockBase {
  public BlockTrans(String id, String names[], String images[]) {
    super(id, names, images);
    isOpaque = false;
    isAlpha = false;
    isComplex = true;
    isSolid = false;
  }
  public void addFace(RenderBuffers obj, RenderData data) {
    addFace(obj, data, textures[data.dir[data.side]]);
  }
  public void buildBuffers(RenderDest dest, RenderData data) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    for(int a=0;a<6;a++) {
      if (data.opaque[a]) continue;
      if (data.opaque[a]) continue;
      if (data.id[a] == id) continue;
      if (data.id[a] == id) continue;
      data.side = a;
      data.dirSide = a;
      addFace(buf,data);
    }
  }
}
