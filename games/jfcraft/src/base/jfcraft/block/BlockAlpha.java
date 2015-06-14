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
    resetBoxes(Type.BOTH);
  }
  public void buildBuffers(RenderDest dest, RenderData data) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    SubTexture st = getTexture(data);
    for(int a=0;a<6;a++) {
      if (data.opaque[a]) continue;
      if (data.id[a] == id) continue;
      data.side = a;
      data.dirSide = a;
      addFace(buf,data,st);
    }
  }
}
