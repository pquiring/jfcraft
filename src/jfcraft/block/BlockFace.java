package jfcraft.block;

/** Blocks with 1 double-sided face (vines, etc.)
 *
 * @author pquiring
 *
 * Created : Mar 31, 2014
 */

import java.util.*;

import javaforce.*;
import javaforce.gl.*;

import jfcraft.data.*;
import jfcraft.opengl.*;
import static jfcraft.data.Direction.*;

public class BlockFace extends BlockBase {
  private static Model model;
  public BlockFace(String id, String names[], String images[]) {
    super(id, names, images);
    isOpaque = false;
    isAlpha = false;
    isComplex = true;
    isSolid = false;
    isDir = true;
    model = Assets.getModel("face").model;
  }

  public void buildBuffers(RenderDest dest) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    buildBuffers(model.getObject("FACE"), buf, textures[0]);
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
}
