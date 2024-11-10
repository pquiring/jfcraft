package jfcraft.block;

/** Torch
 *
 * @author pquiring
 *
 * Created : Mar 31, 2014
 */

import java.util.ArrayList;

import javaforce.gl.*;

import jfcraft.client.*;
import jfcraft.data.*;
import jfcraft.opengl.*;
import static jfcraft.data.Direction.*;

public class BlockTorch extends BlockBase {
  private static Model model;
  private static ArrayList<Box> lb = new ArrayList<Box>();
  private static ArrayList<Box> ln = new ArrayList<Box>();
  private static ArrayList<Box> le = new ArrayList<Box>();
  private static ArrayList<Box> ls = new ArrayList<Box>();
  private static ArrayList<Box> lw = new ArrayList<Box>();
  public BlockTorch(String id, String names[], String images[]) {
    super(id, names, images);
    isOpaque = false;
    isAlpha = false;
    isComplex = true;
    isSolid = false;
    isDirFace = true;
    isSupported = true;
    if (model == null) {
      model = Assets.getModel("torch").model;
      lb.add(new Box( 6, 0, 6, 10,11,10));
      ln.add(new Box( 6, 3, 0, 10,14, 8));
      le.add(new Box( 8, 3, 6, 16,14,10));
      ls.add(new Box( 6, 3, 8, 10,14,16));
      lw.add(new Box( 0, 3, 6,  8,14,10));
    }
  }

  public void buildBuffers(RenderDest dest) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    SubTexture st = getTexture();
    Static.data.rotate = -45.0f;
    Static.data.rotate2 = 0;
    switch (Static.data.dir[X]) {
      case N:
        Static.data.translate_pre = new float [] { 0.0f, 0.0f,-0.3f};
        break;
      case E:
        Static.data.translate_pre = new float [] { 0.3f, 0.0f, 0.0f};
        break;
      case S:
        Static.data.translate_pre = new float [] { 0.0f, 0.0f, 0.3f};
        break;
      case W:
        Static.data.translate_pre = new float [] {-0.3f, 0.0f, 0.0f};
        break;
      case A:
        Static.log("Torch with invalid dir:A:@" + Static.data.x + "," + Static.data.y + "," + Static.data.z);
        break;
      case B:
        Static.data.rotate = 0;  //do not rotate
        break;
    }
    buildBuffers(model.getObject("TORCH"), buf, st);
    Static.data.resetRotate();
  }
  public boolean place(Client client, Coords c) {
    if (c.dir == A) return false;  //can not place torch on ceiling
    return super.place(client, c);
  }
  public ArrayList<Box> getBoxes(Coords c, Type type) {
    if (type == Type.ENTITY) return boxListEmpty;
    int bits = c.chunk.getBits(c.gx, c.gy, c.gz);
    int dir = Chunk.getDir(bits);
    switch(dir) {
      case B: return lb;
      case N: return ln;
      case E: return le;
      case S: return ls;
      case W: return lw;
    }
    return null;
  }
}
