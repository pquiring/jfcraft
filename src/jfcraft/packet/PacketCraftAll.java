package jfcraft.packet;

/** Packet with no data
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.server.Server;
import jfcraft.item.*;

public class PacketCraftAll extends Packet {

  public PacketCraftAll() {}

  public PacketCraftAll(byte cmd) {
    super(cmd);
  }

  //process on server side
  public void process(Server server, Client client) {
    synchronized(client.lock) {
      Item crafted = null, craftedFirst = null;
      do {
        if (client.menu == Client.INVENTORY) crafted = Static.recipes.make2x2(client.craft);
        if (client.menu == Client.CRAFTTABLE) crafted = Static.recipes.make3x3(client.craft);
        if (client.menu == Client.VILLAGER) crafted = Static.client.villager.getOffer(Static.client.craft);
        if (crafted == null) break;
        if (craftedFirst == null) {
          craftedFirst = crafted;
        }
        if (!crafted.equals(craftedFirst)) break;  //making something else
        ItemBase itembase = Static.items.items[crafted.id];
        if (client.hand != null && client.hand.equals(crafted) && client.hand.count + crafted.count <= itembase.maxStack) {
          client.hand.count += crafted.count;
        } else {
          if (client.hand == null) {
            client.hand = crafted;
          } else {
            //put somewhere else in inventory
            if (!client.addItem(crafted, false)) break;
          }
        }
        for(int a=0;a<9;a++) {
          if (client.craft[a].count == 0) continue;
          client.craft[a].count--;
          if (client.craft[a].count == 0) {
            client.craft[a].clear();
          }
        }
      } while (true);
      if (craftedFirst == null) return;  //nothing crafted
      client.serverTransport.setHand(client.hand);
      for(byte a=0;a<4*9;a++) {
        if (client.player.items[a].equals(craftedFirst.id)) {
          client.serverTransport.setInvItem(a, client.player.items[a]);
        }
      }
      int cnt = 4;
      if (client.menu == Client.CRAFTTABLE) cnt = 9;
      for(byte a=0;a<cnt;a++) {
        client.serverTransport.setCraftItem(a, client.craft[a]);
      }
      server.updateCrafted(client);
    }
  }
}
