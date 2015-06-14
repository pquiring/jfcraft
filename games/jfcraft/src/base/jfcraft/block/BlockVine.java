package jfcraft.block;

/**
 *
 * @author pquiring
 */

import java.util.ArrayList;

import javaforce.gl.*;

import jfcraft.client.*;
import jfcraft.data.*;
import jfcraft.opengl.*;
import static jfcraft.data.Direction.*;

public class BlockVine extends BlockBase {
  private static GLModel model;
  public BlockVine(String id, String names[], String images[]) {
    super(id, names, images);
    isOpaque = false;
    isAlpha = false;
    isComplex = true;
    isSolid = false;
    isDirFace = true;
    resetBoxes(Type.BOTH);
    addBox(0,0,0, 16,1,16,Type.SELECTION);
    if (model == null) {
      model = Assets.getModel("face").model;
    }
    //NOTE : this block is NOT Supported but requires support to place()
  }

  public void buildBuffers(RenderDest dest, RenderData data) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    buildBuffers(model.getObject("FACE"), buf, data, getTexture(data));
  }

  public void rtick(Chunk chunk, int x,int y,int z) {
    int bits = chunk.getBits(x, y, z);
    int dir = Chunk.getDir(bits);
    if (dir == A || dir == B) return;
    if (y == 0) return;
    y--;
    synchronized(chunk) {
      if (chunk.isEmpty(x, y, z)) {
        chunk.setBlock(x, y, z, id, bits);
        x += chunk.cx * 16;
        z += chunk.cz * 16;
        Static.server.broadcastSetBlock(chunk.dim, x, y, z, id, bits);
      }
    }
  }

  public ArrayList<Box> getBoxes(Coords c, Type type) {
    ArrayList<Box> list = new ArrayList<Box>();
    int bits = c.chunk.getBits(c.gx, c.gy, c.gz);
    int dir = Chunk.getDir(bits);
    switch(dir) {
      case A: list.add(new Box( 0,15, 0, 16,16,16)); break;
      case B: list.add(new Box( 0, 0, 0, 16, 1,16)); break;
      case N: list.add(new Box( 0, 0, 0, 16,16, 1)); break;
      case E: list.add(new Box(15, 0, 0, 16,16,16)); break;
      case S: list.add(new Box( 0, 0,15, 16,16,16)); break;
      case W: list.add(new Box( 0, 0, 0,  1,16,16)); break;
    }
    return list;
  }

  public boolean place(Client client, Coords c) {
    if (c.y == 0 || c.y == 255) return false;
    Coords s = c.clone();
    s.adjacentBlock();
    Static.server.world.getBlock(c.chunk.dim, s.x, s.y, s.z, s);
    if (!s.block.isSolid) {
      return false;
    }
    return super.place(client, c);
  }
}
