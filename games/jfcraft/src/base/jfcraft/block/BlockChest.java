package jfcraft.block;

/** Block Chest
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.item.*;
import jfcraft.entity.*;
import jfcraft.opengl.*;

public class BlockChest extends BlockBase {
  public BlockChest(String name) {
    super(name, new String[] {"Chest"}, new String[0]);
    canReplace = false;
    isOpaque = false;
    canUse = true;
    renderAsEntity = true;
    isDir = true;
    isDirXZ = true;
    isComplex = true;
    isSolid = false;
  }
  public void getIDs() {
    super.getIDs();
    entityID = Entities.CHEST;
  }
  public void buildBuffers(RenderDest dest, RenderData data) {
    Coords c = new Coords();
    c.setPos(data.x + data.chunk.cx * 16, data.y, data.z + data.chunk.cz * 16);
    Chest chest = (Chest)data.chunk.findBlockEntity(Entities.CHEST, c);
    if (chest == null) {
      return;
    }
    RenderData data2 = new RenderData();
    data2.crack = data.crack;
    chest.buildBuffers(chest.getDest(), data2);
    chest.needCopyBuffers = true;
  }
  public boolean place(Client client, Coords c) {
    Static.log("chest place");
    ExtraChest extra = new ExtraChest(c.gx, c.gy, c.gz, 3*9);
    c.chunk.addExtra(extra);
    Chest chest = new Chest();
    chest.init();
    chest.dim = c.chunk.dim;
    chest.pos.x = ((float)c.x) + 0.5f;
    chest.pos.y = ((float)c.y) + 0.5f;
    chest.pos.z = ((float)c.z) + 0.5f;
    chest.gx = c.gx;
    chest.gy = c.gy;
    chest.gz = c.gz;
    chest.ang.y = c.getYAngle();
    chest.uid = Static.world().generateUID();
    c.chunk.addEntity(chest);
    Static.world().addEntity(chest);
    Static.server.broadcastEntitySpawn(chest);
    return super.place(client, c);
  }
  public void useBlock(Client client, Coords c) {
    synchronized(client.lock) {
      client.container = (ExtraContainer)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.CHEST);
      if (client.container == null) {
        Static.log("chest item not found");
        return;
      }
      client.chunk = c.chunk;
      client.menu = Client.CHEST;
      client.serverTransport.setContainer(c.cx, c.cz, client.container);
      client.serverTransport.enterMenu(client.CHEST);
    }
  }
  public void destroy(Client client, Coords c, boolean doDrop) {
    //find and remove entity
    EntityBase e = c.chunk.findBlockEntity(Entities.CHEST, c);
    if (e != null) {
      c.chunk.delEntity(e);
      Static.world().delEntity(e.uid);
      Static.server.broadcastEntityDespawn(e);
    } else {
      Static.log("Error:BlockChest.destroy():Entity not found");
    }
    super.destroy(client, c, doDrop);
    c.chunk.delExtra(c, Extras.CHEST);
  }
  public Item[] drop(Coords c, int var) {
    ExtraChest chest = (ExtraChest)c.chunk.getExtra(c.gx, c.gy, c.gz, Extras.CHEST);
    if (chest == null) {
      Static.log("BlockChest.drop():Error:Can not find extra data");
      return new Item[] {new Item(Blocks.CHEST)};
    }
    Item drops[] = new Item[chest.items.length + 1];
    System.arraycopy(chest.items, 0, drops, 0, chest.items.length);
    drops[chest.items.length] = new Item(Blocks.CHEST);
    return drops;
  }
}
