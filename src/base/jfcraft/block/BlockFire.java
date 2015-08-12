package jfcraft.block;

/** Fire
 *
 * @author pquiring
 *
 * Created : Mar 25, 2014
 */

import java.util.*;

import javaforce.gl.*;

import jfcraft.data.*;
import jfcraft.opengl.*;
import static jfcraft.data.Direction.*;
import static jfcraft.data.Types.*;

public class BlockFire extends BlockBase {
  private static GLModel model;
  public BlockFire(String id, String names[], String images[]) {
    super(id, names, images);
    isOpaque = false;
    isAlpha = false;
    isComplex = true;
    isSolid = false;
    isBlocks2 = true;
    canSelect = false;
    canReplace = true;
    dropBlock = "AIR";
    model = Assets.getModel("fire").model;
    resetBoxes(Type.BOTH);
  }
  public void buildBuffers(RenderDest dest, RenderData data) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    switch (data.dir2[X]) {
      case A:  //not used
        Static.log("BlockFire:dir==A???");
      case B:
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
  private Random r = new Random();
  public void rtick(Chunk chunk, int gx,int gy,int gz) {
    int bits = chunk.getBits2(gx, gy, gz);
    int dir = Chunk.getDir(bits);
    int x = chunk.cx * 16 + gx;
    int y = gy;
    int z = chunk.cz * 16 + gz;
    //spread fire to other dirs (75% chance)
    if (dir == B && r.nextInt(100) < 75) {
      int dx=0, dy=0, dz=0;
      int sdir = r.nextInt(6);
      switch (sdir) {
        case A: dy = 1; break;
        case B: dy = -1; break;
        case N: dz = -1; break;
        case E: dx = 1; break;
        case S: dz = 1; break;
        case W: dx = -1; break;
      }
      if (chunk.getBlock(gx + dx, gy + dy, gz + dz).material == MAT_WOOD) {
        int xbits = Chunk.makeBits(B, 0);
        chunk.clearBlock(gx + dx, gy + dy, gz + dz);
        Static.server.broadcastClearBlock(chunk.dim, x + dx, y + dy, z + dz);
        chunk.setBlock(gx + dx, gy + dy, gz + dz, id, xbits);  //blocks2
        Static.server.broadcastSetBlock(chunk.dim, x + dx, y + dy, z + dz, id, xbits);  //blocks2
      }
    }
    int dx=0, dy=0, dz=0;
    switch (dir) {
      case N: dz = -1; break;
      case E: dx = 1; break;
      case S: dz = 1; break;
      case W: dx = -1; break;
      case B: dy = -1; break;
    }
    if (dir == B) {
      //check this block too
      if (chunk.getBlock(gx, gy, gz).material == MAT_WOOD) {
        int xbits = Chunk.makeBits(B, 0);
        chunk.clearBlock(gx, gy, gz);
        Static.server.broadcastClearBlock(chunk.dim, x, y, z);
        chunk.setBlock(gx, gy, gz, id, xbits);  //blocks2
        Static.server.broadcastSetBlock(chunk.dim, x, y, z, id, xbits);  //blocks2
      }
    }
    //set block in direction to fire (if material == wood)
    if (chunk.getBlock(gx + dx, gy + dy, gz + dz).material == MAT_WOOD) {
      int xbits = Chunk.makeBits(B, 0);
      chunk.clearBlock(gx + dx, gy + dy, gz + dz);
      Static.server.broadcastClearBlock(chunk.dim, x + dx, y + dy, z + dz);
      chunk.setBlock(gx + dx, gy + dy, gz + dz, id, xbits);  //blocks2
      Static.server.broadcastSetBlock(chunk.dim, x + dx, y + dy, z + dz, id, xbits);  //blocks2
    }
    //put this fire out
    chunk.clearBlock2(gx, gy, gz);
    Static.server.broadcastClearBlock2(chunk.dim, x, y, z);
  }
}
