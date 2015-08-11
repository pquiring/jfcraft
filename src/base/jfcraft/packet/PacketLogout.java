package jfcraft.packet;

/** Packet with no data
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.server.Server;

public class PacketLogout extends Packet {

  public PacketLogout() {}

  public PacketLogout(byte cmd) {
    super(cmd);
  }

  //process on server side
  public void process(Server server, Client client) {
    Static.log("Client logout");
    client.active = false;
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
