package jfcraft.item;

/** Bow
 *
 * @author pquiring
 */

import jfcraft.client.*;
import jfcraft.data.*;
import jfcraft.entity.*;
import jfcraft.packet.*;
import static jfcraft.data.Types.*;

public class ItemBow extends ItemBase {
  private static final int maxPower = 2 * 20;  //2 secs
  public ItemBow(String name, String names[], String texture[]) {
    super(name, names, texture);
    useRelease = true;
    isWeapon = true;
    isVar = true;
    weapon = WEAPON_BOW;
  }
  public boolean useItem(Client client, Coords c) {
    if (client.player.bowPower < maxPower) {
      client.player.bowPower++;
      client.serverTransport.addUpdate(new PacketBow(Packets.BOW, client.player.bowPower));
    }
    Static.log("bowPower=" + client.player.bowPower);
    return true;
  }
  public void releaseItem(Client client) {
    if (client.player.bowPower > 0) {
      Static.log("shootArrow");
      shootArrow(client);
    }
    client.player.bowPower = 0;
    client.serverTransport.addUpdate(new PacketBow(Packets.BOW, client.player.bowPower));
  }
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
    client.player.shootArrow();
  }
}
