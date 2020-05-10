package jfcraft.item;

/** Bucket
 *
 * TODO : coords are currently only at block + 1 above
 *
 * @author pquiring
 */

import jfcraft.client.*;
import jfcraft.block.*;
import jfcraft.data.*;
import static jfcraft.data.Direction.*;

public class ItemBucket extends ItemBase {
  private String filled;
  private char filledid;
  public ItemBucket(String name, String names[], String textures[]) {
    super(name,names,textures);
    isTool = true;
    maxStack = 1;
  }
  public ItemBucket setFilled(String filled) {
    this.filled = filled;
    return this;
  }
  public ItemBucket setCanUseWater() {
    canUseLiquids = true;
    return this;
  }
  public void getIDs(World world) {
    if (filled != null) {
      filledid = world.getBlockID(filled);
    }
  }
  public boolean useItem(Client client, Coords c) {
    if (c == null) return false;
    byte slot = (byte)client.player.activeSlot;
    if (filledid != Blocks.AIR) {
      c.otherSide();
      c.adjacentBlock();
      c.block = c.chunk.getBlock(c.gx, c.gy, c.gz);
      //place contents @ coords
      if (c.chunk.setBlockIfEmpty(c.gx, c.gy, c.gz, filledid, 0)) {
        client.player.items[client.player.activeSlot].id = Items.BUCKET;
        client.serverTransport.setInvItem(slot, client.player.items[slot]);
        client.serverTransport.setHand(client.hand);
        Static.server.broadcastSetBlock(c.chunk.dim,c.x,c.y,c.z,filledid,0);
        return true;
      }
    } else {
      if (c.block == null) return false;
      if (c.block.id == Blocks.WATER) {
        if (c.chunk.clearBlockIf2(c.gx,c.gy,c.gz,Blocks.WATER)) {
          //replace item with BUCKET_WATER
          client.player.items[client.player.activeSlot].id = Items.BUCKET_WATER;
          client.serverTransport.setInvItem(slot, client.player.items[slot]);
          client.serverTransport.setHand(client.hand);
          Static.server.broadcastClearBlock2(c.chunk.dim,c.x,c.y,c.z);
          return true;
        }
      }
      if (c.block.id == Blocks.LAVA) {
        if (c.chunk.clearBlockIf2(c.gx,c.gy,c.gz,Blocks.LAVA)) {
          //replace item with BUCKET_WATER
          client.player.items[client.player.activeSlot].id = Items.BUCKET_LAVA;
          client.serverTransport.setInvItem(slot, client.player.items[slot]);
          client.serverTransport.setHand(client.hand);
          Static.server.broadcastClearBlock2(c.chunk.dim,c.x,c.y,c.z);
          return true;
        }
      }
    }
    return false;
  }
}
