package jfcraft.block;

/** Stairs
 *
 * https://minecraft.fandom.com/wiki/Stairs
 *
 * @author pquiring
 *
 * Created : Sep 11, 2014
 */

import java.util.ArrayList;
import jfcraft.client.Client;
import static jfcraft.data.Direction.*;

import jfcraft.data.*;
import jfcraft.opengl.*;

public class BlockStairs extends BlockBase {
  public static int VAR_UPPER = 8;
  public BlockStairs(String id, String names[], String images[]) {
    super(id, names, images);
    isComplex = true;
    isSolid = false;
    isDir = true;
    isDirXZ = true;
    isVar = true;
    varMask = 0x7;
  }
  public void buildBuffers(RenderDest dest) {
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
    if (Static.data.var[X] == VAR_UPPER) {
      switch (Static.data.dir[X]) {
        default:
          Static.log("BlockStairs with invalid dir:" + Static.data.dir[X]);
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
      switch (Static.data.dir[X]) {
        default:
          Static.log("BlockStairs with invalid dir:" + (Static.data.dir[X]));
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
    Static.data.isDir = false;  //do not allow rotation
    Static.data.isDirXZ = false;  //do not allow rotation
    Static.data.dir[X] = N;  //do not allow rotation
    SubTexture st = getTexture();
    for(int a=0;a<8;a++) {
      if (q[a]) {
        addQuad(buf, a, st);
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
  public int getPreferredDir() {
    return N;
  }
  public boolean place(Client client, Coords c) {
    //check if creating corner stairs and change dir as required
    World world = Static.server.world;
    int _N = N << 4, _E = E << 4, _S = S << 4, _W = W << 4;
    if (c.face_y == A) {
      _N |= VAR_UPPER;
      _E |= VAR_UPPER;
      _S |= VAR_UPPER;
      _W |= VAR_UPPER;
    }
    char id_n = world.getID(client.player.dim, c.x, c.y, c.z - 1);
    int bits_n = world.getBits(client.player.dim, c.x, c.y, c.z - 1) & 0xf8;
    char id_e = world.getID(client.player.dim, c.x + 1, c.y, c.z);
    int bits_e = world.getBits(client.player.dim, c.x + 1, c.y, c.z) & 0xf8;
    char id_s = world.getID(client.player.dim, c.x, c.y, c.z + 1);
    int bits_s = world.getBits(client.player.dim, c.x, c.y, c.z + 1) & 0xf8;
    char id_w = world.getID(client.player.dim, c.x - 1, c.y, c.z);
    int bits_w = world.getBits(client.player.dim, c.x - 1, c.y, c.z) & 0xf8;
    switch (c.dir_xz) {
      case N:
        if (id_s == id) {
          //inner corner
          if (bits_s == _E) {
            c.dir_xz = NE;
          } else if (bits_s == _W) {
            c.dir_xz = NW;
          }
        } else if (id_n == id) {
          //outer corner
          if (bits_n == _E) {
            c.dir_xz = NE2;
          } else if (bits_n == _W) {
            c.dir_xz = NW2;
          }
        }
        break;
      case E:
        if (id_w == id) {
          //inner corner
          if (bits_w == _N) {
            c.dir_xz = NE;
          } else if (bits_w == _S) {
            c.dir_xz = SE;
          }
        } else if (id_e == id) {
          //outer corner
          if (bits_e == _N) {
            c.dir_xz = NE2;
          } else if (bits_e == _S) {
            c.dir_xz = SE2;
          }
        }
        break;
      case S:
        if (id_n == id) {
          //inner corner
          if (bits_n == _E) {
            c.dir_xz = SE;
          } else if (bits_n == _W) {
            c.dir_xz = SW;
          }
        } else if (id_s == id) {
          //outer corner
          if (bits_s == _E) {
            c.dir_xz = SE2;
          } else if (bits_s == _W) {
            c.dir_xz = SW2;
          }
        }
        break;
      case W:
        if (id_e == id) {
          //inner corner
          if (bits_e == _N) {
            c.dir_xz = NW;
          } else if (bits_e == _S) {
            c.dir_xz = SW;
          }
        } else if (id_w == id) {
          //outer corner
          if (bits_w == _N) {
            c.dir_xz = NW2;
          } else if (bits_w == _S) {
            c.dir_xz = SW2;
          }
        }
        break;
    }
    if (c.face_y == A) {
      //add upper flag to var
      c.var |= VAR_UPPER;
    }
    return super.place(client, c);
  }
}
