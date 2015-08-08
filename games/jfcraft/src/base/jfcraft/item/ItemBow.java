package jfcraft.item;

/** Bow
 *
 * @author pquiring
 */

import jfcraft.client.*;
import jfcraft.data.*;
import jfcraft.entity.*;
import static jfcraft.data.Types.*;

public class ItemBow extends ItemBase {
  private static final int maxPower = 2 * 20;  //2 secs
  public ItemBow(String name, String names[], String texture[]) {
    super(name, names, texture);
    useRelease = true;
    isWeapon = true;
    weapon = WEAPON_BOW;
  }
  public void useItem(Client client) {
    if (client.bowPower < maxPower) {
      client.bowPower++;
    }
    Static.log("bowPower=" + client.bowPower);
  }
  public void releaseItem(Client client) {
    if (client.bowPower > 0) {
      Static.log("shootArrow");
      shootArrow(client);
    }
    client.bowPower = 0;
  }
  private static Vectors v = new Vectors();
  //uses static v so MUST be sync'ed
  private synchronized void shootArrow(Client client) {
    Item bow = client.player.items[client.player.activeSlot];
    if (bow.id != Items.BOW) return;  //???
    if (!client.player.creative) {
      boolean ok = false;
      for(int a=0;a<client.player.items.length;a++) {
        Item arrow = client.player.items[a];
        if (arrow.id == Items.ARROW) {
          arrow.count--;
          if (arrow.count == 0) {
            arrow.clear();
          }
          client.serverTransport.setInvItem((byte)a, arrow);
          ok = true;
          break;
        }
      }
      if (!ok) {
        Static.log("no arrow to shoot");
        return;
      }
    }
    Chunk c = client.player.getChunk();
    Arrow e = new Arrow();
    e.setOwner(client.player);
    e.init(Static.server.world);
    e.dim = c.dim;
    e.uid = Static.server.world.generateUID();
    //position should be out of player's hitbox area
    client.player.calcVectors(1, v);
    e.pos.x = client.player.pos.x + v.facing.v[0];
    e.pos.y = client.player.pos.y + client.player.eyeHeight + v.facing.v[1];
    e.pos.z = client.player.pos.z + v.facing.v[2];
    e.ang.x = client.player.ang.x;
    e.ang.y = client.player.ang.y;
    e.ang.z = client.player.ang.z;
    //max velocity = 60m/s
    e.vel.x = v.facing.v[0] * client.bowPower * 1.5f / 20f;
    e.vel.y = v.facing.v[1] * client.bowPower * 1.5f / 20f;
    e.vel.z = v.facing.v[2] * client.bowPower * 1.5f / 20f;
    c.addEntity(e);
    Static.server.world.addEntity(e);
    Static.server.broadcastEntitySpawn(e);
  }
}
