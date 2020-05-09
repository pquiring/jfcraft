package jfcraft.block;

/** Kelp Plant
 *
 * @author pquiring
 *
 * Created : May 8, 2020
 */

import javaforce.gl.*;

import jfcraft.data.*;
import jfcraft.opengl.*;

import static jfcraft.data.Direction.*;
import static jfcraft.data.Types.*;
import jfcraft.client.*;
import jfcraft.entity.*;
import jfcraft.item.*;

public class BlockKelpPlant extends BlockBase {
  private static GLModel model;
  public static byte VAR_TOP = 1;
  public BlockKelpPlant(String id, String names[], String images[]) {
    super(id, names, images);
    isOpaque = false;
    isAlpha = false;
    isComplex = true;
    isSolid = false;
    isVar = true;
    if (model == null) {
      model = Assets.getModel("x").model;
    }
    resetBoxes(Type.BOTH);
  }
  public void buildBuffers(RenderDest dest, RenderData data) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    buildBuffers(model.getObject("X"), buf, data, getTexture(data));
  }

  private static Coords thisBlock = new Coords();

  public void tick(Chunk chunk, Tick tick) {
    tick.toWorldCoords(chunk, thisBlock);
    if (tick.t1 > 0) {
      tick.t1++;
      if (tick.t1 == 5) {
        destroy(null, thisBlock, true);
        chunk.delTick(tick);
      }
      return;
    }
    BlockBase supporting = chunk.getBlock(tick.x, tick.y-1, tick.z);
    char support_id = supporting.id;
    if (support_id != Blocks.KELPPLANT && support_id != Blocks.DIRT && support_id != Blocks.SAND && support_id != Blocks.CLAY) {
      tick.t1 = 1;
    } else {
      int bits = chunk.getBits(tick.x, tick.y, tick.z);
      int var = Chunk.getVar(bits);
      if (var == 0) {
        BlockBase above = chunk.getBlock(tick.x, tick.y+1, tick.z);
        char above_id = above.id;
        if (above_id != Blocks.KELPPLANT) {
          //this is now top
          chunk.setBits(tick.x, tick.y, tick.z, VAR_TOP);
          Static.server.broadcastSetBlock(chunk.dim, thisBlock.x, thisBlock.y, thisBlock.z, id, VAR_TOP);
        }
      }
      chunk.delTick(tick);
    }
  }
}
