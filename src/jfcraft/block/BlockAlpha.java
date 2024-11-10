package jfcraft.block;

/** Blocks with Alpha blending
 * ie:ice block
 *
 * @author pquiring
 *
 * Created : Mar 29, 2014
 */

import javaforce.*;

import jfcraft.data.*;
import jfcraft.opengl.*;

public class BlockAlpha extends BlockBase {
  public BlockAlpha(String name, String names[], String images[]) {
    super(name, names, images);
    isOpaque = false;
    isAlpha = true;
    canReplace = false;
  }
  public void buildBuffers(RenderDest dest) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    SubTexture st = getTexture();
    for(int a=0;a<6;a++) {
      if (Static.data.opaque[a]) continue;
      if (Static.data.id[a] == id && id != 0) continue;
      Static.data.side = a;
      Static.data.dirSide = a;
      Static.data.isDir = isDir;
      Static.data.isDirXZ = isDirXZ;
      addFace(buf,st);
    }
  }
}
