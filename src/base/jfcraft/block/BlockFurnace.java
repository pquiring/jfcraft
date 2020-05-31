package jfcraft.block;

/** Furnace block
 *
 * @author pquiring
 *
 * Created : May 10, 2014
 */

import javaforce.*;

import jfcraft.client.*;
import jfcraft.data.*;
import jfcraft.item.*;

public class BlockFurnace extends BlockOpaque {
  public BlockFurnace(String id, String names[], String images[]) {
    super(id,names,images);
    canUse = true;
    isDir = true;
    isDirXZ = true;
    reverseDir = true;
  }
  public boolean place(Client client, Coords c) {
    ExtraFurnace furnace = new ExtraFurnace(c.gx, c.gy, c.gz);
    c.chunk.addExtra(furnace);
    c.chunk.addTick(c, false);
    return super.place(client, c);
  }
  private static Coords c = new Coords();
  public void tick(Chunk chunk, Tick tick) {
    tick.toWorldCoords(chunk, c);
    ExtraFurnace furnace = (ExtraFurnace)chunk.getExtra(c.gx, c.gy, c.gz, Extras.FURNACE);
    if (furnace == null) {
      Static.log("BlockFurnace.tick():Error:Can not find extra data");
      return;
    }
//    Static.log("Furnace.tick()");
    synchronized(furnace) {
      try {
        if (furnace.timer > 0) {
          furnace.timer--;
          char in = furnace.items[ExtraFurnace.INPUT].id;
          char out = furnace.items[ExtraFurnace.OUTPUT].id;
          if (furnace.timer == 0 && in != 0) {
            //done
            ItemBase inItem = Static.items.items[in];
            if (inItem.canBake) {
              Item baked = inItem.bake();
              boolean done = false;
              if (baked != null) {
                if (out != 0) {
                  ItemBase outItem = Static.items.items[out];
                  if (furnace.items[ExtraFurnace.OUTPUT].equals(baked)) {
                    if (furnace.items[ExtraFurnace.OUTPUT].count < outItem.maxStack) {
                      furnace.items[ExtraFurnace.OUTPUT].count++;
                      done = true;
                    }
                  }
                } else {
                  furnace.items[ExtraFurnace.OUTPUT].copy(baked, (byte)1);
                  done = true;
                }
                if (done) {
                  furnace.items[ExtraFurnace.INPUT].count--;
                  if (furnace.items[ExtraFurnace.INPUT].count == 0) {
                    furnace.items[ExtraFurnace.INPUT].clear();
                  }
                }
              }
            }
          }
        }
        if (furnace.heat > 0) {
          furnace.heat--;
          if (furnace.heat == 0) {
            furnace.timer = 0;
            furnace.heatMax = 0;
          }
        }
        if (furnace.timer == 0) {
          char in = furnace.items[ExtraFurnace.INPUT].id;
          char fuel = furnace.items[ExtraFurnace.FUEL].id;
          char out = furnace.items[ExtraFurnace.OUTPUT].id;
          if (in == 0 || (furnace.heat == 0 && fuel == 0)) throw new Exception("furnace:no input");
          ItemBase inItem = Static.items.items[in];
          ItemBase outItem = null;
          if (out != 0) outItem = Static.items.items[out];
          if (!inItem.canBake) throw new Exception("furnace:input doesn't bake");  //should not happen
          Item baked = inItem.bake();
          if (baked == null) throw new Exception("furnace:input has no baked item");  //should not happen
          if ((furnace.items[ExtraFurnace.OUTPUT].count > 0) && (outItem != null) && (!furnace.items[ExtraFurnace.OUTPUT].equals(baked))) {
            Static.log("output=" + (int)outItem.id);
            Static.log("bake=" + (int)baked.id + "," + baked.var);
            throw new Exception("furnace:can not bake more");
          }
          if (furnace.heat == 0) {
            ItemBase fuelItem = Static.items.items[fuel];
            if (!fuelItem.isFuel) throw new Exception("furnace:fuel is not consumable");  //should not happen
            //use fuel
            furnace.heat = fuelItem.heat;
            furnace.heatMax = furnace.heat;
            furnace.items[ExtraFurnace.FUEL].count--;
            if (furnace.items[ExtraFurnace.FUEL].count == 0) {
              furnace.items[ExtraFurnace.FUEL].clear();
            }
          }
          furnace.timer = 200;
        }
      } catch (Exception e) {
        //exceptions are normal
//        Static.log(e);
      }
    }
    //broadcast only to those who are viewing this furnace
    Static.server.broadcastContainerChange(furnace, c.cx, c.cz);
  }
  public void useBlock(Client client, Coords c) {
    synchronized(client.lock) {
      client.container = (ExtraContainer)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.FURNACE);
      if (client.container == null) {
        Static.log("BlockFurace.useBlock():Error:Can not find extra data");
        return;
      }
      client.chunk = c.chunk;
      client.menu = Client.FURNACE;
      client.serverTransport.setContainer(c.cx, c.cz, client.container);
      client.serverTransport.enterMenu(client.menu);
    }
  }
  public void destroy(Client client, Coords c, boolean doDrop) {
    super.destroy(client, c, doDrop);
    c.chunk.delExtra(c, Extras.FURNACE);
  }
  public Item[] drop(Coords c, int var) {
    ExtraFurnace furnace = (ExtraFurnace)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.FURNACE);
    if (furnace == null) {
      Static.log("BlockFurnace.drop():Error:Can not find extra data");
      return new Item[] {new Item(dropID, 0)};
    }
    Item[] items = new Item[4];
    for(int a=0;a<3;a++) {
      items[a] = furnace.items[a];
    }
    items[3] = new Item(dropID, 0);
    return items;
  }
}
