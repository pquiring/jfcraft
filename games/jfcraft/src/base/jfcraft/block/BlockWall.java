package jfcraft.block;

import java.util.ArrayList;
import javaforce.gl.*;
import jfcraft.data.*;
import jfcraft.opengl.*;
import static jfcraft.data.Direction.*;

/**
 *
 * @author pquiring
 */

public class BlockWall extends BlockBase {
  private static GLModel model;
  public BlockWall(String id, String names[], String images[]) {
    super(id, names, images);
    isOpaque = false;
    hasShape = true;
    isComplex = true;
    isSolid = false;
    if (model == null) {
      model = Assets.getModel("wall").model;
    }
  }

  //dir bits
  private static final int NB = 0x1;
  private static final int EB = 0x2;
  private static final int SB = 0x4;
  private static final int WB = 0x8;

  private static final int N_S = NB | SB;
  private static final int E_W = EB | WB;

  public void buildBuffers(RenderDest dest, RenderData data) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    int dir = data.dir[X];
    SubTexture st = textures[0];
    if ((dir & NB) != 0) {
      buildBuffers(model.getObject("NORTH"), buf, data, st);
    }
    if ((dir & SB) != 0) {
      buildBuffers(model.getObject("SOUTH"), buf, data, st);
    }
    if ((dir & EB) != 0) {
      buildBuffers(model.getObject("EAST"), buf, data, st);
    }
    if ((dir & WB) != 0) {
      buildBuffers(model.getObject("WEST"), buf, data, st);
    }
    //do post if has a corner
    if (dir != N_S && dir != E_W) {
      buildBuffers(model.getObject("POST"), buf, data, st);
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
    if (c.block.isSolid || c.block.id == id) {
      shape |= NB;
    }
    world.getBlock(chunk.dim, x+1, y, z, c);
    if (c.block.isSolid || c.block.id == id) {
      shape |= EB;
    }
    world.getBlock(chunk.dim, x, y, z+1, c);
    if (c.block.isSolid || c.block.id == id) {
      shape |= SB;
    }
    world.getBlock(chunk.dim, x-1, y, z, c);
    if (c.block.isSolid || c.block.id == id) {
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
  public int getPreferredDir() {
    return EB | WB;
  }
}
