package jfcraft.data;

/** Portal related code.
 *
 * @author pquiring
 */

public class Portal {

  /** Creates a portal with a flint&steel.
   * Portal can be 21x21 max (inside dimensions) and must be rectangle in shape.
   *
   * @param c - where flint&steel was used on portal border
   * @param frameID - block ID of frame (obsidian, etc.)
   * @param portalID - block ID of portal to create (BlockNetherPortal, etc.);
   * @return if portal was created
   */
  public static boolean makePortal(Coords c, char frameID, char portalID) {
    Coords p = c.clone();
    p.otherSide();
    p.adjacentBlock();
    int gx = p.gx;
    int gy = p.gy;
    int gz = p.gz;
    Static.log("makePortal : air@" + c + "->" + p);
    Static.log("IDs:" + (int)frameID + "," + (int)portalID);
    int dx;
    int dy;
    int dz;
    char i;
    for (int a = 0; a < 21; a++) {
      if (p.chunk.getBlock(gx, gy - 1, gz) == frameID) {
        break;
      }
      gy--;
      p.y--;
    }
    if (p.chunk.getBlock(gx, gy - 1, gz) != frameID) {
      Static.log("bottom of portal not found:" + gy + "," + (int)p.chunk.getBlock(gx, gy - 1, gz));
      return false;
    }
    if (p.chunk.getBlock(gx + 1, gy - 1, gz) == frameID || p.chunk.getBlock(gx - 1, gy - 1, gz) == frameID) {
      for (int a = 0; a < 21; a++) {
        if (p.chunk.getBlock(gx - 1, gy, gz) == frameID) {
          break;
        }
        gx--;
        p.x--;
      }
      if (p.chunk.getBlock(gx - 1, gy, gz) != frameID) {
Static.log("errX1");
        return false;
      }
      dx = 1;
      for (int x = 1; x < 22; x++) {
        i = p.chunk.getBlock(gx + x, gy, gz);
        if (i == frameID) {
          break;
        }
        if (i != Blocks.AIR) {
Static.log("errX2");
          return false;
        }
        dx++;
      }
      dy = 1;
      for (int y = 1; y < 22; y++) {
        i = p.chunk.getBlock(gx, gy + y, gz);
        if (i == frameID) {
          break;
        }
        if (i != Blocks.AIR) {
Static.log("errX3");
          return false;
        }
        dy++;
      }
      if (dx < 2 || dy < 3) {
Static.log("errX1");
        return false; //too small
      }
      if (dx > 21 || dy > 21) {
Static.log("errX1");
        return false; //too big
      }
      for (int x = 0; x < dx; x++) {
        if (p.chunk.getBlock(gx + x, gy - 1, gz) != frameID) {
Static.log("errX1");
          return false;
        }
        for (int y = 0; y < dy; y++) {
          if (p.chunk.getBlock(gx + x, gy + y, gz) != Blocks.AIR) {
Static.log("errX1");
            return false;
          }
          if (p.chunk.getBlock2(gx + x, gy + y, gz) != Blocks.AIR) {
Static.log("errX1");
            return false;
          }
        }
        if (p.chunk.getBlock(gx + x, gy + dy, gz) != frameID) {
          return false;
        }
      }
      for (int y = 0; y < dy; y++) {
        if (p.chunk.getBlock(gx - 1, gy + y, gz) != frameID) {
Static.log("errX1");
          return false;
        }
        if (p.chunk.getBlock(gx + dx, gy + y, gz) != frameID) {
Static.log("errX1");
          return false;
        }
      }
      int bits = Chunk.makeBits(Direction.N, 0);
      for (int x = 0; x < dx; x++) {
        for (int y = 0; y < dy; y++) {
          p.chunk.setBlock(gx + x, gy + y, gz, portalID, bits);
          Static.server.broadcastSetBlock(p.chunk.dim, p.x + x, p.y + y, p.z, portalID, bits);
        }
      }
      return true;
    } else if (p.chunk.getBlock(gx, gy - 1, gz + 1) == frameID || p.chunk.getBlock(gx, gy - 1, gz - 1) == frameID) {
      for (int a = 0; a < 21; a++) {
        if (p.chunk.getBlock(gx, gy, gz - 1) == frameID) {
          break;
        }
        gz--;
        p.z--;
      }
      if (p.chunk.getBlock(gx, gy, gz - 1) != frameID) {
Static.log("errZ1");
        return false;
      }
      dz = 1;
      for (int z = 1; z < 22; z++) {
        i = p.chunk.getBlock(gx, gy, gz + z);
        if (i == frameID) {
          break;
        }
        if (i != Blocks.AIR) {
Static.log("errZ2:" + (int)i);
          return false;
        }
        dz++;
      }
      dy = 1;
      for (int y = 1; y < 22; y++) {
        i = p.chunk.getBlock(gx, gy + y, gz);
        if (i == frameID) {
          break;
        }
        if (i != Blocks.AIR) {
Static.log("errZ3");
          return false;
        }
        dy++;
      }
      if (dz < 2 || dy < 3) {
Static.log("errZ4");
        return false; //too small
      }
      if (dz > 21 || dy > 21) {
Static.log("errZ5");
        return false; //too big
      }
      for (int z = 0; z < dz; z++) {
        if (p.chunk.getBlock(gx, gy - 1, gz + z) != frameID) {
Static.log("errZ6");
          return false;
        }
        for (int y = 0; y < dy; y++) {
          if (p.chunk.getBlock(gx, gy + y, gz + z) != Blocks.AIR) {
Static.log("errZ7");
            return false;
          }
          if (p.chunk.getBlock2(gx, gy + y, gz + z) != Blocks.AIR) {
Static.log("errZ8");
            return false;
          }
        }
        if (p.chunk.getBlock(gx, gy + dy, gz + z) != frameID) {
Static.log("errZ9");
          return false;
        }
      }
      for (int y = 0; y < dy; y++) {
        if (p.chunk.getBlock(gx, gy + y, gz - 1) != frameID) {
Static.log("errZ10");
          return false;
        }
        if (p.chunk.getBlock(gx, gy + y, gz + dz) != frameID) {
Static.log("errZ11");
          return false;
        }
      }
      int bits = Chunk.makeBits(Direction.E, 0);
      for (int z = 0; z < dz; z++) {
        for (int y = 0; y < dy; y++) {
          p.chunk.setBlock(gx, gy + y, gz + z, portalID, bits);
          Static.server.broadcastSetBlock(p.chunk.dim, p.x, p.y + y, p.z + z, portalID, bits);
        }
      }
      return true;
    }
Static.log("err30");
    return false;
  }
}
