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

public class BlockPane extends BlockBase {
  private static GLModel model_half, model_full;
  public BlockPane(String id, String names[], String images[]) {
    super(id, names, images);
    isOpaque = false;
    hasShape = true;
    isComplex = true;
    isSolid = false;
    if (names.length > 1) {
      //colored glass panes
      isAlpha = true;
      isVar = true;
    }
    if (model_half == null) {
      model_half = Assets.getModel("pane_half").model;
    }
    if (model_full == null) {
      model_full = Assets.getModel("pane_full").model;
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
    if (dir == 0) dir = 0xf;
    SubTexture st1 = textures[data.var[X] * 2 + 0];  //faces
    SubTexture st2 = textures[data.var[X] * 2 + 1];  //top (sides)
    if ((dir & (N_S)) == N_S) {
        buildBuffers(model_full.getObject("NS_FACES"), buf, data, st1);
        buildBuffers(model_full.getObject("NS_SIDES"), buf, data, st2);
    } else {
      if ((dir & NB) != 0) {
        buildBuffers(model_half.getObject("N_FACES"), buf, data, st1);
        buildBuffers(model_half.getObject("N_SIDES"), buf, data, st2);
      }
      if ((dir & SB) != 0) {
        buildBuffers(model_half.getObject("S_FACES"), buf, data, st1);
        buildBuffers(model_half.getObject("S_SIDES"), buf, data, st2);
      }
    }
    if ((dir & E_W) == E_W) {
        buildBuffers(model_full.getObject("EW_FACES"), buf, data, st1);
        buildBuffers(model_full.getObject("EW_SIDES"), buf, data, st2);
    } else {
      if ((dir & EB) != 0) {
        buildBuffers(model_half.getObject("E_FACES"), buf, data, st1);
        buildBuffers(model_half.getObject("E_SIDES"), buf, data, st2);
      }
      if ((dir & WB) != 0) {
        buildBuffers(model_half.getObject("W_FACES"), buf, data, st1);
        buildBuffers(model_half.getObject("W_SIDES"), buf, data, st2);
      }
    }
  }

  public void setShape(Chunk chunk, int gx,int gy,int gz,boolean live) {
    int bits = chunk.getBits(gx,gy,gz);
    int dir = Chunk.getDir(bits);
    Coords c = Coords.alloc();
    int x = gx + chunk.cx * 16;
    int y = gy;
    int z = gz + chunk.cz * 16;
    World world = Static.server.world;
    int shape = 0;
    world.getBlock(chunk.dim, x, y, z-1, c);
    if (c.block.isSolid || c.block.id == id || c.block.id == Blocks.GLASS_PANE || c.block.id == Blocks.GLASS_PANE_COLOR) {
      shape |= NB;
    }
    world.getBlock(chunk.dim, x+1, y, z, c);
    if (c.block.isSolid || c.block.id == id || c.block.id == Blocks.GLASS_PANE || c.block.id == Blocks.GLASS_PANE_COLOR) {
      shape |= EB;
    }
    world.getBlock(chunk.dim, x, y, z+1, c);
    if (c.block.isSolid || c.block.id == id || c.block.id == Blocks.GLASS_PANE || c.block.id == Blocks.GLASS_PANE_COLOR) {
      shape |= SB;
    }
    world.getBlock(chunk.dim, x-1, y, z, c);
    if (c.block.isSolid || c.block.id == id || c.block.id == Blocks.GLASS_PANE || c.block.id == Blocks.GLASS_PANE_COLOR) {
      shape |= WB;
    }
    c.free();
    if (shape == 0) shape = 0xf;
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
}
