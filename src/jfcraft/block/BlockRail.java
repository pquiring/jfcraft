package jfcraft.block;

/** Rails
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
import jfcraft.extra.*;
import jfcraft.opengl.*;
import static jfcraft.data.Direction.*;
import static jfcraft.data.Blocks.*;

public class BlockRail extends BlockBase {
  private static Model facexz, slope;
  public BlockRail(String id, String names[], String images[]) {
    super(id, names, images);
    isOpaque = false;
    isAlpha = false;
    isComplex = true;
    isSolid = false;
    isDir = true;
    isDirXZ = true;
    renderAsItem = true;
    resetBoxes(Type.BOTH);
    addBox(0,0,0, 16,1,16,Type.SELECTION);
    facexz = Assets.getModel("facexz").model;
    slope = Assets.getModel("slope").model;
  }

  public BlockBase setRedstone() {
    isRedstone = true;
    return this;
  }

  public void buildBuffers(RenderDest dest) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);

    int e1 = Static.data.var[X];
    int e2 = Static.data.dir[X];
    if (e1 == 0 && e2 != 0) {
      e1 = e2;
      e2 = 0;
    }

    boolean u1 = false;
    if ((e1 & VAR_UPPER) == VAR_UPPER) {
      u1 = true;
      e1 &= 0x7;
    }
    boolean u2 = false;
    if ((e2 & VAR_UPPER) == VAR_UPPER) {
      u2 = true;
      e2 &= 0x7;
    }

    SubTexture st = textures[0];

    if (isRedstone) {
      ExtraRedstone er = (ExtraRedstone)Static.data.chunk.getExtra((int)Static.data.x, (int)Static.data.y, (int)Static.data.z, Extras.REDSTONE);
      if (er == null) {
        Static.log("BlockRail:ExtraRedstone not found");
        return;
      }
      if (er.powered) st = textures[1];
    }

    Static.data.dir[X] = N;
    Model model = facexz;

    switch (e1) {
      default:
      case N:
        switch (e2) {
          case E:
            st = textures[1];
            Static.data.dir[X] = W;  //N->W
            break;
          default:
          case S:
            if (u1) {
              model = slope;
            } else if (u2) {
              model = slope;
              Static.data.dir[X] = S;  //N->S
            }
            break;
          case W:
            st = textures[1];
            Static.data.dir[X] = S;  //N->S
            break;
        }
        break;
      case E:
        switch (e2) {
          case N:
            st = textures[1];
            Static.data.dir[X] = W;  //N->W
            break;
          case S:
            st = textures[1];
            break;
          default:
          case W:
            if (u1) {
              Static.data.dir[X] = E;  //N->E
              model = slope;
            } else if (u2) {
              Static.data.dir[X] = W;  //N->W
              model = slope;
            } else {
              Static.data.dir[X] = W;  //N->W
            }
            break;
        }
        break;
      case S:
        switch (e2) {
          default:
          case N:
            if (u1) {
              Static.data.dir[X] = S;  //N->S
              model = slope;
            } else if (u2) {
              model = slope;
            }
            break;
          case E:
            st = textures[1];
            break;
          case W:
            st = textures[1];
            Static.data.dir[X] = E;  //N->E
            break;
        }
        break;
      case W:
        switch (e2) {
          case N:
            st = textures[1];
            Static.data.dir[X] = S;  //N->S
            break;
          default:
          case E:
            if (u1) {
              Static.data.dir[X] = W;  //N->W
              model = slope;
            } else if (u2) {
              Static.data.dir[X] = E;  //N->E
              model = slope;
            } else {
              Static.data.dir[X] = E;  //N->E
            }
            break;
          case S:
            st = textures[1];
            Static.data.dir[X] = E;  //N->E
            break;
        }
        break;
    }

    buildBuffers(model.getObject("FACE"), buf, st);
  }

  public static boolean isRail(char id) {
    return id == Blocks.RAIL || id == Blocks.RAIL_DETECTOR || id == Blocks.RAIL_POWERED
      || id == Blocks.RAIL_ACTIVATOR;
  }

  private static boolean canRailBend(char id) {
    return id == Blocks.RAIL;
  }

  private boolean connectEnd(Coords c, int side1, int dx, int dy, int dz) {
    char rid = c.chunk.getBlock(c.gx + dx, c.gy + dy, c.gz + dz);
    if (!isRail(rid)) return false;
    int bits = c.chunk.getBits(c.gx + dx, c.gy + dy, c.gz + dz);
    int e1 = bits & 0x0f;
    int e2 = (bits & 0xf0) >> 4;
    int b1 = e1;
    int b2 = e2;
    boolean u1 = false;
    if ((b1 & 0x8) == 0x8) {
      u1 = true;
      b1 &= 0x7;
    }
    boolean u2 = false;
    if ((b2 & 0x8) == 0x8) {
      u2 = true;
      b2 &= 0x7;
    }
    int side2 = Direction.opposite(side1);
    if (b1 == 0 && b2 == 0) {
      e1 = side2;
      c.chunk.setBlock(c.gx + dx,c.gy + dy,c.gz + dz,rid,Chunk.makeBits(e1,e2));
      Static.server.broadcastSetBlock(c.chunk.dim,c.x + dx,c.y + dy,c.z + dz,rid,Chunk.makeBits(e1,e2));
      return true;
    }
    else if (b1 == 0) {
      if (b2 == side1) {
        e1 = side2;
        c.chunk.setBlock(c.gx + dx,c.gy + dy,c.gz + dz,rid,Chunk.makeBits(e1,e2));
        Static.server.broadcastSetBlock(c.chunk.dim,c.x + dx,c.y + dy,c.z + dz,rid,Chunk.makeBits(e1,e2));
        return true;
      } else if (canRailBend(rid) && !u2) {
        if (b2 == side2) return false;
        //bend rail
        e1 = side2;
        c.chunk.setBlock(c.gx + dx,c.gy + dy,c.gz + dz,rid,Chunk.makeBits(e1,e2));
        Static.server.broadcastSetBlock(c.chunk.dim,c.x + dx,c.y + dy,c.z + dz,rid,Chunk.makeBits(e1,e2));
        return true;
      }
    }
    else if (b2 == 0) {
      if (b1 == side1) {
        e2 = side2;
        c.chunk.setBlock(c.gx + dx,c.gy + dy,c.gz + dz,rid,Chunk.makeBits(e1,e2));
        Static.server.broadcastSetBlock(c.chunk.dim,c.x + dx,c.y + dy,c.z + dz,rid,Chunk.makeBits(e1,e2));
        return true;
      } else if (canRailBend(rid) && !u1) {
        if (b1 == side2) return false;
        //bend rail
        e2 = side2;
        c.chunk.setBlock(c.gx + dx,c.gy + dy,c.gz + dz,rid,Chunk.makeBits(e1,e2));
        Static.server.broadcastSetBlock(c.chunk.dim,c.x + dx,c.y + dy,c.z + dz,rid,Chunk.makeBits(e1,e2));
        return true;
      }
    }
    return false;
  }

  private int connectEnd(Coords c, int e1) {

    boolean u1 = false;
    if ((e1 & 0x8) == 0x8) {
      u1 = true;
      e1 &= 0x7;
    }

    if (connectEnd(c, N,  0, 0, -1)) return N;
    if (connectEnd(c, E, +1, 0, 0)) return E;
    if (connectEnd(c, S,  0, 0, +1)) return S;
    if (connectEnd(c, W, -1, 0, 0)) return W;

    if (c.y < 255) {
      if (e1 != E && e1 != W) {
        if (connectEnd(c, N,  0, 1, -1)) return N | 0x8;
        if (connectEnd(c, S,  0, 1, +1)) return S | 0x8;
      }
      if (e1 != N && e1 != S) {
        if (connectEnd(c, E, +1, 1, 0)) return E | 0x8;
        if (connectEnd(c, W, -1, 1, 0)) return W | 0x8;
      }
    }
    return 0;
  }

  public boolean place(Client client, Coords c) {
    //connect both ends if possible
    int e1 = connectEnd(c, 0);
    int e2 = connectEnd(c, e1);
//    Static.log("Rail:" + e1 + "," + e2);
    if (isRedstone) {
      ExtraRedstone er = new ExtraRedstone(c.gx, c.gy, c.gz);
      c.chunk.addExtra(er);
      Static.server.broadcastExtra(c.chunk.dim, c.x, c.y, c.z, er, true);
    }
    c.chunk.setBlock(c.gx,c.gy,c.gz, id,Chunk.makeBits(e1,e2));
    Static.server.broadcastSetBlock(c.chunk.dim,c.x,c.y,c.z, id,Chunk.makeBits(e1,e2));
    return true;
  }

  private void deleteEnd(Coords c, int side, int dx, int dy, int dz) {
    char rid = c.chunk.getBlock(c.gx + dx, c.gy + dy, c.gz + dz);
    if (!isRail(rid)) return;
    int bits = c.chunk.getBits(c.gx + dx, c.gy + dy, c.gz + dz);
    int b1 = bits & 0x0f;
    int b2 = (bits & 0xf0) >> 4;
    if (b1 == side) b1 = 0;
    else if (b2 == side) b2 = 0;
    else return;
    c.chunk.setBlock(c.gx + dx,c.gy + dy,c.gz + dz,rid,Chunk.makeBits(b1,b2));
    Static.server.broadcastSetBlock(c.chunk.dim,c.x + dx,c.y + dy,c.z + dz,rid,Chunk.makeBits(b1,b2));
  }

  public void destroy(Client client, Coords c, boolean doDrop) {
    int bits = c.chunk.getBits(c.gx, c.gy, c.gz);
    int e1 = bits & 0x0f;
    int e2 = (bits & 0xf0) >> 4;

    super.destroy(client, c, doDrop);
    //may need to unhook from adj rails
    int dy;
    if ((e1 & 0x8) == 0x8) {
      e1 &= 0x7;
      dy = 1;
    } else {
      dy = 0;
    }
    switch (e1) {
      case N: deleteEnd(c,S, 0, dy,-1); break;
      case E: deleteEnd(c,W, 1, dy, 0); break;
      case S: deleteEnd(c,N, 0, dy, 1); break;
      case W: deleteEnd(c,E,-1, dy, 0); break;
    }
    if ((e2 & 0x8) == 0x8) {
      e2 &= 0x7;
      dy = 1;
    } else {
      dy = 0;
    }
    switch (e2) {
      case N: deleteEnd(c,S, 0, dy,-1); break;
      case E: deleteEnd(c,W, 1, dy, 0); break;
      case S: deleteEnd(c,N, 0, dy, 1); break;
      case W: deleteEnd(c,E,-1, dy, 0); break;
    }
  }

  public void checkPowered(Coords c) {
    ExtraRedstone er = (ExtraRedstone)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) return;
    int powerLevel = 0;
    int pl;
    int dim = c.chunk.dim;
    int x = c.x;
    int y = c.y;
    int z = c.z;
    World world = Static.server.world;
    pl = world.getPowerLevel(dim,x+1,y,z,c); if (pl > powerLevel) powerLevel = pl;
    pl = world.getPowerLevel(dim,x-1,y,z,c); if (pl > powerLevel) powerLevel = pl;
    pl = world.getPowerLevel(dim,x,y,z+1,c); if (pl > powerLevel) powerLevel = pl;
    pl = world.getPowerLevel(dim,x,y,z-1,c); if (pl > powerLevel) powerLevel = pl;
    if (powerLevel == 0 && er.powered) {
      powerOff(null, c);
    } else if (powerLevel > 0 && !er.powered) {
      c.powerLevel = powerLevel;
      powerOn(null, c);
    }
  }

/*
  public ArrayList<Box> getBoxes(Coords c, Type type) {
    ArrayList<Box> list = new ArrayList<Box>();
    switch(c.dir) {
      case A: list.add(new Box(0,15,0, 16,16,16)); break;
      case B: list.add(new Box(0,0,0, 16,1,16)); break;
      case N: list.add(new Box(0,0,0, 16,16,1)); break;
      case E: list.add(new Box(15,0,0, 16,16,16)); break;
      case S: list.add(new Box(0,15,0, 16,16,16)); break;
      case W: list.add(new Box(0,0,0, 1,16,16)); break;
    }
    return list;
  }
*/

  public byte rotateBits(byte bits, int rotation) {
    int e1 = bits & 0xf0;
    int e2 = bits & 0x0f;
    if (e1 != 0) {
      e1 >>= 4;
      e1 = Direction.rotate((byte)e1, rotation);
      e1 <<= 4;
    }
    if (e2 != 0) {
      e2 = Direction.rotate((byte)e2, rotation);
    }
    return (byte)(e1 | e2);
  }

}
