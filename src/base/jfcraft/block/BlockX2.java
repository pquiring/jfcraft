package jfcraft.block;

/** Block with X pattern 2 blocks high (tallgrass)
 *
 * @author pquiring
 *
 * Created : Mar 25, 2014
 */

import javaforce.*;
import javaforce.gl.*;

import jfcraft.data.*;
import jfcraft.opengl.*;
import jfcraft.client.*;

import static jfcraft.data.Direction.*;
import static jfcraft.data.Blocks.*;

public class BlockX2 extends BlockBase {
  private static GLModel model;
  public BlockX2(String id, String names[], String images[]) {
    super(id, names, images);
    isOpaque = false;
    isAlpha = false;
    isComplex = true;
    isSolid = false;
    varMask = 0x7;  //remove VAR_UPPER
    if (model == null) {
      model = Assets.getModel("x").model;
    }
    resetBoxes(Type.BOTH);
  }
  public void buildBuffers(RenderDest dest, RenderData data) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    SubTexture st;
    int idx = 0;
    if ((data.var[X] & VAR_UPPER) == VAR_UPPER) {
      idx = (data.var[X] & varMask) * 2;
    } else {
      idx = (data.var[X] & varMask) * 2 + 1;
    }
    if (idx >= textures.length) {
      JFLog.log("Debug:Error:invalid var:var=" + data.var[X] + ",name=" + getName());
      idx = 0;
    }
    st = textures[idx];
    buildBuffers(model.getObject("X"), buf, data, st);
  }
  public boolean place(Client client, Coords c) {
    Coords c2 = c.clone();
    c2.y++;
    c2.gy++;
    synchronized(c.chunk.lock) {
      if (c.chunk.getID(c.gx, c.gy, c.gz) != 0) return false;
      if (c.chunk.getID(c2.gx, c2.gy, c2.gz) != 0) return false;
      int bits1 = Chunk.makeBits(0,c.var);
      c.chunk.setBlock(c.gx,c.gy,c.gz,id,bits1);
      Static.server.broadcastSetBlock(c.chunk.dim,c.x,c.y,c.z,id,bits1);
      int bits2 = Chunk.makeBits(0,c.var | VAR_UPPER);
      c2.chunk.setBlock(c2.gx,c2.gy,c2.gz,id,bits2);
      Static.server.broadcastSetBlock(c2.chunk.dim,c2.x,c2.y,c2.z,id,bits2);
    }
    return true;
  }
  public void destroy(Client client, Coords c, boolean doDrop) {
    int bits = c.chunk.getBits(c.gx, c.gy, c.gz);
    int var = Chunk.getVar(bits);
    super.destroy(client, c, doDrop);
    if ((var & VAR_UPPER) == VAR_UPPER) {
      c.y--;
      c.gy--;
    } else {
      c.y++;
      c.gy++;
    }
    c.chunk.clearBlock(c.gx,c.gy,c.gz);
    Static.server.broadcastClearBlock(c.chunk.dim,c.x,c.y,c.z);
  }
}
