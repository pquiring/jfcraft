package jfcraft.packet;

/** Packet with no data
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.server.Server;
import jfcraft.item.*;

public class PacketTrashHand extends Packet {

  public PacketTrashHand() {}

  public PacketTrashHand(byte cmd) {
    super(cmd);
  }

  //process on server side
  public void process(Server server, Client client) {
    synchronized(client.lock) {
      if (!Settings.current.creativeMode) {
        Static.log("TrashHand:Player cheat:creative mode disabled");
        return;
      }
      if (!client.player.creative) {
        Static.log("TrashHand:Player cheat:not in creative mode");
        return;
      }
      client.hand.clear();
      client.serverTransport.setHand(client.hand);
    }
  }
}
