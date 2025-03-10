package jfcraft.packet;

/** Packet with no data
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.server.Server;
import jfcraft.item.*;

public class PacketCraftOne extends Packet {

  public PacketCraftOne() {}

  public PacketCraftOne(byte cmd) {
    super(cmd);
  }

  //process on server side
  public void process(Server server, Client client) {
    synchronized(client.lock) {
      Item crafted = null;
      if (client.menu == Client.INVENTORY) crafted = Static.recipes.make2x2(client.craft);
      if (client.menu == Client.CRAFTTABLE) crafted = Static.recipes.make3x3(client.craft);
      if (client.menu == Client.VILLAGER) crafted = Static.client.villager.getOffer(client, client.craft, false);
      if (crafted == null) {
        Static.log(":nothing to craft");
        return;
      }
      ItemBase itembase = Static.items.items[crafted.id];
      if (client.hand != null) {
        if (!client.hand.equals(crafted)) {
          Static.log(":hand not the same");
          return;
        }
        if (client.hand.count + crafted.count > itembase.maxStack) {
          Static.log(":can not hold item");
          return;
        }
        client.hand.count += crafted.count;
      } else {
        client.hand = crafted;
      }
      client.serverTransport.setHand(client.hand);
      if (client.menu == Client.VILLAGER) {
        Static.client.villager.getOffer(client, client.craft, true);
      } else {
        Static.recipes.take(client.craft);
      }
      int cnt = 4;
      if (client.menu == Client.CRAFTTABLE) cnt = 9;
      for(byte a=0;a<cnt;a++) {
        client.serverTransport.setCraftItem(a, client.craft[a]);
      }
      server.updateCrafted(client);
    }
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    return true;
  }
}
