package jfcraft.block;

/** Step blocks
 *
 * @author pquiring
 *
 * Created : Mar 31, 2014
 */

import java.util.ArrayList;

import jfcraft.data.*;
import jfcraft.opengl.*;
import jfcraft.client.*;
import static jfcraft.data.Direction.*;

public class BlockStep extends BlockBase {
  public BlockStep(String id, String names[], String images[]) {
    super(id, names, images);
    isOpaque = false;
    isAlpha = false;
    hasShape = true;
    isComplex = true;
    isSolid = false;
    isDir = true;
    isDirXZ = true;
    isVar = true;
    varMask = 0x7;  //remove VAR_UPPER
//    setGreenTop();  //test
//    dropID = 0;
  }

  public static final int VAR_UPPER = 8;

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
          Static.log("BlockStep with invalid dir:" + data.dir[X]);
          //no break
        case N:
          q = new boolean[] {true, true, false, false, false, false, false, false};
          break;
        case E:
          q = new boolean[] {false, true, false, false, false, true, false, false};
          break;
        case S:
          q = new boolean[] {false, false, false, false, true, true, false, false};
          break;
        case W:
          q = new boolean[] {true, false, false, false, true, false, false, false};
          break;
        case NE:
          q = new boolean[] {true, true, false, false, false, true, false, false};
          break;
        case NW:
          q = new boolean[] {true, true, false, false, true, false, false, false};
          break;
        case SE:
          q = new boolean[] {false, true, false, false, true, true, false, false};
          break;
        case SW:
          q = new boolean[] {true, false, false, false, true, true, false, false};
          break;
        case NE2:
          q = new boolean[] {false, true, false, false, false, false, false, false};
          break;
        case NW2:
          q = new boolean[] {false, false, true, false, false, false, false, false};
          break;
        case SE2:
          q = new boolean[] {false, false, false, false, false, false, false, true};
          break;
        case SW2:
          q = new boolean[] {false, false, false, false, false, false, true, false};
          break;
      }
    } else {
      switch (data.dir[X]) {
        default:
          Static.log("BlockStep with invalid dir:" + data.dir[X]);
        case N:
          q = new boolean[] {false, false, true, true, false, false, false, false};
          break;
        case E:
          q = new boolean[] {false, false, false, true, false, false, false, true};
          break;
        case S:
          q = new boolean[] {false, false, false, false, false, false, true, true};
          break;
        case W:
          q = new boolean[] {false, false, true, false, false, false, true, false};
          break;
        case NE:
          q = new boolean[] {false, false, true, true, false, false, false, true};
          break;
        case NW:
          q = new boolean[] {false, false, true, true, false, false, true, false};
          break;
        case SE:
          q = new boolean[] {false, false, false, true, false, false, true, true};
          break;
        case SW:
          q = new boolean[] {false, false, true, false, false, false, true, true};
          break;
        case NE2:
          q = new boolean[] {false, false, false, true, false, false, false, false};
          break;
        case NW2:
          q = new boolean[] {false, false, true, false, false, false, false, false};
          break;
        case SE2:
          q = new boolean[] {false, false, false, false, false, false, false, true};
          break;
        case SW2:
          q = new boolean[] {false, false, false, false, false, false, true, false};
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

  public void setShape(Chunk chunk, int gx,int gy,int gz,boolean live) {
    int bits = chunk.getBits(gx,gy,gz);
    int dir = Chunk.getDir(bits);
    int shape = dir;
    int x = gx + chunk.cx * 16;
    int y = gy;
    int z = gz + chunk.cz * 16;
    World world = Static.server.world;
    Coords c = Coords.alloc();
    do {  //just need a good break out
      if (dir == N || dir == NE || dir == NW || dir == NE2 || dir == NW2) {
        world.getBlock(chunk.dim, x, y, z+1, c);
        if (c.block.id == id) {
          if (c.dir == E || c.dir == SE || c.dir == NE2) {
            shape = NE;
            break;
          } else if (c.dir == W || c.dir == SW || c.dir == NW2) {
            shape = NW;
            break;
          }
        }
        world.getBlock(chunk.dim, x, y, z-1, c);
        if (c.block.id == id) {
          if (c.dir == E || c.dir == NE || c.dir == SE2) {
            shape = NE2;
            break;
          } else if (c.dir == W || c.dir == NW || c.dir == SW2) {
            shape = NW2;
            break;
          }
        }
      }
      if (dir == S || dir == SE || dir == SW || dir == SE2 || dir == SW2) {
        world.getBlock(chunk.dim, x, y, z-1, c);
        if (c.block.id == id) {
          if (c.dir == E || c.dir == NE || c.dir == SE2) {
            shape = SE;
            break;
          } else if (c.dir == W || c.dir == NW || c.dir == SW2) {
            shape = SW;
            break;
          }
        }
        world.getBlock(chunk.dim, x, y, z+1, c);
        if (c.block.id == id) {
          if (c.dir == E || c.dir == SE || c.dir == NE2) {
            shape = SE2;
            break;
          } else if (c.dir == W || c.dir == SW || c.dir == NW2) {
            shape = SW2;
            break;
          }
        }
      }
      if (dir == E || dir == NE || dir == SE || dir == NE2 || dir == SE2) {
        world.getBlock(chunk.dim, x-1, y, z, c);
        if (c.block.id == id) {
          if (c.dir == N || c.dir == NW || c.dir == NE2) {
            shape = NE;
            break;
          } else if (c.dir == S || c.dir == SW || c.dir == SE2) {
            shape = SE;
            break;
          }
        }
        world.getBlock(chunk.dim, x+1, y, z, c);
        if (c.block.id == id) {
          if (c.dir == N || c.dir == NE || c.dir == NW2) {
            shape = NE2;
            break;
          } else if (c.dir == S || c.dir == SE || c.dir == SW2) {
            shape = SE2;
            break;
          }
        }
      }
      if (dir == W || dir == NW || dir == SW || dir == NW2 || dir == SW2) {
        world.getBlock(chunk.dim, x+1, y, z, c);
        if (c.block.id == id) {
          if (c.dir == N || c.dir == NE || c.dir == NW2) {
            shape = NW;
            break;
          } else if (c.dir == S || c.dir == SE || c.dir == SW2) {
            shape = SW;
            break;
          }
        }
        world.getBlock(chunk.dim, x-1, y, z, c);
        if (c.block.id == id) {
          if (c.dir == N || c.dir == NW || c.dir == NE2) {
            shape = NW2;
            break;
          } else if (c.dir == S || c.dir == SW || c.dir == SE2) {
            shape = SW2;
            break;
          }
        }
      }
    } while (false);
    c.free();
    if (shape != dir) {
      Static.log("setShape:old=" + dir + ",new=" + shape);
      bits = Chunk.makeBits(shape, Chunk.getVar(bits));
      chunk.setBits(gx,gy,gz, bits);
      if (live) {
        Static.server.broadcastSetBlock(chunk.dim, x, y, z, id, bits);
      }
    }
  }

  public ArrayList<Box> getBoxes(Coords c, Type type) {
    ArrayList<Box> list = new ArrayList<Box>();
    int y1, y2;
    if (Chunk.getVar(c.bits) == VAR_UPPER) {
      y1 = 8;
      y2 = 16;
    } else {
      y1 = 0;
      y2 = 8;
    }
//    int bits = c.chunk.getBits(c.gx, c.gy, c.gz);
    int dir = Chunk.getDir(c.bits);
    switch (dir) {
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
  public boolean place(Client client, Coords c) {
//    Static.log("dir=" + c.dir + "," + c.xzdir + "," + c.ydir);
    c.otherSide();
    if (c.dir_y == A) {
      c.var = VAR_UPPER;
    }
    if (!super.place(client, c)) return false;
//    setShape(c.chunk, c.gx, c.gy, c.gz, true);  //tick will be generated
    return true;
  }
}
