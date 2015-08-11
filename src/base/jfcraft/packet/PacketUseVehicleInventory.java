package jfcraft.packet;

/** Packet with no data
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.entity.VehicleBase;
import jfcraft.server.Server;
import jfcraft.item.*;

public class PacketUseVehicleInventory extends Packet {

  public PacketUseVehicleInventory() {}

  public PacketUseVehicleInventory(byte cmd) {
    super(cmd);
  }

  //process on server side
  public void process(Server server, Client client) {
    synchronized(client.lock) {
      client.player.vehicle.useEntity(client, true);
    }
  }
}
