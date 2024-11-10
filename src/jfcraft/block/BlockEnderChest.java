package jfcraft.block;

/** Block Ender Chest
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.item.*;
import jfcraft.entity.*;
import jfcraft.opengl.*;

public class BlockEnderChest extends BlockBase {
  public BlockEnderChest(String name) {
    super(name, new String[] {"Ender Chest"}, new String[0]);
    canReplace = false;
    isOpaque = false;
    canUse = true;
    renderAsEntity = true;
    isDir = true;
    isDirXZ = true;
    isComplex = true;
    isSolid = false;
  }
  public void getIDs(World world) {
    super.getIDs(world);
    entityID = Entities.ENDER_CHEST;
  }
  public void buildBuffers(RenderDest dest) {
    Coords c = new Coords();
    c.setPos(Static.data.x + Static.data.chunk.cx * 16, Static.data.y, Static.data.z + Static.data.chunk.cz * 16);
    EnderChest chest = (EnderChest)Static.data.chunk.findBlockEntity(Entities.ENDER_CHEST, c);
    if (chest == null) {
      return;
    }
    chest.buildBuffers(chest.getDest());
    chest.needCopyBuffers = true;
  }
  public boolean place(Client client, Coords c) {
    World world = Static.server.world;
    EnderChest echest = new EnderChest();
    echest.init(world);
    echest.pos.x = ((float)c.x) + 0.5f;
    echest.pos.y = ((float)c.y) + 0.5f;
    echest.pos.z = ((float)c.z) + 0.5f;
    echest.gx = c.gx;
    echest.gy = c.gy;
    echest.gz = c.gz;
    echest.ang.y = c.getYAngle();
    echest.uid = world.generateUID();
    c.chunk.addEntity(echest);
    world.addEntity(echest);
    Static.server.broadcastEntitySpawn(echest);
    return super.place(client, c);
  }
  public void useBlock(Client client, Coords c) {
    synchronized(client.lock) {
      client.container = client.player.enderChest;
      client.chunk = c.chunk;
      client.menu = Client.CHEST;
      client.serverTransport.openEnderChest();
      client.serverTransport.enterMenu(client.CHEST);
    }
  }
  public void destroy(Client client, Coords c, boolean doDrop) {
    super.destroy(client, c, doDrop);
    //find and remove entity
    EntityBase e = c.chunk.findBlockEntity(Entities.ENDER_CHEST, c);
    if (e != null) {
      c.chunk.delEntity(e);
      Static.server.world.delEntity(e.uid);
      Static.server.broadcastEntityDespawn(e);
    }
  }
}
