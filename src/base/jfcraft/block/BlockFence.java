package jfcraft.block;

/** Fence
 *
 * Created : Apr 18, 2015
 *
 * @author pquiring
 */

import java.util.*;

import javaforce.gl.*;

import jfcraft.opengl.*;
import jfcraft.data.*;
import static jfcraft.data.Direction.*;

public class BlockFence extends BlockBase {
  private static Model model;
  public BlockFence(String id, String names[], String images[]) {
    super(id, names, images);
    isOpaque = false;
    isAlpha = false;
    hasShape = true;
    isComplex = true;
    isSolid = false;
    isVar = true;
    model = Assets.getModel("fence").model;
  }

  public void buildBuffers(RenderDest dest, RenderData data) {
    SubTexture st = getTexture(data);
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    buildBuffers(model.getObject("POST"), buf, data, st);
    int dir = data.dir[X];
    if ((dir & NB) != 0) {
      buildBuffers(model.getObject("N1"), buf, data, st);
      buildBuffers(model.getObject("N2"), buf, data, st);
    }
    if ((dir & EB) != 0) {
      buildBuffers(model.getObject("E1"), buf, data, st);
      buildBuffers(model.getObject("E2"), buf, data, st);
    }
    if ((dir & SB) != 0) {
      buildBuffers(model.getObject("S1"), buf, data, st);
      buildBuffers(model.getObject("S2"), buf, data, st);
    }
    if ((dir & WB) != 0) {
      buildBuffers(model.getObject("W1"), buf, data, st);
      buildBuffers(model.getObject("W2"), buf, data, st);
    }
  }

  public void setShape(Chunk chunk, int gx, int gy, int gz, boolean live, Coords c) {
    int bits = chunk.getBits(gx,gy,gz);
    int dir = Chunk.getDir(bits);
    int x = gx + chunk.cx * 16;
    int y = gy;
    int z = gz + chunk.cz * 16;
    World world = Static.server.world;
    int shape = 0;
    world.getBlock(chunk.dim, x, y, z-1, c);
    if (c.block.isSolid || c.block.id == id || c.block.id == Blocks.GATE) {
      shape |= NB;
    }
    world.getBlock(chunk.dim, x+1, y, z, c);
    if (c.block.isSolid || c.block.id == id || c.block.id == Blocks.GATE) {
      shape |= EB;
    }
    world.getBlock(chunk.dim, x, y, z+1, c);
    if (c.block.isSolid || c.block.id == id || c.block.id == Blocks.GATE) {
      shape |= SB;
    }
    world.getBlock(chunk.dim, x-1, y, z, c);
    if (c.block.isSolid || c.block.id == id || c.block.id == Blocks.GATE) {
      shape |= WB;
    }
    if (shape != dir) {
      bits = Chunk.makeBits(shape, Chunk.getVar(bits));
      chunk.setBits(gx,gy,gz, bits);
      if (live) {
        Static.server.broadcastSetBlock(chunk.dim, x, y, z, id, bits);
      }
    }
  }
  public ArrayList<Box> getBoxes(Coords c, Type type) {
    ArrayList<Box> list = new ArrayList<Box>();
    int dir = Chunk.getDir(c.bits);
    int x1 = 4, x2 = 12;
    int y1 = 0, y2 = 16;
    int z1 = 4, z2 = 12;
    if ((dir & NB) != 0) {
      z1 = 0;
    }
    if ((dir & EB) != 0) {
      x2 = 16;
    }
    if ((dir & SB) != 0) {
      z2 = 16;
    }
    if ((dir & WB) != 0) {
      x1 = 0;
    }
    list.add(new Box(x1,y1,z1, x2,y2,z2));
    return list;
  }
  public boolean canSupportBlock(Coords c) {
    return c.dir == B;
  }
  public int getPreferredDir() {
    return EB | WB;
  }
}
