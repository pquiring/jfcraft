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
  private static Model model;
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
  public void buildBuffers(RenderDest dest) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    SubTexture st;
    int idx = 0;
    if ((Static.data.var[X] & VAR_UPPER) == VAR_UPPER) {
      idx = (Static.data.var[X] & varMask) * 2;
    } else {
      idx = (Static.data.var[X] & varMask) * 2 + 1;
    }
    st = textures[idx];
    buildBuffers(model.getObject("X"), buf, st);
  }
  public boolean place(Client client, Coords c) {
    Coords c2 = c.clone();
    c2.y++;
    c2.gy++;
    int bits1 = Chunk.makeBits(0,c.var);
    int bits2 = Chunk.makeBits(0,c.var | VAR_UPPER);
    if (!c.chunk.setBlocksIfEmpty(c, id, bits1, c2, id, bits2)) return false;
    Static.server.broadcastSetBlock(c.chunk.dim,c.x,c.y,c.z,id,bits1);
    Static.server.broadcastSetBlock(c2.chunk.dim,c2.x,c2.y,c2.z,id,bits2);
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
  public void tick(Chunk chunk, Tick tick) {
    //this block can be replaced so check if other half is still there
    super.tick(chunk, tick);
    int y = tick.y;
    int bits = chunk.getBits(tick.x, tick.y, tick.z);
    int var = Chunk.getVar(bits);
    if ((var & VAR_UPPER) == VAR_UPPER) {
      y--;
    } else {
      y++;
    }

    if (chunk.getBlock(tick.x, y, tick.z) != id) {
      chunk.clearBlock(tick.x,tick.y,tick.z);
      Static.server.broadcastClearBlock(chunk.dim,chunk.cx * 16 + tick.x,tick.y,chunk.cz * 16 + tick.z);
    }
  }
}
