package jfcraft.block;

/** Blocks with 1 double-sided face on floor only (lillypad)
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

public class BlockFaceXZ extends BlockBase {
  private static Model model;
  public BlockFaceXZ(String id, String names[], String images[]) {
    super(id, names, images);
    isOpaque = false;
    isAlpha = false;
    isComplex = true;
    isSolid = false;
    isDir = true;
    isDirXZ = true;
    model = Assets.getModel("facexz").model;
  }

  public void buildBuffers(RenderDest dest, RenderData data) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    buildBuffers(model.getObject("FACE"), buf, data, textures[0]);
  }

  public ArrayList<Box> getBoxes(Coords c, Type type) {
    ArrayList<Box> list = new ArrayList<Box>();
    switch(c.dir) {
      case A: list.add(new Box( 0,15, 0, 16,16,16)); break;
      case B: list.add(new Box( 0, 0, 0, 16, 1,16)); break;
      case N: list.add(new Box( 0, 0, 0, 16,16, 1)); break;
      case E: list.add(new Box(15, 0, 0, 16,16,16)); break;
      case S: list.add(new Box( 0,15, 0, 16,16,16)); break;
      case W: list.add(new Box( 0, 0, 0,  1,16,16)); break;
    }
    return list;
  }
}
