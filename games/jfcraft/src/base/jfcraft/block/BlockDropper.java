package jfcraft.block;

/** Dropper
 *
 * @author pquiring
 *
 * Created : May 2, 2015
 */

import java.util.*;

import javaforce.*;
import javaforce.gl.*;

import jfcraft.data.*;
import jfcraft.client.*;
import jfcraft.opengl.*;
import static jfcraft.data.Direction.*;
import jfcraft.entity.*;
import jfcraft.item.*;

public class BlockDropper extends BlockOpaque {
  private static final int cooldownTicks = 4;
  public BlockDropper(String id, String names[], String images[]) {
    super(id, names, images);
    isDir = true;
    isRedstone = true;
    canUse = true;
  }

  public SubTexture getTexture(RenderData data) {
    if (data.dirSide == data.dir[X]) {
      if (data.dirSide == A || data.dirSide == B) {
        return textures[1];  //vertical
      } else {
        return textures[0];  //horizontal
      }
    }
    return textures[2];  //sides
  }

  public boolean place(Client client, Coords c) {
    ExtraDropper dropper = new ExtraDropper(c.gx, c.gy, c.gz);
    c.chunk.addExtra(dropper);
    c.chunk.addTick(c, false);
    return super.place(client, c);
  }

  public void destroy(Client client, Coords c, boolean doDrop) {
    super.destroy(client, c, doDrop);
    c.chunk.delExtra(c, Extras.DROPPER);
  }

  public Item[] drop(Coords c, int var) {
    ExtraDropper dropper = (ExtraDropper)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.DROPPER);
    if (dropper == null) {
      Static.log("BlockDropper.drop():Error:Can not find extra data");
      return new Item[] {new Item(dropID, 0)};
    }
    Item[] items = new Item[10];
    for(int a=0;a<9;a++) {
      items[a] = dropper.items[a];
    }
    items[9] = new Item(dropID, 0);
    return items;
  }

  public void useBlock(Client client, Coords c) {
    synchronized(client.lock) {
      client.container = (ExtraContainer)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.DROPPER);
      if (client.container == null) {
        Static.log("BlockDropper.useBlock():Error:Can not find extra data");
        return;
      }
      client.chunk = c.chunk;
      client.menu = Client.DROPPER;
      client.serverTransport.setContainer(c.cx, c.cz, client.container);
      client.serverTransport.enterMenu(client.DROPPER);
    }
  }

  private static Coords c = new Coords();
  private Random r = new Random();

  public void tick(Chunk chunk, Tick tick) {
    tick.toWorldCoords(chunk, c);
    int bits = chunk.getBits(c.gx, c.gy, c.gz);
    c.dir = Chunk.getDir(bits);
    ExtraDropper dropper = (ExtraDropper)chunk.getExtra(c.gx, c.gy, c.gz, Extras.DROPPER);
    if (dropper == null) {
      Static.log("BlockDropper.tick():Error:Can not find extra data (dropper)");
      return;
    }
    ExtraRedstone er = (ExtraRedstone)chunk.getExtra(c.gx, c.gy, c.gz, Extras.REDSTONE);
    if (er == null) {
      Static.log("BlockDropper.tick():Error:Can not find extra data (redstone)");
      return;
    }
    //drop item
    float x = c.x + 0.5f;
    float y = c.y + 0.25f;
    float z = c.z + 0.5f;
    synchronized(dropper) {
      //edge level triggering
      boolean triggered = false;
      if (!er.active) {
        if (er.powered) {
          er.active = true;
          triggered = true;
        }
      } else {
        if (!er.powered) {
          er.active = false;
        }
      }
      if (dropper.dropCooldown > 0) {
        dropper.dropCooldown--;
        return;
      }
      if (!triggered) return;
      //push 1 item (cooldown)
      c.adjacentBlock();
      int src_start = r.nextInt(9);
      int src_idx = src_start;
      do {
        if (!dropper.items[src_idx].isEmpty()) break;
        src_idx++;
        if (src_idx == 9) src_idx = 0;
      } while (src_idx != src_start);
      if (dropper.items[src_idx].isEmpty()) return;  //nothing to push
      Item src_item = dropper.items[src_idx];
      ItemBase base = Static.items.items[src_item.id];
      int maxStack = base.maxStack;
      {
        ExtraChest chest = (ExtraChest)chunk.getExtra(c.gx, c.gy, c.gz, Extras.CHEST);
        if (chest != null) {
          synchronized(chest) {
            int cnt = chest.items.length;
            int dst_start = 0;
            int dst_idx = dst_start;
            do {
              //try to push item from src_idx to dst_idx
              if (chest.items[dst_idx].isEmpty()) {
                chest.items[dst_idx].copy(src_item, (byte)1);
                src_item.count--;
                if (src_item.count == 0) {
                  src_item.clear();
                }
                Static.server.broadcastContainerChange(chest, chunk.cx, chunk.cz);
                Static.server.broadcastContainerChange(dropper, chunk.cx, chunk.cz);
                dropper.dropCooldown = cooldownTicks;
                return;
              } else if (chest.items[dst_idx].equals(src_item)) {
                if (chest.items[dst_idx].count == maxStack) continue;  //stack full
                chest.items[dst_idx].count++;
                src_item.count--;
                if (src_item.count == 0) {
                  src_item.clear();
                }
                Static.server.broadcastContainerChange(chest, chunk.cx, chunk.cz);
                Static.server.broadcastContainerChange(dropper, chunk.cx, chunk.cz);
                dropper.dropCooldown = cooldownTicks;
                return;
              }
              dst_idx++;
              if (dst_idx == cnt) dst_idx = 0;
            } while (dst_idx != dst_start);
          }
        }
      }
      {
        ExtraHopper hopper = (ExtraHopper)chunk.getExtra(c.gx, c.gy, c.gz, Extras.HOPPER);
        if (hopper != null) {
          synchronized(hopper) {
            int cnt = hopper.items.length;
            int dst_start = 0;
            int dst_idx = dst_start;
            do {
              //try to push item from src_idx to dst_idx
              if (hopper.items[dst_idx].isEmpty()) {
                hopper.items[dst_idx].copy(src_item, (byte)1);
                src_item.count--;
                if (src_item.count == 0) {
                  src_item.clear();
                }
                Static.server.broadcastContainerChange(hopper, chunk.cx, chunk.cz);
                Static.server.broadcastContainerChange(dropper, chunk.cx, chunk.cz);
                dropper.dropCooldown = cooldownTicks;
                return;
              } else if (hopper.items[dst_idx].equals(src_item)) {
                if (hopper.items[dst_idx].count == maxStack) continue;  //stack full
                hopper.items[dst_idx].count++;
                src_item.count--;
                if (src_item.count == 0) {
                  src_item.clear();
                }
                Static.server.broadcastContainerChange(hopper, chunk.cx, chunk.cz);
                Static.server.broadcastContainerChange(dropper, chunk.cx, chunk.cz);
                dropper.dropCooldown = cooldownTicks;
                return;
              }
              dst_idx++;
              if (dst_idx == cnt) dst_idx = 0;
            } while (dst_idx != dst_start);
          }
        }
      }
      //just drop item
      item1.copy(src_item, (byte)1);
      drop(item1, x,y,z, chunk, c.dir);
      src_item.count--;
      if (src_item.count == 0) {
        src_item.clear();
      }
      Static.server.broadcastContainerChange(dropper, chunk.cx, chunk.cz);
      dropper.dropCooldown = cooldownTicks;
    }
  }

  private static Item item1 = new Item();

  public void drop(Item item, float x, float y, float z, Chunk chunk, int dir) {
    switch (dir) {
      case N: z -= 0.7f; break;
      case E: x += 0.7f; break;
      case S: z += 0.7f; break;
      case W: x -= 0.7f; break;
      case A: y += 0.75f; break;
      case B: y -= 0.25f; break;
    }
    WorldItem.create(item, chunk.dim, x, y, z, chunk, dir);
  }
  public int getPreferredDir() {
    return S;
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
    World world = Static.world();
    pl = world.getPowerLevel(dim,x,y+1,z,c); if (pl > powerLevel) powerLevel = pl;
    pl = world.getPowerLevel(dim,x,y-1,z,c); if (pl > powerLevel) powerLevel = pl;
    pl = world.getPowerLevel(dim,x+1,y,z,c); if (pl > powerLevel) powerLevel = pl;
    pl = world.getPowerLevel(dim,x-1,y,z,c); if (pl > powerLevel) powerLevel = pl;
    pl = world.getPowerLevel(dim,x,y,z+1,c); if (pl > powerLevel) powerLevel = pl;
    pl = world.getPowerLevel(dim,x,y,z-1,c); if (pl > powerLevel) powerLevel = pl;

    pl = world.getPowerLevel(dim,x+1,y-1,z,c); if (pl > powerLevel) powerLevel = pl;
    pl = world.getPowerLevel(dim,x-1,y-1,z,c); if (pl > powerLevel) powerLevel = pl;
    pl = world.getPowerLevel(dim,x,y-1,z+1,c); if (pl > powerLevel) powerLevel = pl;
    pl = world.getPowerLevel(dim,x,y-1,z-1,c); if (pl > powerLevel) powerLevel = pl;

    if (powerLevel == 0 && er.powered) {
      powerOff(null, c);
    } else if (powerLevel > 0 && !er.powered) {
      c.powerLevel = powerLevel;
      powerOn(null, c);
    }
  }

}
