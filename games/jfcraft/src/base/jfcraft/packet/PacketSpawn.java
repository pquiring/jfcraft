package jfcraft.packet;

/** Packet with Entity
 *
 * @author pquiring
 */

import jfcraft.client.Client;
import jfcraft.data.*;
import jfcraft.entity.*;

public class PacketSpawn extends Packet {
  public EntityBase entity;

  public PacketSpawn() {}

  public PacketSpawn(byte cmd) {
    super(cmd);
  }

  public PacketSpawn(byte cmd, EntityBase entity) {
    super(cmd);
    this.entity = entity;
  }

  //process on client side
  public void process(Client client) {
    EntityBase e = entity;
    if (client.world.hasEntity(e.uid)) return;
    e.init();
    int cx = Static.floor(e.pos.x / 16.0f);
    int cz = Static.floor(e.pos.z / 16.0f);
    Chunk chunk = client.world.chunks.getChunk(e.dim, cx, cz);
    if (chunk == null) return;
    chunk.addEntity(e);
    client.world.addEntity(e);
  }

  @Override
  public boolean write(SerialBuffer buffer, boolean file) {
    super.write(buffer, file);
    entity.write(buffer, file);
    return true;
  }

  @Override
  public boolean read(SerialBuffer buffer, boolean file) {
    super.read(buffer, file);
    entity = (EntityBase)Static.entities.create(buffer);
    if (entity == null) {
      Static.log("Error:PacketSpawn:Entity not registered");
      return false;
    }
    entity.read(buffer, file);
    return true;
  }
}
