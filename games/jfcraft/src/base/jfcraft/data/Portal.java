package jfcraft.data;

/** Portal related code.
 *
 * @author pquiring
 */

public class Portal {

  /** portal can be 21x21 max (inside dimensions) and must be rectangle in shape
   *
   * @param c - where flint&steel was used on portal border
   * @return if portal was created
   */
  public static boolean makePortal(Coords c, char frameID, char portalID) {
    Coords p = c.clone();
    p.otherSide();
    p.adjacentBlock();
    int gx = p.gx;
    int gy = p.gy;
    int gz = p.gz;
    //    Static.log("air=" + gx + "," + gy + "," + gz);
    int dx;
    int dy;
    int dz;
    char i;
    char[] f = new char[45 * 45];
    f[22 * 45 + 22] = frameID;
    for (int a = 0; a < 21; a++) {
      if (c.chunk.getID(gx, gy - 1, gz) == frameID) {
        break;
      }
      gy--;
      p.y--;
    }
    if (c.chunk.getID(gx, gy - 1, gz) != frameID) {
      return false;
    }
    if (c.chunk.getID(gx + 1, gy - 1, gz) == frameID || c.chunk.getID(gx - 1, gy - 1, gz) == frameID) {
      for (int a = 0; a < 21; a++) {
        if (c.chunk.getID(gx - 1, gy, gz) == frameID) {
          break;
        }
        gx--;
        p.x--;
      }
      if (c.chunk.getID(gx - 1, gy, gz) != frameID) {
        return false;
      }
      dx = 1;
      for (int x = 1; x < 22; x++) {
        i = c.chunk.getID(gx + x, gy, gz);
        if (i == frameID) {
          break;
        }
        if (i != Blocks.AIR) {
          return false;
        }
        dx++;
      }
      dy = 1;
      for (int y = 1; y < 22; y++) {
        i = c.chunk.getID(gx, gy + y, gz);
        if (i == frameID) {
          break;
        }
        if (i != Blocks.AIR) {
          return false;
        }
        dy++;
      }
      if (dx < 2 || dy < 3) {
        return false; //too small
      }
      if (dx > 21 || dy > 21) {
        return false; //too big
      }
      for (int x = 0; x < dx; x++) {
        if (c.chunk.getID(gx + x, gy - 1, gz) != frameID) {
          return false;
        }
        for (int y = 0; y < dy; y++) {
          if (c.chunk.getID(gx + x, gy + y, gz) != Blocks.AIR) {
            return false;
          }
          if (c.chunk.getID2(gx + x, gy + y, gz) != Blocks.AIR) {
            return false;
          }
        }
        if (c.chunk.getID(gx + x, gy + dy, gz) != frameID) {
          return false;
        }
      }
      for (int y = 0; y < dy; y++) {
        if (c.chunk.getID(gx - 1, gy + y, gz) != frameID) {
          return false;
        }
        if (c.chunk.getID(gx + dx, gy + y, gz) != frameID) {
          return false;
        }
      }
      int bits = Chunk.makeBits(Direction.N, 0);
      for (int x = 0; x < dx; x++) {
        for (int y = 0; y < dy; y++) {
          c.chunk.setBlock(gx + x, gy + y, gz, portalID, bits);
          Static.server.broadcastSetBlock(c.chunk.dim, p.x + x, p.y + y, p.z, portalID, bits);
        }
      }
      return true;
    } else if (c.chunk.getID(gx, gy - 1, gz + 1) == frameID || c.chunk.getID(gx, gy - 1, gz - 1) == frameID) {
      for (int a = 0; a < 21; a++) {
        if (c.chunk.getID(gx, gy, gz - 1) == frameID) {
          break;
        }
        gz--;
        p.z--;
      }
      if (c.chunk.getID(gx, gy, gz - 1) != frameID) {
        return false;
      }
      dz = 1;
      for (int z = 1; z < 22; z++) {
        i = c.chunk.getID(gx, gy, gz + z);
        if (i == frameID) {
          break;
        }
        if (i != Blocks.AIR) {
          return false;
        }
        dz++;
      }
      dy = 1;
      for (int y = 1; y < 22; y++) {
        i = c.chunk.getID(gx, gy + y, gz);
        if (i == frameID) {
          break;
        }
        if (i != Blocks.AIR) {
          return false;
        }
        dy++;
      }
      if (dz < 2 || dy < 3) {
        return false; //too small
      }
      if (dz > 21 || dy > 21) {
        return false; //too big
      }
      for (int z = 0; z < dz; z++) {
        if (c.chunk.getID(gx, gy - 1, gz + z) != frameID) {
          return false;
        }
        for (int y = 0; y < dy; y++) {
          if (c.chunk.getID(gx, gy + y, gz + z) != Blocks.AIR) {
            return false;
          }
          if (c.chunk.getID2(gx, gy + y, gz + z) != Blocks.AIR) {
            return false;
          }
        }
        if (c.chunk.getID(gx, gy + dy, gz + z) != frameID) {
          return false;
        }
      }
      for (int y = 0; y < dy; y++) {
        if (c.chunk.getID(gx, gy + y, gz - 1) != frameID) {
          return false;
        }
        if (c.chunk.getID(gx, gy + y, gz + dz) != frameID) {
          return false;
        }
      }
      int bits = Chunk.makeBits(Direction.E, 0);
      for (int z = 0; z < dz; z++) {
        for (int y = 0; y < dy; y++) {
          c.chunk.setBlock(gx, gy + y, gz + z, portalID, bits);
          Static.server.broadcastSetBlock(c.chunk.dim, p.x, p.y + y, p.z + z, portalID, bits);
        }
      }
      return true;
    }
    return false;
  }
}
