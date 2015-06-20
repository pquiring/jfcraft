package jfcraft.block;

/** Fire
 *
 * @author pquiring
 *
 * Created : Mar 25, 2014
 */

import javaforce.gl.*;

import jfcraft.data.*;
import jfcraft.item.Item;
import jfcraft.opengl.*;
import static jfcraft.data.Direction.*;

public class BlockFire extends BlockBase {
  private static GLModel model;
  public BlockFire(String id, String names[], String images[]) {
    super(id, names, images);
    isOpaque = false;
    isAlpha = false;
    isComplex = true;
    isSolid = false;
    canSelect = false;
    dropBlock = "AIR";
    model = Assets.getModel("fire").model;
    resetBoxes(Type.BOTH);
  }
  public void buildBuffers(RenderDest dest, RenderData data) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    switch (data.dir[X]) {
      case A:
      case B:  //not used I think
      case X:
        buildBuffers(model.getObject("FIRE"), buf, data, textures[0]);
        return;
      case N:
        buildBuffers(model.getObject("NORTH"), buf, data, textures[0]);
        return;
      case E:
        buildBuffers(model.getObject("EAST"), buf, data, textures[0]);
        return;
      case S:
        buildBuffers(model.getObject("SOUTH"), buf, data, textures[0]);
        return;
      case W:
        buildBuffers(model.getObject("WEST"), buf, data, textures[0]);
        return;
    }
  }
  public void rtick(Chunk chunk, int gx,int gy,int gz) {
    int bits = chunk.getBits(gx, gy, gz);
    int dir = Chunk.getDir(bits);
    int x = chunk.cx * 16 + gx;
    int y = gy;
    int z = chunk.cz * 16 + gz;
    //TODO : spread fire to adj blocks (50% chance ???)
    int dx=0,dy=0,dz=0;
    if (dir != X) {
      switch (dir) {
        case N: dz = -1; break;
        case E: dx = 1; break;
        case S: dz = 1; break;
        case W: dx = -1; break;
      }
      //destroy block in direction (if isWooden)
      if (chunk.getBlock(gx + dx, gy + dy, gz + dz).isWooden) {
        int xbits = Chunk.makeBits(X, 0);
        chunk.setBlock(gx + dx, gy + dy, gz + dz, id, xbits);
        Static.server.broadcastSetBlock(chunk.dim, x + dx, y, z + dz, id, xbits);
      }
    }
    //put fire out
    chunk.setBlock(gx, gy, gz, Blocks.AIR, 0);
    Static.server.broadcastSetBlock(chunk.dim, x, y, z, Blocks.AIR, 0);
  }
}
