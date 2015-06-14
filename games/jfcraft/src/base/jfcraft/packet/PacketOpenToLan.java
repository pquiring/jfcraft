package jfcraft.packet;

/** Packet with no data
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.server.Server;

public class PacketOpenToLan extends Packet {

  public PacketOpenToLan() {}

  public PacketOpenToLan(byte cmd) {
    super(cmd);
  }

  //process on server side
  public void process(Server server, Client client) {
    server.startNetworking();
  }
}
