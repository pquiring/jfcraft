package jfcraft.block;

/**
 *
 * @author pquiring
 *
 * Created : Sep 11, 2014
 */

import java.util.ArrayList;
import static jfcraft.data.Direction.*;

import jfcraft.data.*;
import jfcraft.opengl.*;

public class BlockStairs extends BlockStep {
  public static int VAR_UPPER = 8;
  public BlockStairs(String id, String names[], String images[]) {
    super(id, names, images);
  }
  public void buildBuffers(RenderDest dest, RenderData data) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    /*
      -z
     0|1
    --|--x
     2|3
      y
     4|5
    -----x
     6|7
      +z
    */
    boolean q[] = null;
    if (data.var[X] == VAR_UPPER) {
      switch (data.dir[X]) {
        default:
          Static.log("BlockStairs with invalid dir:" + data.dir[X]);
          //no break
        case N:
          q = new boolean[] {true, true, true, true, true, true, false, false};
          break;
        case E:
          q = new boolean[] {true, true, false, true, true, true, false, true};
          break;
        case S:
          q = new boolean[] {true, true, false, false, true, true, true, true};
          break;
        case W:
          q = new boolean[] {true, true, true, false, true, true, true, false};
          break;
        case NE:
          q = new boolean[] {true, true, true, true, true, true, false, true};
          break;
        case NW:
          q = new boolean[] {true, true, true, true, true, true, true, false};
          break;
        case SE:
          q = new boolean[] {true, true, false, true, true, true, true, true};
          break;
        case SW:
          q = new boolean[] {true, true, true, false, true, true, true, true};
          break;
        case NE2:
          q = new boolean[] {true, true, false, true, true, true, false, false};
          break;
        case NW2:
          q = new boolean[] {true, true, true, false, true, false, false, false};
          break;
        case SE2:
          q = new boolean[] {true, true, false, false, true, true, false, true};
          break;
        case SW2:
          q = new boolean[] {true, true, false, false, true, true, true, false};
          break;
      }
    } else {
      switch (data.dir[X]) {
        default:
          Static.log("BlockStairs with invalid dir:" + (data.dir[X]));
          //no break
        case N:
          q = new boolean[] {true, true, true, true, false, false, true, true};
          break;
        case E:
          q = new boolean[] {false, true, true, true, false, true, true, true};
          break;
        case S:
          q = new boolean[] {false, false, true, true, true, true, true, true};
          break;
        case W:
          q = new boolean[] {true, false, true, true, true, false, true, true};
          break;
        case NE:
          q = new boolean[] {true, true, true, true, false, true, true, true};
          break;
        case NW:
          q = new boolean[] {true, true, true, true, true, false, true, true};
          break;
        case SE:
          q = new boolean[] {false, true, true, true, true, true, true, true};
          break;
        case SW:
          q = new boolean[] {true, false, true, true, true, true, true, true};
          break;
        case NE2:
          q = new boolean[] {false, true, true, true, false, false, true, true};
          break;
        case NW2:
          q = new boolean[] {true, false, true, true, false, false, true, true};
          break;
        case SE2:
          q = new boolean[] {false, false, true, true, false, true, true, true};
          break;
        case SW2:
          q = new boolean[] {false, false, true, true, true, false, true, true};
          break;
      }
    }
    data.isDir = false;  //do not allow rotation
    data.dir[X] = N;  //do not allow rotation
    SubTexture st = getTexture(data);
    for(int a=0;a<8;a++) {
      if (q[a]) {
        addQuad(buf, data, a, st);
      }
    }
  }

  public ArrayList<Box> getBoxes(Coords c, Type type) {
    ArrayList<Box> list = new ArrayList<Box>();
    int y1, y2;
    if (Chunk.getVar(c.bits) == VAR_UPPER) {
      list.add(new Box( 0, 8, 0,  16, 16, 16));
      y1 = 0;
      y2 = 8;
    } else {
      list.add(new Box( 0, 0, 0,  16, 8, 16));
      y1 = 8;
      y2 = 16;
    }
    switch (c.dir) {
      case N: list.add(new Box( 0, y1, 0,  16, y2,  8)); break;
      case E: list.add(new Box( 8, y1, 0,  16, y2, 16)); break;
      case S: list.add(new Box( 0, y1, 8,  16, y2, 16)); break;
      case W: list.add(new Box( 0, y1, 0,   8, y2, 16)); break;
      case NE:
        list.add(new Box( 0, y1, 0, 16, y2, 8));
        list.add(new Box( 8, y1, 0, 16, y2,16));
        break;
      case NW:
        list.add(new Box( 0, y1, 0, 16, y2, 8));
        list.add(new Box( 0, y1, 0,  8, y2,16));
        break;
      case SE:
        list.add(new Box( 0, y1, 8, 16, y2,16));
        list.add(new Box( 8, y1, 0, 16, y2,16));
        break;
      case SW:
        list.add(new Box( 0, y1, 8, 16, y2,16));
        list.add(new Box( 0, y1, 0,  8, y2,16));
        break;
      case NE2: list.add(new Box( 8, y1, 0, 16, y2, 8)); break;
      case NW2: list.add(new Box( 0, y1, 0,  8, y2, 8)); break;
      case SE2: list.add(new Box( 8, y1, 8, 16, y2,16)); break;
      case SW2: list.add(new Box( 0, y1, 8,  8, y2,16)); break;
    }
    return list;
  }
}
