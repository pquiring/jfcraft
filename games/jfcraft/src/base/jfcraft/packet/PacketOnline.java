package jfcraft.packet;

/** Packet with no data
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.server.Server;

public class PacketOnline extends Packet {

  public PacketOnline() {}

  public PacketOnline(byte cmd) {
    super(cmd);
  }

  //process on server side
  public void process(Server server, Client client) {
    if (!client.player.offline) return;
    client.player.offline = false;
    synchronized(server.clientsLock) {
      server.dims[client.player.dim]++;
    }
  }
}
