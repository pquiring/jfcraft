package jfcraft.packet;

/** Packet with World
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;

public class PacketWorld extends Packet {
  public World world;

  public PacketWorld() {}

  public PacketWorld(byte cmd) {
    super(cmd);
  }

  public PacketWorld(byte cmd, World world) {
    super(cmd);
    this.world = world;
  }

  //process on client side
  public void process(Client client) {
    client.world = world;
    client.world.init();
    client.world.chunks = new Chunks(world);
    if (!client.clientTransport.isLocal()) {
      client.world.assignIDs();
      Static.dims.init();
    }
    if (client.world.genSpawnAreaDone) {
      client.spawnAreaDonePercent = 100;
    }
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    world.write(buffer, file);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    world = new World(false);
    world.read(buffer, file);
    return true;
  }
}
