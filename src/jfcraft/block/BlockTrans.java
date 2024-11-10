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
  public void addFace(RenderBuffers obj) {
    addFace(obj, textures[Static.data.dir[Static.data.side]]);
  }
  public void buildBuffers(RenderDest dest) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    for(int a=0;a<6;a++) {
      if (Static.data.opaque[a]) continue;
      if (Static.data.opaque[a]) continue;
      if (Static.data.id[a] == id) continue;
      if (Static.data.id[a] == id) continue;
      Static.data.side = a;
      Static.data.dirSide = a;
      Static.data.isDir = isDir;
      Static.data.isDirXZ = isDirXZ;
      addFace(buf);
    }
  }
}
