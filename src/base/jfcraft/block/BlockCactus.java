package jfcraft.block;

/** Cactus
 *
 * @author pquiring
 *
 */

import javaforce.gl.*;

import jfcraft.data.*;
import jfcraft.client.*;
import jfcraft.opengl.*;
import static jfcraft.data.Direction.*;

public class BlockCactus extends BlockBase {
  private static GLModel model;
  public BlockCactus(String name, String names[], String images[]) {
    super(name, names, images);
    isOpaque = false;
    isAlpha = false;
    isComplex = true;
    isSolid = false;
    if (model == null) {
      model = Assets.getModel("cactus").model;
    }
  }

  public void buildBuffers(RenderDest dest, RenderData data) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    buildBuffers(model.getObject("TOP"), buf, data, textures[0]);
    buildBuffers(model.getObject("SIDES"), buf, data, textures[1]);
    buildBuffers(model.getObject("BOTTOM"), buf, data, textures[2]);
  }

  public boolean place(Client client, Coords c) {
    //can only place on sand or another cactus
    //if placing on sand - make sure no other cactus is near
    if (c.gy == 0) return false;
    int bid = c.chunk.getID(c.gx, c.gy-1, c.gz);
    if (bid == Blocks.SAND) {
      //check if cactus near by
      if (c.chunk.getID(c.gx+1, c.gy, c.gz) == id) return false;
      if (c.chunk.getID(c.gx-1, c.gy, c.gz) == id) return false;
      if (c.chunk.getID(c.gx, c.gy, c.gz+1) == id) return false;
      if (c.chunk.getID(c.gx, c.gy, c.gz-1) == id) return false;
    } else if (bid == id) {
      //ok
    } else {
      return false;
    }
    c.chunk.setBlock(c.gx,c.gy,c.gz,id,0);
    Static.server.broadcastSetBlock(c.chunk.dim,c.x,c.y,c.z,id,0);
    return true;
  }

  private Coords c = new Coords();
  public void tick(Chunk chunk, Tick tick) {
    super.tick(chunk, tick);
    //is block below still sand or cactus?
    tick.toWorldCoords(chunk, c);
    c.block = chunk.getBlock(c.gx, c.gy-1, c.gz);
    int bid = c.block.id;
    if (bid == Blocks.SAND || bid == id) {
      return;
    }
    destroy(null, c, true);
  }
}
