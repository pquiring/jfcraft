package jfcraft.block;

/** Hopper
 *
 * @author pquiring
 *
 * Created : May 1, 2015
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

public class BlockHopper extends BlockBase {
  private static final int cooldownTicks = 8;
  private static Model model;
  public BlockHopper(String id, String names[], String images[]) {
    super(id, names, images);
    isOpaque = false;
    isAlpha = false;
    isDirFace = true;
    isComplex = true;
    isSolid = false;
    canUse = true;
    cantGive = true;
    dropBlock = "HOPPER_ITEM";
    model = Assets.getModel("hopper").model;
  }

  public void buildBuffers(RenderDest dest) {
    RenderBuffers buf = dest.getBuffers(buffersIdx);
    Static.data.norotate = true;  //do not rotate based on dir
    buildBuffers(model.getObject("TOP"), buf, textures[0]);
    buildBuffers(model.getObject("INSIDE"), buf, textures[1]);
    buildBuffers(model.getObject("SIDES"), buf, textures[2]);
    switch (Static.data.dir[X]) {
      case B: Static.data.translate_pre = new float[] {0,-4f * 0.0625f,0}; break;
      case N: Static.data.translate_pre = new float[] {0,0,-5f * 0.0625f}; break;
      case E: Static.data.translate_pre = new float[] {+5f * 0.0625f,0,0}; break;
      case S: Static.data.translate_pre = new float[] {0,0,+5f * 0.0625f}; break;
      case W: Static.data.translate_pre = new float[] {-5f * 0.0625f,0,0}; break;
    }
    buildBuffers(model.getObject("SPOUT"), buf, textures[2]);
    Static.data.translate_pre = null;
    Static.data.norotate = false;
  }

  public boolean place(Client client, Coords c) {
    if (c.dir == A) return false;
    ExtraHopper hopper = new ExtraHopper(c.gx, c.gy, c.gz);
    c.chunk.addExtra(hopper);
    c.chunk.addTick(c, false);
    return super.place(client, c);
  }

  public void destroy(Client client, Coords c, boolean doDrop) {
    super.destroy(client, c, doDrop);
    c.chunk.delExtra(c, Extras.HOPPER);
  }

  public Item[] drop(Coords c, int var) {
    ExtraHopper hopper = (ExtraHopper)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.HOPPER);
    if (hopper == null) {
      Static.log("BlockHopper.drop():Error:Can not find extra data");
      return new Item[] {new Item(dropID, 0)};
    }
    Item[] items = new Item[6];
    for(int a=0;a<5;a++) {
      items[a] = hopper.items[a];
    }
    items[5] = new Item(dropID, 0);
    return items;
  }

  public void useBlock(Client client, Coords c) {
    synchronized(client.lock) {
      client.container = (ExtraContainer)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.HOPPER);
      if (client.container == null) {
        Static.log("BlockHopper.useBlock():Error:Can not find extra data");
        return;
      }
      client.chunk = c.chunk;
      client.menu = Client.HOPPER;
      client.serverTransport.setContainer(c.cx, c.cz, client.container);
      client.serverTransport.enterMenu(client.HOPPER);
    }
  }

  private static Coords c = new Coords();

  public void tick(Chunk chunk, Tick tick) {
    tick.toWorldCoords(chunk, c);
    int bits = chunk.getBits(c.gx, c.gy, c.gz);
    c.dir = Chunk.getDir(bits);
    ExtraHopper hopper = (ExtraHopper)chunk.getExtra(c.gx, c.gy, c.gz, Extras.HOPPER);
    if (hopper == null) {
      Static.log("BlockHopper.tick():Error:Can not find extra data");
      return;
    }
    //suck items (any time)
    float x = c.x; x += 0.5f;
    float y = c.y; y += 0.5f;
    float z = c.z; z += 0.5f;
    synchronized(hopper) {
      EntityBase entities[] = c.chunk.getEntities();
      for(int a=0;a<entities.length;a++) {
        if (entities[a].id == Entities.WORLDITEM) {
          WorldItem wi = (WorldItem)entities[a];
          synchronized(wi) {
            if (wi.hitBox(x, y, z, 0.5f, 0.5f, 0.5f)) {
              if (addItem(wi.item, hopper, wi.item.count) > 0) {
                if (wi.item.isEmpty()) {
                  //delete wi
                  if (wi.item.isEmpty()) {
                    chunk.delEntity(wi);
                    Static.server.world.delEntity(wi.uid);
                    Static.server.broadcastEntityDespawn(wi);
                  }
                } else {
                  //TODO : broadcastWorldItemCountChanged() : not necessary
                }
              }
            }
          }
        }
      }

      if (hopper.transferCooldown > 0) {
        hopper.transferCooldown--;
        return;
      }
      //pull 1 item
      c.gy++;
      boolean pulled = false;
      if (!pulled) {
        ExtraChest chest = (ExtraChest)chunk.getExtra(c.gx, c.gy, c.gz, Extras.CHEST);
        if (chest != null) {
          synchronized(chest) {
            int cnt = chest.items.length;
            int start = 0;
            int idx = start;
            do {
              //try to pull item from chest @ idx
              if (addItem(chest.items[idx], hopper, 1) > 0) {
                Static.server.broadcastContainerChange(chest, chunk.cx, chunk.cz);
                Static.server.broadcastContainerChange(hopper, chunk.cx, chunk.cz);
                hopper.transferCooldown = cooldownTicks;
                pulled = true;
                break;
              }
              idx++;
              if (idx == cnt) idx = 0;
            } while (idx != start);
          }
        }
      }
      if (!pulled) {
        ExtraHopper hopper2 = (ExtraHopper)chunk.getExtra(c.gx, c.gy, c.gz, Extras.HOPPER);
        if (hopper2 != null) {
          synchronized(hopper2) {
            int cnt = hopper2.items.length;
            int start = 0;
            int idx = start;
            do {
              //try to pull item from hopper @ idx
              if (addItem(hopper2.items[idx], hopper, 1) > 0) {
                Static.server.broadcastContainerChange(hopper2, chunk.cx, chunk.cz);
                Static.server.broadcastContainerChange(hopper, chunk.cx, chunk.cz);
                hopper.transferCooldown = cooldownTicks;
                pulled = true;
                break;
              }
              idx++;
              if (idx == cnt) idx = 0;
            } while (idx != start);
          }
        }
      }
      c.gy--;
      //push 1 item (cooldown)
      c.adjacentBlock();
      int src_start = 0;
      int src_idx = src_start;
      do {
        if (!hopper.items[src_idx].isEmpty()) break;
        src_idx++;
        if (src_idx == 5) src_idx = 0;
      } while (src_idx != src_start);
      if (hopper.items[src_idx].isEmpty()) return;  //nothing to push
      Item src_item = hopper.items[src_idx];
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
                Static.server.broadcastContainerChange(hopper, chunk.cx, chunk.cz);
                hopper.transferCooldown = cooldownTicks;
                return;
              } else if (chest.items[dst_idx].equals(src_item)) {
                if (chest.items[dst_idx].count == maxStack) continue;  //stack full
                chest.items[dst_idx].count++;
                src_item.count--;
                if (src_item.count == 0) {
                  src_item.clear();
                }
                Static.server.broadcastContainerChange(chest, chunk.cx, chunk.cz);
                Static.server.broadcastContainerChange(hopper, chunk.cx, chunk.cz);
                hopper.transferCooldown = cooldownTicks;
                return;
              }
              dst_idx++;
              if (dst_idx == cnt) dst_idx = 0;
            } while (dst_idx != dst_start);
          }
        }
      }
      {
        ExtraHopper hopper2 = (ExtraHopper)chunk.getExtra(c.gx, c.gy, c.gz, Extras.HOPPER);
        if (hopper2 != null) {
          synchronized(hopper2) {
            int cnt = hopper2.items.length;
            int dst_start = 0;
            int dst_idx = dst_start;
            do {
              //try to push item from src_idx to dst_idx
              if (hopper2.items[dst_idx].isEmpty()) {
                hopper2.items[dst_idx].copy(src_item, (byte)1);
                src_item.count--;
                if (src_item.count == 0) {
                  src_item.clear();
                }
                Static.server.broadcastContainerChange(hopper2, chunk.cx, chunk.cz);
                Static.server.broadcastContainerChange(hopper, chunk.cx, chunk.cz);
                hopper.transferCooldown = cooldownTicks;
                return;
              } else if (hopper2.items[dst_idx].equals(src_item)) {
                if (hopper2.items[dst_idx].count == maxStack) continue;  //stack full
                hopper2.items[dst_idx].count++;
                src_item.count--;
                if (src_item.count == 0) {
                  src_item.clear();
                }
                Static.server.broadcastContainerChange(hopper2, chunk.cx, chunk.cz);
                Static.server.broadcastContainerChange(hopper, chunk.cx, chunk.cz);
                hopper.transferCooldown = cooldownTicks;
                return;
              }
              dst_idx++;
              if (dst_idx == cnt) dst_idx = 0;
            } while (dst_idx != dst_start);
          }
        }
      }
    }
  }

  /** Add item to hopper, returns # of items transfered. */
  private int addItem(Item item, ExtraHopper hopper, int maxcnt) {
    if (item.isEmpty()) return 0;
    synchronized(hopper) {
      for(int a=0;a<5;a++) {
        if (hopper.items[a].isEmpty()) {
          int itemCnt = item.count;
          if (itemCnt > maxcnt) {
            itemCnt = maxcnt;
          }
          hopper.items[a].copy(item, (byte)itemCnt);
          item.count -= itemCnt;
          if (item.count == 0) item.clear();
          return hopper.items[a].count;
        }
        if (hopper.items[a].equals(item)) {
          ItemBase base = Static.items.items[item.id];
          int itemCnt = item.count;
          if (itemCnt > maxcnt) {
            itemCnt = maxcnt;
          }
          int newCnt = hopper.items[a].count + itemCnt;
          int amt = itemCnt;
          if (newCnt > base.maxStack) {
            amt = hopper.items[a].count - base.maxStack;
            if (amt == 0) continue;  //no room in this slot
            //copy some
            hopper.items[a].count += amt;
            item.count -= amt;
          } else {
            //copy all (upto maxcnt)
            hopper.items[a].count = (byte)newCnt;
            item.count -= itemCnt;
            if (item.count == 0) {
              item.clear();
            }
          }
          return amt;
        }
      }
    }
    return 0;
  }
}
