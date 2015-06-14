package jfcraft.block;

import jfcraft.client.Client;
import jfcraft.data.Blocks;
import jfcraft.data.Chunk;
import jfcraft.data.Coords;
import jfcraft.data.Items;
import jfcraft.data.Static;
import jfcraft.item.Item;
import static jfcraft.data.Direction.*;

/** Block Obsidian
 *
 * @author pquiring
 */

public class BlockObsidian extends BlockOpaque {
  public BlockObsidian(String id, String names[], String images[]) {
    super(id, names, images);
  }

  public boolean useTool(Client client, Coords c) {
    Item item = client.player.items[client.activeSlot];
    if (item.id == Items.FLINT_STEEL) {
      if (id == Blocks.OBSIDIAN) {
        //create portal?
        if (makePortal(c)) return true;
        Static.log("makePortal:failed");
      }
    }
    return super.useTool(client, c);
  }

  //portal can be 21x21 max (inside dimensions) and must be rectangle in shape
  private boolean makePortal(Coords c) {
    Coords p = c.clone();
    p.adjacentBlock();  //air block that fire was set in
    int gx = p.gx;
    int gy = p.gy;
    int gz = p.gz;
    Static.log("air=" + gx + "," + gy + "," + gz);
    int dx,dy,dz;
    char i;
    char f[] = new char[45*45];
    f[22 * 45 + 22] = Blocks.OBSIDIAN;
    //find bottom side
    for(int a=0;a<21;a++) {
      if (c.chunk.getID(gx, gy-1, gz) == Blocks.OBSIDIAN) break;
      gy--;
      p.y--;
    }
    if (c.chunk.getID(gx, gy-1, gz) != Blocks.OBSIDIAN) return false;
    //check which plane portal is on
    if (c.chunk.getID(gx+1, gy-1, gz) == Blocks.OBSIDIAN || c.chunk.getID(gx-1, gy-1, gz) == Blocks.OBSIDIAN) {
      //xy plane
      //find left side
      for(int a=0;a<21;a++) {
        if (c.chunk.getID(gx-1, gy, gz) == Blocks.OBSIDIAN) break;
        gx--;
        p.x--;
      }
      if (c.chunk.getID(gx-1, gy, gz) != Blocks.OBSIDIAN) return false;
      dx = 1;
      for(int x=1;x<22;x++) {
        i = c.chunk.getID(gx+x, gy, gz);
        if (i == Blocks.OBSIDIAN) break;
        if (i != Blocks.AIR) return false;
        dx++;
      }
      dy = 1;
      for(int y=1;y<22;y++) {
        i = c.chunk.getID(gx, gy+y, gz);
        if (i == Blocks.OBSIDIAN) break;
        if (i != Blocks.AIR) return false;
        dy++;
      }
      //we now have dx,dy (size) and start (sx,sy,sz)
      if (dx < 2 || dy < 3) return false;  //too small
      if (dx > 21 || dy > 21) return false;  //too big
      //lets find if we have a proper portal
      for(int x=0;x<dx;x++) {
        if (c.chunk.getID(gx + x, gy-1, gz) != Blocks.OBSIDIAN) return false;
        for(int y=0;y<dy;y++) {
          if (c.chunk.getID(gx + x, gy + y, gz) != Blocks.AIR) return false;
          if (c.chunk.getID2(gx + x, gy + y, gz) != Blocks.AIR) return false;
        }
        if (c.chunk.getID(gx + x, gy+dy, gz) != Blocks.OBSIDIAN) return false;
      }
      for(int y=0;y<dy;y++) {
        if (c.chunk.getID(gx-1, gy+y, gz) != Blocks.OBSIDIAN) return false;
        if (c.chunk.getID(gx+dx, gy+y, gz) != Blocks.OBSIDIAN) return false;
      }
      //we have a portal - fill it in with portal block
      Static.log("portal:xy@" + gx + "," + gy + "," + gz);
      int bits = Chunk.makeBits(N, 0);
      for(int x=0;x<dx;x++) {
        for(int y=0;y<dy;y++) {
          c.chunk.setBlock(gx + x, gy + y, gz, Blocks.PORTAL, bits);
          Static.server.broadcastSetBlock(c.chunk.dim, p.x + x, p.y + y, p.z, Blocks.PORTAL, bits);
        }
      }
      return true;
    } else if (c.chunk.getID(gx, gy-1, gz+1) == Blocks.OBSIDIAN || c.chunk.getID(gx, gy-1, gz-1) == Blocks.OBSIDIAN) {
      //zy plane
      //find north side
      for(int a=0;a<21;a++) {
        if (c.chunk.getID(gx, gy, gz-1) == Blocks.OBSIDIAN) break;
        gz--;
        p.z--;
      }
      if (c.chunk.getID(gx, gy, gz-1) != Blocks.OBSIDIAN) return false;
      dz = 1;
      for(int z=1;z<22;z++) {
        i = c.chunk.getID(gx, gy, gz+z);
        if (i == Blocks.OBSIDIAN) break;
        if (i != Blocks.AIR) return false;
        dz++;
      }
      dy = 1;
      for(int y=1;y<22;y++) {
        i = c.chunk.getID(gx, gy+y, gz);
        if (i == Blocks.OBSIDIAN) break;
        if (i != Blocks.AIR) return false;
        dy++;
      }
      //we now have dx,dy (size) and start (sx,sy,sz)
      if (dz < 2 || dy < 3) return false;  //too small
      if (dz > 21 || dy > 21) return false;  //too big
      //lets find if we have a proper portal
      for(int z=0;z<dz;z++) {
        if (c.chunk.getID(gx, gy-1, gz + z) != Blocks.OBSIDIAN) return false;
        for(int y=0;y<dy;y++) {
          if (c.chunk.getID(gx, gy + y, gz + z) != Blocks.AIR) return false;
          if (c.chunk.getID2(gx, gy + y, gz + z) != Blocks.AIR) return false;
        }
        if (c.chunk.getID(gx, gy+dy, gz + z) != Blocks.OBSIDIAN) return false;
      }
      for(int y=0;y<dy;y++) {
        if (c.chunk.getID(gx, gy+y, gz-1) != Blocks.OBSIDIAN) return false;
        if (c.chunk.getID(gx, gy+y, gz+dz) != Blocks.OBSIDIAN) return false;
      }
      //we have a portal - fill it in with portal block
      Static.log("portal:zy@" + gx + "," + gy + "," + gz);
      int bits = Chunk.makeBits(E, 0);
      for(int z=0;z<dz;z++) {
        for(int y=0;y<dy;y++) {
          c.chunk.setBlock(gx, gy + y, gz + z, Blocks.PORTAL, bits);
          Static.server.broadcastSetBlock(c.chunk.dim, p.x, p.y + y, p.z + z, Blocks.PORTAL, bits);
        }
      }
      return true;
    }
    return false;
  }
}
