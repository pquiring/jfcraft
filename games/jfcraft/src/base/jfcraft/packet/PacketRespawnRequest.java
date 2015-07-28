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
    client.player.pos.x = server.world.spawn.x;
    client.player.pos.y = server.world.spawn.y;
    client.player.pos.z = server.world.spawn.z;
    client.player.health = 20;
    client.player.hunger = 20;
    client.player.saturation = 20;
    client.player.exhaustion = 0;
    Chunk chunk = client.player.getChunk();
    if (chunk == null) {
      int cx = Static.floor(client.player.pos.x / 16f);
      int cz = Static.floor(client.player.pos.z / 16f);
      chunk = server.world.chunks.getChunk2(client.player.dim, cx, cz, true, true, true);
    }
    chunk.addEntity(client.player);
    Static.log("Adding player back into world");
    server.world.addEntity(client.player);
    server.broadcastEntitySpawn(client.player);
    client.serverTransport.respawn(client.player.pos.x, client.player.pos.y, client.player.pos.z, Settings.current.dropItemsOnDeath);
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
