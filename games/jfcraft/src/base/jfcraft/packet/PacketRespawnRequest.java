package jfcraft.packet;

/** Packet with no data
 *
 * @author pquiring
 */

import jfcraft.client.*;
import jfcraft.data.*;
import jfcraft.server.Server;

public class PacketRespawnRequest extends Packet {
  public PacketRespawnRequest() {}

  public PacketRespawnRequest(byte cmd) {
    super(cmd);
  }

  //process on server side
  public void process(Server server, Client client) {
    if (client.player.health != 0) {
      Static.log("Attempt to respawn but not dead!");
      return;
    }
    client.player.dim = 0;
    client.player.pos.x = server.world.spawn.x + 0.5f;
    client.player.pos.y = server.world.spawn.y;
    client.player.pos.z = server.world.spawn.z + 0.5f;
    client.player.health = 20;
    client.player.hunger = 20;
    client.player.saturation = 20;
    client.player.exhaustion = 0;
    client.serverTransport.respawn(client.player.pos.x, client.player.pos.y, client.player.pos.z);
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
