package jfcraft.packet;

/** Packet with no data
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.server.Server;

public class PacketMenuInv extends Packet {

  public PacketMenuInv() {}

  public PacketMenuInv(byte cmd) {
    super(cmd);
  }

  //process on server side
  public void process(Server server, Client client) {
    synchronized(client.lock) {
      client.menu = Client.INVENTORY;
    }
  }
}
