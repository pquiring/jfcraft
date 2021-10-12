package jfcraft.block;

/** Portal.
 *
 * To create a new portal :
 *  - derive from this class
 *  - implement getDimension()
 *  - implement getFrameBlock()
 *  - implement getPortalBlock()
 *
 * See : BlockNetherPortal
 *
 * @author pquiring
 *
 * Created : Mar 31, 2014
 */

import javaforce.gl.*;

import jfcraft.client.*;
import jfcraft.data.*;
import jfcraft.entity.*;
import jfcraft.opengl.*;
import static jfcraft.data.Direction.*;

public abstract class BlockPortal extends BlockBase {
  private static Model model;
  public BlockPortal(String id, String names[], String images[]) {
    super(id, names, images);
    isOpaque = false;
    isAlpha = true;
    isComplex = true;
    isSolid = false;
    isDir = true;
    isDirXZ = true;
    emitLight = 15;
    dropBlock = "AIR";
    resetBoxes(Type.BOTH);
    if (model == null) {
      model = Assets.getModel("portal").model;
    }
  }

  public void buildBuffers(RenderDest dest, RenderData data) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    buildBuffers(model.getObject("PORTAL"), buf, data, textures[0]);
  }

  private static final int maxSize = 15;
  public void destroy(Client client, Coords c, boolean doDrop) {
    //must destroy all adjacent portal blocks
    Coords p = c.clone();
    int bits = c.chunk.getBits(c.gx, c.gy, c.gz);
    int dir = Chunk.getDir(bits);
    int dx,dy,dz;
    if (dir == N) {
      //xy plane
      while (c.chunk.getBlock(p.gx-1, p.gy, p.gz) == id) {
        p.gx--;
        p.x--;
      }
      while (c.chunk.getBlock(p.gx, p.gy-1, p.gz) == id) {
        p.gy--;
        p.y--;
      }
      dx = 1;
      for(int x=1;x<maxSize;x++) {
        if (c.chunk.getBlock(p.gx + x, p.gy, p.gz) != id) break;
        dx++;
      }
      dy = 1;
      for(int y=1;y<maxSize;y++) {
        if (c.chunk.getBlock(p.gx, p.gy + y, p.gz) != id) break;
        dy++;
      }
      for(int x=0;x<dx;x++) {
        for(int y=0;y<dy;y++) {
          c.chunk.clearBlock(p.gx + x, p.gy + y, p.gz);
          Static.server.broadcastClearBlock(c.chunk.dim, p.x + x, p.y + y, p.z);
        }
      }
    } else {
      //zy plane
      while (c.chunk.getBlock(p.gx, p.gy, p.gz-1) == id) {
        p.gz--;
        p.z--;
      }
      while (c.chunk.getBlock(p.gx, p.gy-1, p.gz) == id) {
        p.gy--;
        p.y--;
      }
      dz = 1;
      for(int z=1;z<maxSize;z++) {
        if (c.chunk.getBlock(p.gx, p.gy, p.gz + z) != id) break;
        dz++;
      }
      dy = 1;
      for(int y=1;y<maxSize;y++) {
        if (c.chunk.getBlock(p.gx, p.gy + y, p.gz) != id) break;
        dy++;
      }
      for(int z=0;z<dz;z++) {
        for(int y=0;y<dy;y++) {
          c.chunk.clearBlock(p.gx, p.gy + y, p.gz + z);
          Static.server.broadcastClearBlock(c.chunk.dim, p.x, p.y + y, p.z + z);
        }
      }
    }
  }
  private Coords c = new Coords();
  public void tick(Chunk chunk, Tick tick) {
    //check if portal is still intact
    int bits = chunk.getBits(tick.x, tick.y, tick.z);
    int dir = Chunk.getDir(bits);
    char tid;
    char frameID = getFrameBlock();
    char portalID = getPortalBlock();
    if (dir == N || dir == S) {
      //xy plane
      tid = chunk.getBlock(tick.x+1, tick.y, tick.z);
      if (tid != portalID && tid != frameID) {
        destroy(null, tick.toWorldCoords(chunk, c), true);
        return;
      }
      tid = chunk.getBlock(tick.x-1, tick.y, tick.z);
      if (tid != portalID && tid != frameID) {
        destroy(null, tick.toWorldCoords(chunk, c), true);
        return;
      }
    } else {
      //zy plane
      tid = chunk.getBlock(tick.x, tick.y, tick.z+1);
      if (tid != portalID && tid != frameID) {
        destroy(null, tick.toWorldCoords(chunk, c), true);
        return;
      }
      tid = chunk.getBlock(tick.x, tick.y, tick.z-1);
      if (tid != portalID && tid != frameID) {
        destroy(null, tick.toWorldCoords(chunk, c), true);
        return;
      }
    }
    tid = chunk.getBlock(tick.x, tick.y-1, tick.z);
    if (tid != portalID && tid != frameID) {
      destroy(null, tick.toWorldCoords(chunk, c), true);
      return;
    }
    tid = chunk.getBlock(tick.x, tick.y+1, tick.z);
    if (tid != portalID && tid != frameID) {
      destroy(null, tick.toWorldCoords(chunk, c), true);
      return;
    }
    super.tick(chunk, tick);
  }

  /** Returns the dimension this portal goes to. */
  public abstract int getDimension();

  public abstract char getFrameBlock();

  public abstract char getPortalBlock();

  public void etick(EntityBase e, Coords c) {
    if (e.teleportTimer > 0) {
      e.teleportTimer = 20;
    } else {
      Coords p = c.clone();
      Static.log("teleport:" + c);
      Static.server.teleport(e, p, getDimension());
    }
  }

  /** Find or create portal in current dim.
   * @param e = entity already in new dim
   * @param c = coords of portal block from old dimension
   */
  public void teleport(EntityBase e, Coords c) {
    Static.log("teleport:" + e + "@" + c + ":dim=" + e.dim);
    Coords p = c.clone();
    //for now just convert coords as is
    Chunk chunk = Static.server.world.chunks.getChunk2(e.dim, p.cx, p.cz, true, true, true);
    Static.server.world.chunks.loadSurroundingChunks(e.dim,p.cx,p.cz);  //ensure surrounding chunks are present
    char frameID = getFrameBlock();
    char portalID = getPortalBlock();
    boolean found = false;
    for(int y = 0;y < 256;y++) {
      if (chunk.getBlock(p.gx, y, p.gz) == portalID) {
        e.pos.y = y;
        found = true;
        break;
      }
    }
    if (!found) {
      int dir = Chunk.getDir(p.bits);
      //create portal at entity coords
      boolean foundAir = false;
      for(int y = 64;y < 128-5;y++) {
        if ((chunk.getBlock(p.gx, y, p.gz) == 0) && (chunk.getBlock(p.gx, y - 1, p.gz) != 0)) {
          p.gy = y;
          foundAir = true;
          break;
        }
      }
      if (!foundAir) {
        for(int y = 64;y > 0;y--) {
          if ((chunk.getBlock(p.gx, y, p.gz) == 0) && (chunk.getBlock(p.gx, y - 1, p.gz) != 0)) {
            p.gy = y;
            foundAir = true;
            break;
          }
        }
      }
      if (!foundAir) {
        //solid world? put portal at sea level
        p.gy = 64;
      }
      e.pos.y = p.gy + 1;
      Static.log("no portal found in other dim, creating new portal @ " + p);
      if (dir == N) {
        //xy plane
        p.gx--;
        for(int x=0;x<=3;x++) {
          for(int y=0;y<=4;y++) {
            for(int z=-2;z<=2;z++) {
              if (z == 0) {
                if (x == 0 || x == 3 || y == 0 || y == 4) {
                  chunk.setBlock(p.gx + x, p.gy + y, p.gz, frameID, 0);
                } else {
                  chunk.setBlock(p.gx + x, p.gy + y, p.gz, portalID, Chunk.makeBits(dir, 0));
                }
              } else {
                if (x != 0 && x != 3 && y != 0 && y != 4) {
                  chunk.clearBlock(p.gx + x, p.gy + y, p.gz + z);
                }
              }
            }
          }
        }
      } else {
        //zy plane
        p.gz--;
        for(int z=0;z<=3;z++) {
          for(int y=0;y<=4;y++) {
            for(int x=-2;x<=2;x++) {
              if (x == 0) {
                if (z == 0 || z == 3 || y == 0 || y == 4) {
                  chunk.setBlock(p.gx, p.gy + y, p.gz + z, frameID, 0);
                } else {
                  chunk.setBlock(p.gx, p.gy + y, p.gz + z, portalID, Chunk.makeBits(dir, 0));
                }
              } else {
                if (z != 0 && z != 3 && y != 0 && y != 4) {
                  chunk.clearBlock(p.gx + x, p.gy + y, p.gz + z);
                }
              }
            }
          }
        }
      }
    }
    chunk.addEntity(e);
  }
}
