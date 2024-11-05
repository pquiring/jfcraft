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
    reverseDir = true;
  }
  public void getIDs(World world) {
    super.getIDs(world);
    entityID = Entities.CHEST;
  }
  public void buildBuffers(RenderDest dest) {
    Coords c = new Coords();
    c.setPos(Static.data.x + Static.data.chunk.cx * 16, Static.data.y, Static.data.z + Static.data.chunk.cz * 16);
    Chest chest = (Chest)Static.data.chunk.findBlockEntity(Entities.CHEST, c);
    if (chest == null) {
      return;
    }
    chest.buildBuffers(chest.getDest());
    chest.needCopyBuffers = true;
  }
  public boolean place(Client client, Coords c) {
    super.place(client, c);  //this will reverse side
    World world = Static.server.world;
    Static.log("chest place");
    ExtraChest extra = new ExtraChest(c.gx, c.gy, c.gz, 3*9);
    c.chunk.addExtra(extra);
    Chest chest = new Chest();
    chest.init(world);
    chest.dim = c.chunk.dim;
    chest.pos.x = ((float)c.x) + 0.5f;
    chest.pos.y = ((float)c.y) + 0.5f;
    chest.pos.z = ((float)c.z) + 0.5f;
    chest.gx = c.gx;
    chest.gy = c.gy;
    chest.gz = c.gz;
    chest.ang.y = c.getYAngle();
    chest.uid = world.generateUID();
    c.chunk.addEntity(chest);
    world.addEntity(chest);
    Static.server.broadcastEntitySpawn(chest);
    return true;
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
      Static.server.world.delEntity(e.uid);
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
  public SubTexture getDestroyTexture(int var) {
    return Static.blocks.blocks[Blocks.PLANKS].textures[0];
  }
}
