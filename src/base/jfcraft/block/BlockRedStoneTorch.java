package jfcraft.block;

/** Redstone Torch
 *
 * @author pquiring
 *
 * Created : Mar 31, 2014
 */

import java.util.*;

import javaforce.*;
import javaforce.gl.*;
import jfcraft.client.*;

import jfcraft.data.*;
import jfcraft.opengl.*;
import static jfcraft.data.Direction.*;

public class BlockRedStoneTorch extends BlockBase {
  private static Model model;
  public BlockRedStoneTorch(String id, String names[], String images[]) {
    super(id, names, images);
    isOpaque = false;
    isAlpha = false;
    isComplex = true;
    isSolid = false;
    isRedstone = true;
    isDirFace = true;
    isSupported = true;
    model = Assets.getModel("torch").model;
  }

  public void buildBuffers(RenderDest dest, RenderData data) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    SubTexture st = getTexture(data);
    data.rotate = -45.0f;
    data.rotate2 = 0;
    switch (data.dir[X]) {
      case N:
        data.translate_pre = new float [] { 0.0f, 0.0f,-0.3f};
        break;
      case E:
        data.translate_pre = new float [] { 0.3f, 0.0f, 0.0f};
        break;
      case S:
        data.translate_pre = new float [] { 0.0f, 0.0f, 0.3f};
        break;
      case W:
        data.translate_pre = new float [] {-0.3f, 0.0f, 0.0f};
        break;
      case A:
//        Static.log("RedStoneTorch with invalid dir:A:@" + data.x + "," + data.y + "," + data.z);
//        break;
      case B:
        data.rotate = 0;  //do not rotate
        break;
    }
    buildBuffers(model.getObject("TORCH"), buf, data, st);
    data.rotate = 90.0f;
    data.rotate2 = 90.0f;
    data.translate_pre = null;
  }
  public int getPowerLevel(Coords c, Coords from) {return 16;}
  public boolean place(Client client, Coords c) {
    if (c.dir == A) return false;  //can not place torch on ceiling
    if (!super.place(client, c)) return false;
    Static.server.world.powerChanged(c.chunk.dim, c.x, c.y, c.z);
    return true;
  }
  public void destroy(Client client, Coords c, boolean doDrop) {
    super.destroy(client, c, doDrop);
    Static.server.world.powerChanged(c.chunk.dim, c.x, c.y, c.z);
  }
  public ArrayList<Box> getBoxes(Coords c, Type type) {
    ArrayList<Box> list = new ArrayList<Box>();
    if (type == Type.ENTITY) return list;
    int bits = c.chunk.getBits(c.gx, c.gy, c.gz);
    int dir = Chunk.getDir(bits);
    switch(dir) {
      case B: list.add(new Box( 6, 0, 6, 10,11,10)); break;
      case N: list.add(new Box( 6, 3, 0, 10,14, 8)); break;
      case E: list.add(new Box( 8, 3, 6, 16,14,10)); break;
      case S: list.add(new Box( 6, 3, 8, 10,14,16)); break;
      case W: list.add(new Box( 0, 3, 6,  8,14,10)); break;
    }
    return list;
  }
}
