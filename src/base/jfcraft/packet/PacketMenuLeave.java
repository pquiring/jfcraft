package jfcraft.packet;

/** Packet with no data
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.server.Server;
import jfcraft.opengl.*;

public class PacketMenuLeave extends Packet {

  public PacketMenuLeave() {}

  public PacketMenuLeave(byte cmd) {
    super(cmd);
  }

  //process on client side
  public void process(Client client) {
    client.bedtime = 0;
    Static.game.leaveMenu();
    client.container = null;
  }

  //process on server side
  public void process(Server server, Client client) {
    synchronized(client.lock) {
      if (client.hand != null) {
        client.dropItem(client.hand);
        client.hand = null;
        client.serverTransport.setHand(null);
      }
      for(byte a=0;a<9;a++) {
        if (client.craft[a].id == 0) continue;
        client.dropItem(client.craft[a]);
        client.craft[a].clear();
        client.serverTransport.setCraftItem(a, client.craft[a]);
      }
      server.updateCrafted(client);
      client.container = null;
      client.chunk = null;
      client.menu = Client.GAME;
    }
  }
}
